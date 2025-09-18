package com.example.study.data

import android.util.Log
import com.example.study.data.sync.DeckSync
import com.example.study.data.sync.FlashcardSync
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

/**
 * Repositório para gerir a sincronização de dados com o Firestore.
 */
class SyncRepository {

    private val db = Firebase.firestore
    private val decksCollection = db.collection("decks")
    private val flashcardsCollection = db.collection("flashcards")

    /**
     * Envia um Deck para o Firestore.
     * @param deck O objeto Deck do banco de dados local (Room).
     * @return O ID do novo documento criado no Firestore.
     */
    suspend fun syncDeckUp(deck: Deck): String? {
        return try {
            // Converte o objeto local (Deck) para o objeto de sincronização (DeckSync)
            val deckSync = DeckSync(
                name = deck.name,
                theme = deck.theme,
                createdAt = deck.createdAt
            )

            // Adiciona o novo deck à coleção "decks" e aguarda o resultado
            val documentReference = decksCollection.add(deckSync).await()
            Log.d("SyncRepo", "Deck '${deck.name}' sincronizado com sucesso com o ID: ${documentReference.id}")
            documentReference.id // Retorna o ID do documento criado
        } catch (e: Exception) {
            Log.e("SyncRepo", "Erro ao sincronizar o deck: ", e)
            null // Retorna nulo em caso de erro
        }
    }

    /**
     * Envia um Flashcard para o Firestore.
     * @param flashcard O objeto Flashcard do Room.
     * @param firebaseDeckId O ID do documento do Deck no Firestore.
     */
    suspend fun syncFlashcardUp(flashcard: Flashcard, firebaseDeckId: String) {
        try {
            val flashcardSync = FlashcardSync(
                deckId = firebaseDeckId,
                front = flashcard.front,
                back = flashcard.back,
                createdAt = flashcard.createdAt
            )

            flashcardsCollection.add(flashcardSync).await()
            Log.d("SyncRepo", "Flashcard '${flashcard.front}' sincronizado com sucesso.")
        } catch (e: Exception) {
            Log.e("SyncRepo", "Erro ao sincronizar flashcard: ", e)
        }
    }

    suspend fun syncDecksDown(): List<DeckSync> {
        return try {
            val querySnapshot = decksCollection.get().await()
            // Converte os documentos do Firebase para a nossa classe DeckSync
            val decks = querySnapshot.toObjects(DeckSync::class.java)
            Log.d("SyncRepo", "Foram descarregados ${decks.size} decks.")
            decks
        } catch (e: Exception) {
            Log.e("SyncRepo", "Erro ao descarregar os decks: ", e)
            emptyList() // Retorna uma lista vazia se houver um erro
        }
    }
}