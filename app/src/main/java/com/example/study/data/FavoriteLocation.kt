package com.example.study.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int = 100,
    val iconName: String = "ic_location", // CAMPO ADICIONADO
    val preferredCardTypes: List<FlashcardType> = emptyList(),
    val studySessionCount: Int = 0,
    val averagePerformance: Double = 0.0
)