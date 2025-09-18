
package com.example.study.ui.view // Certifique-se de que o pacote corresponde ao seu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlashcardRepository
    private val userLocationDao: UserLocationDao
    private val favoriteLocationDao: FavoriteLocationDao
    private val flashcardDao: FlashcardDao
    // 1. ADICIONE O REPOSITÓRIO DE SINCRONIZAÇÃO
    private val syncRepository: SyncRepository

    val allFlashcardsByReview: Flow<List<Flashcard>>
    val allFlashcardsByCreation: Flow<List<Flashcard>>
    val dueFlashcards: Flow<List<Flashcard>>

    init {
        val database = FlashcardDatabase.getDatabase(application)
        flashcardDao = database.flashcardDao()
        userLocationDao = database.userLocationDao()
        favoriteLocationDao = database.favoriteLocationDao()
        repository = FlashcardRepository(flashcardDao)
        // 2. INICIALIZE O SYNC REPOSITORY
        syncRepository = SyncRepository()
        allFlashcardsByReview = repository.allFlashcardsByReview
        allFlashcardsByCreation = repository.allFlashcardsByCreation
        dueFlashcards = repository.getDueFlashcards()
    }

    // ... (as suas outras funções como getFlashcardsForDeckByReview, etc., continuam iguais) ...
    fun getFlashcardsForDeckByReview(deckId: Long): Flow<List<Flashcard>> {
        return repository.getFlashcardsForDeckByReview(deckId)
    }

    fun getFlashcardsForDeckByCreation(deckId: Long): Flow<List<Flashcard>> {
        return repository.getFlashcardsForDeckByCreation(deckId)
    }

    fun getDueFlashcardsForDeck(deckId: Long): Flow<List<Flashcard>> {
        return repository.getDueFlashcardsForDeck(deckId)
    }
    // 3. MODIFIQUE A FUNÇÃO INSERT
    // Ela agora precisa do firebaseDeckId para saber onde guardar o flashcard na nuvem.
    fun insert(flashcard: Flashcard, firebaseDeckId: String?) = viewModelScope.launch {
        // Guarda sempre localmente, para o modo offline
        repository.insert(flashcard)

        // Se o firebaseDeckId for válido, também envia para a nuvem
        if (!firebaseDeckId.isNullOrBlank()) {
            syncRepository.syncFlashcardUp(flashcard, firebaseDeckId)
        }
    }

    fun update(flashcard: Flashcard) = viewModelScope.launch {
        repository.update(flashcard)
        // TODO: Adicionar lógica de sincronização de update
    }

    fun delete(flashcard: Flashcard) = viewModelScope.launch {
        repository.delete(flashcard)
        // TODO: Adicionar lógica de sincronização de delete
    }

    fun deleteAllFlashcardsForDeck(deckId: Long) = viewModelScope.launch {
        repository.deleteAllForDeck(deckId)
        // TODO: Adicionar lógica de sincronização para delete all
    }

    // ... (o resto das suas funções, como calculateNextReview, etc., continuam exatamente iguais) ...
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
            val location = favoriteLocationDao.getFavoriteLocationById(locationId)
            val preferredTypes = location?.preferredCardTypes

            val filteredFlashcards = if (!preferredTypes.isNullOrEmpty()) {
                allFlashcards.filter { it.type in preferredTypes }
            } else {
                allFlashcards
            }

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
            val newAverage = ((it.averagePerformance * it.studySessionCount) + sessionPerformance) / newSessionCount

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

    fun getAllFavoriteLocations() = favoriteLocationDao.getAllFavoriteLocations()

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

/*package com.example.study.ui.view // Certifique-se de que o pacote corresponde ao seu


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.study.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlashcardRepository
    private val userLocationDao: UserLocationDao
    private val favoriteLocationDao: FavoriteLocationDao
    private val flashcardDao: FlashcardDao


    val allFlashcardsByReview: Flow<List<Flashcard>>
    val allFlashcardsByCreation: Flow<List<Flashcard>>
    val dueFlashcards: Flow<List<Flashcard>>

    init {
        val database = FlashcardDatabase.getDatabase(application)
        flashcardDao = database.flashcardDao()
        userLocationDao = database.userLocationDao()
        favoriteLocationDao = database.favoriteLocationDao()
        repository = FlashcardRepository(flashcardDao)
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

    fun insert(flashcard: Flashcard) = viewModelScope.launch {
        repository.insert(flashcard)
    }

    fun update(flashcard: Flashcard) = viewModelScope.launch {
        repository.update(flashcard)
    }

    fun delete(flashcard: Flashcard) = viewModelScope.launch {
        repository.delete(flashcard)
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
            val location = favoriteLocationDao.getFavoriteLocationById(locationId)
            val preferredTypes = location?.preferredCardTypes

            val filteredFlashcards = if (!preferredTypes.isNullOrEmpty()) {
                allFlashcards.filter { it.type in preferredTypes }
            } else {
                allFlashcards
            }

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
            val newAverage = ((it.averagePerformance * it.studySessionCount) + sessionPerformance) / newSessionCount

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

    fun getAllFavoriteLocations() = favoriteLocationDao.getAllFavoriteLocations()

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

 */