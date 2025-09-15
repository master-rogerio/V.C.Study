package com.example.study.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.ui.components.*
import com.example.study.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDecks: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    onNavigateToAI: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bem-vindo de volta!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Continue seus estudos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Configurações */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
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
        },
        floatingActionButton = {
            StudyFAB(
                onClick = onNavigateToDecks,
                icon = Icons.Default.Add,
                expanded = true,
                text = "Novo Deck",
                contentDescription = "Criar novo deck"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                WelcomeHeroSection()
            }
            
            item {
                StudyStatsSection()
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
                RecentActivitySection()
            }
        }
    }
}

@Composable
private fun WelcomeHeroSection() {
    StudyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GradientStart.copy(alpha = 0.8f),
                                GradientEnd.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "V.C.Study",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Estude de forma inteligente com flashcards adaptativos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StudyButton(
                    onClick = { /* TODO: Navegar para estudos */ },
                    text = "Começar a estudar",
                    icon = Icons.Default.PlayArrow,
                    variant = ButtonVariant.Secondary
                )
            }
        }
    }
}

@Composable
private fun StudyStatsSection() {
    Column {
        Text(
            text = "Seu progresso",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Decks",
                value = "12",
                icon = Icons.Default.Book,
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Flashcards",
                value = "156",
                icon = Icons.Default.Quiz,
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Estudados hoje",
                value = "24",
                icon = Icons.Default.Today,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        StudyCard {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Progresso semanal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StudyProgressBar(
                    progress = 0.68f,
                    showPercentage = true
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    StudyCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToDecks: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    onNavigateToAI: () -> Unit
) {
    Column {
        Text(
            text = "Ações rápidas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                QuickActionCard(
                    title = "Meus Decks",
                    subtitle = "Gerenciar flashcards",
                    icon = Icons.Default.Book,
                    onClick = onNavigateToDecks
                )
            }
            
            item {
                QuickActionCard(
                    title = "Exercícios",
                    subtitle = "Praticar conhecimento",
                    icon = Icons.Default.Quiz,
                    onClick = onNavigateToExercise
                )
            }
            
            item {
                QuickActionCard(
                    title = "Ambientes",
                    subtitle = "Estudar por localização",
                    icon = Icons.Default.LocationOn,
                    onClick = onNavigateToEnvironments
                )
            }
            
            item {
                QuickActionCard(
                    title = "Viber.AI",
                    subtitle = "Assistente inteligente",
                    icon = Icons.Default.Psychology,
                    onClick = onNavigateToAI
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    StudyCard(
        onClick = onClick,
        modifier = Modifier.width(160.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecentActivitySection() {
    Column {
        Text(
            text = "Atividade recente",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // TODO: Implementar lista de atividades recentes
        StudyEmptyState(
            title = "Nenhuma atividade ainda",
            subtitle = "Comece estudando para ver sua atividade recente aqui",
            icon = Icons.Default.History,
            actionText = "Começar a estudar",
            onActionClick = { /* TODO: Navegar para estudos */ }
        )
    }
}