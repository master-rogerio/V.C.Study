package com.example.study.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import com.example.study.ui.FlashcardViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    deckId: Long,
    deckName: String,
    onNavigateBack: () -> Unit,
    onNavigateToResults: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlashcardViewModel = viewModel()
) {
    val flashcards by viewModel.getFlashcardsForDeckByCreation(deckId).collectAsState(initial = emptyList())
    val dueFlashcards = remember(flashcards) {
        flashcards.filter { it.nextReviewDate?.time ?: 0 <= System.currentTimeMillis() }
    }
    
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isShowingAnswer by remember { mutableStateOf(false) }
    var exerciseCompleted by remember { mutableStateOf(false) }
    var userAnswer by remember { mutableStateOf("") }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var showQualityButtons by remember { mutableStateOf(false) }

    val currentFlashcard = if (dueFlashcards.isNotEmpty() && currentIndex < dueFlashcards.size) {
        dueFlashcards[currentIndex]
    } else null

    LaunchedEffect(dueFlashcards) {
        if (dueFlashcards.isEmpty()) {
            exerciseCompleted = true
        }
    }

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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    Text(
                        text = "${currentIndex + 1}/${dueFlashcards.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                exerciseCompleted -> {
                    ExerciseCompletedScreen(
                        score = score,
                        total = dueFlashcards.size,
                        onNavigateToResults = { onNavigateToResults(score, dueFlashcards.size) }
                    )
                }
                
                currentFlashcard != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Progress bar
                        StudyProgressBar(
                            progress = (currentIndex + 1).toFloat() / dueFlashcards.size,
                            showPercentage = false
                        )

                        // Flashcard content
                        FlashcardExerciseContent(
                            flashcard = currentFlashcard,
                            isShowingAnswer = isShowingAnswer,
                            userAnswer = userAnswer,
                            onUserAnswerChange = { userAnswer = it },
                            selectedOption = selectedOption,
                            onOptionSelected = { selectedOption = it },
                            showQualityButtons = showQualityButtons,
                            onRevealAnswer = { 
                                isShowingAnswer = true
                                showQualityButtons = currentFlashcard.type == FlashcardType.FRONT_BACK
                            },
                            onQualitySelected = { quality ->
                                val isCorrect = when (currentFlashcard.type) {
                                    FlashcardType.FRONT_BACK -> quality >= 3
                                    FlashcardType.TEXT_INPUT -> {
                                        userAnswer.trim().equals(currentFlashcard.back.trim(), ignoreCase = true)
                                    }
                                    FlashcardType.MULTIPLE_CHOICE -> {
                                        selectedOption == currentFlashcard.correctOptionIndex
                                    }
                                    FlashcardType.CLOZE -> {
                                        userAnswer.trim().equals(currentFlashcard.clozeAnswer?.trim() ?: "", ignoreCase = true)
                                    }
                                }
                                
                                if (isCorrect) score++
                                
                                // Update spaced repetition
                                val updatedFlashcard = viewModel.calculateNextReview(currentFlashcard, quality)
                                viewModel.update(updatedFlashcard)
                                
                                // Move to next flashcard
                                if (currentIndex < dueFlashcards.size - 1) {
                                    currentIndex++
                                    resetForNextCard(
                                        onResetAnswer = { userAnswer = "" },
                                        onResetOption = { selectedOption = -1 },
                                        onResetShowAnswer = { isShowingAnswer = false },
                                        onResetQualityButtons = { showQualityButtons = false }
                                    )
                                } else {
                                    exerciseCompleted = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                else -> {
                    StudyEmptyState(
                        title = "Nenhum flashcard para revisar",
                        subtitle = "Todos os flashcards deste deck estão em dia!",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardExerciseContent(
    flashcard: Flashcard,
    isShowingAnswer: Boolean,
    userAnswer: String,
    onUserAnswerChange: (String) -> Unit,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    showQualityButtons: Boolean,
    onRevealAnswer: () -> Unit,
    onQualitySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Question Card
        StudyCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                FlashcardTypeChip(
                    type = flashcard.type,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = when (flashcard.type) {
                        FlashcardType.CLOZE -> flashcard.clozeText?.replace("___", "______") ?: flashcard.front
                        else -> flashcard.front
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Answer Section
        when (flashcard.type) {
            FlashcardType.FRONT_BACK -> {
                if (!isShowingAnswer) {
                    StudyButton(
                        onClick = onRevealAnswer,
                        text = "Revelar Resposta",
                        icon = Icons.Default.Visibility,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AnswerCard(
                        answer = flashcard.back,
                        isCorrect = null
                    )
                }
            }
            
            FlashcardType.TEXT_INPUT -> {
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = onUserAnswerChange,
                    label = { Text("Digite sua resposta") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )
                
                if (!isShowingAnswer) {
                    StudyButton(
                        onClick = onRevealAnswer,
                        text = "Verificar Resposta",
                        icon = Icons.Default.Check,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val isCorrect = userAnswer.trim().equals(flashcard.back.trim(), ignoreCase = true)
                    AnswerCard(
                        answer = flashcard.back,
                        isCorrect = isCorrect
                    )
                }
            }
            
            FlashcardType.MULTIPLE_CHOICE -> {
                flashcard.options?.forEachIndexed { index, option ->
                    OptionCard(
                        option = option,
                        isSelected = selectedOption == index,
                        isCorrect = if (isShowingAnswer) index == flashcard.correctOptionIndex else null,
                        onClick = { 
                            if (!isShowingAnswer) {
                                onOptionSelected(index)
                            }
                        }
                    )
                }
                
                if (selectedOption != -1 && !isShowingAnswer) {
                    StudyButton(
                        onClick = onRevealAnswer,
                        text = "Verificar Resposta",
                        icon = Icons.Default.Check,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            FlashcardType.CLOZE -> {
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = onUserAnswerChange,
                    label = { Text("Complete a lacuna") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                if (!isShowingAnswer) {
                    StudyButton(
                        onClick = onRevealAnswer,
                        text = "Verificar Resposta",
                        icon = Icons.Default.Check,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val isCorrect = userAnswer.trim().equals(flashcard.clozeAnswer?.trim() ?: "", ignoreCase = true)
                    AnswerCard(
                        answer = flashcard.clozeAnswer ?: "Resposta não definida",
                        isCorrect = isCorrect
                    )
                }
            }
        }

        // Quality Buttons (for spaced repetition)
        if (showQualityButtons || (isShowingAnswer && flashcard.type != FlashcardType.FRONT_BACK)) {
            QualityButtons(
                onQualitySelected = onQualitySelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AnswerCard(
    answer: String,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier
) {
    StudyCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (isCorrect) {
                true -> SuccessColor.copy(alpha = 0.1f)
                false -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                null -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCorrect != null) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isCorrect) SuccessColor else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column {
                Text(
                    text = if (isCorrect != null) {
                        if (isCorrect) "Correto!" else "Incorreto"
                    } else "Resposta:",
                    style = MaterialTheme.typography.labelMedium,
                    color = when (isCorrect) {
                        true -> SuccessColor
                        false -> MaterialTheme.colorScheme.error
                        null -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OptionCard(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StudyCard(
        onClick = if (isCorrect == null) onClick else null,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCorrect == true -> SuccessColor.copy(alpha = 0.2f)
                isCorrect == false && isSelected -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    isCorrect == true -> Icons.Default.CheckCircle
                    isCorrect == false && isSelected -> Icons.Default.Cancel
                    isSelected -> Icons.Default.RadioButtonChecked
                    else -> Icons.Default.RadioButtonUnchecked
                },
                contentDescription = null,
                tint = when {
                    isCorrect == true -> SuccessColor
                    isCorrect == false && isSelected -> MaterialTheme.colorScheme.error
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun QualityButtons(
    onQualitySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Como você se saiu?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        val qualities = listOf(
            1 to "Muito Difícil",
            2 to "Difícil", 
            3 to "Normal",
            4 to "Fácil",
            5 to "Muito Fácil"
        )
        
        qualities.forEach { (quality, text) ->
            QualityButton(
                text = text,
                quality = quality,
                onClick = { onQualitySelected(quality) }
            )
        }
    }
}

@Composable
private fun QualityButton(
    text: String,
    quality: Int,
    onClick: () -> Unit
) {
    val color = when (quality) {
        1, 2 -> MaterialTheme.colorScheme.error
        3 -> MaterialTheme.colorScheme.tertiary
        4, 5 -> SuccessColor
        else -> MaterialTheme.colorScheme.primary
    }
    
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = StudyShapes.buttonShape,
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ExerciseCompletedScreen(
    score: Int,
    total: Int,
    onNavigateToResults: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Exercício Concluído!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Você completou todas as revisões pendentes.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        StudyButton(
            onClick = onNavigateToResults,
            text = "Ver Resultados",
            icon = Icons.Default.Assessment,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun resetForNextCard(
    onResetAnswer: () -> Unit,
    onResetOption: () -> Unit,
    onResetShowAnswer: () -> Unit,
    onResetQualityButtons: () -> Unit
) {
    onResetAnswer()
    onResetOption()
    onResetShowAnswer()
    onResetQualityButtons()
}

@Composable
private fun FlashcardTypeChip(
    type: FlashcardType,
    modifier: Modifier = Modifier
) {
    val (text, icon) = when (type) {
        FlashcardType.FRONT_BACK -> "Frente/Verso" to Icons.Default.FlipToFront
        FlashcardType.CLOZE -> "Lacuna" to Icons.Default.TextFormat
        FlashcardType.TEXT_INPUT -> "Digitação" to Icons.Default.Edit
        FlashcardType.MULTIPLE_CHOICE -> "Múltipla Escolha" to Icons.Default.CheckCircle
    }

    StudyChip(
        text = text,
        leadingIcon = icon,
        selected = false,
        modifier = modifier
    )
}