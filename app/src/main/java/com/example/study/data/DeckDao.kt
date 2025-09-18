package com.example.study.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun getAllDecks(): Flow<List<Deck>>

    @Query("SELECT * FROM decks WHERE id = :id")
    fun getDeckById(id: Long): Flow<Deck?>

<<<<<<< HEAD
    // ADICIONE ESTA NOVA FUNÇÃO AQUI
    @Query("SELECT * FROM decks WHERE firebaseId = :firebaseId LIMIT 1")
    suspend fun getDeckByFirebaseId(firebaseId: String): Deck?

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Mude para REPLACE para facilitar updates
=======
    // ADIÇÃO: Nova função para encontrar um deck pelo nome
    @Query("SELECT * FROM decks WHERE name = :name LIMIT 1")
    suspend fun getDeckByName(name: String): Deck?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
>>>>>>> origin/UX_UI.v4-FINAL
    suspend fun insert(deck: Deck): Long

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    suspend fun getFlashcardCountForDeck(deckId: Long): Int
<<<<<<< HEAD
=======

    @Query("SELECT COUNT(*) FROM decks")
    fun getDeckCount(): Flow<Int>

    @Query("SELECT * FROM decks ORDER BY createdAt DESC LIMIT 5")
    fun getRecentDecks(): Flow<List<Deck>>

>>>>>>> origin/UX_UI.v4-FINAL
}