package com.example.study.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_location")
data class UserLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val iconName: String = "ic_location",
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)