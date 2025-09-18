package com.example.study.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val theme: String,
    val createdAt: Long = System.currentTimeMillis(),
    var firebaseId: String? = null // Usamos 'var' para poder atualizá-lo depois de inserir
)