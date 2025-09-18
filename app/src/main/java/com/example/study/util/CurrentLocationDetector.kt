package com.example.study.util

import android.content.Context
import android.location.Location
import com.example.study.data.FavoriteLocation
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Serviço para detectar a localização atual do usuário usando GPS
 */
class CurrentLocationDetector(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Obtém a localização atual do usuário
     * @return Location atual ou null se não conseguir obter
     */
    suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    1000L // 1 segundo
                ).build()
                
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            fusedLocationClient.removeLocationUpdates(this)
                            val location = locationResult.lastLocation
                            if (location != null) {
                                continuation.resume(location)
                            } else {
                                continuation.resume(null)
                            }
                        }
                    },
                    null
                )
                
                continuation.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
                }
            } catch (e: SecurityException) {
                continuation.resumeWithException(e)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
    
    /**
     * Encontra a localização favorita mais próxima da localização atual
     * @param currentLocation Localização atual do usuário
     * @param favoriteLocations Lista de localizações favoritas
     * @param maxDistance Distância máxima em metros (padrão: 100m)
     * @return ID da localização mais próxima ou null se nenhuma estiver próxima
     */
    fun findNearestLocation(
        currentLocation: Location,
        favoriteLocations: List<FavoriteLocation>,
        maxDistance: Float = 100f
    ): String? {
        if (favoriteLocations.isEmpty()) return null
        
        var nearestLocation: FavoriteLocation? = null
        var minDistance = Float.MAX_VALUE
        
        favoriteLocations.forEach { location ->
            val distance = calculateDistance(
                currentLocation.latitude,
                currentLocation.longitude,
                location.latitude,
                location.longitude
            )
            
            if (distance <= maxDistance && distance < minDistance) {
                minDistance = distance
                nearestLocation = location
            }
        }
        
        return nearestLocation?.id
    }
    
    /**
     * Calcula a distância entre duas coordenadas usando a fórmula de Haversine
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    /**
     * Detecta se o usuário está em uma localização conhecida
     * @param currentLocation Localização atual
     * @param favoriteLocations Lista de localizações favoritas
     * @param threshold Distância limite em metros
     * @return true se estiver em uma localização conhecida
     */
    fun isInKnownLocation(
        currentLocation: Location,
        favoriteLocations: List<FavoriteLocation>,
        threshold: Float = 50f
    ): Boolean {
        return findNearestLocation(currentLocation, favoriteLocations, threshold) != null
    }
}
