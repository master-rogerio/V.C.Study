package com.example.study.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class FlashcardRepository(private val dao: FlashcardDao) {
    // Para repetição espaçada
    val allFlashcardsByReview: Flow<List<Flashcard>> = dao.getAllFlashcardsByReview()

    // Para visualização normal
    val allFlashcardsByCreation: Flow<List<Flashcard>> = dao.getAllFlashcardsByCreation()

    fun getFlashcardsForDeckByReview(deckId: Long): Flow<List<Flashcard>> {
        return dao.getFlashcardsForDeckByReview(deckId)
    }

    fun getFlashcardsForDeckByCreation(deckId: Long): Flow<List<Flashcard>> {
        return dao.getFlashcardsForDeckByCreation(deckId)
    }

    fun getDueFlashcards(): Flow<List<Flashcard>> {
        return dao.getDueFlashcards(Date())
    }

    fun getDueFlashcardsForDeck(deckId: Long): Flow<List<Flashcard>> {
        return dao.getDueFlashcardsForDeck(deckId, Date())
    }

    suspend fun insert(flashcard: Flashcard) {
        dao.insert(flashcard)
    }

    suspend fun update(flashcard: Flashcard) {
        dao.update(flashcard)
    }

    suspend fun delete(flashcard: Flashcard) {
        dao.delete(flashcard)
    }

    suspend fun getById(id: Long): Flashcard? {
        return dao.getById(id)
    }

    suspend fun deleteAllForDeck(deckId: Long) {
        dao.deleteAllForDeck(deckId)
    }
}