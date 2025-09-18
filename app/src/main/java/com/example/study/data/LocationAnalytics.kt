package com.example.study.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "location_analytics",
    foreignKeys = [
        ForeignKey(
            entity = FavoriteLocation::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["locationId"])
    ]
)
data class LocationAnalytics(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locationId: String,
    val sessionDate: Date,
    val duration: Long, // em minutos
    val cardsStudied: Int,
    val correctAnswers: Int,
    val averageResponseTime: Long, // em milissegundos
    val preferredCardTypes: List<FlashcardType>,
    val productivityScore: Double = 0.0,
    val difficultyLevel: String = "medium" // easy, medium, hard
)

@Entity(tableName = "location_rotation_memory")
data class LocationRotationMemory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locationId: String,
    val flashcardId: Long,
    val lastSeenDate: Date,
    val seenCount: Int = 1,
    val performanceScore: Double = 0.0
)
