package com.example.study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.data.FlashcardType
import com.example.study.ui.components.*
import com.example.study.ui.theme.SuccessColor
import com.example.study.ui.theme.WarningColor
import com.example.study.ui.view.FlashcardViewModel
import com.example.study.util.CurrentLocationDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String,
    onNavigateToHome: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    selectedTab: Int,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            StudyBottomNavigation(
                selectedItem = selectedTab,
                onItemSelected = { index ->
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToDecks()
                        2 -> onNavigateToExercise()
                        3 -> onNavigateToEnvironments()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            StudyEmptyState(
                title = title,
                subtitle = "$subtitle\n\nEsta tela será implementada em breve!",
                icon = when (selectedTab) {
                    2 -> Icons.Default.Quiz
                    3 -> Icons.Default.LocationOn
                    else -> Icons.Default.Construction
                },
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderExerciseScreen(
    deckName: String,
    onNavigateBack: () -> Unit,
    onNavigateToResults: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Exercício",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = deckName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Exercício em Desenvolvimento",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "A funcionalidade de exercícios será implementada em breve. Por enquanto, você pode simular um resultado.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StudyButton(
                onClick = {
                    // Simular resultado
                    val score = (7..10).random()
                    val total = 10
                    onNavigateToResults(score, total)
                },
                text = "Simular Resultado",
                icon = Icons.Default.PlayArrow
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderResultsScreen(
    score: Int,
    total: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onRetryExercise: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlashcardViewModel = viewModel()
) {
    val percentage = if (total > 0) (score.toFloat() / total * 100).toInt() else 0
    val isGoodScore = percentage >= 70
    
    // Registrar sessão de estudo quando a tela é carregada
    LaunchedEffect(Unit) {
        try {
            // Obter todas as localizações favoritas salvas pelo usuário
            val favoriteLocations = viewModel.getAllFavoriteLocationsSync()
            
            val currentLocationId = if (favoriteLocations.isNotEmpty()) {
                // Usar a primeira localização disponível
                // TODO: Implementar detecção real de GPS e cálculo de distância
                favoriteLocations.first().id
            } else {
                // Criar localização padrão "Casa" se não houver nenhuma
                viewModel.saveFavoriteLocation(
                    name = "Casa",
                    latitude = -18.9186, // Coordenadas de Uberlândia
                    longitude = -48.2772,
                    iconName = "ic_home",
                    preferredTypes = listOf(FlashcardType.FRONT_BACK, FlashcardType.MULTIPLE_CHOICE)
                )
                "casa"
            }
            
            // Registrar sessão completa na localização detectada
            viewModel.recordCompleteStudySession(
                locationId = currentLocationId,
                duration = 15L, // 15 minutos
                cardsStudied = total,
                correctAnswers = score,
                averageResponseTime = 5000L, // 5 segundos por card
                preferredCardTypes = listOf(FlashcardType.FRONT_BACK, FlashcardType.MULTIPLE_CHOICE)
            )
            
            // Também atualizar analytics básicos
            viewModel.updateLocationAnalytics(currentLocationId, score, total)
            
        } catch (e: Exception) {
            // Em caso de erro, usar localização padrão
            val fallbackLocationId = "casa"
            
            viewModel.recordCompleteStudySession(
                locationId = fallbackLocationId,
                duration = 15L,
                cardsStudied = total,
                correctAnswers = score,
                averageResponseTime = 5000L,
                preferredCardTypes = listOf(FlashcardType.FRONT_BACK, FlashcardType.MULTIPLE_CHOICE)
            )
            
            viewModel.updateLocationAnalytics(fallbackLocationId, score, total)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Resultado", fontWeight = FontWeight.Bold) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isGoodScore) Icons.Default.EmojiEvents else Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = if (isGoodScore) SuccessColor else WarningColor
            )
            Spacer(Modifier.height(16.dp))
            Text(if (isGoodScore) "Parabéns!" else "Continue praticando!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Você acertou $score de $total questões", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Taxa de acerto: $percentage%", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StudyButton(
                    onClick = onRetryExercise,
                    text = "Tentar Novamente",
                    icon = Icons.Default.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )

                StudyButton(
                    onClick = onNavigateToDecks,
                    text = "Ver Meus Decks",
                    icon = Icons.Default.Book,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            StudyButton(
                onClick = onNavigateToHome,
                text = "Voltar ao Início",
                variant = ButtonVariant.Tertiary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
