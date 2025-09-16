package com.example.study.ui.view

import android.Manifest
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.GeofenceBroadcastReceiver
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
    
    val allLocations: Flow<List<FavoriteLocation>> = locationDao.getAllFavoriteLocationsFlow()
    
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
            return
        }
        
        val geofence = Geofence.Builder()
            .setRequestId(location.id.toString())
            .setCircularRegion(
                location.latitude,
                location.longitude,
                100f // 100 metros de raio
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
                // Geofence adicionado com sucesso
            }
            .addOnFailureListener {
                // Falha ao adicionar geofence
            }
    }
    
    fun removeGeofence(locationId: String) {
        geofencingClient.removeGeofences(listOf(locationId))
            .addOnSuccessListener {
                // Geofence removido com sucesso
            }
            .addOnFailureListener {
                // Falha ao remover geofence
            }
    }
    
    private fun createGeofencePendingIntent(): PendingIntent {
        val intent = Intent(getApplication(), GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            getApplication(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}