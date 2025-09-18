package com.example.study.ui.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.Deck
import com.example.study.data.DeckRepository
import com.example.study.data.FlashcardDatabase
import com.example.study.data.FlashcardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Classe de dados para agrupar todas as informações da tela inicial
data class HomeUiState(
    val deckCount: Int = 0,
    val flashcardCount: Int = 0,
    val recentDecks: List<Deck> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val deckRepository: DeckRepository
    private val flashcardRepository: FlashcardRepository

    // Combina os diferentes fluxos de dados em um único estado para a UI
    val uiState: StateFlow<HomeUiState>

    init {
        val database = FlashcardDatabase.getDatabase(application)
        deckRepository = DeckRepository(database.deckDao())
        flashcardRepository = FlashcardRepository(database.flashcardDao())

        val deckCountFlow = deckRepository.getDeckCount()
        val flashcardCountFlow = flashcardRepository.getFlashcardCount()
        val recentDecksFlow = deckRepository.getRecentDecks()

        uiState = combine(
            deckCountFlow,
            flashcardCountFlow,
            recentDecksFlow
        ) { deckCount, flashcardCount, recentDecks ->
            HomeUiState(
                deckCount = deckCount,
                flashcardCount = flashcardCount,
                recentDecks = recentDecks
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )
    }
}