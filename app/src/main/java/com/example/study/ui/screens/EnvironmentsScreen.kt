package com.example.study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.data.FavoriteLocation
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.view.EnvironmentViewModel

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

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = if (location == null) Icons.Default.Add else Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        
                        Text(
                            text = if (location == null) "Novo Local" else "Editar Local",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        text = if (location == null) 
                            "Adicione um local onde você gosta de estudar" 
                        else 
                            "Edite as informações do local",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Form Fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do local") },
                        placeholder = { Text("Ex: Biblioteca Central") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Endereço") },
                        placeholder = { Text("Toque no ícone de localização") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2,
                        readOnly = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        trailingIcon = {
                            Surface(
                                onClick = { showLocationPicker = true },
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Selecionar localização",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    )

                    // Location Info Card
                    if (selectedLocation != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Localização selecionada",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                
                                Text(
                                    text = "Lat: ${String.format("%.6f", selectedLocation!!.latitude)}, " +
                                          "Lng: ${String.format("%.6f", selectedLocation!!.longitude)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    } else {
                        Surface(
                            onClick = { showLocationPicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                1.dp, 
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                
                                Text(
                                    text = "Toque para selecionar localização",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                Spacer(modifier = Modifier.weight(1f))
                                
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Button(
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
                        enabled = name.isNotBlank() && address.isNotBlank() && selectedLocation != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Salvar",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    
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