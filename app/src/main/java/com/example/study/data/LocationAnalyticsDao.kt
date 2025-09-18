package com.example.study.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface LocationAnalyticsDao {
    
    @Query("SELECT * FROM location_analytics WHERE locationId = :locationId ORDER BY sessionDate DESC")
    fun getAnalyticsForLocation(locationId: String): Flow<List<LocationAnalytics>>
    
    @Query("SELECT * FROM location_analytics ORDER BY sessionDate DESC LIMIT 100")
    fun getAllRecentAnalytics(): Flow<List<LocationAnalytics>>
    
    @Query("SELECT * FROM location_analytics WHERE locationId = :locationId AND sessionDate >= :startDate")
    fun getAnalyticsForLocationSince(locationId: String, startDate: Date): Flow<List<LocationAnalytics>>
    
    @Query("SELECT AVG(productivityScore) FROM location_analytics WHERE locationId = :locationId")
    suspend fun getAverageProductivityScore(locationId: String): Double?
    
    @Query("SELECT COUNT(*) FROM location_analytics WHERE locationId = :locationId")
    suspend fun getSessionCount(locationId: String): Int
    
    @Query("SELECT AVG(duration) FROM location_analytics WHERE locationId = :locationId")
    suspend fun getAverageSessionDuration(locationId: String): Double?
    
    @Insert
    suspend fun insert(analytics: LocationAnalytics)
    
    @Update
    suspend fun update(analytics: LocationAnalytics)
    
    @Delete
    suspend fun delete(analytics: LocationAnalytics)
    
    @Query("DELETE FROM location_analytics WHERE locationId = :locationId")
    suspend fun deleteAllForLocation(locationId: String)
}

@Dao
interface LocationRotationMemoryDao {
    
    @Query("SELECT * FROM location_rotation_memory WHERE locationId = :locationId AND flashcardId = :flashcardId")
    suspend fun getRotationMemory(locationId: String, flashcardId: Long): LocationRotationMemory?
    
    @Query("SELECT * FROM location_rotation_memory WHERE locationId = :locationId ORDER BY lastSeenDate ASC")
    fun getRotationMemoryForLocation(locationId: String): Flow<List<LocationRotationMemory>>
    
    @Query("SELECT flashcardId FROM location_rotation_memory WHERE locationId = :locationId AND lastSeenDate >= :sinceDate")
    suspend fun getRecentlySeenFlashcards(locationId: String, sinceDate: Date): List<Long>
    
    @Query("SELECT flashcardId FROM location_rotation_memory WHERE locationId = :locationId ORDER BY lastSeenDate ASC LIMIT :limit")
    suspend fun getOldestSeenFlashcards(locationId: String, limit: Int): List<Long>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(memory: LocationRotationMemory)
    
    @Delete
    suspend fun delete(memory: LocationRotationMemory)
    
    @Query("DELETE FROM location_rotation_memory WHERE locationId = :locationId")
    suspend fun deleteAllForLocation(locationId: String)
    
    @Query("DELETE FROM location_rotation_memory WHERE lastSeenDate < :cutoffDate")
    suspend fun deleteOldMemories(cutoffDate: Date)
}
