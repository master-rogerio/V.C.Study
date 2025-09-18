package com.example.study

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.study.databinding.ActivityAiQuizBinding
import com.example.study.network.ApiQuizQuestion
// A LINHA CORRETA E COMPLETA É ESTA:
import com.example.study.ui.view.FlashcardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AIQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiQuizBinding
    private lateinit var viewModel: FlashcardViewModel
    private var quizTheme: String = ""
    private var currentCorrectAnswer: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizTheme = intent.getStringExtra("quizTheme") ?: "Conhecimentos Gerais"
        supportActionBar?.title = "Quiz IA: $quizTheme"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]

        observeViewModel()
        setupClickListeners()

        fetchNewQuestion()
    }

    private fun fetchNewQuestion() {
        viewModel.generateAiQuestion(quizTheme)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (isLoading) {
                    binding.quizContainer.visibility = View.INVISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.apiQuizQuestion.collectLatest { result ->
                result?.onSuccess { question ->
                    displayQuestion(question)
                    binding.quizContainer.visibility = View.VISIBLE
                }?.onFailure { error ->
                    showErrorDialog(error.message ?: "Erro desconhecido")
                }
            }
        }
    }

    private fun displayQuestion(question: ApiQuizQuestion) {
        binding.questionText.text = question.question
        currentCorrectAnswer = question.correct_answer

        binding.optionsRadioGroup.apply {
            clearCheck()
            removeAllViews()
            forEach { it.isEnabled = true }
            question.options.forEach { optionText ->
                val radioButton = RadioButton(this@AIQuizActivity).apply {
                    text = optionText
                    textSize = 16f
                    layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(16, 16, 16, 16)
                }
                addView(radioButton)
            }
        }
        binding.submitButton.isEnabled = true
    }

    private fun setupClickListeners() {
        binding.submitButton.setOnClickListener {
            checkAnswer()
        }
        binding.nextButton.setOnClickListener {
            fetchNewQuestion()
        }
    }

    private fun checkAnswer() {
        val selectedRadioButtonId = binding.optionsRadioGroup.checkedRadioButtonId
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Selecione uma resposta", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        val isCorrect = selectedRadioButton.text.toString() == currentCorrectAnswer

        binding.submitButton.isEnabled = false
        binding.optionsRadioGroup.forEach {
            it.isEnabled = false
        }

        val title = if (isCorrect) "Correto!" else "Incorreto!"
        val message = if (isCorrect) {
            "Parabéns, você acertou!"
        } else {
            "A resposta correta era: \n\"$currentCorrectAnswer\""
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showErrorDialog(errorMessage: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Erro ao Gerar Pergunta")
            .setMessage(errorMessage)
            .setPositiveButton("Tentar Novamente") { _, _ ->
                fetchNewQuestion()
            }
            .setNegativeButton("Voltar") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}