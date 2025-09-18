package com.example.study.ui.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.*
import com.example.study.network.ApiQuizQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlashcardRepository
    private val deckRepository: DeckRepository
    private val userLocationDao: UserLocationDao
    private val favoriteLocationDao: FavoriteLocationDao
    private val flashcardDao: FlashcardDao
    private val syncRepository: SyncRepository
    private val quizApiRepository: QuizApiRepository

    val allFlashcardsByReview: Flow<List<Flashcard>>
    val allFlashcardsByCreation: Flow<List<Flashcard>>
    val dueFlashcards: Flow<List<Flashcard>>

    private val _apiQuizQuestion = MutableStateFlow<Result<ApiQuizQuestion>?>(null)
    val apiQuizQuestion: StateFlow<Result<ApiQuizQuestion>?> = _apiQuizQuestion

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _flashcardGenerationResult = MutableStateFlow<Result<Int>?>(null)
    val flashcardGenerationResult: StateFlow<Result<Int>?> = _flashcardGenerationResult

    fun clearFlashcardGenerationResult() {
        _flashcardGenerationResult.value = null
    }

    init {
        val database = FlashcardDatabase.getDatabase(application)
        flashcardDao = database.flashcardDao()
        val deckDao = database.deckDao()
        deckRepository = DeckRepository(deckDao)
        userLocationDao = database.userLocationDao()
        favoriteLocationDao = database.favoriteLocationDao()
        repository = FlashcardRepository(flashcardDao)
        syncRepository = SyncRepository()
        quizApiRepository = QuizApiRepository()
        allFlashcardsByReview = repository.allFlashcardsByReview
        allFlashcardsByCreation = repository.allFlashcardsByCreation
        dueFlashcards = repository.getDueFlashcards()
    }

    fun getFlashcardsForDeckByReview(deckId: Long): Flow<List<Flashcard>> {
        return repository.getFlashcardsForDeckByReview(deckId)
    }

    fun getFlashcardsForDeckByCreation(deckId: Long): Flow<List<Flashcard>> {
        return repository.getFlashcardsForDeckByCreation(deckId)
    }

    fun getDueFlashcardsForDeck(deckId: Long): Flow<List<Flashcard>> {
        return repository.getDueFlashcardsForDeck(deckId)
    }

    fun generateAiQuestion(theme: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _apiQuizQuestion.value = null
            val result = quizApiRepository.generateQuizQuestion(theme)
            _apiQuizQuestion.value = result
            _isLoading.value = false
        }
    }

    // ATUALIZAÇÃO: A função agora aceita um FlashcardType
    fun generateAndSaveFlashcards(topic: String, deckName: String, type: FlashcardType) {
        viewModelScope.launch {
            _isLoading.value = true
            _flashcardGenerationResult.value = null
            // A escolha do tipo é passada para o repositório
            val generationResult = quizApiRepository.generateFlashcards(topic, type)

            generationResult.onSuccess { response ->
                var deck = deckRepository.getDeckByName(deckName.trim())
                if (deck == null) {
                    val newDeck = Deck(name = deckName.trim(), theme = topic)
                    val newDeckId = deckRepository.insert(newDeck)
                    deck = Deck(id = newDeckId, name = newDeck.name, theme = newDeck.theme, createdAt = newDeck.createdAt)
                }

                // A lógica agora cria o tipo correto de flashcard
                val flashcardsToSave = response.flashcards.map { generated ->
                    Flashcard(
                        deckId = deck.id,
                        front = generated.front,
                        back = generated.back ?: "", // Usa o verso se existir, senão usa uma string vazia
                        type = type, // Guarda o tipo correto
                        options = generated.options,
                        correctOptionIndex = generated.correctOptionIndex
                    )
                }

                flashcardsToSave.forEach { repository.insert(it) }
                _flashcardGenerationResult.value = Result.success(flashcardsToSave.size)
            }.onFailure { error ->
                _flashcardGenerationResult.value = Result.failure(error)
            }
            _isLoading.value = false
        }
    }

    fun insert(flashcard: Flashcard, firebaseDeckId: String? = null) = viewModelScope.launch {
        // Guarda sempre localmente, para o modo offline
        repository.insert(flashcard)

        // Se o firebaseDeckId for válido, também envia para a nuvem
        if (!firebaseDeckId.isNullOrBlank()) {
            syncRepository.syncFlashcardUp(flashcard, firebaseDeckId)
        }
    }

    fun update(flashcard: Flashcard) = viewModelScope.launch {
        repository.update(flashcard)
        // TODO: Chamar syncRepository.syncFlashcardUpdate(flashcard)
        // (Esta função precisaria ser criada no SyncRepository)
    }

    fun delete(flashcard: Flashcard) = viewModelScope.launch {
        repository.delete(flashcard)
        // TODO: Chamar syncRepository.syncFlashcardDelete(flashcard)
        // (Esta função precisaria ser criada no SyncRepository)
    }

    fun deleteAllFlashcardsForDeck(deckId: Long) = viewModelScope.launch {
        // 1. Obtém a lista de flashcards que serão apagados para ter os firebaseIds
        val flashcardsToDelete = repository.getFlashcardsForDeckByCreation(deckId).first()

        // 2. Apaga cada um na nuvem
        flashcardsToDelete.forEach { flashcard ->
            // TODO: Chamar syncRepository.syncFlashcardDelete(flashcard) para cada um
        }

        // 3. Apaga todos da base de dados local de uma vez
        repository.deleteAllForDeck(deckId)
    }

    fun calculateNextReview(flashcard: Flashcard, quality: Int): Flashcard {
        val now = Date()
        val newEaseFactor = calculateNewEaseFactor(flashcard.easeFactor, quality)
        val newInterval = calculateNewInterval(flashcard.interval, newEaseFactor, quality)
        val nextReview = Date(now.time + (newInterval * 24 * 60 * 60 * 1000L))

        return flashcard.copy(
            lastReviewed = now,
            nextReviewDate = nextReview,
            easeFactor = newEaseFactor,
            interval = newInterval,
            repetitions = flashcard.repetitions + 1
        )
    }

    fun saveUserLocation(latitude: Double, longitude: Double) = viewModelScope.launch {
        val userLocation = UserLocation(
            name = "Localização automática",
            iconName = "ic_location",
            latitude = latitude,
            longitude = longitude
        )
        userLocationDao.insert(userLocation)
    }

    fun getFlashcardsForLocation(locationId: String, seenIds: MutableSet<Long>): Flow<List<Flashcard>> {
        return flashcardDao.getAllFlashcardsByReview().map { allFlashcards ->
            // Por enquanto, vamos usar todos os flashcards
            // TODO: Implementar filtro por preferências de tipo quando necessário
            val filteredFlashcards = allFlashcards

            val finalFlashcards = filteredFlashcards.filterNot { it.id in seenIds }

            if (finalFlashcards.isEmpty() && seenIds.isNotEmpty()) {
                seenIds.clear()
                filteredFlashcards.shuffled()
            } else {
                finalFlashcards.shuffled()
            }
        }
    }

    fun updateLocationAnalytics(locationId: String, correct: Int, total: Int) = viewModelScope.launch {
        val location = favoriteLocationDao.getFavoriteLocationById(locationId)
        location?.let {
            val sessionPerformance = if (total > 0) (correct.toDouble() / total) * 100 else 0.0
            val newSessionCount = it.studySessionCount + 1
            val newAverage = if (newSessionCount > 1) {
                ((it.averagePerformance * it.studySessionCount) + sessionPerformance) / newSessionCount
            } else {
                sessionPerformance
            }

            val updatedLocation = it.copy(
                studySessionCount = newSessionCount,
                averagePerformance = newAverage
            )
            favoriteLocationDao.update(updatedLocation)
        }
    }

    fun saveFavoriteLocation(name: String, latitude: Double, longitude: Double, iconName: String, preferredTypes: List<FlashcardType>) = viewModelScope.launch {
        val favoriteLocation = FavoriteLocation(
            name = name,
            latitude = latitude,
            longitude = longitude,
            iconName = iconName,
            preferredCardTypes = preferredTypes
        )
        favoriteLocationDao.insert(favoriteLocation)
    }

    fun getAllFavoriteLocations() = favoriteLocationDao.getAllFavoriteLocationsFlow()

    fun deleteFavoriteLocation(id: String) = viewModelScope.launch {
        favoriteLocationDao.deleteById(id)
    }

    private fun calculateNewEaseFactor(oldEaseFactor: Float, quality: Int): Float {
        val newEaseFactor = oldEaseFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        return maxOf(1.3f, newEaseFactor)
    }

    private fun calculateNewInterval(oldInterval: Int, easeFactor: Float, quality: Int): Int {
        return when {
            quality < 3 -> 1
            oldInterval == 0 -> 1
            oldInterval == 1 -> 6
            else -> (oldInterval * easeFactor).toInt()
        }
    }
}