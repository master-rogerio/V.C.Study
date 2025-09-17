package com.example.study.network

// MODELO ATUALIZADO para ser mais flexível
data class GeneratedFlashcard(
    val front: String,
    val back: String?, // O verso é opcional para Múltipla Escolha
    val options: List<String>?, // A lista de opções
    val correctOptionIndex: Int? // O índice da opção correta
)

data class FlashcardListResponse(
    val flashcards: List<GeneratedFlashcard>
)

// Modelos que já existiam
data class GeminiResponse(val candidates: List<Candidate>)
data class Candidate(val content: Content)
data class GenerateQuestionRequest(val contents: List<Content>)
data class Content(val parts: List<Part>)
data class Part(val text: String)
data class ApiQuizQuestion(val question: String, val options: List<String>, val correct_answer: String)