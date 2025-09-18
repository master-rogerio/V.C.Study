package com.example.study.ui.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.Deck
import com.example.study.data.DeckRepository
import com.example.study.data.FlashcardDatabase
import com.example.study.data.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DeckViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DeckRepository
    private val syncRepository: SyncRepository
    val allDecks: Flow<List<Deck>>

    init {
        val deckDao = FlashcardDatabase.getDatabase(application).deckDao()
        repository = DeckRepository(deckDao)
        allDecks = repository.allDecks
        syncRepository = SyncRepository()

        // Chama a sincronização para descarregar os dados assim que o ViewModel é criado
        syncDownDecks()
    }

    /**
     * Descarrega os baralhos do Firebase e guarda-os localmente se ainda não existirem.
     */
    private fun syncDownDecks() = viewModelScope.launch {
        // 1. Vai ao Firebase buscar todos os baralhos
        val decksFromFirebase = syncRepository.syncDecksDown()

        // 2. Itera sobre cada baralho que veio da nuvem
        for (deckSync in decksFromFirebase) {
            // 3. Verifica se este baralho (pelo seu ID do Firebase) já existe na base de dados local
            val existingDeck = repository.getDeckByFirebaseId(deckSync.id)

            // 4. Se não existir, cria um novo registo local para ele
            if (existingDeck == null) {
                val newDeck = Deck(
                    name = deckSync.name,
                    theme = deckSync.theme,
                    createdAt = deckSync.createdAt,
                    firebaseId = deckSync.id // Guarda o ID da nuvem para referência futura
                )
                repository.insert(newDeck)
            } else {
                // Opcional: Se o baralho já existir, pode-se verificar se há atualizações
                // e usar repository.update(existingDeck.copy(...)) para atualizar os dados.
                println("Baralho com firebaseId ${deckSync.id} já existe localmente.")
            }
        }
    }

    fun insert(deck: Deck) = viewModelScope.launch {
        try {
            // 1. Insere no Room e recebe o ID local gerado
            val localId = repository.insert(deck)
            println("Deck inserido localmente com o ID: $localId")

            // 2. Envia para o Firebase e recebe o ID da nuvem
            val firebaseId = syncRepository.syncDeckUp(deck)

            // 3. ATUALIZA O REGISTO LOCAL com o ID da nuvem
            if (firebaseId != null) {
                val deckToUpdate = deck.copy(id = localId, firebaseId = firebaseId)
                repository.update(deckToUpdate)
                println("Deck local atualizado com o Firebase ID: $firebaseId")
            }
        } catch (e: Exception) {
            println("Erro ao inserir o deck: ${e.message}")
            e.printStackTrace()
        }
    }

    fun update(deck: Deck) = viewModelScope.launch {
        // 1. Atualiza o baralho na base de dados local (Room)
        repository.update(deck)
        // 2. Chama a sua função para sincronizar a atualização com o Firebase
        syncRepository.syncDeckUpdate(deck)
    }

    fun delete(deck: Deck) = viewModelScope.launch {
        // 1. Apaga o baralho da base de dados local (Room)
        repository.delete(deck)
        // 2. Chama a sua função para sincronizar a exclusão com o Firebase (que também apaga os flashcards)
        syncRepository.syncDeckDelete(deck)
    }

    fun getDeckById(id: Long): Flow<Deck?> { // Não retorna mais um 'viewModelScope.launch'
        return repository.getDeckById(id)
    }


    suspend fun getFlashcardCountForDeck(deckId: Long): Int {
        return repository.getFlashcardCountForDeck(deckId)
    }
}