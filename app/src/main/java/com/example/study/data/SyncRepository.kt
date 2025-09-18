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

    /**
     * Atualiza um Deck no Firestore.
     * @param deck O objeto Deck atualizado do Room.
     */
    suspend fun syncDeckUpdate(deck: Deck) {
        val firebaseId = deck.firebaseId
        if (firebaseId != null) {
            try {
                val deckSync = DeckSync(
                    name = deck.name,
                    theme = deck.theme,
                    createdAt = deck.createdAt
                )
                
                decksCollection.document(firebaseId).set(deckSync).await()
                Log.d("SyncRepo", "Deck '${deck.name}' atualizado com sucesso no Firebase.")
            } catch (e: Exception) {
                Log.e("SyncRepo", "Erro ao atualizar deck no Firebase: ", e)
            }
        } else {
            Log.w("SyncRepo", "Deck '${deck.name}' não possui firebaseId, não pode ser atualizado.")
        }
    }

    /**
     * Remove um Deck do Firestore.
     * @param deck O objeto Deck a ser removido.
     */
    suspend fun syncDeckDelete(deck: Deck) {
        val firebaseId = deck.firebaseId
        if (firebaseId != null) {
            try {
                // Remove o deck
                decksCollection.document(firebaseId).delete().await()
                
                // Remove todos os flashcards associados a este deck
                val flashcardsQuery = flashcardsCollection.whereEqualTo("deckId", firebaseId).get().await()
                for (document in flashcardsQuery.documents) {
                    document.reference.delete().await()
                }
                
                Log.d("SyncRepo", "Deck '${deck.name}' e seus flashcards removidos com sucesso do Firebase.")
            } catch (e: Exception) {
                Log.e("SyncRepo", "Erro ao remover deck do Firebase: ", e)
            }
        } else {
            Log.w("SyncRepo", "Deck '${deck.name}' não possui firebaseId, não pode ser removido.")
        }
    }

    /**
     * Atualiza um Flashcard no Firestore.
     * @param flashcard O objeto Flashcard atualizado do Room.
     * @param firebaseDeckId O ID do documento do Deck no Firestore.
     */
    suspend fun syncFlashcardUpdate(flashcard: Flashcard, firebaseDeckId: String) {
        try {
            val flashcardSync = FlashcardSync(
                deckId = firebaseDeckId,
                front = flashcard.front,
                back = flashcard.back,
                createdAt = flashcard.createdAt
            )

            // Como não temos firebaseId no Flashcard, precisamos buscar o documento
            val querySnapshot = flashcardsCollection
                .whereEqualTo("deckId", firebaseDeckId)
                .whereEqualTo("front", flashcard.front)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.set(flashcardSync).await()
                Log.d("SyncRepo", "Flashcard '${flashcard.front}' atualizado com sucesso no Firebase.")
            } else {
                Log.w("SyncRepo", "Flashcard '${flashcard.front}' não encontrado no Firebase para atualização.")
            }
        } catch (e: Exception) {
            Log.e("SyncRepo", "Erro ao atualizar flashcard no Firebase: ", e)
        }
    }

    /**
     * Remove um Flashcard do Firestore.
     * @param flashcard O objeto Flashcard a ser removido.
     * @param firebaseDeckId O ID do documento do Deck no Firestore.
     */
    suspend fun syncFlashcardDelete(flashcard: Flashcard, firebaseDeckId: String) {
        try {
            // Busca o documento do flashcard no Firebase
            val querySnapshot = flashcardsCollection
                .whereEqualTo("deckId", firebaseDeckId)
                .whereEqualTo("front", flashcard.front)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.delete().await()
                Log.d("SyncRepo", "Flashcard '${flashcard.front}' removido com sucesso do Firebase.")
            } else {
                Log.w("SyncRepo", "Flashcard '${flashcard.front}' não encontrado no Firebase para remoção.")
            }
        } catch (e: Exception) {
            Log.e("SyncRepo", "Erro ao remover flashcard do Firebase: ", e)
        }
    }

    /**
     * Remove todos os Flashcards de um Deck do Firestore.
     * @param firebaseDeckId O ID do documento do Deck no Firestore.
     */
    suspend fun syncFlashcardDeleteAll(firebaseDeckId: String) {
        try {
            val querySnapshot = flashcardsCollection
                .whereEqualTo("deckId", firebaseDeckId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }
            
            Log.d("SyncRepo", "Todos os flashcards do deck '$firebaseDeckId' removidos com sucesso do Firebase.")
        } catch (e: Exception) {
            Log.e("SyncRepo", "Erro ao remover todos os flashcards do Firebase: ", e)
        }
    }
}