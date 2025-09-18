package com.example.study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.data.Deck
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.view.HomeUiState
import com.example.study.ui.view.HomeViewModel
import com.example.study.util.ColorUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDecks: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToDeckExercise: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bem-vindo de volta!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Continue seus estudos", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Navegar para configurações */ }) {
                        Icon(Icons.Default.Settings, "Configurações")
                    }
                }
            )
        },
        bottomBar = {
            // A CORREÇÃO ESTÁ AQUI
            StudyBottomNavigation(
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        1 -> onNavigateToDecks()
                        2 -> onNavigateToExercise()
                        3 -> onNavigateToEnvironments()
                        4 -> onNavigateToAI()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                WelcomeHeroSection(onStartStudyClick = onNavigateToExercise)
            }
            item {
                StudyStatsSection(uiState = uiState)
            }
            item {
                QuickActionsSection(
                    onNavigateToDecks = onNavigateToDecks,
                    onNavigateToExercise = onNavigateToExercise,
                    onNavigateToEnvironments = onNavigateToEnvironments,
                    onNavigateToAI = onNavigateToAI
                )
            }
            item {
                RecentActivitySection(
                    recentDecks = uiState.recentDecks,
                    onDeckClick = { onNavigateToDecks() }, // Ou navegar direto para o deck
                    onStartStudyClick = onNavigateToExercise,
                    onReviewDeck = { deck -> 
                        onNavigateToDeckExercise(deck.id, deck.name)
                    }
                )
            }
        }
    }
}

@Composable
private fun WelcomeHeroSection(onStartStudyClick: () -> Unit) {
    StudyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Brush.horizontalGradient(colors = listOf(GradientStart, GradientEnd)))
            )
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.School, null, modifier = Modifier.size(48.dp), tint = Color.White)
                Spacer(Modifier.height(16.dp))
                Text("V.C.Study", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Estude de forma inteligente com flashcards adaptativos", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                Spacer(Modifier.height(16.dp))
                StudyButton(onClick = onStartStudyClick, text = "Começar a estudar", icon = Icons.Default.PlayArrow)
            }
        }
    }
}

@Composable
private fun StudyStatsSection(uiState: HomeUiState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Seu progresso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Decks", uiState.deckCount.toString(), Icons.Default.Book, Modifier.weight(1f))
            StatCard("Flashcards", uiState.flashcardCount.toString(), Icons.Default.Quiz, Modifier.weight(1f))
            StatCard("Revisões", uiState.dueFlashcardsCount.toString(), Icons.Default.Today, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    StudyCard(modifier = modifier) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun QuickActionsSection(onNavigateToDecks: () -> Unit, onNavigateToExercise: () -> Unit, onNavigateToEnvironments: () -> Unit, onNavigateToAI: () -> Unit) {
    Column {
        Text("Ações rápidas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 16.dp)) {
            item { QuickActionCard("Meus Decks", "Gerenciar flashcards", Icons.Default.Book, onNavigateToDecks) }
            item { QuickActionCard("Exercícios", "Praticar conhecimento", Icons.Default.Quiz, onNavigateToExercise) }
            item { QuickActionCard("Ambientes", "Estudo por localização", Icons.Default.LocationOn, onNavigateToEnvironments) }
            item { QuickActionCard("Viber.AI", "Assistente inteligente", Icons.Default.Psychology, onNavigateToAI) }
        }
    }
}

@Composable
private fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    StudyCard(onClick = onClick, modifier = Modifier.width(160.dp)) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun RecentActivitySection(
    recentDecks: List<Deck>,
    onDeckClick: (Deck) -> Unit,
    onStartStudyClick: () -> Unit,
    onReviewDeck: (Deck) -> Unit
) {
    Column {
        Text("Atividade recente", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        if (recentDecks.isEmpty()) {
            StudyEmptyState(
                title = "Nenhuma atividade ainda",
                subtitle = "Comece estudando para ver sua atividade recente aqui",
                icon = Icons.Default.History,
                actionText = "Começar a estudar",
                onActionClick = onStartStudyClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(recentDecks) { deck ->
                    RecentDeckCard(deck = deck, onDeckClick = onDeckClick, onReviewClick = { onReviewDeck(deck) })
                }
            }
        }
    }
}

@Composable
private fun RecentDeckCard(deck: Deck, onDeckClick: (Deck) -> Unit, onReviewClick: () -> Unit) {
    val cardColor = Color(ColorUtils.getColorFromString(deck.name))
    Card(
        modifier = Modifier.width(220.dp).clickable { onDeckClick(deck) },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(deck.theme, style = MaterialTheme.typography.labelMedium, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text(deck.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(16.dp))
            StudyButton(onClick = onReviewClick, text = "Revisar")
        }
    }
}