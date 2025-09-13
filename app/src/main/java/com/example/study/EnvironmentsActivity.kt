package com.example.study

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study.adapter.LocationAdapter
import com.example.study.databinding.ActivityEnvironmentsBinding
import com.example.study.databinding.DialogAddLocationBinding
import com.example.study.ui.FlashcardViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EnvironmentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnvironmentsBinding
    private lateinit var viewModel: FlashcardViewModel
    private lateinit var adapter: LocationAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnvironmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.environments)
        
        viewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        setupRecyclerView()
        setupFab()
        setupBottomNavigation()
        observeLocations()
    }

    private fun setupRecyclerView() {
        adapter = LocationAdapter(this) { location ->
            // Ação ao clicar no botão de exclusão
            viewModel.deleteUserLocation(location.id)
            Toast.makeText(this, R.string.location_deleted, Toast.LENGTH_SHORT).show()
        }
        
        binding.rvLocations.layoutManager = LinearLayoutManager(this)
        binding.rvLocations.adapter = adapter
    }

    private fun observeLocations() {
        viewModel.getAllUserLocations().observe(this) { locations ->
            adapter.submitList(locations)
            
            if (locations.isNullOrEmpty()) {
                binding.tvEmptyLocations.visibility = View.VISIBLE
                binding.rvLocations.visibility = View.GONE
            } else {
                binding.tvEmptyLocations.visibility = View.GONE
                binding.rvLocations.visibility = View.VISIBLE
            }
        }
    }

    private fun setupFab() {
        binding.fabAddLocation.setOnClickListener {
            showAddLocationDialog()
        }
    }

    private fun showAddLocationDialog() {
        val initialDialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_location)
            .setMessage(R.string.add_new_location)
            .setPositiveButton(R.string.add_new_location) { _, _ ->
                showNewLocationForm()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
        
        initialDialog.show()
    }

    private fun showNewLocationForm() {
        val dialogBinding = DialogAddLocationBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root
        
        // Animação de slide horizontal
        dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right))
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_location)
            .setView(dialogView)
            .setPositiveButton(R.string.save_location, null) // Será configurado depois para não fechar automaticamente
            .setNegativeButton(R.string.cancel, null)
            .create()
        
        dialog.show()
        
        // Configurar o botão positivo para não fechar automaticamente
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val locationName = dialogBinding.etLocationName.text.toString().trim()
            
            if (locationName.isEmpty()) {
                dialogBinding.tilLocationName.error = "Nome é obrigatório"
                return@setOnClickListener
            }
            
            // Obter o ícone selecionado
            val selectedId = dialogBinding.rgIconSelection.checkedRadioButtonId
            val radioButton = dialogView.findViewById<RadioButton>(selectedId)
            val iconName = radioButton?.tag?.toString() ?: "ic_location"
            
            // Verificar permissão de localização
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                return@setOnClickListener
            }
            
            // Buscar localização atual
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        
                        // Salvar no banco de dados
                        viewModel.saveUserLocation(locationName, iconName, latitude, longitude)
                        Toast.makeText(this, R.string.location_saved, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } ?: run {
                        Toast.makeText(this, R.string.error_saving_location, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, R.string.error_saving_location, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() 
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permissão concedida, mostrar diálogo novamente
            showNewLocationForm()
        } else {
            Toast.makeText(this, R.string.error_location_permission, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_environments
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish() // Finaliza a atividade atual para evitar acúmulo na pilha
                    true
                }
                R.id.navigation_decks -> {
                    startActivity(Intent(this, DeckActivity::class.java))
                    finish() // Finaliza a atividade atual para evitar acúmulo na pilha
                    true
                }
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, ExerciseSelectionActivity::class.java))
                    finish() // Finaliza a atividade atual para evitar acúmulo na pilha
                    true
                }
                R.id.navigation_environments -> true
                else -> false
            }
        }
    }
}