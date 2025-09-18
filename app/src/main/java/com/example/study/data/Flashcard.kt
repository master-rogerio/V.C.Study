package com.example.study.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId")]
)
data class Flashcard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val type: FlashcardType = FlashcardType.FRONT_BACK,

    // Conteúdo Principal
    val front: String,
    val back: String,

    // Suporte a Multimídia
    val frontImageUrl: String? = null,
    val frontAudioUrl: String? = null,
    val backImageUrl: String? = null,
    val backAudioUrl: String? = null,

    // Campos para tipos específicos
    val clozeText: String? = null,
    val clozeAnswer: String? = null,
    val options: List<String>? = null,
    val correctOptionIndex: Int? = null,

    // Campos de Repetição Espaçada
    val lastReviewed: Date? = null,
    val nextReviewDate: Date? = null,
    val easeFactor: Float = 2.5f,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)