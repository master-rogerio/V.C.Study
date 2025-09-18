package com.example.study.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun LocationPickerDialog(
    isVisible: Boolean,
    initialLocation: LatLng? = null,
    initialAddress: String = "",
    onLocationSelected: (LatLng, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    
    var address by remember { mutableStateOf(initialAddress) }
    var latitude by remember { mutableStateOf(initialLocation?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(initialLocation?.longitude?.toString() ?: "") }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var isLoadingGeocode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(fusedLocationClient, context) { location, addr ->
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                address = addr
                isLoadingLocation = false
            }
        } else {
            isLoadingLocation = false
            errorMessage = "Permissão de localização negada"
        }
    }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Selecionar Localização")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Address field with geocoding
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Endereço") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Row {
                                if (isLoadingGeocode) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    IconButton(
                                        onClick = {
                                            if (address.isNotBlank()) {
                                                isLoadingGeocode = true
                                                scope.launch {
                                                    val coords = geocodeAddress(context, address)
                                                    coords?.let { (lat, lng) ->
                                                        latitude = lat.toString()
                                                        longitude = lng.toString()
                                                    }
                                                    isLoadingGeocode = false
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Buscar coordenadas"
                                        )
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                if (address.isNotBlank()) {
                                    isLoadingGeocode = true
                                    scope.launch {
                                        val coords = geocodeAddress(context, address)
                                        coords?.let { (lat, lng) ->
                                            latitude = lat.toString()
                                            longitude = lng.toString()
                                        }
                                        isLoadingGeocode = false
                                    }
                                }
                            }
                        )
                    )
                    
                    // Current location button
                    StudyButton(
                        onClick = {
                            isLoadingLocation = true
                            errorMessage = null
                            
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                getCurrentLocation(fusedLocationClient, context) { location, addr ->
                                    latitude = location.latitude.toString()
                                    longitude = location.longitude.toString()
                                    address = addr
                                    isLoadingLocation = false
                                }
                            } else {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        text = "Usar Localização Atual",
                        icon = Icons.Default.MyLocation,
                        isLoading = isLoadingLocation,
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.Secondary
                    )
                    
                    // Manual coordinate input
                    Text(
                        text = "Ou insira as coordenadas manualmente:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = latitude,
                            onValueChange = { latitude = it },
                            label = { Text("Latitude") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = longitude,
                            onValueChange = { longitude = it },
                            label = { Text("Longitude") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true
                        )
                    }
                    
                    // Reverse geocoding button
                    if (latitude.isNotBlank() && longitude.isNotBlank()) {
                        StudyButton(
                            onClick = {
                                scope.launch {
                                    val lat = latitude.toDoubleOrNull()
                                    val lng = longitude.toDoubleOrNull()
                                    if (lat != null && lng != null) {
                                        val addr = reverseGeocode(context, lat, lng)
                                        if (addr.isNotBlank()) {
                                            address = addr
                                        }
                                    }
                                }
                            },
                            text = "Obter Endereço",
                            icon = Icons.Default.Place,
                            modifier = Modifier.fillMaxWidth(),
                            variant = ButtonVariant.Tertiary
                        )
                    }
                    
                    // Error message
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Instructions
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "💡 Dicas:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "• Digite um endereço e clique na lupa\n" +
                                      "• Use sua localização atual\n" +
                                      "• Insira coordenadas do Google Maps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            },
            confirmButton = {
                StudyButton(
                    onClick = {
                        val lat = latitude.toDoubleOrNull()
                        val lng = longitude.toDoubleOrNull()
                        if (lat != null && lng != null && address.isNotBlank()) {
                            onLocationSelected(LatLng(lat, lng), address)
                        }
                    },
                    text = "Confirmar",
                    enabled = latitude.toDoubleOrNull() != null && 
                              longitude.toDoubleOrNull() != null && 
                              address.isNotBlank()
                )
            },
            dismissButton = {
                StudyButton(
                    onClick = onDismiss,
                    text = "Cancelar",
                    variant = ButtonVariant.Secondary
                )
            }
        )
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Location, String) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            kotlinx.coroutines.GlobalScope.launch {
                val address = reverseGeocode(context, location.latitude, location.longitude)
                kotlinx.coroutines.MainScope().launch {
                    onLocationReceived(location, address)
                }
            }
        }
    }
}

private suspend fun geocodeAddress(context: Context, address: String): Pair<Double, Double>? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses?.isNotEmpty() == true) {
                val location = addresses[0]
                Pair(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

private suspend fun reverseGeocode(context: Context, latitude: Double, longitude: Double): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                buildString {
                    address.thoroughfare?.let { append("$it, ") }
                    address.subLocality?.let { append("$it, ") }
                    address.locality?.let { append("$it, ") }
                    address.adminArea?.let { append("$it, ") }
                    address.countryName?.let { append(it) }
                }.removeSuffix(", ")
            } else {
                "Endereço não encontrado"
            }
        } catch (e: Exception) {
            "Erro ao obter endereço"
        }
    }
}