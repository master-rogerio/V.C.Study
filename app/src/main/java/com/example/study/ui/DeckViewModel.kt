package com.example.study.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.Deck
import com.example.study.data.DeckRepository
import com.example.study.data.FlashcardDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DeckViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DeckRepository
    val allDecks: Flow<List<Deck>>

    init {
        val deckDao = FlashcardDatabase.getDatabase(application).deckDao()
        repository = DeckRepository(deckDao)
        allDecks = repository.allDecks
    }

    fun insert(deck: Deck) = viewModelScope.launch {
        try {
            val id = repository.insert(deck)
            println("Deck inserido com ID: $id")
        } catch (e: Exception) {
            println("Erro ao inserir deck: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    fun update(deck: Deck) = viewModelScope.launch {
        repository.update(deck)
    }

    fun delete(deck: Deck) = viewModelScope.launch {
        repository.delete(deck)
    }

    fun getDeckById(id: Long) = viewModelScope.launch {
        repository.getDeckById(id)
    }

    suspend fun getFlashcardCountForDeck(deckId: Long): Int {
        return repository.getFlashcardCountForDeck(deckId)
    }
} 