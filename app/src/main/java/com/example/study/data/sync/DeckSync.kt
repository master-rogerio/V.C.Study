package com.example.study.data.sync

import com.google.firebase.firestore.DocumentId

/**
 * Representa um Deck como ele é guardado no Firestore.
 */
data class DeckSync(
    @DocumentId // Esta anotação diz ao Firebase para preencher este campo com o ID do documento
    val id: String = "",
    val name: String = "",
    val theme: String = "",
    val createdAt: Long = System.currentTimeMillis()
    // Não precisamos do userId, pois a base de dados está aberta
)