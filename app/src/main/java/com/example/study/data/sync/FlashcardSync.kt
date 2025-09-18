package com.example.study.data.sync

import com.google.firebase.firestore.DocumentId

/**
 * Representa um Flashcard como ele é guardado no Firestore.
 */
data class FlashcardSync(
    @DocumentId
    val id: String = "",
    val deckId: String = "", // Este será o ID do Deck *no Firebase*
    val front: String = "",
    val back: String = "",
    val createdAt: Long = System.currentTimeMillis()
)