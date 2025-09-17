package com.example.study

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.study.data.FlashcardDatabase
import com.example.study.data.FavoriteLocation
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val CHANNEL_ID = "location_service_channel"
        const val NOTIFICATION_ID = 1
        const val LOCATION_UPDATE_INTERVAL = 30000L // 30 segundos
        const val TAG = "LocationService"
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationCallback()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "LocationService iniciado")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "FOREGROUND_SERVICE permission not granted")
            stopSelf()
            return START_NOT_STICKY
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "ACCESS_FINE_LOCATION permission not granted")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d(TAG, "Nova localização: ${location.latitude}, ${location.longitude}")
                    checkNearbyLocations(location)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Permissão de localização não concedida")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        )
            .setMinUpdateIntervalMillis(15000L) // Mínimo 15 segundos
            .setMaxUpdateDelayMillis(60000L) // Máximo 1 minuto
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        Log.d(TAG, "Atualizações de localização iniciadas")
    }

    private fun checkNearbyLocations(currentLocation: Location) {
        serviceScope.launch {
            try {
                val db = FlashcardDatabase.getDatabase(this@LocationService)
                val locations = db.favoriteLocationDao().getAllFavoriteLocationsFlow()
                
                // Como getAllFavoriteLocationsFlow() retorna Flow, precisamos coletar os dados
                // Por enquanto, vamos usar uma abordagem diferente
                val allLocations = db.favoriteLocationDao().getAllFavoriteLocations()
                
                // Como getAllFavoriteLocations() retorna LiveData, vamos usar uma abordagem síncrona
                // Vamos implementar um método que retorna List diretamente
                val locationsList = getAllLocationsSync(db)
                
                Log.d(TAG, "Verificando ${locationsList.size} locais favoritos")

                locationsList.forEach { location: FavoriteLocation ->
                    if (location.isGeofenceActive) {
                        val distance = calculateDistance(
                            currentLocation.latitude,
                            currentLocation.longitude,
                            location.latitude,
                            location.longitude
                        )

                        Log.d(TAG, "Distância para ${location.name}: ${distance}m")

                        // Se estiver dentro do raio de 50 metros
                        if (distance <= 50) {
                            Log.d(TAG, "Usuário próximo de ${location.name}, enviando lembrete")
                            sendStudyReminder(location)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao verificar locais próximos: ${e.message}")
            }
        }
    }

    private suspend fun getAllLocationsSync(db: com.example.study.data.FlashcardDatabase): List<FavoriteLocation> {
        return withContext(Dispatchers.IO) {
            try {
                db.favoriteLocationDao().getAllFavoriteLocationsSync()
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter locais: ${e.message}")
                emptyList()
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun sendStudyReminder(location: FavoriteLocation) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "study_reminder_channel",
                "Study Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Lembretes de estudo baseados em localização"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("locationId", location.id)
            putExtra("preferredCardTypes", location.preferredCardTypes.map { it.name }.toTypedArray())
            putExtra("intelligentRotation", true)
            putExtra("source", "location_service")
            putExtra("locationName", location.name)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, location.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "study_reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hora de Estudar! 📚")
            .setContentText("Você está em ${location.name}. Que tal uma sessão de estudos?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Você está em ${location.name}. Que tal uma sessão de estudos contextual? Os flashcards preferidos para este local estão prontos para você!"))
            .build()

        notificationManager.notify(location.id.hashCode(), notification)
        Log.d(TAG, "Lembrete de estudo enviado para: ${location.name}")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Serviço de monitoramento de localização para lembretes de estudo"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VCStudy - Monitoramento de Localização")
            .setContentText("Monitorando sua localização para lembretes de estudo")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "LocationService destruído")
    }
}