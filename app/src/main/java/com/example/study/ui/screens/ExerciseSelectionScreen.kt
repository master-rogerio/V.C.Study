package com.example.study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.study.data.Deck
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.view.DeckViewModel
import com.example.study.ui.view.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToExercise: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    deckViewModel: DeckViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel(),
    intelligentRotation: Boolean = false,
    preferredCardTypes: List<String> = emptyList(),
    locationId: String? = null,
    source: String? = null,
    locationName: String? = null
) {
    val decks by deckViewModel.allDecks.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    
    // Rotação inteligente: filtrar decks baseado nos tipos de cards preferidos
    val filteredDecksForIntelligentRotation = remember(decks, preferredCardTypes, intelligentRotation) {
        if (intelligentRotation && preferredCardTypes.isNotEmpty()) {
            // Filtrar decks que contêm os tipos de cards preferidos
            decks.filter { deck ->
                // Aqui você pode implementar lógica mais sofisticada
                // Por enquanto, vamos mostrar todos os decks se a rotação inteligente estiver ativa
                true
            }
        } else {
            decks
        }
    }
    
    // Get flashcard counts for each deck
    val deckStats = remember(filteredDecksForIntelligentRotation) {
        filteredDecksForIntelligentRotation.map { deck ->
            deck to mutableStateOf(0)
        }
    }
    
    // Update flashcard counts
    deckStats.forEach { (deck, countState) ->
        val flashcards by flashcardViewModel.getFlashcardsForDeckByCreation(deck.id).collectAsState(initial = emptyList())
        val dueCount = flashcards.count { 
            it.nextReviewDate?.time ?: 0 <= System.currentTimeMillis() 
        }
        LaunchedEffect(flashcards) {
            countState.value = dueCount
        }
    }
    
    val filteredDecks = remember(filteredDecksForIntelligentRotation, searchQuery) {
        if (searchQuery.isBlank()) {
            filteredDecksForIntelligentRotation
        } else {
            filteredDecksForIntelligentRotation.filter { deck ->
                deck.name.contains(searchQuery, ignoreCase = true) ||
                deck.theme.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val totalDueCards = deckStats.sumOf { it.second.value }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Exercícios",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (totalDueCards > 0) {
                            Text(
                                text = "$totalDueCards cards para revisar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (totalDueCards > 0) {
                        IconButton(onClick = { 
                            // Iniciar exercício misto com todos os cards pendentes
                            onNavigateToExercise(-1L, "Exercício Misto") // ID especial para exercício misto
                        }) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Exercício misto"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            StudyBottomNavigation(
                selectedItem = 2,
                onItemSelected = { index ->
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToDecks()
                        3 -> onNavigateToEnvironments()
                        4 -> onNavigateToAI()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Intelligent Rotation Banner
            if (intelligentRotation && locationName != null) {
                IntelligentRotationBanner(
                    locationName = locationName,
                    preferredCardTypes = preferredCardTypes,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Stats Card
            if (totalDueCards > 0) {
                ExerciseStatsCard(
                    totalDueCards = totalDueCards,
                    totalDecks = filteredDecksForIntelligentRotation.size,
                    onStartMixedExercise = {
                        // Iniciar exercício misto com todos os cards pendentes
                        onNavigateToExercise(-1L, "Exercício Misto")
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Search bar
            if (decks.isNotEmpty()) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (filteredDecks.isEmpty()) {
                if (decks.isEmpty()) {
                    // No decks at all
                    StudyEmptyState(
                        title = "Nenhum deck criado",
                        subtitle = "Crie decks com flashcards para começar a praticar exercícios",
                        icon = Icons.Default.Quiz,
                        actionText = "Criar Deck",
                        onActionClick = onNavigateToDecks,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                } else {
                    // No decks match search
                    StudyEmptyState(
                        title = "Nenhum deck encontrado",
                        subtitle = "Tente ajustar sua busca",
                        icon = Icons.Default.SearchOff,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                }
            } else {
                DeckExerciseList(
                    decks = filteredDecks,
                    deckStats = deckStats.toMap(),
                    onDeckClick = { deck -> onNavigateToExercise(deck.id, deck.name) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ExerciseStatsCard(
    totalDueCards: Int,
    totalDecks: Int,
    onStartMixedExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Revisões Pendentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$totalDueCards cards em $totalDecks decks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = totalDueCards.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            StudyButton(
                onClick = onStartMixedExercise,
                text = "Exercício Misto",
                icon = Icons.Default.Shuffle,
                modifier = Modifier.fillMaxWidth()
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

@Composable
private fun DeckExerciseList(
    decks: List<Deck>,
    deckStats: Map<Deck, MutableState<Int>>,
    onDeckClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(decks, key = { it.id }) { deck ->
            val dueCount = deckStats[deck]?.value ?: 0
            
            DeckExerciseItem(
                deck = deck,
                dueCount = dueCount,
                onClick = { onDeckClick(deck) }
            )
        }
    }
}

@Composable
private fun DeckExerciseItem(
    deck: Deck,
    dueCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (dueCount > 0) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        colors = if (dueCount > 0) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (dueCount > 0) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Deck icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (dueCount > 0) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = deck.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (dueCount > 0) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Deck info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (dueCount > 0) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = deck.theme,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (dueCount > 0) Icons.Default.Schedule else Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (dueCount > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            SuccessColor
                        }
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (dueCount > 0) {
                            "$dueCount para revisar"
                        } else {
                            "Em dia"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dueCount > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            SuccessColor
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Action indicator
            if (dueCount > 0) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Iniciar exercício",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Completo",
                    tint = SuccessColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun IntelligentRotationBanner(
    locationName: String,
    preferredCardTypes: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Rotação Inteligente Ativa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Você está em $locationName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (preferredCardTypes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Tipos de cards preferidos para este local:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    preferredCardTypes.forEach { cardType ->
                        StudyChip(
                            text = cardType,
                            selected = true,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}