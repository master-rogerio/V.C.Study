package com.example.study.util

import android.content.Context
import android.location.Location
import com.example.study.data.FavoriteLocation
import kotlin.math.*

/**
 * Serviço para detectar a localização atual do usuário e encontrar
 * a localização favorita mais próxima
 */
class LocationDetectionService(private val context: Context) {
    
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
     * Cria uma localização padrão baseada na localização atual
     * @param currentLocation Localização atual do usuário
     * @return Localização favorita criada
     */
    fun createDefaultLocation(currentLocation: Location): FavoriteLocation {
        return FavoriteLocation(
            name = "Local Atual",
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
            iconName = "ic_location",
            preferredCardTypes = emptyList()
        )
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
