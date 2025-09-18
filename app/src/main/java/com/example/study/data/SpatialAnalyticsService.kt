package com.example.study.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.math.*

class SpatialAnalyticsService(
    private val analyticsDao: LocationAnalyticsDao,
    private val rotationMemoryDao: LocationRotationMemoryDao,
    private val favoriteLocationDao: FavoriteLocationDao
) {
    
    suspend fun recordStudySession(
        locationId: String,
        duration: Long,
        cardsStudied: Int,
        correctAnswers: Int,
        averageResponseTime: Long,
        preferredCardTypes: List<FlashcardType>
    ) {
        val productivityScore = calculateProductivityScore(
            correctAnswers, cardsStudied, duration, averageResponseTime
        )
        
        val analytics = LocationAnalytics(
            locationId = locationId,
            sessionDate = Date(),
            duration = duration,
            cardsStudied = cardsStudied,
            correctAnswers = correctAnswers,
            averageResponseTime = averageResponseTime,
            preferredCardTypes = preferredCardTypes,
            productivityScore = productivityScore,
            difficultyLevel = determineDifficultyLevel(correctAnswers, cardsStudied)
        )
        
        analyticsDao.insert(analytics)
        
        // Atualiza performance média na localização favorita
        updateLocationPerformance(locationId, productivityScore)
    }
    
    private suspend fun updateLocationPerformance(locationId: String, newScore: Double) {
        val location = favoriteLocationDao.getFavoriteLocationById(locationId)
        location?.let {
            val newSessionCount = it.studySessionCount + 1
            val newAverage = ((it.averagePerformance * it.studySessionCount) + newScore) / newSessionCount
            
            val updatedLocation = it.copy(
                studySessionCount = newSessionCount,
                averagePerformance = newAverage
            )
            favoriteLocationDao.update(updatedLocation)
        }
    }
    
    private fun calculateProductivityScore(
        correctAnswers: Int,
        cardsStudied: Int,
        duration: Long,
        averageResponseTime: Long
    ): Double {
        if (cardsStudied == 0) return 0.0
        
        val accuracy = correctAnswers.toDouble() / cardsStudied
        val speedFactor = max(0.1, 1.0 - (averageResponseTime / 10000.0)) // Penaliza respostas muito lentas
        val efficiency = cardsStudied.toDouble() / (duration / 60.0) // Cards por minuto
        
        return (accuracy * 0.4 + speedFactor * 0.3 + efficiency * 0.3) * 100
    }
    
    private fun determineDifficultyLevel(correctAnswers: Int, cardsStudied: Int): String {
        if (cardsStudied == 0) return "medium"
        
        val accuracy = correctAnswers.toDouble() / cardsStudied
        return when {
            accuracy >= 0.8 -> "easy"
            accuracy >= 0.6 -> "medium"
            else -> "hard"
        }
    }
    
    fun getAnalyticsForLocation(locationId: String): Flow<List<LocationAnalytics>> {
        return analyticsDao.getAnalyticsForLocation(locationId)
    }
    
    fun getAllRecentAnalytics(): Flow<List<LocationAnalytics>> {
        return analyticsDao.getAllRecentAnalytics()
    }
    
    suspend fun generatePerformanceReport(locationId: String): LocationPerformanceReport? {
        val location = favoriteLocationDao.getFavoriteLocationById(locationId) ?: return null
        
        // Obtem analytics reais da localização
        val recentAnalytics = try {
            analyticsDao.getAnalyticsForLocation(locationId).first()
        } catch (e: Exception) {
            emptyList<LocationAnalytics>()
        }
        
        if (recentAnalytics.isEmpty()) {
            // Retorna relatório básico se não há analytics
            return LocationPerformanceReport(
                locationId = locationId,
                locationName = location.name,
                totalSessions = location.studySessionCount,
                averagePerformance = location.averagePerformance,
                averageDuration = 0.0,
                productivityScore = location.averagePerformance,
                bestCardTypes = location.preferredCardTypes,
                performanceTrend = PerformanceTrend.INSUFFICIENT_DATA,
                insights = emptyList()
            )
        }
        
        val totalSessions = recentAnalytics.size
        val averagePerformance = recentAnalytics.map { it.productivityScore }.average()
        val averageDuration = recentAnalytics.map { it.duration }.average()
        
        val bestCardTypes = findBestPerformingCardTypes(recentAnalytics)
        val performanceTrend = calculatePerformanceTrend(recentAnalytics)
        val insights = generateInsights(locationId, location.name, recentAnalytics)
        
        return LocationPerformanceReport(
            locationId = locationId,
            locationName = location.name,
            totalSessions = totalSessions,
            averagePerformance = averagePerformance,
            averageDuration = averageDuration,
            productivityScore = averagePerformance,
            bestCardTypes = bestCardTypes,
            performanceTrend = performanceTrend,
            insights = insights
        )
    }
    
    private fun findBestPerformingCardTypes(analytics: List<LocationAnalytics>): List<FlashcardType> {
        val cardTypePerformance = mutableMapOf<FlashcardType, MutableList<Double>>()
        
        analytics.forEach { session ->
            session.preferredCardTypes.forEach { cardType ->
                cardTypePerformance.getOrPut(cardType) { mutableListOf() }.add(session.productivityScore)
            }
        }
        
        return cardTypePerformance.mapValues { (_, scores) -> scores.average() }
            .toList()
            .sortedByDescending { it.second }
            .take(2)
            .map { it.first }
    }
    
    private fun calculatePerformanceTrend(analytics: List<LocationAnalytics>): PerformanceTrend {
        if (analytics.size < 3) return PerformanceTrend.INSUFFICIENT_DATA
        
        val sortedAnalytics = analytics.sortedBy { it.sessionDate }
        val firstHalf = sortedAnalytics.take(sortedAnalytics.size / 2)
        val secondHalf = sortedAnalytics.drop(sortedAnalytics.size / 2)
        
        val firstHalfAvg = firstHalf.map { it.productivityScore }.average()
        val secondHalfAvg = secondHalf.map { it.productivityScore }.average()
        
        val improvement = secondHalfAvg - firstHalfAvg
        
        return when {
            improvement > 5 -> PerformanceTrend.IMPROVING
            improvement < -5 -> PerformanceTrend.DECLINING
            else -> PerformanceTrend.STABLE
        }
    }
    
    private fun generateInsights(
        locationId: String,
        locationName: String,
        analytics: List<LocationAnalytics>
    ): List<ProductivityInsight> {
        val insights = mutableListOf<ProductivityInsight>()
        
        // Insight sobre melhor horário
        val hourlyPerformance = analytics.groupBy { 
            Calendar.getInstance().apply { time = it.sessionDate }.get(Calendar.HOUR_OF_DAY) 
        }.mapValues { (_, sessions) -> sessions.map { it.productivityScore }.average() }
        
        val bestHour = hourlyPerformance.maxByOrNull { it.value }
        if (bestHour != null && bestHour.value > 70) {
            insights.add(ProductivityInsight(
                locationId = locationId,
                locationName = locationName,
                insightType = InsightType.BEST_TIME_TO_STUDY,
                message = "Você tem melhor performance às ${bestHour.key}:00",
                recommendation = "Tente estudar mais neste horário para maximizar sua produtividade",
                confidence = 0.8
            ))
        }
        
        // Insight sobre duração ideal
        val durationPerformance = analytics.groupBy { 
            when (it.duration) {
                in 0..15 -> "curta"
                in 16..45 -> "média"
                else -> "longa"
            }
        }.mapValues { (_, sessions) -> sessions.map { it.productivityScore }.average() }
        
        val optimalDuration = durationPerformance.maxByOrNull { it.value }
        if (optimalDuration != null) {
            insights.add(ProductivityInsight(
                locationId = locationId,
                locationName = locationName,
                insightType = InsightType.OPTIMAL_DURATION,
                message = "Sessões de duração ${optimalDuration.key} têm melhor performance",
                recommendation = "Mantenha sessões de ${optimalDuration.key} duração para melhor resultado",
                confidence = 0.7
            ))
        }
        
        return insights
    }
    
    suspend fun compareLocations(): LocationComparison {
        val locations = favoriteLocationDao.getAllFavoriteLocationsSync()
        val reports = locations.mapNotNull { location ->
            generatePerformanceReport(location.id)
        }
        
        val bestPerformingLocation = reports.maxByOrNull { it.productivityScore }
        
        val recommendations = mutableListOf<ProductivityInsight>()
        if (bestPerformingLocation != null) {
            recommendations.add(ProductivityInsight(
                locationId = bestPerformingLocation.locationId,
                locationName = bestPerformingLocation.locationName,
                insightType = InsightType.MOST_PRODUCTIVE_LOCATION,
                message = "${bestPerformingLocation.locationName} é seu local mais produtivo",
                recommendation = "Use este local mais frequentemente para melhorar seus estudos",
                confidence = 0.9
            ))
        }
        
        return LocationComparison(
            locations = reports,
            bestPerformingLocation = bestPerformingLocation,
            recommendations = recommendations
        )
    }
}
