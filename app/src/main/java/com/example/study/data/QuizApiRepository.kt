package com.example.study.data

import com.example.study.BuildConfig
import com.example.study.network.*
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuizApiRepository {
    private val geminiApiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    // A função de gerar quiz continua aqui, sem alterações
    suspend fun generateQuizQuestion(theme: String): Result<ApiQuizQuestion> {
        return try {
            val prompt = createPromptForQuiz(theme)
            val request = GenerateQuestionRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
            val response = geminiApiService.generateContent(BuildConfig.GEMINI_API_KEY, request)
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                val cleanedJson = content.substringAfter("```json").substringBefore("```").trim()
                if (cleanedJson.isNotEmpty()) {
                    val question = Gson().fromJson(cleanedJson, ApiQuizQuestion::class.java)
                    Result.success(question)
                } else {
                    Result.failure(Exception("Resposta da API vazia ou em formato inválido."))
                }
            } else {
                Result.failure(Exception("Erro na API: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ATUALIZAÇÃO: Agora aceita um FlashcardType para saber o que gerar
    suspend fun generateFlashcards(topic: String, type: FlashcardType): Result<FlashcardListResponse> {
        return try {
            val prompt = createPromptForFlashcards(topic, type) // Passa o tipo para o prompt
            val request = GenerateQuestionRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
            val response = geminiApiService.generateContent(BuildConfig.GEMINI_API_KEY, request)

            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                val cleanedJson = content.substringAfter("```json").substringBefore("```").trim()

                if (cleanedJson.isNotEmpty()) {
                    val flashcardList = Gson().fromJson(cleanedJson, FlashcardListResponse::class.java)
                    Result.success(flashcardList)
                } else {
                    Result.failure(Exception("Resposta da API para flashcards vazia ou em formato inválido."))
                }
            } else {
                Result.failure(Exception("Erro na API ao gerar flashcards: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createPromptForQuiz(theme: String): String {
        return """
        Você é um gerador de perguntas para um quiz em português do Brasil.
        Crie uma pergunta de múltipla escolha sobre o tema: "$theme".
        A pergunta deve ter 4 opções e apenas uma correta.
        As opções não devem ter marcadores como "A)", "B)", etc., apenas o texto.

        Retorne sua resposta estritamente no seguinte formato JSON, dentro de um bloco de código, e nada mais:
        ```json
        {
          "question": "Aqui vai a pergunta em português",
          "options": ["Opção 1", "Opção 2", "Opção 3", "Opção 4"],
          "correct_answer": "O texto exato da resposta correta, que deve ser idêntico a uma das opções"
        }
        ```
        """.trimIndent()
    }

    // ATUALIZAÇÃO: O prompt agora é dinâmico e muda com base no tipo
    private fun createPromptForFlashcards(topic: String, type: FlashcardType): String {
        return when (type) {
            FlashcardType.MULTIPLE_CHOICE -> """
                Você é um especialista em educação e um assistente de estudos.
                Sua tarefa é criar 5 flashcards do tipo "Múltipla Escolha" sobre o tema: "$topic".
                O campo 'front' deve conter a pergunta.
                O campo 'options' deve conter 4 alternativas plausíveis.
                O campo 'correctOptionIndex' deve ser o índice da resposta correta na lista de opções (de 0 a 3).
                O campo 'back' pode ser nulo ou uma breve explicação da resposta correta.
                
                Retorne sua resposta estritamente no seguinte formato JSON, dentro de um bloco de código, e nada mais:
                ```json
                {
                  "flashcards": [
                    {
                      "front": "Qual destes planetas é conhecido como o 'Planeta Vermelho'?",
                      "back": "Marte é conhecido como o Planeta Vermelho devido à sua superfície rica em óxido de ferro.",
                      "options": ["Vênus", "Marte", "Júpiter", "Saturno"],
                      "correctOptionIndex": 1
                    }
                  ]
                }
                ```
            """.trimIndent()

            else -> """
                Você é um especialista em educação e um assistente de estudos.
                Sua tarefa é criar 5 flashcards do tipo "Frente e Verso" sobre o tema: "$topic".
                A frente deve conter uma pergunta clara ou um conceito-chave.
                O verso deve conter a resposta concisa e correta.
                
                Retorne sua resposta estritamente no seguinte formato JSON, dentro de um bloco de código, e nada mais:
                ```json
                {
                  "flashcards": [
                    {
                      "front": "Qual é a principal função das mitocôndrias?",
                      "back": "A principal função é a respiração celular e a produção de ATP (energia)."
                    }
                  ]
                }
                ```
            """.trimIndent()
        }
    }
}