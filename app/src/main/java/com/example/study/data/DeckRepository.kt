package com.example.study.data

import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao) {
    val allDecks: Flow<List<Deck>> = deckDao.getAllDecks()

    suspend fun getDeckById(id: Long): Deck? {
        return deckDao.getDeckById(id)
    }

    suspend fun insert(deck: Deck): Long {
        return deckDao.insert(deck)
    }

    suspend fun update(deck: Deck) {
        deckDao.update(deck)
    }

    suspend fun delete(deck: Deck) {
        deckDao.delete(deck)
    }

    suspend fun getFlashcardCountForDeck(deckId: Long): Int {
        return deckDao.getFlashcardCountForDeck(deckId)
    }
} 