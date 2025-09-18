package com.example.study.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface FlashcardDao {
    // Flashcards ordenados por data de revisão (para sistema de repetição espaçada)
    @Query("SELECT * FROM flashcards ORDER BY nextReviewDate ASC")
    fun getAllFlashcardsByReview(): Flow<List<Flashcard>>

    // Flashcards ordenados por criação (para visualização padrão)
    @Query("SELECT * FROM flashcards ORDER BY createdAt DESC")
    fun getAllFlashcardsByCreation(): Flow<List<Flashcard>>

    // Flashcards de um deck específico ordenados por revisão
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY nextReviewDate ASC")
    fun getFlashcardsForDeckByReview(deckId: Long): Flow<List<Flashcard>>

    // Flashcards de um deck específico ordenados por criação
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY createdAt DESC")
    fun getFlashcardsForDeckByCreation(deckId: Long): Flow<List<Flashcard>>

    // Flashcards pendentes (para repetição espaçada)
    @Query("SELECT * FROM flashcards WHERE nextReviewDate <= :date OR nextReviewDate IS NULL")
    fun getDueFlashcards(date: Date): Flow<List<Flashcard>>

    // Flashcards pendentes de um deck específico
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND (nextReviewDate <= :date OR nextReviewDate IS NULL)")
    fun getDueFlashcardsForDeck(deckId: Long, date: Date): Flow<List<Flashcard>>

    // Operações CRUD
    @Insert
    suspend fun insert(flashcard: Flashcard)

    @Update
    suspend fun update(flashcard: Flashcard)

    @Delete
    suspend fun delete(flashcard: Flashcard)

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getById(id: Long): Flashcard?

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    suspend fun deleteAllForDeck(deckId: Long)

    @Query("SELECT COUNT(*) FROM flashcards")
    fun getFlashcardCount():Flow<Int>
    
    // Método síncrono para obter todos os flashcards ordenados por revisão
    @Query("SELECT * FROM flashcards ORDER BY nextReviewDate ASC")
    suspend fun getAllFlashcardsByReviewSync(): List<Flashcard>
}