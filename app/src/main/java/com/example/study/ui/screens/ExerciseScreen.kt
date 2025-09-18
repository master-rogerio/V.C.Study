package com.example.study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    // Estado para rastrear respostas corretas de forma mais confiável
    var correctAnswers by remember { mutableStateOf(0) }
    
    // Para Exercício Misto (deckId = -1L), buscar flashcards de todos os decks
    val flashcards by if (deckId == -1L) {
        println("DEBUG: Exercício Misto - buscando flashcards de todos os decks")
        viewModel.allFlashcardsByReview.collectAsState(initial = emptyList())
    } else {
        println("DEBUG: Exercício normal - buscando flashcards do deck $deckId")
        viewModel.getFlashcardsForDeckByCreation(deckId).collectAsState(initial = emptyList())
    }
    val dueFlashcards = remember(flashcards) {
        println("DEBUG: Total de flashcards encontrados: ${flashcards.size}")
        val currentTime = System.currentTimeMillis()
        
        // Lógica melhorada para flashcards pendentes
        val filtered = flashcards.filter { flashcard ->
            val isPending = flashcard.nextReviewDate == null || 
                           flashcard.nextReviewDate.time <= currentTime
            if (isPending) {
                println("DEBUG: Flashcard ${flashcard.id} está pendente - nextReview: ${flashcard.nextReviewDate?.time}, current: $currentTime")
            }
            isPending
        }
        println("DEBUG: Flashcards pendentes após filtro: ${filtered.size}")
        
        // Para Exercício Misto, usar todos os flashcards disponíveis
        val result = if (deckId == -1L) {
            // Exercício Misto: usar todos os flashcards disponíveis
            if (flashcards.isNotEmpty()) {
                println("DEBUG: Exercício Misto - usando todos os flashcards: ${flashcards.size}")
                flashcards.shuffled()
            } else {
                println("DEBUG: Exercício Misto - nenhum flashcard encontrado")
                emptyList()
            }
        } else {
            // Exercício normal: usar flashcards pendentes ou todos se não há pendentes
            if (filtered.isNotEmpty()) {
                println("DEBUG: Exercício normal - usando flashcards pendentes: ${filtered.size}")
                filtered.shuffled()
            } else if (flashcards.isNotEmpty()) {
                println("DEBUG: Exercício normal - nenhum pendente, usando todos: ${flashcards.size}")
                flashcards.shuffled()
            } else {
                println("DEBUG: Exercício normal - nenhum flashcard encontrado")
                emptyList()
            }
        }
        println("DEBUG: Flashcards finais para exercício: ${result.size}")
        result
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    
    // Debug: Log inicial do score
    LaunchedEffect(Unit) {
        println("DEBUG: Score inicializado em: $score")
    }
    var isShowingAnswer by remember { mutableStateOf(false) }
    var exerciseCompleted by remember { mutableStateOf(false) }
    var userAnswer by remember { mutableStateOf("") }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var showQualityButtons by remember { mutableStateOf(false) }

    val currentFlashcard = if (dueFlashcards.isNotEmpty() && currentIndex < dueFlashcards.size) {
        dueFlashcards[currentIndex]
    } else null

    // Função centralizada para processar a resposta e avançar
    fun handleNextCard(flashcard: Flashcard, quality: Int, isCorrect: Boolean) {
        // Debug: Log da resposta
        println("DEBUG: Flashcard ${currentIndex + 1}/${dueFlashcards.size}")
        println("DEBUG: Tipo: ${flashcard.type}")
        println("DEBUG: isCorrect: $isCorrect")
        println("DEBUG: Score antes: $score, CorrectAnswers antes: $correctAnswers")
        
        if (isCorrect) {
            score++
            correctAnswers++
            println("DEBUG: Acerto! Score: $score, CorrectAnswers: $correctAnswers")
        } else {
            println("DEBUG: Erro! Score: $score, CorrectAnswers: $correctAnswers")
        }

        val updatedFlashcard = viewModel.calculateNextReview(flashcard, quality)
        viewModel.update(updatedFlashcard)

        if (currentIndex < dueFlashcards.size - 1) {
            currentIndex++
            resetForNextCard(
                onResetAnswer = { userAnswer = "" },
                onResetOption = { selectedOption = -1 },
                onResetShowAnswer = { isShowingAnswer = false },
                onResetQualityButtons = { showQualityButtons = false }
            )
        } else {
            println("DEBUG: Exercício concluído! Score final: $score, CorrectAnswers: $correctAnswers de ${dueFlashcards.size}")
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
                    if (dueFlashcards.isNotEmpty() && currentFlashcard != null) {
                        Text(
                            text = "${currentIndex + 1}/${dueFlashcards.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
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
                        score = correctAnswers,
                        total = dueFlashcards.size,
                        onNavigateToResults = { onNavigateToResults(correctAnswers, dueFlashcards.size) }
                    )
                }

                currentFlashcard != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StudyProgressBar(
                            progress = (currentIndex + 1).toFloat() / dueFlashcards.size,
                            showPercentage = false
                        )

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
                                // Mostra botões de qualidade apenas para o tipo FRENTE/VERSO
                                showQualityButtons = currentFlashcard.type == FlashcardType.FRONT_BACK
                            },
                            onQualitySelected = { quality ->
                                // Chamado apenas pelos botões de qualidade (FRENTE/VERSO)
                                val isCorrect = quality >= 3
                                handleNextCard(currentFlashcard, quality, isCorrect)
                            },
                            onContinue = {
                                // Chamado pelo botão "Continuar" para outros tipos de card
                                val isCorrect = when (currentFlashcard.type) {
                                    FlashcardType.TEXT_INPUT -> {
                                        val userAnswerTrimmed = userAnswer.trim()
                                        val correctAnswerTrimmed = currentFlashcard.back?.trim() ?: ""
                                        val result = userAnswerTrimmed.isNotEmpty() && 
                                                   correctAnswerTrimmed.isNotEmpty() && 
                                                   userAnswerTrimmed.equals(correctAnswerTrimmed, ignoreCase = true)
                                        println("DEBUG TEXT_INPUT: User='$userAnswerTrimmed', Correct='$correctAnswerTrimmed', Result=$result")
                                        result
                                    }
                                    FlashcardType.MULTIPLE_CHOICE -> {
                                        val result = selectedOption != -1 && selectedOption == currentFlashcard.correctOptionIndex
                                        println("DEBUG MULTIPLE_CHOICE: Selected=$selectedOption, Correct=${currentFlashcard.correctOptionIndex}, Result=$result")
                                        result
                                    }
                                    FlashcardType.CLOZE -> {
                                        val userAnswerTrimmed = userAnswer.trim()
                                        val correctAnswerTrimmed = currentFlashcard.clozeAnswer?.trim() ?: ""
                                        val result = userAnswerTrimmed.isNotEmpty() && 
                                                   correctAnswerTrimmed.isNotEmpty() && 
                                                   userAnswerTrimmed.equals(correctAnswerTrimmed, ignoreCase = true)
                                        println("DEBUG CLOZE: User='$userAnswerTrimmed', Correct='$correctAnswerTrimmed', Result=$result")
                                        result
                                    }
                                    else -> false // Não deve acontecer
                                }
                                // Qualidade automática: 4 (Fácil) se acertou, 1 (Muito Difícil) se errou.
                                val quality = if (isCorrect) 4 else 1
                                println("DEBUG: Qualidade calculada: $quality")
                                handleNextCard(currentFlashcard, quality, isCorrect)
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
    onContinue: () -> Unit, // Novo callback
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()), // Adicionado para evitar overflow
        verticalArrangement = Arrangement.spacedBy(16.dp) // Reduzido para melhor encaixe
    ) {
        StudyCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StudyChip(text = flashcard.type.name, selected = false)
                flashcard.frontImageUrl?.let { AsyncImage(it, "Imagem da pergunta", modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) }
                flashcard.frontAudioUrl?.let { AudioPlayer(it) }
                Text(when (flashcard.type) {
                    FlashcardType.CLOZE -> flashcard.clozeText?.replace("___", "______") ?: flashcard.front
                    else -> flashcard.front
                }, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Start)
            }
        }

        // Renderiza os inputs e botões de acordo com o tipo
        when (flashcard.type) {
            FlashcardType.FRONT_BACK -> {
                if (!isShowingAnswer) {
                    StudyButton(onRevealAnswer, text = "Revelar Resposta", icon = Icons.Default.Visibility, modifier = Modifier.fillMaxWidth())
                } else {
                    AnswerCard(flashcard.back, null, flashcard.backImageUrl, flashcard.backAudioUrl)
                }
            }
            FlashcardType.TEXT_INPUT, FlashcardType.CLOZE -> {
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = onUserAnswerChange,
                    label = { Text("Digite sua resposta") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isShowingAnswer
                )
                if (!isShowingAnswer) {
                    StudyButton(onRevealAnswer, text = "Verificar Resposta", icon = Icons.Default.Check, modifier = Modifier.fillMaxWidth())
                } else {
                    val expected = flashcard.clozeAnswer ?: flashcard.back
                    AnswerCard(expected, userAnswer.trim().equals(expected.trim(), ignoreCase = true), flashcard.backImageUrl, flashcard.backAudioUrl)
                }
            }
            FlashcardType.MULTIPLE_CHOICE -> {
                flashcard.options?.forEachIndexed { index, option ->
                    OptionCard(option, selectedOption == index, if (isShowingAnswer) index == flashcard.correctOptionIndex else null, onClick = { if (!isShowingAnswer) onOptionSelected(index) })
                }
                if (selectedOption != -1 && !isShowingAnswer) {
                    StudyButton(onRevealAnswer, text = "Verificar Resposta", icon = Icons.Default.Check, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Empurra os botões para baixo

        // Mostra botões de qualidade APENAS para FRENTE/VERSO
        if (showQualityButtons) {
            QualityButtons(
                onQualitySelected = onQualitySelected,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Mostra botão "Continuar" para os OUTROS tipos após a resposta ser revelada
        if (isShowingAnswer && flashcard.type != FlashcardType.FRONT_BACK) {
            StudyButton(
                onClick = onContinue,
                text = "Continuar",
                icon = Icons.Default.ArrowForward,
                modifier = Modifier.fillMaxWidth()
            )
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
                isCorrect == true -> SuccessColor
                isCorrect == false && isSelected -> MaterialTheme.colorScheme.errorContainer
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
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
                tint = contentColor,
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

//@Composable
//fun FlashcardTypeChip(
//    type: FlashcardType,
//    modifier: Modifier = Modifier
//) {
//    val (text, icon) = when (type) {
//        FlashcardType.FRONT_BACK -> "Frente/Verso" to Icons.Default.FlipToFront
//        FlashcardType.CLOZE -> "Lacuna" to Icons.Default.TextFormat
//        FlashcardType.TEXT_INPUT -> "Digitação" to Icons.Default.Edit
//        FlashcardType.MULTIPLE_CHOICE -> "Múltipla Escolha" to Icons.Default.CheckCircle
//    }
//
//    StudyChip(
//        text = text,
//        leadingIcon = icon,
//        selected = false,
//        modifier = modifier
//    )
//}