package com.example.study

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.databinding.ActivityExerciseBinding
import com.example.study.ui.FlashcardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseBinding
    private lateinit var viewModel: FlashcardViewModel
    private var currentDeckId: Long = -1
    private var locationId: String? = null // Para o contexto de localização
    private var currentDeckName: String = ""
    private var flashcards: List<Flashcard> = emptyList()
    private var currentIndex: Int = 0
    private var correctAnswers: Int = 0
    private var wrongAnswers: Int = 0
    private val seenFlashcardIds = mutableSetOf<Long>() // Para a rotação inteligente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera dados do Intent, incluindo o novo locationId
        currentDeckId = intent.getLongExtra("deckId", -1)
        locationId = intent.getStringExtra("locationId")
        currentDeckName = intent.getStringExtra("deckName") ?: "Exercício"
        supportActionBar?.title = currentDeckName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]
        setupExercise()
    }

    private fun setupExercise() {
        lifecycleScope.launch {
            // Escolhe o fluxo de flashcards com base no contexto (localização, deck ou todos)
            val flashcardFlow = when {
                locationId != null -> viewModel.getFlashcardsForLocation(locationId!!, seenFlashcardIds)
                currentDeckId != -1L -> viewModel.getFlashcardsForDeckByReview(currentDeckId)
                else -> viewModel.allFlashcardsByReview
            }

            flashcardFlow.collectLatest { flashcardList ->
                flashcards = flashcardList
                if (flashcards.isNotEmpty()) {
                    showCurrentFlashcard()
                } else {
                    showEmptyState()
                }
            }
        }
    }

    private fun showCurrentFlashcard() {
        binding.exerciseContainer.visibility = View.VISIBLE
        binding.emptyView.root.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.counterText.visibility = View.VISIBLE

        val flashcard = flashcards[currentIndex]
        seenFlashcardIds.add(flashcard.id) // Adiciona à lista de vistos para a rotação inteligente

        val progress = ((currentIndex + 1) * 100) / flashcards.size
        binding.progressBar.progress = progress
        binding.counterText.text = "${currentIndex + 1}/${flashcards.size}"
        binding.questionText.text = flashcard.front

        when (flashcard.type) {
            FlashcardType.FRONT_BACK -> setupFrontBackLayout(flashcard)
            FlashcardType.CLOZE -> setupClozeLayout(flashcard)
            FlashcardType.TEXT_INPUT -> setupTextInputLayout(flashcard)
            FlashcardType.MULTIPLE_CHOICE -> setupMultipleChoiceLayout(flashcard)
        }
    }

    private fun showExerciseResults() {
        // Atualiza os analytics se for uma sessão baseada em localização
        locationId?.let {
            viewModel.updateLocationAnalytics(it, correctAnswers, flashcards.size)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Exercício Concluído!")
            .setMessage("Você acertou $correctAnswers de ${flashcards.size} flashcards.\n" +
                    "Acertos: $correctAnswers\n" +
                    "Erros: $wrongAnswers")
            .setPositiveButton("Concluir") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // --- MÉTODOS AUXILIARES (sem alterações) ---

    private fun showEmptyState() {
        binding.exerciseContainer.visibility = View.GONE
        binding.emptyView.root.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.counterText.visibility = View.GONE
    }

    private fun setupFrontBackLayout(flashcard: Flashcard) {
        binding.clozeLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.GONE
        binding.frontBackLayout.visibility = View.VISIBLE

        binding.answerInput.text?.clear()
        binding.answerInput.hint = "Digite a resposta"

        binding.submitButton.setOnClickListener {
            checkAnswer(flashcard, binding.answerInput.text?.toString() ?: "")
        }
    }

    private fun setupClozeLayout(flashcard: Flashcard) {
        binding.frontBackLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.GONE
        binding.clozeLayout.visibility = View.VISIBLE

        binding.clozeAnswerInput.text?.clear()
        binding.clozeAnswerInput.hint = "Digite a palavra que falta"

        binding.submitButton.setOnClickListener {
            checkAnswer(flashcard, binding.clozeAnswerInput.text?.toString() ?: "")
        }
    }

    private fun setupTextInputLayout(flashcard: Flashcard) {
        binding.frontBackLayout.visibility = View.GONE
        binding.clozeLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.VISIBLE

        binding.textInputAnswer.text?.clear()
        binding.textInputAnswer.hint = "Digite a resposta"

        binding.submitButton.setOnClickListener {
            checkAnswer(flashcard, binding.textInputAnswer.text?.toString() ?: "")
        }
    }

    private fun setupMultipleChoiceLayout(flashcard: Flashcard) {
        binding.frontBackLayout.visibility = View.GONE
        binding.clozeLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.VISIBLE

        binding.optionsRadioGroup.clearCheck()
        binding.optionsRadioGroup.removeAllViews()

        flashcard.options?.let { options ->
            for (i in options.indices) {
                val radioButton = RadioButton(this)
                radioButton.id = View.generateViewId()
                radioButton.text = options[i]
                binding.optionsRadioGroup.addView(radioButton)
            }
        }

        binding.submitButton.setOnClickListener {
            val selectedId = binding.optionsRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedIndex = binding.optionsRadioGroup.indexOfChild(findViewById(selectedId))
                checkAnswer(flashcard, selectedIndex.toString())
            } else {
                showError("Por favor, selecione uma opção")
            }
        }
    }

    private fun checkAnswer(flashcard: Flashcard, userAnswer: String) {
        val isCorrect = when (flashcard.type) {
            FlashcardType.FRONT_BACK -> userAnswer.equals(flashcard.back, ignoreCase = true)
            FlashcardType.CLOZE -> userAnswer.equals(flashcard.clozeAnswer, ignoreCase = true)
            FlashcardType.TEXT_INPUT -> userAnswer.equals(flashcard.back, ignoreCase = true)
            FlashcardType.MULTIPLE_CHOICE -> userAnswer.toInt() == flashcard.correctOptionIndex
        }

        if (isCorrect) {
            correctAnswers++
            showCorrectFeedback()
        } else {
            wrongAnswers++
            showWrongFeedback(flashcard)
        }
    }

    private fun showCorrectFeedback() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Correto!")
            .setMessage("Parabéns! Você acertou!")
            .setPositiveButton("Próximo") { _, _ ->
                moveToNextFlashcard()
            }
            .setCancelable(false)
            .show()
    }

    private fun showWrongFeedback(flashcard: Flashcard) {
        val correctAnswer = when (flashcard.type) {
            FlashcardType.FRONT_BACK -> flashcard.back
            FlashcardType.CLOZE -> flashcard.clozeAnswer
            FlashcardType.TEXT_INPUT -> flashcard.back
            FlashcardType.MULTIPLE_CHOICE -> flashcard.options?.get(flashcard.correctOptionIndex ?: 0) ?: ""
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Incorreto!")
            .setMessage("A resposta correta era: $correctAnswer")
            .setPositiveButton("Próximo") { _, _ ->
                moveToNextFlashcard()
            }
            .setCancelable(false)
            .show()
    }

    private fun moveToNextFlashcard() {
        currentIndex++
        if (currentIndex < flashcards.size) {
            showCurrentFlashcard()
        } else {
            showExerciseResults()
        }
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Erro")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // O menu foi removido, então estes métodos podem ser simplificados ou removidos
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}

/*
package com.example.study

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.databinding.ActivityExerciseBinding
import com.example.study.ui.FlashcardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseBinding
    private lateinit var viewModel: FlashcardViewModel
    private var currentDeckId: Long = -1
    private var currentDeckName: String = ""
    private var flashcards: List<Flashcard> = emptyList()
    private var currentIndex: Int = 0
    private var correctAnswers: Int = 0
    private var wrongAnswers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentDeckId = intent.getLongExtra("deckId", -1)
        currentDeckName = intent.getStringExtra("deckName") ?: ""
        supportActionBar?.title = "Exercício: $currentDeckName"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]
        setupExercise()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Menu removido conforme solicitado
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun shuffleFlashcards() {
        flashcards = flashcards.shuffled()
        if (flashcards.isNotEmpty()) {
            currentIndex = 0
            correctAnswers = 0
            wrongAnswers = 0
            showCurrentFlashcard()
        }
    }

    private fun setupExercise() {
        lifecycleScope.launch {
            if (currentDeckId != -1L) {
                viewModel.getFlashcardsForDeckByReview(currentDeckId).collectLatest { flashcardList ->
                    flashcards = flashcardList.shuffled()
                    if (flashcards.isNotEmpty()) {
                        showCurrentFlashcard()
                    } else {
                        showEmptyState()
                    }
                }
            } else {
                viewModel.allFlashcardsByReview.collectLatest { flashcardList ->
                    flashcards = flashcardList.shuffled()
                    if (flashcards.isNotEmpty()) {
                        showCurrentFlashcard()
                    } else {
                        showEmptyState()
                    }
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.exerciseContainer.visibility = View.GONE
        binding.emptyView.root.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE  // Esconde a barra de progresso no estado vazio
        binding.counterText.visibility = View.GONE  // Esconde o contador também
    }

    private fun showCurrentFlashcard() {
        binding.exerciseContainer.visibility = View.VISIBLE
        binding.emptyView.root.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE  // Mostra a barra de progresso durante o exercício
        binding.counterText.visibility = View.VISIBLE  // Mostra o contador durante o exercício

        val flashcard = flashcards[currentIndex]
        val progress = ((currentIndex + 1) * 100) / flashcards.size
        binding.progressBar.progress = progress
        binding.counterText.text = "${currentIndex + 1}/${flashcards.size}"
        binding.questionText.text = flashcard.front

        when (flashcard.type) {
            FlashcardType.FRONT_BACK -> setupFrontBackLayout(flashcard)
            FlashcardType.CLOZE -> setupClozeLayout(flashcard)
            FlashcardType.TEXT_INPUT -> setupTextInputLayout(flashcard)
            FlashcardType.MULTIPLE_CHOICE -> setupMultipleChoiceLayout(flashcard)
        }
    }

    private fun setupFrontBackLayout(flashcard: Flashcard) {
        binding.clozeLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.GONE
        binding.frontBackLayout.visibility = View.VISIBLE

        binding.answerInput.text?.clear()
        binding.answerInput.hint = "Digite a resposta"

        binding.submitButton.setOnClickListener {
            checkAnswer(flashcard, binding.answerInput.text?.toString() ?: "")
        }
    }

    private fun setupClozeLayout(flashcard: Flashcard) {
        binding.frontBackLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.GONE
        binding.clozeLayout.visibility = View.VISIBLE

        binding.clozeAnswerInput.text?.clear()
        binding.clozeAnswerInput.hint = "Digite a palavra que falta"

        binding.submitButton.setOnClickListener {
            checkAnswer(flashcard, binding.clozeAnswerInput.text?.toString() ?: "")
        }
    }

    private fun setupTextInputLayout(flashcard: Flashcard) {
        binding.frontBackLayout.visibility = View.GONE
        binding.clozeLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.VISIBLE

        binding.textInputAnswer.text?.clear()
        binding.textInputAnswer.hint = "Digite a resposta"

        binding.submitButton.setOnClickListener {
            checkAnswer(flashcard, binding.textInputAnswer.text?.toString() ?: "")
        }
    }

    private fun setupMultipleChoiceLayout(flashcard: Flashcard) {
        binding.frontBackLayout.visibility = View.GONE
        binding.clozeLayout.visibility = View.GONE
        binding.textInputLayout.visibility = View.GONE
        binding.multipleChoiceLayout.visibility = View.VISIBLE

        binding.optionsRadioGroup.clearCheck()
        binding.optionsRadioGroup.removeAllViews()

        flashcard.options?.let { options ->
            for (i in options.indices) {
                val radioButton = RadioButton(this)
                radioButton.id = View.generateViewId()
                radioButton.text = options[i]
                binding.optionsRadioGroup.addView(radioButton)
            }
        }

        binding.submitButton.setOnClickListener {
            val selectedId = binding.optionsRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedIndex = binding.optionsRadioGroup.indexOfChild(findViewById(selectedId))
                checkAnswer(flashcard, selectedIndex.toString())
            } else {
                showError("Por favor, selecione uma opção")
            }
        }
    }

    private fun checkAnswer(flashcard: Flashcard, userAnswer: String) {
        val isCorrect = when (flashcard.type) {
            FlashcardType.FRONT_BACK -> userAnswer.equals(flashcard.back, ignoreCase = true)
            FlashcardType.CLOZE -> userAnswer.equals(flashcard.clozeAnswer, ignoreCase = true)
            FlashcardType.TEXT_INPUT -> userAnswer.equals(flashcard.back, ignoreCase = true)
            FlashcardType.MULTIPLE_CHOICE -> userAnswer.toInt() == flashcard.correctOptionIndex
        }

        if (isCorrect) {
            correctAnswers++
            showCorrectFeedback()
        } else {
            wrongAnswers++
            showWrongFeedback(flashcard)
        }
    }

    private fun showCorrectFeedback() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Correto!")
            .setMessage("Parabéns! Você acertou!")
            .setPositiveButton("Próximo") { _, _ ->
                moveToNextFlashcard()
            }
            .setCancelable(false)
            .show()
    }

    private fun showWrongFeedback(flashcard: Flashcard) {
        val correctAnswer = when (flashcard.type) {
            FlashcardType.FRONT_BACK -> flashcard.back
            FlashcardType.CLOZE -> flashcard.clozeAnswer
            FlashcardType.TEXT_INPUT -> flashcard.back
            FlashcardType.MULTIPLE_CHOICE -> flashcard.options?.get(flashcard.correctOptionIndex ?: 0) ?: ""
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Incorreto!")
            .setMessage("A resposta correta era: $correctAnswer")
            .setPositiveButton("Próximo") { _, _ ->
                moveToNextFlashcard()
            }
            .setCancelable(false)
            .show()
    }

    private fun moveToNextFlashcard() {
        currentIndex++
        if (currentIndex < flashcards.size) {
            showCurrentFlashcard()
        } else {
            showExerciseResults()
        }
    }

    private fun showExerciseResults() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exercício Concluído!")
            .setMessage("Você acertou $correctAnswers de ${flashcards.size} flashcards.\n" +
                    "Acertos: $correctAnswers\n" +
                    "Erros: $wrongAnswers")
            .setPositiveButton("Concluir") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Erro")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
*/