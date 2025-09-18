package com.example.study.ui.screens

import android.widget.Space
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.data.Deck
import com.example.study.data.FlashcardType
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.view.DeckViewModel
import com.example.study.ui.view.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DecksScreen(
    onNavigateToFlashcards: (deckId: Long, deckName: String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    onNavigateToAI: () -> Unit,
    modifier: Modifier = Modifier,
    deckViewModel: DeckViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val decks by deckViewModel.allDecks.collectAsState(initial = emptyList())
    var showAddDeckDialog by remember { mutableStateOf(false) }
    var showGenerateDeckDialog by remember { mutableStateOf(false) }
    var deckToEdit by remember { mutableStateOf<Deck?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Deck?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isGridView by remember { mutableStateOf(true) }
    var flashcardCounts by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }

    LaunchedEffect(decks) {
        decks.forEach { deck ->
            val count = deckViewModel.getFlashcardCountForDeck(deck.id)
            flashcardCounts = flashcardCounts + (deck.id to count)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val generationResult by flashcardViewModel.flashcardGenerationResult.collectAsState()
    LaunchedEffect(generationResult) {
        generationResult?.onSuccess { count ->
            snackbarHostState.showSnackbar(
                message = "Deck criado com $count flashcards!",
                duration = SnackbarDuration.Short
            )
            flashcardViewModel.clearFlashcardGenerationResult()
        }
        generationResult?.onFailure { error ->
            snackbarHostState.showSnackbar(
                message = "Erro ao gerar flashcards: ${error.message}",
                duration = SnackbarDuration.Long
            )
            flashcardViewModel.clearFlashcardGenerationResult()
        }
    }

    val filteredDecks = remember(decks, searchQuery) {
        if (searchQuery.isBlank()) {
            decks
        } else {
            decks.filter { deck ->
                deck.name.contains(searchQuery, ignoreCase = true) ||
                deck.theme.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meus Decks",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Vista em lista" else "Vista em grade"
                        )
                    }
                    IconButton(onClick = { /* TODO: Filtros */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros"
                        )
                    }
                }
            )
        },
        bottomBar = {
            StudyBottomNavigation(
                selectedItem = 1,
                onItemSelected = { index ->
                    when (index) {
                        0 -> onNavigateToHome()
                        2 -> onNavigateToExercise()
                        3 -> onNavigateToEnvironments()
                        4 -> onNavigateToAI()
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StudyFAB(
                    onClick = { showGenerateDeckDialog = true },
                    icon = Icons.Default.Psychology,
                    expanded = true,
                    text = "Gerar com IA",
                    contentDescription = "Gerar deck com Inteligência Artificial"
                )
                StudyFAB(
                    onClick = { showAddDeckDialog = true },
                    icon = Icons.Default.Add,
                    expanded = true,
                    text = "Novo Deck",
                    contentDescription = "Criar novo deck"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (filteredDecks.isEmpty()) {
                if (decks.isEmpty()) {
                    // No decks at all
                    StudyEmptyState(
                        title = "Nenhum deck criado",
                        subtitle = "Crie seu primeiro deck manualmente ou use a IA para gerar um para você!",
                        icon = Icons.Default.Book,
                        actionText = "Criar Primeiro Deck",
                        onActionClick = { showAddDeckDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                } else {
                    // No decks match search
                    StudyEmptyState(
                        title = "Nenhum deck encontrado",
                        subtitle = "Tente ajustar sua busca ou criar um novo deck",
                        icon = Icons.Default.SearchOff,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                }
            } else {
                if (isGridView) {
                    DecksGrid(
                        decks = filteredDecks,
                        flashcardCounts = flashcardCounts,
                        onDeckClick = { deck -> onNavigateToFlashcards(deck.id, deck.name) },
                        onEditClick = { deck -> deckToEdit = deck },
                        onDeleteClick = { deck -> showDeleteDialog = deck },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    DecksList(
                        decks = filteredDecks,
                        flashcardCounts = flashcardCounts,
                        onDeckClick = { deck -> onNavigateToFlashcards(deck.id, deck.name) },
                        onEditClick = { deck -> deckToEdit = deck },
                        onDeleteClick = { deck -> showDeleteDialog = deck },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Add/Edit Deck Dialog
    if (showAddDeckDialog || deckToEdit != null) {
        AddEditDeckDialog(
            deck = deckToEdit,
            onDismiss = {
                showAddDeckDialog = false
                deckToEdit = null
            },
            onSave = { name, theme ->
                if (deckToEdit != null) {
                    deckViewModel.update(deckToEdit!!.copy(name = name, theme = theme))
                } else {
                    deckViewModel.insert(Deck(name = name, theme = theme))
                }
                showAddDeckDialog = false
                deckToEdit = null
            }
        )
    }

    if (showGenerateDeckDialog) {
        GenerateFlashcardsDialog(
            onDismiss = { showGenerateDeckDialog = false },
            onGenerate = { topic, deckName, type ->
                showGenerateDeckDialog = false
                flashcardViewModel.generateAndSaveFlashcards(topic, deckName, type)
            }
        )
    }

    showDeleteDialog?.let { deck ->
        DeleteDeckDialog(
            deckName = deck.name,
            onConfirm = {
                deckViewModel.delete(deck)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
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
        placeholder = { Text("Buscar decks...") },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DecksGrid(
    decks: List<Deck>,
    flashcardCounts: Map<Long, Int>,
    onDeckClick: (Deck) -> Unit,
    onEditClick: (Deck) -> Unit,
    onDeleteClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(160.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(decks, key = { it.id }) { deck ->
            DeckGridItem(
                deck = deck,
                flashcardCount = flashcardCounts[deck.id] ?: 0,
                onClick = { onDeckClick(deck) },
                onEditClick = { onEditClick(deck) },
                onDeleteClick = { onDeleteClick(deck) },
                modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            )
        }
    }
}

@Composable
private fun DecksList(
    decks: List<Deck>,
    flashcardCounts: Map<Long, Int>,
    onDeckClick: (Deck) -> Unit,
    onEditClick: (Deck) -> Unit,
    onDeleteClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(decks, key = { it.id }) { deck ->
            DeckListItem(
                deck = deck,
                flashcardCount = flashcardCounts[deck.id] ?: 0,
                onClick = { onDeckClick(deck) },
                onEditClick = { onEditClick(deck) },
                onDeleteClick = { onDeleteClick(deck) }
            )
        }
    }
}

@Composable
private fun DeckGridItem(
    deck: Deck,
    flashcardCount: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    StudyCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = deck.name.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            modifier = Modifier.size(16.dp)
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

            Text(
                text = deck.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = deck.theme,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            StudyChip(
                text = "$flashcardCount cards", // Usa número real de cards
                selected = false
            )
        }
    }
}

@Composable
private fun DeckListItem(
    deck: Deck,
    flashcardCount: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    StudyCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = deck.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = deck.theme,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$flashcardCount flashcards",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
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
    }
}

@Composable
private fun AddEditDeckDialog(
    deck: Deck?,
    onDismiss: () -> Unit,
    onSave: (name: String, theme: String) -> Unit
) {
    var name by remember { mutableStateOf(deck?.name ?: "") }
    var theme by remember { mutableStateOf(deck?.theme ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                imageVector = if (deck == null) Icons.Default.Add else Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Text(
                            text = if (deck == null) "Novo Deck" else "Editar Deck",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (deck == null)
                            "Crie um novo deck para organizar seus flashcards"
                        else
                            "Edite as informações do seu deck",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Form Fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do deck") },
                        placeholder = { Text("Ex: Matemática Básica") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = theme,
                        onValueChange = { theme = it },
                        label = { Text("Tema/Assunto") },
                        placeholder = { Text("Ex: Álgebra e Geometria") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Topic,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
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
                            if (name.isNotBlank() && theme.isNotBlank()) {
                                onSave(name.trim(), theme.trim())
                            }
                        },
                        enabled = name.isNotBlank() && theme.isNotBlank(),
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
}

@Composable
private fun DeleteDeckDialog(
    deckName: String,
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
            Text("Excluir deck")
        },
        text = {
            Text("Tem certeza que deseja excluir o deck \"$deckName\"? Esta ação não pode ser desfeita.")
        },
        confirmButton = {
            StudyButton(
                onClick = onConfirm,
                text = "Excluir",
                variant = ButtonVariant.Primary // TODO: Add error variant
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenerateFlashcardsDialog(
    onDismiss: () -> Unit,
    onGenerate: (String, String, FlashcardType) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var deckName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(FlashcardType.FRONT_BACK) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val cardTypes = mapOf(
        FlashcardType.FRONT_BACK to "Frente e Verso",
        FlashcardType.MULTIPLE_CHOICE to "Múltipla Escolha"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gerar Deck com IA") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Digite o tema dos flashcards") },
                    placeholder = { Text("Ex: Segunda Guerra Mundial") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Nome para o novo deck") },
                    placeholder = { Text("Ex: História") },
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = cardTypes[selectedType] ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Flashcard") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        cardTypes.forEach { (type, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedType = type
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGenerate(topic, deckName, selectedType) },
                enabled = topic.isNotBlank() && deckName.isNotBlank()
            ) {
                Text("Gerar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}