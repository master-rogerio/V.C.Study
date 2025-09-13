package com.example.study.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteLocation: FavoriteLocation): Long

    @Query("SELECT * FROM favorite_locations")
    fun getAllFavoriteLocations(): LiveData<List<FavoriteLocation>>

    @Query("SELECT * FROM favorite_locations WHERE id = :id")
    suspend fun getFavoriteLocationById(id: String): FavoriteLocation?

    @Delete
    suspend fun delete(favoriteLocation: FavoriteLocation)

    @Query("DELETE FROM favorite_locations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM favorite_locations")
    suspend fun deleteAll()

    @Update
    suspend fun update(favoriteLocation: FavoriteLocation)
}