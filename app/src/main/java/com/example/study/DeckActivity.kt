package com.example.study // Certifique-se de que o pacote corresponde ao seu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study.data.Deck
import com.example.study.databinding.ActivityDeckBinding
import com.example.study.ui.DeckAdapter
import com.example.study.ui.DeckViewModel
import com.example.study.ui.FlashcardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeckBinding
    private lateinit var deckViewModel: DeckViewModel
    private lateinit var flashcardViewModel: FlashcardViewModel
    private lateinit var adapter: DeckAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deckViewModel = ViewModelProvider(this)[DeckViewModel::class.java]
        flashcardViewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]

        setupRecyclerView()
        setupFab()
        setupBottomNavigation()
        observeDecks()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_decks

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_decks -> true
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
        adapter = DeckAdapter(
            onItemClick = { deck -> openFlashcardActivity(deck) },
            onEditClick = { deck -> showAddDeckDialog(deck) },
            getFlashcardCount = { deckId ->
                var count = 0
                lifecycleScope.launch {
                    count = deckViewModel.getFlashcardCountForDeck(deckId)
                    adapter.updateFlashcardCount(deckId, count)
                }
                count
            }
        )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@DeckActivity)
            adapter = this@DeckActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            showAddDeckDialog()
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

    private fun showAddDeckDialog(deck: Deck? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_deck, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.deckNameEditText)
        val themeEditText = dialogView.findViewById<EditText>(R.id.deckThemeEditText)

        deck?.let {
            nameEditText.setText(it.name)
            themeEditText.setText(it.theme)
        }

        val dialogBuilder = MaterialAlertDialogBuilder(this)
            .setTitle(getString(if (deck == null) R.string.add_deck else R.string.edit_deck))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val name = nameEditText.text.toString()
                val theme = themeEditText.text.toString()

                if (name.isBlank() || theme.isBlank()) {
                    Toast.makeText(this, "Nome e tema são obrigatórios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newDeck = Deck(
                    id = deck?.id ?: 0,
                    name = name,
                    theme = theme
                )

                if (deck == null) {
                    deckViewModel.insert(newDeck)
                } else {
                    deckViewModel.update(newDeck)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)

        if (deck != null) {
            dialogBuilder.setNeutralButton(getString(R.string.delete_deck_from_edit)) { _, _ ->
                showDeleteDialog(deck)
            }
        }

        dialogBuilder.show()
    }

    private fun showDeleteDialog(deck: Deck) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_deck))
            .setMessage(getString(R.string.delete_deck_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deckViewModel.delete(deck)
                flashcardViewModel.deleteAllFlashcardsForDeck(deck.id)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun openFlashcardActivity(deck: Deck) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("deckId", deck.id)
        intent.putExtra("deckName", deck.name)
        startActivity(intent)
    }
}

/*
package com.example.study

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study.data.Deck
import com.example.study.databinding.ActivityDeckBinding
import com.example.study.ui.DeckAdapter
import com.example.study.ui.DeckViewModel
import com.example.study.ui.FlashcardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeckBinding
    private lateinit var deckViewModel: DeckViewModel
    private lateinit var flashcardViewModel: FlashcardViewModel
    private lateinit var adapter: DeckAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deckViewModel = ViewModelProvider(this)[DeckViewModel::class.java]
        flashcardViewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]

        setupRecyclerView()
        setupFab()
        setupBottomNavigation()
        observeDecks()
    }

    private fun setupBottomNavigation() {
        // Definindo explicitamente que o item selecionado é o de Decks
        binding.bottomNavigation.selectedItemId = R.id.navigation_decks
        
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish() // Finaliza a atividade atual para evitar acúmulo na pilha
                    true
                }
                R.id.navigation_decks -> true
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, ExerciseSelectionActivity::class.java))
                    finish() // Finaliza a atividade atual para evitar acúmulo na pilha
                    true
                }
                R.id.navigation_environments -> {
                    startActivity(Intent(this, EnvironmentsActivity::class.java))
                    finish() // Finaliza a atividade atual para evitar acúmulo na pilha
                    true
                }
                else -> false
            }
        }
    }

    private fun startExercise() {
        val intent = Intent(this, ExerciseSelectionActivity::class.java)
        startActivity(intent)
        finish() // Finaliza a atividade atual para evitar acúmulo na pilha
    }

    private fun setupRecyclerView() {
        adapter = DeckAdapter(
            onItemClick = { deck -> openFlashcardActivity(deck) },
            onEditClick = { deck -> showAddDeckDialog(deck) },
            getFlashcardCount = { deckId ->
                var count = 0
                lifecycleScope.launch {
                    count = deckViewModel.getFlashcardCountForDeck(deckId)
                    adapter.updateFlashcardCount(deckId, count)
                }
                count
            }
        )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@DeckActivity)
            adapter = this@DeckActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            showAddDeckDialog()
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

    private fun showAddDeckDialog(deck: Deck? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_deck, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.deckNameEditText)
        val themeEditText = dialogView.findViewById<EditText>(R.id.deckThemeEditText)

        deck?.let {
            nameEditText.setText(it.name)
            themeEditText.setText(it.theme)
        }

        val dialogBuilder = MaterialAlertDialogBuilder(this)
            .setTitle(getString(if (deck == null) R.string.add_deck else R.string.edit_deck))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val name = nameEditText.text.toString()
                val theme = themeEditText.text.toString()

                if (name.isBlank() || theme.isBlank()) {
                    Toast.makeText(this, "Nome e tema são obrigatórios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newDeck = Deck(
                    id = deck?.id ?: 0,
                    name = name,
                    theme = theme
                )

                if (deck == null) {
                    lifecycleScope.launch {
                        try {
                            deckViewModel.insert(newDeck)
                            Toast.makeText(this@DeckActivity, "Deck adicionado com sucesso", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@DeckActivity, "Erro ao adicionar deck: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    lifecycleScope.launch {
                        try {
                            deckViewModel.update(newDeck)
                            Toast.makeText(this@DeckActivity, "Deck atualizado com sucesso", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@DeckActivity, "Erro ao atualizar deck: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)

        if (deck != null) {
            dialogBuilder.setNeutralButton(getString(R.string.delete_deck_from_edit)) { _, _ ->
                showDeleteDialog(deck)
            }
        }

        dialogBuilder.show()
    }

    private fun showDeleteDialog(deck: Deck) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_deck))
            .setMessage(getString(R.string.delete_deck_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deckViewModel.delete(deck)
                flashcardViewModel.deleteAllFlashcardsForDeck(deck.id)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun openFlashcardActivity(deck: Deck) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("deckId", deck.id)
        intent.putExtra("deckName", deck.name)
        startActivity(intent)
    }

    private fun showDeckOptionsDialog(deck: Deck) {
        val options = arrayOf(
            getString(R.string.open),
            getString(R.string.edit),
            getString(R.string.delete)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(deck.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openFlashcardActivity(deck)
                    1 -> showAddDeckDialog(deck)
                    2 -> showDeleteDialog(deck)
                }
            }
            .show()
    }
}
*/