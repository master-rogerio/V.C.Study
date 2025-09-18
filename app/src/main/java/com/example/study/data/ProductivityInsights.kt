package com.example.study.data

import java.util.Date

data class ProductivityInsight(
    val locationId: String,
    val locationName: String,
    val insightType: InsightType,
    val message: String,
    val recommendation: String,
    val confidence: Double,
    val generatedDate: Date = Date()
)

enum class InsightType {
    BEST_TIME_TO_STUDY,
    MOST_PRODUCTIVE_LOCATION,
    OPTIMAL_DURATION,
    BEST_CARD_TYPES,
    PERFORMANCE_TREND,
    CONSISTENCY_SCORE
}

data class LocationPerformanceReport(
    val locationId: String,
    val locationName: String,
    val totalSessions: Int,
    val averagePerformance: Double,
    val averageDuration: Double,
    val productivityScore: Double,
    val bestCardTypes: List<FlashcardType>,
    val performanceTrend: PerformanceTrend,
    val insights: List<ProductivityInsight>,
    val generatedDate: Date = Date()
)

enum class PerformanceTrend {
    IMPROVING,
    DECLINING,
    STABLE,
    INSUFFICIENT_DATA
}

data class LocationComparison(
    val locations: List<LocationPerformanceReport>,
    val bestPerformingLocation: LocationPerformanceReport?,
    val recommendations: List<ProductivityInsight>
)
