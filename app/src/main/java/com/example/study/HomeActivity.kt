package com.example.study

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.study.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupStartStudyingButton()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_decks -> {
                    startActivity(Intent(this, DeckActivity::class.java))
                    finish() // Finalizando a atividade atual para evitar problemas de navegação
                    true
                }
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, ExerciseSelectionActivity::class.java))
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

    private fun setupStartStudyingButton() {
        binding.startStudyingButton.setOnClickListener {
            startActivity(Intent(this, DeckActivity::class.java))
            finish() // Finalizando a atividade atual para não manter a pilha de navegação
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.root.clearFocus()
    }
}