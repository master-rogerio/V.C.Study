package com.example.study.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class IntelligentRotationService(
    private val flashcardDao: FlashcardDao,
    private val rotationMemoryDao: LocationRotationMemoryDao,
    private val favoriteLocationDao: FavoriteLocationDao,
    private val analyticsService: SpatialAnalyticsService
) {
    
    private val memoryThreshold = 7 * 24 * 60 * 60 * 1000L // 7 dias em milissegundos
    private val maxRecentCards = 20
    
    suspend fun getFlashcardsForLocationWithIntelligentRotation(
        locationId: String,
        limit: Int = 10
    ): List<Flashcard> {
        val location = favoriteLocationDao.getFavoriteLocationById(locationId) ?: return emptyList()
        val preferredTypes = location.preferredCardTypes
        
        // Obte todos os flashcards disponíveis usando uma abordagem diferente
        val allFlashcards = getAllFlashcardsSync()
        
        // Filtra por tipos preferidos se especificado
        val filteredFlashcards = if (preferredTypes.isNotEmpty()) {
            allFlashcards.filter { it.type in preferredTypes }
        } else {
            allFlashcards
        }
        
        if (filteredFlashcards.isEmpty()) return emptyList()
        
        // Aplica algoritmo de rotação inteligente
        return applyIntelligentRotation(locationId, filteredFlashcards, limit)
    }
    
    private suspend fun getAllFlashcardsSync(): List<Flashcard> {
        return flashcardDao.getAllFlashcardsByReviewSync()
    }
    
    private suspend fun applyIntelligentRotation(
        locationId: String,
        availableFlashcards: List<Flashcard>,
        limit: Int
    ): List<Flashcard> {
        val now = Date()
        val cutoffDate = Date(now.time - memoryThreshold)
        
        // Limpa memórias antigas
        rotationMemoryDao.deleteOldMemories(cutoffDate)
        
        // Obtem flashcards recentemente vistos
        val recentlySeenIds = rotationMemoryDao.getRecentlySeenFlashcards(locationId, cutoffDate)
        
        // Obtem flashcards nunca vistos nesta localização
        val neverSeenFlashcards = availableFlashcards.filter { flashcard ->
            rotationMemoryDao.getRotationMemory(locationId, flashcard.id) == null
        }
        
        // Obtem flashcards vistos há muito tempo
        val oldSeenFlashcards = availableFlashcards.filter { flashcard ->
            val memory = rotationMemoryDao.getRotationMemory(locationId, flashcard.id)
            memory != null && memory.lastSeenDate < cutoffDate
        }
        
        // Obtem flashcards vistos recentemente mas com baixa performance
        val lowPerformanceFlashcards = availableFlashcards.filter { flashcard ->
            val memory = rotationMemoryDao.getRotationMemory(locationId, flashcard.id)
            memory != null && 
            memory.lastSeenDate >= cutoffDate && 
            memory.performanceScore < 0.6 // Performance baixa
        }
        
        // Priorizar flashcards baseado em algoritmo inteligente
        val prioritizedFlashcards = prioritizeFlashcards(
            neverSeenFlashcards,
            oldSeenFlashcards,
            lowPerformanceFlashcards,
            availableFlashcards.filter { it.id in recentlySeenIds },
            limit
        )
        
        // Embaralhar para evitar ordem previsível
        return prioritizedFlashcards.shuffled().take(limit)
    }
    
    private suspend fun prioritizeFlashcards(
        neverSeen: List<Flashcard>,
        oldSeen: List<Flashcard>,
        lowPerformance: List<Flashcard>,
        recentlySeen: List<Flashcard>,
        limit: Int
    ): List<Flashcard> {
        val prioritized = mutableListOf<Flashcard>()
        
        // 1. Prioridade máxima: Flashcards nunca vistos (40% da seleção)
        val neverSeenCount = minOf((limit * 0.4).toInt(), neverSeen.size)
        prioritized.addAll(neverSeen.take(neverSeenCount))
        
        // 2. Segunda prioridade: Flashcards vistos há muito tempo (30% da seleção)
        val oldSeenCount = minOf((limit * 0.3).toInt(), oldSeen.size)
        prioritized.addAll(oldSeen.take(oldSeenCount))
        
        // 3. Terceira prioridade: Flashcards com baixa performance (20% da seleção)
        val lowPerformanceCount = minOf((limit * 0.2).toInt(), lowPerformance.size)
        prioritized.addAll(lowPerformance.take(lowPerformanceCount))
        
        // 4. Preencher com flashcards recentes se necessário (10% da seleção)
        val remaining = limit - prioritized.size
        if (remaining > 0) {
            val recentCount = minOf(remaining, recentlySeen.size)
            prioritized.addAll(recentlySeen.take(recentCount))
        }
        
        return prioritized
    }
    
    suspend fun recordFlashcardInteraction(
        locationId: String,
        flashcardId: Long,
        performanceScore: Double
    ) {
        val now = Date()
        val existingMemory = rotationMemoryDao.getRotationMemory(locationId, flashcardId)
        
        val memory = if (existingMemory != null) {
            existingMemory.copy(
                lastSeenDate = now,
                seenCount = existingMemory.seenCount + 1,
                performanceScore = (existingMemory.performanceScore + performanceScore) / 2
            )
        } else {
            LocationRotationMemory(
                locationId = locationId,
                flashcardId = flashcardId,
                lastSeenDate = now,
                seenCount = 1,
                performanceScore = performanceScore
            )
        }
        
        rotationMemoryDao.insertOrUpdate(memory)
    }
    
    suspend fun getRotationStatsForLocation(locationId: String): RotationStats {
        val now = Date()
        val cutoffDate = Date(now.time - memoryThreshold)
        
        // Obter memórias reais da localização
        val recentMemories = try {
            rotationMemoryDao.getRotationMemoryForLocation(locationId).first()
        } catch (e: Exception) {
            emptyList<LocationRotationMemory>()
        }
        
        val totalFlashcardsSeen = recentMemories.size
        val averagePerformance = if (recentMemories.isNotEmpty()) {
            recentMemories.map { it.performanceScore }.average()
        } else 0.0
        
        val lowPerformanceCount = recentMemories.count { it.performanceScore < 0.6 }
        val highPerformanceCount = recentMemories.count { it.performanceScore > 0.8 }
        
        return RotationStats(
            locationId = locationId,
            totalFlashcardsSeen = totalFlashcardsSeen,
            averagePerformance = averagePerformance,
            lowPerformanceCount = lowPerformanceCount,
            highPerformanceCount = highPerformanceCount,
            rotationEfficiency = calculateRotationEfficiency(recentMemories)
        )
    }
    
    private fun calculateRotationEfficiency(memories: List<LocationRotationMemory>): Double {
        if (memories.isEmpty()) return 0.0
        
        val avgSeenCount = memories.map { it.seenCount }.average()
        val avgPerformance = memories.map { it.performanceScore }.average()
        
        // Eficiência baseada em performance alta com poucas repetições
        return (avgPerformance * 0.7) + ((1.0 / avgSeenCount) * 0.3)
    }
    
    suspend fun optimizeRotationForLocation(locationId: String): OptimizationRecommendations {
        val stats = getRotationStatsForLocation(locationId)
        val location = favoriteLocationDao.getFavoriteLocationById(locationId)
        
        val recommendations = mutableListOf<String>()
        
        if (stats.averagePerformance < 0.6) {
            recommendations.add("Considere reduzir a frequência de revisão para melhorar a retenção")
        }
        
        if (stats.lowPerformanceCount > stats.highPerformanceCount) {
            recommendations.add("Foque em flashcards com baixa performance para melhorar os resultados")
        }
        
        if (stats.rotationEfficiency < 0.5) {
            recommendations.add("O algoritmo de rotação pode ser otimizado para este local")
        }
        
        return OptimizationRecommendations(
            locationId = locationId,
            locationName = location?.name ?: "Local desconhecido",
            recommendations = recommendations,
            currentEfficiency = stats.rotationEfficiency,
            suggestedImprovements = generateImprovementSuggestions(stats)
        )
    }
    
    private fun generateImprovementSuggestions(stats: RotationStats): List<String> {
        val suggestions = mutableListOf<String>()
        
        if (stats.averagePerformance < 0.7) {
            suggestions.add("Aumentar intervalo entre revisões")
        }
        
        if (stats.highPerformanceCount < stats.lowPerformanceCount) {
            suggestions.add("Priorizar flashcards com baixa performance")
        }
        
        return suggestions
    }
}

data class RotationStats(
    val locationId: String,
    val totalFlashcardsSeen: Int,
    val averagePerformance: Double,
    val lowPerformanceCount: Int,
    val highPerformanceCount: Int,
    val rotationEfficiency: Double
)

data class OptimizationRecommendations(
    val locationId: String,
    val locationName: String,
    val recommendations: List<String>,
    val currentEfficiency: Double,
    val suggestedImprovements: List<String>
)
