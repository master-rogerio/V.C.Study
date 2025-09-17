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
    suspend fun getDeckById(id: Long): Deck?

    // ADIÇÃO: Nova função para encontrar um deck pelo nome
    @Query("SELECT * FROM decks WHERE name = :name LIMIT 1")
    suspend fun getDeckByName(name: String): Deck?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deck: Deck): Long

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    suspend fun getFlashcardCountForDeck(deckId: Long): Int
}