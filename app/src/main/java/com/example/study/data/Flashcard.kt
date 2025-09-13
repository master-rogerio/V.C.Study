// File: app/src/main/java/com/example/flashcards/data/Flashcard.kt
package com.example.study.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date // Mantenha este import se usar Date em outros lugares

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
    val front: String,
    val back: String,
    val clozeText: String? = null,
    val clozeAnswer: String? = null,
    val options: List<String>? = null,
    val correctOptionIndex: Int? = null,
    val lastReviewed: Date? = null,
    val nextReviewDate: Date? = null,
    val easeFactor: Float = 2.5f,
    val interval: Int = 0,
    val repetitions: Int = 0,
    // --- ADICIONADO ---
    val createdAt: Long = System.currentTimeMillis() // Adiciona o timestamp de criação
    // --- FIM DA ADIÇÃO ---
)