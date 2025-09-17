package com.example.study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.ui.components.*
import com.example.study.ui.theme.SuccessColor
import com.example.study.ui.view.FlashcardViewModel

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
        val currentTime = System.currentTimeMillis()
        val filtered = flashcards.filter { it.nextReviewDate == null || it.nextReviewDate.time <= currentTime }
        if (filtered.isEmpty() && flashcards.isNotEmpty()) flashcards else filtered
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isShowingAnswer by remember { mutableStateOf(false) }
    var exerciseCompleted by remember { mutableStateOf(false) }
    var userAnswer by remember { mutableStateOf("") }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var showQualityButtons by remember { mutableStateOf(false) }

    val currentFlashcard = dueFlashcards.getOrNull(currentIndex)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Exercício", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(deckName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Voltar") } },
                actions = { Text("${currentIndex + 1}/${dueFlashcards.size}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 16.dp)) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                exerciseCompleted -> ExerciseCompletedScreen(score, dueFlashcards.size) { onNavigateToResults(score, dueFlashcards.size) }
                currentFlashcard != null -> {
                    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        StudyProgressBar((currentIndex + 1).toFloat() / dueFlashcards.size, showPercentage = false)
                        FlashcardExerciseContent(
                            flashcard = currentFlashcard,
                            isShowingAnswer = isShowingAnswer,
                            userAnswer = userAnswer,
                            onUserAnswerChange = { userAnswer = it },
                            selectedOption = selectedOption,
                            onOptionSelected = { selectedOption = it },
                            showQualityButtons = showQualityButtons,
                            onRevealAnswer = { isShowingAnswer = true; showQualityButtons = currentFlashcard.type == FlashcardType.FRONT_BACK },
                            onQualitySelected = { quality ->
                                val isCorrect = when (currentFlashcard.type) {
                                    FlashcardType.FRONT_BACK -> quality >= 3
                                    FlashcardType.TEXT_INPUT -> userAnswer.trim().equals(currentFlashcard.back.trim(), ignoreCase = true)
                                    FlashcardType.MULTIPLE_CHOICE -> selectedOption == currentFlashcard.correctOptionIndex
                                    FlashcardType.CLOZE -> userAnswer.trim().equals(currentFlashcard.clozeAnswer?.trim() ?: "", ignoreCase = true)
                                }
                                if (isCorrect) score++
                                viewModel.update(viewModel.calculateNextReview(currentFlashcard, quality))
                                if (currentIndex < dueFlashcards.size - 1) {
                                    currentIndex++
                                    userAnswer = ""
                                    selectedOption = -1
                                    isShowingAnswer = false
                                    showQualityButtons = false
                                } else {
                                    exerciseCompleted = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                else -> StudyEmptyState("Nenhum flashcard para revisar", "Todos os flashcards deste deck estão em dia!", Icons.Default.CheckCircle, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun FlashcardExerciseContent(flashcard: Flashcard, isShowingAnswer: Boolean, userAnswer: String, onUserAnswerChange: (String) -> Unit, selectedOption: Int, onOptionSelected: (Int) -> Unit, showQualityButtons: Boolean, onRevealAnswer: () -> Unit, onQualitySelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        StudyCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                StudyChip(text = flashcard.type.name, selected = false)
                flashcard.frontImageUrl?.let { AsyncImage(it, "Imagem da pergunta", modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) }
                flashcard.frontAudioUrl?.let { AudioPlayer(it) }
                Text(when (flashcard.type) {
                    FlashcardType.CLOZE -> flashcard.clozeText?.replace("___", "______") ?: flashcard.front
                    else -> flashcard.front
                }, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Start)
            }
        }

        when (flashcard.type) {
            FlashcardType.FRONT_BACK -> {
                if (!isShowingAnswer) StudyButton(onRevealAnswer, text = "Revelar Resposta", icon = Icons.Default.Visibility, modifier = Modifier.fillMaxWidth())
                else AnswerCard(flashcard.back, null, flashcard.backImageUrl, flashcard.backAudioUrl)
            }
            FlashcardType.TEXT_INPUT, FlashcardType.CLOZE -> {
                OutlinedTextField(userAnswer, onUserAnswerChange, label = { Text("Digite sua resposta") }, modifier = Modifier.fillMaxWidth())
                if (!isShowingAnswer) StudyButton(onRevealAnswer, text = "Verificar Resposta", icon = Icons.Default.Check, modifier = Modifier.fillMaxWidth())
                else {
                    val expected = flashcard.clozeAnswer ?: flashcard.back
                    AnswerCard(expected, userAnswer.trim().equals(expected.trim(), ignoreCase = true), flashcard.backImageUrl, flashcard.backAudioUrl)
                }
            }
            FlashcardType.MULTIPLE_CHOICE -> {
                flashcard.options?.forEachIndexed { index, option ->
                    OptionCard(option, selectedOption == index, if (isShowingAnswer) index == flashcard.correctOptionIndex else null, onClick = { if (!isShowingAnswer) onOptionSelected(index) })
                }
                if (selectedOption != -1 && !isShowingAnswer) StudyButton(onRevealAnswer, text = "Verificar Resposta", icon = Icons.Default.Check, modifier = Modifier.fillMaxWidth())
            }
        }

        if (showQualityButtons || (isShowingAnswer && flashcard.type != FlashcardType.FRONT_BACK)) {
            QualityButtons(onQualitySelected, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun AnswerCard(answer: String, isCorrect: Boolean?, imageUrl: String?, audioUrl: String?, modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = when (isCorrect) { true -> MaterialTheme.colorScheme.primaryContainer; false -> MaterialTheme.colorScheme.errorContainer; null -> MaterialTheme.colorScheme.surfaceVariant })) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCorrect != null) {
                    Icon(if (isCorrect) Icons.Default.Check else Icons.Default.Close, null, tint = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                }
                Text(when (isCorrect) { true -> "Correto!"; false -> "Incorreto"; null -> "Resposta:" }, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
            imageUrl?.let { AsyncImage(it, "Imagem da resposta", modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) }
            audioUrl?.let { AudioPlayer(it) }
            Text(answer, style = MaterialTheme.typography.bodyLarge)
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Como você se saiu?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            val qualities = listOf(
                1 to Pair("Muito Difícil", "😰"),
                2 to Pair("Difícil", "😅"), 
                3 to Pair("Normal", "😐"),
                4 to Pair("Fácil", "😊"),
                5 to Pair("Muito Fácil", "😎")
            )
            
            qualities.forEach { (quality, textEmoji) ->
                QualityButton(
                    text = textEmoji.first,
                    emoji = textEmoji.second,
                    quality = quality,
                    onClick = { onQualitySelected(quality) }
                )
            }
        }
    }
}

@Composable
private fun QualityButton(
    text: String,
    emoji: String,
    quality: Int,
    onClick: () -> Unit
) {
    val containerColor = when (quality) {
        1, 2 -> MaterialTheme.colorScheme.errorContainer
        3 -> MaterialTheme.colorScheme.tertiaryContainer
        4, 5 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    
    val contentColor = when (quality) {
        1, 2 -> MaterialTheme.colorScheme.onErrorContainer
        3 -> MaterialTheme.colorScheme.onTertiaryContainer
        4, 5 -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }
    
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
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
fun FlashcardTypeChip(
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