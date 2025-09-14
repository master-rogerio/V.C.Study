package com.example.study

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study.adapter.LocationAdapter
import com.example.study.data.FlashcardType
import com.example.study.databinding.ActivityEnvironmentsBinding
import com.example.study.databinding.DialogAddLocationBinding
import com.example.study.data.FavoriteLocation
import com.example.study.ui.FlashcardViewModel
import com.example.study.util.GeofenceHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.widget.RadioButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EnvironmentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnvironmentsBinding
    private lateinit var viewModel: FlashcardViewModel
    private lateinit var adapter: LocationAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofenceHelper: GeofenceHelper

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // CORRIGIDO: Substituído 'getOrDefault' pelo operador Elvis '?:' para compatibilidade
        when {
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: false -> {
                observeLocations()
            }
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                requestBackgroundLocationPermission()
            }
            else -> {
                Toast.makeText(this, "Permissão de localização é necessária para esta funcionalidade.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private var locationService: LocationForegroundService? = null
    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }


    private fun startLocationForegroundService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        serviceIntent.action = LocationForegroundService.ACTION_START_SERVICE

        // Verifica a versão do Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Para Android 8.0 (Oreo) e superior
            startForegroundService(serviceIntent)
        } else {
            // Para versões mais antigas
            startService(serviceIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Não parar o serviço aqui - deixar rodando em background
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnvironmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.environments)

        viewModel = ViewModelProvider(this)[FlashcardViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofenceHelper = GeofenceHelper(this)

        setupRecyclerView()
        setupFab()
        setupBottomNavigation()
        checkPermissionsAndObserve()
        startLocationForegroundService()
    }

    private fun checkPermissionsAndObserve() {
        if (hasFineLocationPermission() && hasBackgroundLocationPermission()) {
            observeLocations()
        } else if (!hasFineLocationPermission()){
            requestFineLocationPermission()
        } else {
            requestBackgroundLocationPermission()
        }
    }

    private fun hasFineLocationPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun hasBackgroundLocationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private fun requestFineLocationPermission() {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AlertDialog.Builder(this)
                .setTitle("Permissão Adicional Necessária")
                .setMessage("Para que as notificações de estudo funcionem com a aplicação fechada, por favor, escolha 'Permitir o tempo todo' na janela de permissões.")
                .setPositiveButton("Ok") { _, _ ->
                    permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun setupRecyclerView() {
        adapter = LocationAdapter(
            this,
            onDeleteClick = { location ->
                viewModel.deleteFavoriteLocation(location.id)
                Toast.makeText(this, R.string.location_deleted, Toast.LENGTH_SHORT).show()
            },
            onItemClick = { location -> //
                showLocationAnalytics(location)
            }
        )

        binding.rvLocations.layoutManager = LinearLayoutManager(this)
        binding.rvLocations.adapter = adapter
    }

    private fun observeLocations() {
        viewModel.getAllFavoriteLocations().observe(this) { locations ->
            adapter.submitList(locations)
            binding.tvEmptyLocations.visibility = if (locations.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.rvLocations.visibility = if (locations.isNullOrEmpty()) View.GONE else View.VISIBLE

            if (hasBackgroundLocationPermission() && locations.isNotEmpty()) {
                Log.d("Geofence", "A adicionar ${locations.size} geofences.")
                geofenceHelper.addGeofences(locations, geofencePendingIntent)
            }
        }
    }

    private fun setupFab() {
        binding.fabAddLocation.setOnClickListener {
            showAddLocationDialog()
        }
    }

    @SuppressLint("MissingPermission") // Adicionado para informar que a permissão é verificada antes
    private fun showAddLocationDialog() {
        if (!hasFineLocationPermission()) {
            requestFineLocationPermission()
            return
        }

        val dialogBinding = DialogAddLocationBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root

        // Configuração do slider de raio
        dialogBinding.radiusSlider.addOnChangeListener { _, value, _ ->
            dialogBinding.radiusValue.text = "${value.toInt()} metros"
        }

        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_location)
            .setView(dialogView)
            .setPositiveButton(R.string.save_location, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val locationName = dialogBinding.etLocationName.text.toString().trim()

            if (locationName.isEmpty()) {
                dialogBinding.tilLocationName.error = "Nome é obrigatório"
                return@setOnClickListener
            }

            // Captura do ícone selecionado
            val selectedRadioButtonId = dialogBinding.rgIconSelection.checkedRadioButtonId
            val selectedRadioButton = dialogView.findViewById<RadioButton>(selectedRadioButtonId)
            val iconName = selectedRadioButton?.tag?.toString() ?: "ic_location"

            // Captura dos tipos preferidos
            val preferredTypes = mutableListOf<FlashcardType>()
            if (dialogBinding.checkboxFrontBack.isChecked) {
                preferredTypes.add(FlashcardType.FRONT_BACK)
            }
            if (dialogBinding.checkboxCloze.isChecked) {
                preferredTypes.add(FlashcardType.CLOZE)
            }
            if (dialogBinding.checkboxTextInput.isChecked) {
                preferredTypes.add(FlashcardType.TEXT_INPUT)
            }
            if (dialogBinding.checkboxMultipleChoice.isChecked) {
                preferredTypes.add(FlashcardType.MULTIPLE_CHOICE)
            }

            // Captura do raio selecionado
            val radius = dialogBinding.radiusSlider.value.toInt()

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        // Passa o iconName e tipos preferidos para serem guardados
                        viewModel.saveFavoriteLocationWithRadius(locationName, latitude, longitude, iconName, preferredTypes, radius)
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

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_environments
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
                R.id.navigation_environments -> true
                else -> false
            }
        }
    }

    private fun showLocationAnalytics(location: FavoriteLocation) {
        val analyticsText = """
        �� ${location.name}
        
        📊 Estatísticas:
        • Sessões de estudo: ${location.studySessionCount}
        • Performance média: ${location.averagePerformance.toInt()}%
        • Raio do geofence: ${location.radius}m
        
        🎯 Tipos preferidos:
        ${if (location.preferredCardTypes.isNotEmpty()) {
            location.preferredCardTypes.joinToString("\n• ") { type ->
                when (type) {
                    FlashcardType.FRONT_BACK -> "• Frente e Verso"
                    FlashcardType.CLOZE -> "• Omissão de palavras"
                    FlashcardType.TEXT_INPUT -> "• Digite a resposta"
                    FlashcardType.MULTIPLE_CHOICE -> "• Múltipla escolha"
                }
            }
        } else {
            "• Todos os tipos"
        }}
        
        📍 Coordenadas:
        • Latitude: ${location.latitude}
        • Longitude: ${location.longitude}
    """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Analytics do Local")
            .setMessage(analyticsText)
            .setPositiveButton("OK", null)
            .show()
    }




}

/*
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

 */