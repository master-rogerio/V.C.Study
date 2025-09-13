package com.example.study

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study.databinding.ActivityExerciseSelectionBinding
import com.example.study.ui.DeckAdapter
import com.example.study.ui.DeckViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExerciseSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseSelectionBinding
    private lateinit var deckViewModel: DeckViewModel
    private lateinit var adapter: DeckAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Selecionar Deck para Exercício"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        deckViewModel = ViewModelProvider(this)[DeckViewModel::class.java]
        setupRecyclerView()
        setupAllDecksButton()
        setupBottomNavigation()
        observeDecks()
    }

    private fun setupRecyclerView() {
        adapter = DeckAdapter(
            onItemClick = { deck -> startExercise(deck.id, deck.name) },
            getFlashcardCount = { deckId ->
                var count = 0
                lifecycleScope.launch {
                    val actualCount = deckViewModel.getFlashcardCountForDeck(deckId)
                    adapter.updateFlashcardCount(deckId, actualCount)
                }
                count
            }
        )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ExerciseSelectionActivity)
            adapter = this@ExerciseSelectionActivity.adapter
        }
    }

    private fun setupAllDecksButton() {
        binding.allDecksButton.setOnClickListener {
            startExercise(-1, "Todos os Decks")
        }
    }

    private fun observeDecks() {
        lifecycleScope.launch {
            deckViewModel.allDecks.collectLatest { decks ->
                adapter.submitList(decks)
                if (decks.isEmpty()) {
                    binding.emptyView.root.visibility = View.VISIBLE
                    binding.recyclerview.visibility = View.GONE
                } else {
                    binding.emptyView.root.visibility = View.GONE
                    binding.recyclerview.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun startExercise(deckId: Long, deckName: String) {
        val intent = Intent(this, ExerciseActivity::class.java)
        intent.putExtra("deckId", deckId)
        intent.putExtra("deckName", deckName)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_exercise
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
                R.id.navigation_exercise -> true
                R.id.navigation_environments -> {
                    startActivity(Intent(this, EnvironmentsActivity::class.java))
                    finish() // Finalizando a atividade atual para evitar problemas de navegação
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}