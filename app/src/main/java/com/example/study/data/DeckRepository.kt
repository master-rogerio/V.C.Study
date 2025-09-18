package com.example.study.data

import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao) {
    val allDecks: Flow<List<Deck>> = deckDao.getAllDecks()

    fun getDeckById(id: Long): Flow<Deck?> { // Não é mais 'suspend'
        return deckDao.getDeckById(id)
    }

    // ADIÇÃO: Tornando a nova função acessível
    suspend fun getDeckByName(name: String): Deck? {
        return deckDao.getDeckByName(name)
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

<<<<<<< HEAD
    suspend fun getDeckByFirebaseId(firebaseId: String): Deck? {
        return deckDao.getDeckByFirebaseId(firebaseId)
    }
} 
=======
    fun getDeckCount(): Flow<Int> = deckDao.getDeckCount()
    fun getRecentDecks(): Flow<List<Deck>> = deckDao.getRecentDecks()

}
>>>>>>> origin/UX_UI.v4-FINAL
