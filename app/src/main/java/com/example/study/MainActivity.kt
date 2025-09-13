package com.example.study

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study.data.Flashcard
import com.example.study.databinding.ActivityMainBinding
import com.example.study.ui.FlashcardAdapter
import com.example.study.ui.FlashcardViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FlashcardViewModel
    private lateinit var adapter: FlashcardAdapter
    private var currentDeckId: Long = -1
    private var currentDeckName: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentDeckId = intent.getLongExtra("deckId", -1)
        currentDeckName = intent.getStringExtra("deckName") ?: ""
        supportActionBar?.title = currentDeckName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]
        setupRecyclerView()
        setupFab()
        setupBottomNavigation()
        observeFlashcards()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestUserLocation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_decks -> {
                    startActivity(Intent(this, DeckActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, ExerciseSelectionActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_environments -> {
                    startActivity(Intent(this, EnvironmentsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = FlashcardAdapter(
            onItemClick = { flashcard -> showQualityDialog(flashcard) },
            onEditClick = { flashcard -> showAddFlashcardDialog(flashcard) }
        )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            showAddFlashcardDialog()
        }
    }

    private fun observeFlashcards() {
        lifecycleScope.launch {
            if (currentDeckId != -1L) {
                viewModel.getFlashcardsForDeckByCreation(currentDeckId).collectLatest { flashcards ->
                    adapter.submitList(flashcards)
                    updateEmptyView(flashcards.isEmpty())
                }
            } else {
                viewModel.dueFlashcards.collectLatest { flashcards ->
                    adapter.submitList(flashcards)
                    updateEmptyView(flashcards.isEmpty())
                }
            }
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.emptyView.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerview.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddFlashcardDialog(flashcard: Flashcard? = null) {
        // ...
    }
/*
    private fun showDeleteDialog(flashcard: Flashcard) {
        // ...
    }

 */

    private fun showQualityDialog(flashcard: Flashcard) {
        // ...
    }


    @SuppressLint("MissingPermission")
    private fun requestUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                viewModel.saveUserLocation(latitude, longitude)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestUserLocation()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.exercise_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_shuffle -> {
                true
            }
            R.id.action_environments -> {
                startActivity(Intent(this, EnvironmentsActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

/*package com.example.study

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.databinding.ActivityMainBinding
import com.example.study.ui.FlashcardAdapter
import com.example.study.ui.FlashcardViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FlashcardViewModel
    private lateinit var adapter: FlashcardAdapter
    private var currentDeckId: Long = -1
    private var currentDeckName: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentDeckId = intent.getLongExtra("deckId", -1)
        currentDeckName = intent.getStringExtra("deckName") ?: ""
        supportActionBar?.title = currentDeckName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]
        setupRecyclerView()
        setupFab()
        setupBottomNavigation()
        observeFlashcards()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestUserLocation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish() // Finalizando a atividade atual para evitar problemas de navegação
                    true
                }
                R.id.navigation_decks -> {
                    startActivity(Intent(this, DeckActivity::class.java))
                    finish() // Finalizando a atividade atual para evitar problemas de navegação
                    true
                }
                R.id.navigation_exercise -> {
                    startExercise()
                    finish() // Finalizando a atividade atual para evitar problemas de navegação
                    true
                }
                R.id.navigation_environments -> {
                    startActivity(Intent(this, EnvironmentsActivity::class.java))
                    finish() // Finalizando a atividade atual para evitar problemas de navegação
                    true
                }
                else -> false
            }
        }
    }

    private fun startExercise() {
        val intent = Intent(this, ExerciseActivity::class.java)
        intent.putExtra("deckId", currentDeckId)
        intent.putExtra("deckName", currentDeckName)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        adapter = FlashcardAdapter(
            onItemClick = { flashcard -> showQualityDialog(flashcard) },
            onEditClick = { flashcard -> showAddFlashcardDialog(flashcard) }
        )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            showAddFlashcardDialog()
        }
    }

    private fun observeFlashcards() {
        lifecycleScope.launch {
            if (currentDeckId != -1L) {
                viewModel.getFlashcardsForDeckByCreation(currentDeckId).collectLatest { flashcards ->
                    adapter.submitList(flashcards)
                    updateEmptyView(flashcards.isEmpty())
                }
            } else {
                viewModel.dueFlashcards.collectLatest { flashcards ->
                    adapter.submitList(flashcards)
                    updateEmptyView(flashcards.isEmpty())
                }
            }
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.emptyView.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerview.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddFlashcardDialog(flashcard: Flashcard? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_flashcard, null)

        val frontBackLayout = dialogView.findViewById<LinearLayout>(R.id.frontBackLayout)
        val clozeLayout = dialogView.findViewById<LinearLayout>(R.id.clozeLayout)
        val textInputLayout = dialogView.findViewById<LinearLayout>(R.id.textInputLayout)
        val multipleChoiceLayout = dialogView.findViewById<LinearLayout>(R.id.multipleChoiceLayout)

        val frontEditText = dialogView.findViewById<EditText>(R.id.frontEditText)
        val backEditText = dialogView.findViewById<EditText>(R.id.backEditText)
        val clozeTextEditText = dialogView.findViewById<EditText>(R.id.clozeTextEditText)
        val clozeAnswerEditText = dialogView.findViewById<EditText>(R.id.clozeAnswerEditText)
        val textInputQuestionEditText = dialogView.findViewById<EditText>(R.id.textInputQuestionEditText)
        val textInputAnswerEditText = dialogView.findViewById<EditText>(R.id.textInputAnswerEditText)
        val multipleChoiceQuestionEditText = dialogView.findViewById<EditText>(R.id.multipleChoiceQuestionEditText)
        val option1EditText = dialogView.findViewById<EditText>(R.id.option1EditText)
        val option2EditText = dialogView.findViewById<EditText>(R.id.option2EditText)
        val option3EditText = dialogView.findViewById<EditText>(R.id.option3EditText)
        val option4EditText = dialogView.findViewById<EditText>(R.id.option4EditText)

        val flashcardTypeSpinner = dialogView.findViewById<AutoCompleteTextView>(R.id.flashcardTypeSpinner)
        val correctOptionSpinner = dialogView.findViewById<AutoCompleteTextView>(R.id.correctOptionSpinner)

        val flashcardTypes = arrayOf("Frente e Verso", "Omissão de palavras", "Digite a resposta", "Múltipla escolha")
        val flashcardTypeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, flashcardTypes)
        flashcardTypeSpinner.setAdapter(flashcardTypeAdapter)

        val options = arrayOf("Opção 1", "Opção 2", "Opção 3", "Opção 4")
        val correctOptionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        correctOptionSpinner.setAdapter(correctOptionAdapter)

        flashcard?.let {
            frontBackLayout.visibility = View.GONE
            clozeLayout.visibility = View.GONE
            textInputLayout.visibility = View.GONE
            multipleChoiceLayout.visibility = View.GONE

            when (it.type) {
                FlashcardType.FRONT_BACK -> {
                    flashcardTypeSpinner.setText(flashcardTypes[0], false)
                    frontEditText.setText(it.front)
                    backEditText.setText(it.back)
                    frontBackLayout.visibility = View.VISIBLE
                }
                FlashcardType.CLOZE -> {
                    flashcardTypeSpinner.setText(flashcardTypes[1], false)
                    clozeTextEditText.setText(it.clozeText)
                    clozeAnswerEditText.setText(it.clozeAnswer)
                    clozeLayout.visibility = View.VISIBLE
                }
                FlashcardType.TEXT_INPUT -> {
                    flashcardTypeSpinner.setText(flashcardTypes[2], false)
                    textInputQuestionEditText.setText(it.front)
                    textInputAnswerEditText.setText(it.back)
                    textInputLayout.visibility = View.VISIBLE
                }
                FlashcardType.MULTIPLE_CHOICE -> {
                    flashcardTypeSpinner.setText(flashcardTypes[3], false)
                    multipleChoiceQuestionEditText.setText(it.front)
                    it.options?.let { options ->
                        if (options.size >= 1) option1EditText.setText(options[0])
                        if (options.size >= 2) option2EditText.setText(options[1])
                        if (options.size >= 3) option3EditText.setText(options[2])
                        if (options.size >= 4) option4EditText.setText(options[3])
                    }
                    it.correctOptionIndex?.let { index ->
                        correctOptionSpinner.setText(options[index], false)
                    }
                    multipleChoiceLayout.visibility = View.VISIBLE
                }
            }
        }

        flashcardTypeSpinner.setOnItemClickListener { _, _, position, _ ->
            frontBackLayout.visibility = View.GONE
            clozeLayout.visibility = View.GONE
            textInputLayout.visibility = View.GONE
            multipleChoiceLayout.visibility = View.GONE

            when (position) {
                0 -> frontBackLayout.visibility = View.VISIBLE
                1 -> clozeLayout.visibility = View.VISIBLE
                2 -> textInputLayout.visibility = View.VISIBLE
                3 -> multipleChoiceLayout.visibility = View.VISIBLE
            }
        }

        if (flashcard == null) {
            frontBackLayout.visibility = View.VISIBLE
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.add_flashcard))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val selectedType = when (flashcardTypeSpinner.text.toString()) {
                    "Frente e Verso" -> FlashcardType.FRONT_BACK
                    "Omissão de palavras" -> FlashcardType.CLOZE
                    "Digite a resposta" -> FlashcardType.TEXT_INPUT
                    "Múltipla escolha" -> FlashcardType.MULTIPLE_CHOICE
                    else -> FlashcardType.FRONT_BACK
                }

                val newFlashcard = when (selectedType) {
                    FlashcardType.FRONT_BACK -> {
                        Flashcard(
                            id = flashcard?.id ?: 0,
                            deckId = currentDeckId,
                            type = FlashcardType.FRONT_BACK,
                            front = frontEditText.text.toString(),
                            back = backEditText.text.toString()
                        )
                    }
                    FlashcardType.CLOZE -> {
                        Flashcard(
                            id = flashcard?.id ?: 0,
                            deckId = currentDeckId,
                            type = FlashcardType.CLOZE,
                            front = clozeTextEditText.text.toString(),
                            back = "",
                            clozeText = clozeTextEditText.text.toString(),
                            clozeAnswer = clozeAnswerEditText.text.toString()
                        )
                    }
                    FlashcardType.TEXT_INPUT -> {
                        Flashcard(
                            id = flashcard?.id ?: 0,
                            deckId = currentDeckId,
                            type = FlashcardType.TEXT_INPUT,
                            front = textInputQuestionEditText.text.toString(),
                            back = textInputAnswerEditText.text.toString()
                        )
                    }
                    FlashcardType.MULTIPLE_CHOICE -> {
                        val options = listOf(
                            option1EditText.text.toString(),
                            option2EditText.text.toString(),
                            option3EditText.text.toString(),
                            option4EditText.text.toString()
                        )

                        val correctOptionIndex = when (correctOptionSpinner.text.toString()) {
                            "Opção 1" -> 0
                            "Opção 2" -> 1
                            "Opção 3" -> 2
                            "Opção 4" -> 3
                            else -> 0
                        }

                        Flashcard(
                            id = flashcard?.id ?: 0,
                            deckId = currentDeckId,
                            type = FlashcardType.MULTIPLE_CHOICE,
                            front = multipleChoiceQuestionEditText.text.toString(),
                            back = "",
                            options = options,
                            correctOptionIndex = correctOptionIndex
                        )
                    }
                }

                if (flashcard == null) {
                    viewModel.insert(newFlashcard)
                } else {
                    viewModel.update(newFlashcard)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showDeleteDialog(flashcard: Flashcard) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete))
            .setMessage("Deseja realmente excluir este flashcard?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.delete(flashcard)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showQualityDialog(flashcard: Flashcard) {
        val qualities = arrayOf(
            getString(R.string.quality_1),
            getString(R.string.quality_2),
            getString(R.string.quality_3),
            getString(R.string.quality_4),
            getString(R.string.quality_5)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("Como você se saiu?")
            .setItems(qualities) { _, which ->
                val updatedFlashcard = viewModel.calculateNextReview(flashcard, which + 1)
                viewModel.update(updatedFlashcard)
            }
            .show()
    }

    private fun showFlashcardOptionsDialog(flashcard: Flashcard) {
        val options = arrayOf(
            getString(R.string.review),
            getString(R.string.edit),
            getString(R.string.delete)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.flashcard_options))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showQualityDialog(flashcard)
                    1 -> showAddFlashcardDialog(flashcard)
                    2 -> showDeleteDialog(flashcard)
                }
            }
            .show()
    }

    private fun requestUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicita permissão de forma silenciosa sem exigir uma confirmação imediata
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            return
        }
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                // Salva no banco de dados local sem mostrar Toast
                viewModel.saveUserLocation(latitude, longitude)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestUserLocation()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.exercise_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_shuffle -> {
                true
            }
            R.id.action_environments -> {
                startActivity(Intent(this, EnvironmentsActivity::class.java))
                finish() // Finalizando a atividade atual para evitar problemas de navegação
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

 */