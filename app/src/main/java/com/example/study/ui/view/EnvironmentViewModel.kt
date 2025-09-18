package com.example.study.ui.view

import android.Manifest
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.GeofenceBroadcastReceiver
import com.example.study.LocationService
import com.example.study.data.FavoriteLocation
import com.example.study.data.FlashcardDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EnvironmentViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FlashcardDatabase.getDatabase(application)
    private val locationDao = database.favoriteLocationDao()
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(application)
    private val context = getApplication<Application>()

    val allLocations: Flow<List<FavoriteLocation>> = locationDao.getAllFavoriteLocationsFlow()

    fun startLocationService() {
        Log.d("EnvironmentViewModel", "Iniciando LocationService")
        val intent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopLocationService() {
        Log.d("EnvironmentViewModel", "Parando LocationService")
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
    }

    fun insert(location: FavoriteLocation) {
        viewModelScope.launch {
            locationDao.insert(location)
            Log.d("EnvironmentViewModel", "Local inserido: ${location.name}")
        }
    }

    fun update(location: FavoriteLocation) {
        viewModelScope.launch {
            locationDao.update(location)
            Log.d("EnvironmentViewModel", "Local atualizado: ${location.name}")
        }
    }

    fun delete(location: FavoriteLocation) {
        viewModelScope.launch {
            locationDao.delete(location)
            Log.d("EnvironmentViewModel", "Local deletado: ${location.name}")
        }
    }

    fun addGeofence(location: FavoriteLocation) {
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("EnvironmentViewModel", "Permissão de localização não concedida")
            return
        }

        Log.d("EnvironmentViewModel", "Adicionando geofence para: ${location.name}")

        val geofence = Geofence.Builder()
            .setRequestId(location.id)
            .setCircularRegion(
                location.latitude,
                location.longitude,
                50f // 50 metros de raio
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencePendingIntent = createGeofencePendingIntent()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                Log.d("EnvironmentViewModel", "Geofence adicionado com sucesso para: ${location.name}")
                Log.d("EnvironmentViewModel", "ID do geofence: ${location.id}")
                Log.d("EnvironmentViewModel", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                
                // Aguarda um pouco antes de marcar como ativo
                Handler(Looper.getMainLooper()).postDelayed({
                    update(location.copy(isGeofenceActive = true))
                    Log.d("EnvironmentViewModel", "Geofence marcado como ativo após delay")
                }, 2000) // 2 segundos de delay
            }
            .addOnFailureListener { exception ->
                Log.e("EnvironmentViewModel", "Falha ao adicionar geofence: ${exception.message}")
            }
    }

    fun removeGeofence(location: FavoriteLocation) {
        Log.d("EnvironmentViewModel", "Removendo geofence para: ${location.name}")
        
        val geofenceIds = listOf(location.id)

        geofencingClient.removeGeofences(geofenceIds)
            .addOnSuccessListener {
                Log.d("EnvironmentViewModel", "Geofence removido com sucesso para: ${location.name}")
                update(location.copy(isGeofenceActive = false))
            }
            .addOnFailureListener { exception ->
                Log.e("EnvironmentViewModel", "Erro ao remover geofence: ${exception.message}")
            }
    }

    private fun createGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = "com.example.study.ACTION_GEOFENCE_TRANSITION"
            putExtra("source", "geofence")
        }

        Log.d("EnvironmentViewModel", "Criando PendingIntent com action: ${intent.action}")
        Log.d("EnvironmentViewModel", "Intent extras: ${intent.extras}")

        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // Método para verificar se há locais ativos e iniciar/parar o serviço
    fun checkAndManageLocationService() {
        viewModelScope.launch {
            allLocations.collect { locations ->
                val hasActiveLocations = locations.any { it.isGeofenceActive }
                Log.d("EnvironmentViewModel", "Locais ativos: $hasActiveLocations")
                
                if (hasActiveLocations) {
                    startLocationService()
                } else {
                    stopLocationService()
                }
            }
        }
    }
    
    // Método para obter locais síncronamente
    suspend fun getAllLocationsSync(): List<FavoriteLocation> {
        return locationDao.getAllFavoriteLocationsSync()
    }
}