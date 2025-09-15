package com.example.study.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.data.FavoriteLocation
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.EnvironmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onNavigateToExercise: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EnvironmentViewModel = viewModel()
) {
    val locations by viewModel.allLocations.collectAsState(initial = emptyList())
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var locationToEdit by remember { mutableStateOf<FavoriteLocation?>(null) }
    var showDeleteDialog by remember { mutableStateOf<FavoriteLocation?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLocations = remember(locations, searchQuery) {
        if (searchQuery.isBlank()) {
            locations
        } else {
            locations.filter { location ->
                location.name.contains(searchQuery, ignoreCase = true) ||
                location.address.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ambientes de Estudo",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Configurações de geofencing */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                }
            )
        },
        bottomBar = {
            StudyBottomNavigation(
                selectedItem = 3,
                onItemSelected = { index ->
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToDecks()
                        2 -> onNavigateToExercise()
                    }
                }
            )
        },
        floatingActionButton = {
            StudyFAB(
                onClick = { showAddLocationDialog = true },
                icon = Icons.Default.Add,
                expanded = true,
                text = "Novo Local",
                contentDescription = "Adicionar local favorito"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Info card
            InfoCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Search bar
            if (locations.isNotEmpty()) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (filteredLocations.isEmpty()) {
                if (locations.isEmpty()) {
                    // No locations at all
                    StudyEmptyState(
                        title = "Nenhum ambiente criado",
                        subtitle = "Adicione locais onde você gosta de estudar para receber lembretes contextuais",
                        icon = Icons.Default.LocationOn,
                        actionText = "Adicionar Local",
                        onActionClick = { showAddLocationDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                } else {
                    // No locations match search
                    StudyEmptyState(
                        title = "Nenhum local encontrado",
                        subtitle = "Tente ajustar sua busca ou adicionar um novo local",
                        icon = Icons.Default.SearchOff,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                }
            } else {
                LocationsList(
                    locations = filteredLocations,
                    onLocationClick = { location -> 
                        // TODO: Navegar para estudos neste local
                    },
                    onEditClick = { location -> locationToEdit = location },
                    onDeleteClick = { location -> showDeleteDialog = location },
                    onToggleGeofence = { location ->
                        val updated = location.copy(isGeofenceActive = !location.isGeofenceActive)
                        viewModel.update(updated)
                        if (updated.isGeofenceActive) {
                            viewModel.addGeofence(updated)
                        } else {
                            viewModel.removeGeofence(updated.id.toString())
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Add/Edit Location Dialog
    if (showAddLocationDialog || locationToEdit != null) {
        AddEditLocationDialog(
            location = locationToEdit,
            onDismiss = {
                showAddLocationDialog = false
                locationToEdit = null
            },
            onSave = { name, address, latitude, longitude ->
                if (locationToEdit != null) {
                    val updated = locationToEdit!!.copy(
                        name = name,
                        address = address,
                        latitude = latitude,
                        longitude = longitude
                    )
                    viewModel.update(updated)
                } else {
                    val newLocation = FavoriteLocation(
                        name = name,
                        address = address,
                        latitude = latitude,
                        longitude = longitude
                    )
                    viewModel.insert(newLocation)
                }
                showAddLocationDialog = false
                locationToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { location ->
        DeleteLocationDialog(
            locationName = location.name,
            onConfirm = {
                if (location.isGeofenceActive) {
                    viewModel.removeGeofence(location.id.toString())
                }
                viewModel.delete(location)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier
) {
    StudyCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Adicione seus locais favoritos de estudo e receba lembretes automáticos quando estiver por perto!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar locais...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpar busca"
                    )
                }
            }
        },
        singleLine = true,
        shape = StudyShapes.buttonShape
    )
}

@Composable
private fun LocationsList(
    locations: List<FavoriteLocation>,
    onLocationClick: (FavoriteLocation) -> Unit,
    onEditClick: (FavoriteLocation) -> Unit,
    onDeleteClick: (FavoriteLocation) -> Unit,
    onToggleGeofence: (FavoriteLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(locations, key = { it.id }) { location ->
            LocationItem(
                location = location,
                onClick = { onLocationClick(location) },
                onEditClick = { onEditClick(location) },
                onDeleteClick = { onDeleteClick(location) },
                onToggleGeofence = { onToggleGeofence(location) }
            )
        }
    }
}

@Composable
private fun LocationItem(
    location: FavoriteLocation,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleGeofence: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    StudyCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = location.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                onEditClick()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Excluir") },
                            onClick = {
                                onDeleteClick()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (location.isGeofenceActive) {
                            Icons.Default.NotificationsActive
                        } else {
                            Icons.Default.NotificationsOff
                        },
                        contentDescription = null,
                        tint = if (location.isGeofenceActive) {
                            SuccessColor
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = if (location.isGeofenceActive) {
                            "Lembretes ativos"
                        } else {
                            "Lembretes desativados"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (location.isGeofenceActive) {
                            SuccessColor
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                
                Switch(
                    checked = location.isGeofenceActive,
                    onCheckedChange = { onToggleGeofence() }
                )
            }
        }
    }
}

@Composable
private fun AddEditLocationDialog(
    location: FavoriteLocation?,
    onDismiss: () -> Unit,
    onSave: (name: String, address: String, latitude: Double, longitude: Double) -> Unit
) {
    var name by remember { mutableStateOf(location?.name ?: "") }
    var address by remember { mutableStateOf(location?.address ?: "") }
    var selectedLocation by remember { 
        mutableStateOf(
            if (location != null) {
                com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude)
            } else null
        )
    }
    var showLocationPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (location == null) "Novo Local" else "Editar Local",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do local") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Endereço") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showLocationPicker = true }) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Selecionar localização"
                            )
                        }
                    }
                )

                if (selectedLocation != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Coordenadas selecionadas:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            Text(
                                text = "Lat: ${String.format("%.6f", selectedLocation!!.latitude)}\n" +
                                      "Lng: ${String.format("%.6f", selectedLocation!!.longitude)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    StudyButton(
                        onClick = { showLocationPicker = true },
                        text = "Selecionar Localização",
                        icon = Icons.Default.LocationOn,
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.Secondary
                    )
                }
            }
        },
        confirmButton = {
            StudyButton(
                onClick = {
                    if (name.isNotBlank() && address.isNotBlank() && selectedLocation != null) {
                        onSave(
                            name.trim(), 
                            address.trim(), 
                            selectedLocation!!.latitude, 
                            selectedLocation!!.longitude
                        )
                    }
                },
                text = "Salvar",
                enabled = name.isNotBlank() && address.isNotBlank() && selectedLocation != null
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
    
    // Location Picker Dialog
    LocationPickerDialog(
        isVisible = showLocationPicker,
        initialLocation = selectedLocation,
        initialAddress = address,
        onLocationSelected = { latLng, addressText ->
            selectedLocation = latLng
            address = addressText
            showLocationPicker = false
        },
        onDismiss = { showLocationPicker = false }
    )
}

@Composable
private fun DeleteLocationDialog(
    locationName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Excluir local")
        },
        text = {
            Text("Tem certeza que deseja excluir \"$locationName\"? Os lembretes também serão desativados.")
        },
        confirmButton = {
            StudyButton(
                onClick = onConfirm,
                text = "Excluir"
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