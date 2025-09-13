package com.example.study.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userLocation: UserLocation): Long

    @Query("SELECT * FROM user_location ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLocation(): Flow<UserLocation?>

    @Query("SELECT * FROM user_location ORDER BY timestamp DESC")
    fun getAllUserLocations(): LiveData<List<UserLocation>>
    
    @Query("SELECT * FROM user_location WHERE id = :id")
    suspend fun getUserLocationById(id: Long): UserLocation?
    
    @Delete
    suspend fun delete(userLocation: UserLocation)
    
    @Query("DELETE FROM user_location WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM user_location")
    suspend fun deleteAll()
}