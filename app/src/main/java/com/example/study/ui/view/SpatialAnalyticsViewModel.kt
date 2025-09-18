package com.example.study.ui.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpatialAnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = FlashcardDatabase.getDatabase(application)
    private val analyticsDao = database.locationAnalyticsDao()
    private val rotationMemoryDao = database.locationRotationMemoryDao()
    private val favoriteLocationDao = database.favoriteLocationDao()
    private val flashcardDao = database.flashcardDao()
    
    private val spatialAnalyticsService = SpatialAnalyticsService(
        analyticsDao, rotationMemoryDao, favoriteLocationDao
    )
    
    private val intelligentRotationService = IntelligentRotationService(
        flashcardDao, rotationMemoryDao, favoriteLocationDao, spatialAnalyticsService
    )
    
    val allLocations: Flow<List<FavoriteLocation>> = favoriteLocationDao.getAllFavoriteLocationsFlow()
    
    private val _locationComparison = MutableStateFlow<LocationComparison?>(null)
    val locationComparison: StateFlow<LocationComparison?> = _locationComparison
    
    private val _locationReport = MutableStateFlow<LocationPerformanceReport?>(null)
    val locationReport: StateFlow<LocationPerformanceReport?> = _locationReport
    
    private val _rotationStats = MutableStateFlow<RotationStats?>(null)
    val rotationStats: StateFlow<RotationStats?> = _rotationStats
    
    private val _optimizationRecommendations = MutableStateFlow<OptimizationRecommendations?>(null)
    val optimizationRecommendations: StateFlow<OptimizationRecommendations?> = _optimizationRecommendations
    
    fun loadLocationComparison() = viewModelScope.launch {
        try {
            // Primeiro, corrigir dados de performance incorretos
            val flashcardViewModel = FlashcardViewModel(getApplication())
            flashcardViewModel.fixIncorrectPerformanceData()
            
            val comparison = spatialAnalyticsService.compareLocations()
            _locationComparison.value = comparison
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun loadLocationReport(locationId: String) = viewModelScope.launch {
        try {
            val report = spatialAnalyticsService.generatePerformanceReport(locationId)
            _locationReport.value = report
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun loadRotationStats(locationId: String) = viewModelScope.launch {
        try {
            val stats = intelligentRotationService.getRotationStatsForLocation(locationId)
            _rotationStats.value = stats
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun loadOptimizationRecommendations(locationId: String) = viewModelScope.launch {
        try {
            val recommendations = intelligentRotationService.optimizeRotationForLocation(locationId)
            _optimizationRecommendations.value = recommendations
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun recordStudySession(
        locationId: String,
        duration: Long,
        cardsStudied: Int,
        correctAnswers: Int,
        averageResponseTime: Long,
        preferredCardTypes: List<FlashcardType>
    ) = viewModelScope.launch {
        try {
            spatialAnalyticsService.recordStudySession(
                locationId, duration, cardsStudied, correctAnswers,
                averageResponseTime, preferredCardTypes
            )
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun recordFlashcardInteraction(
        locationId: String,
        flashcardId: Long,
        performanceScore: Double
    ) = viewModelScope.launch {
        try {
            intelligentRotationService.recordFlashcardInteraction(
                locationId, flashcardId, performanceScore
            )
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun getFlashcardsForLocationWithIntelligentRotation(
        locationId: String,
        limit: Int = 10
    ): List<Flashcard> {
        return try {
            intelligentRotationService.getFlashcardsForLocationWithIntelligentRotation(
                locationId, limit
            )
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun refreshAnalytics() = viewModelScope.launch {
        loadLocationComparison()
        _locationReport.value?.let { report ->
            loadLocationReport(report.locationId)
        }
    }
    
    fun clearLocationAnalytics(locationId: String) = viewModelScope.launch {
        try {
            analyticsDao.deleteAllForLocation(locationId)
            rotationMemoryDao.deleteAllForLocation(locationId)
            refreshAnalytics()
        } catch (e: Exception) {
            // Handle error
        }
    }
}
