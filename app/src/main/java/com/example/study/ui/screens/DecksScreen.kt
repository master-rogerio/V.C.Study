package com.example.study.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import com.example.study.data.Deck
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.DeckViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DecksScreen(
    onNavigateToFlashcards: (deckId: Long, deckName: String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckViewModel = viewModel()
) {
    val decks by viewModel.allDecks.collectAsState(initial = emptyList())
    var showAddDeckDialog by remember { mutableStateOf(false) }
    var deckToEdit by remember { mutableStateOf<Deck?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Deck?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isGridView by remember { mutableStateOf(true) }

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
                    }
                }
            )
        },
        floatingActionButton = {
            StudyFAB(
                onClick = { showAddDeckDialog = true },
                icon = Icons.Default.Add,
                expanded = true,
                text = "Novo Deck",
                contentDescription = "Criar novo deck"
            )
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
                        subtitle = "Crie seu primeiro deck para começar a estudar com flashcards",
                        icon = Icons.Default.Book,
                        actionText = "Criar Deck",
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
                        onDeckClick = { deck -> onNavigateToFlashcards(deck.id, deck.name) },
                        onEditClick = { deck -> deckToEdit = deck },
                        onDeleteClick = { deck -> showDeleteDialog = deck },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    DecksList(
                        decks = filteredDecks,
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
                    viewModel.update(deckToEdit!!.copy(name = name, theme = theme))
                } else {
                    viewModel.insert(Deck(name = name, theme = theme))
                }
                showAddDeckDialog = false
                deckToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { deck ->
        DeleteDeckDialog(
            deckName = deck.name,
            onConfirm = {
                viewModel.delete(deck)
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
                text = "0 cards", // TODO: Buscar número real de cards
                selected = false
            )
        }
    }
}

@Composable
private fun DeckListItem(
    deck: Deck,
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
                    text = "0 flashcards", // TODO: Buscar número real
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (deck == null) "Novo Deck" else "Editar Deck",
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
                    label = { Text("Nome do deck") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = theme,
                    onValueChange = { theme = it },
                    label = { Text("Tema/Assunto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            StudyButton(
                onClick = {
                    if (name.isNotBlank() && theme.isNotBlank()) {
                        onSave(name.trim(), theme.trim())
                    }
                },
                text = "Salvar",
                enabled = name.isNotBlank() && theme.isNotBlank()
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