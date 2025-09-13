package com.example.study.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardDatabase
import com.example.study.data.FlashcardRepository
import com.example.study.data.UserLocation
import com.example.study.data.UserLocationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlashcardRepository
    private val userLocationDao: UserLocationDao

    // Fluxos para diferentes modos de visualização
    val allFlashcardsByReview: Flow<List<Flashcard>>
    val allFlashcardsByCreation: Flow<List<Flashcard>>
    val dueFlashcards: Flow<List<Flashcard>>
    val latestUserLocation: Flow<UserLocation?>

    init {
        val database = FlashcardDatabase.getDatabase(application)
        val flashcardDao = database.flashcardDao()
        userLocationDao = database.userLocationDao()
        repository = FlashcardRepository(flashcardDao)
        allFlashcardsByReview = repository.allFlashcardsByReview
        allFlashcardsByCreation = repository.allFlashcardsByCreation
        dueFlashcards = repository.getDueFlashcards()
        latestUserLocation = userLocationDao.getLatestLocation()
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

    fun insert(flashcard: Flashcard) = viewModelScope.launch {
        repository.insert(flashcard)
    }

    fun update(flashcard: Flashcard) = viewModelScope.launch {
        repository.update(flashcard)
    }

    fun delete(flashcard: Flashcard) = viewModelScope.launch {
        repository.delete(flashcard)
    }

    suspend fun getFlashcardById(id: Long): Flashcard? {
        return repository.getById(id)
    }

    fun deleteAllFlashcardsForDeck(deckId: Long) = viewModelScope.launch {
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

    // Métodos para gerenciar localização do usuário
    fun saveUserLocation(name: String, iconName: String, latitude: Double, longitude: Double) = viewModelScope.launch {
        val userLocation = UserLocation(
            name = name,
            iconName = iconName,
            latitude = latitude,
            longitude = longitude
        )
        userLocationDao.insert(userLocation)
    }

    // Método sobrecarregado para salvar localização do usuário com apenas latitude e longitude
    fun saveUserLocation(latitude: Double, longitude: Double) = viewModelScope.launch {
        val userLocation = UserLocation(
            name = "Localização automática",
            iconName = "ic_location",
            latitude = latitude,
            longitude = longitude
        )
        userLocationDao.insert(userLocation)
    }
    
    fun getAllUserLocations() = userLocationDao.getAllUserLocations()
    
    fun deleteUserLocation(id: Long) = viewModelScope.launch {
        userLocationDao.deleteById(id)
    }

    fun clearUserLocationHistory() = viewModelScope.launch {
        userLocationDao.deleteAll()
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