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
        val intent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopLocationService() {
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
    }

    fun insert(location: FavoriteLocation) {
        viewModelScope.launch {
            locationDao.insert(location)
        }
    }

    fun update(location: FavoriteLocation) {
        viewModelScope.launch {
            locationDao.update(location)
        }
    }

    fun delete(location: FavoriteLocation) {
        viewModelScope.launch {
            locationDao.delete(location)
        }
    }

    fun addGeofence(location: FavoriteLocation) {
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            android.util.Log.w("Geofence", "Permissão de localização não concedida")
            return
        }

        val geofence = Geofence.Builder()
            .setRequestId(location.id.toString())
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

        // CORREÇÃO: Remover parâmetro context da chamada
        val geofencePendingIntent = createGeofencePendingIntent()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                Log.d("Geofence", "Geofence adicionado com sucesso para: ${location.name}")
                Log.d("Geofence", "ID do geofence: ${location.id}")
                Log.d("Geofence", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                // Aguarda um pouco antes de marcar como ativo e atualiza estado no banco de dados

                Handler(Looper.getMainLooper()).postDelayed({
                    update(location.copy(isGeofenceActive = true))
                    Log.d("Geofence", "Geofence marcado como ativo após delay")
                }, 2000) // 2 segundos de delay
            }
            .addOnFailureListener {exception ->
                android.util.Log.e("Geofence", "Falha ao adicionar geofence: ${exception.message}")
            }
    }

    // CORREÇÃO: Removeção do método isGeofenceActive que usa registeredGeofences (não disponível)
    // private fun isGeofenceActive(locationId: String): Boolean {
    //     return try {
    //         // Verificar se o geofence está ativo no sistema
    //         val geofenceList = geofencingClient.registeredGeofences
    //         geofenceList.result?.any { it.requestId == locationId } ?: false
    //     } catch (e: Exception) {
    //         Log.e("Geofence", "Erro ao verificar estado do geofence: ${e.message}")
    //         false
    //     }
    // }

    fun removeGeofence(location: FavoriteLocation) {
        val geofenceIds = listOf(location.id.toString())

        geofencingClient.removeGeofences(geofenceIds)
            .addOnSuccessListener {
                Log.d("Geofence", "Geofence removido com sucesso para: ${location.name}")
                update(location.copy(isGeofenceActive = false))
            }
            .addOnFailureListener { exception ->
                Log.e("Geofence", "Erro ao remover geofence: ${exception.message}")
            }
    }

    // CORREÇÃO: Remover parâmetro context do método
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
}
