package com.example.study.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.ui.theme.StudyShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFlashcardDialog(
    deckId: Long,
    flashcard: Flashcard?,
    onDismiss: () -> Unit,
    onSave: (Flashcard) -> Unit
) {
    var selectedType by remember { 
        mutableStateOf(flashcard?.type ?: FlashcardType.FRONT_BACK) 
    }
    
    // Front/Back fields
    var frontText by remember { mutableStateOf(flashcard?.front ?: "") }
    var backText by remember { mutableStateOf(flashcard?.back ?: "") }
    
    // Cloze fields
    var clozeText by remember { mutableStateOf(flashcard?.clozeText ?: "") }
    var clozeAnswer by remember { mutableStateOf(flashcard?.clozeAnswer ?: "") }
    
    // Multiple choice fields
    var mcQuestion by remember { mutableStateOf(flashcard?.front ?: "") }
    var option1 by remember { mutableStateOf(flashcard?.options?.getOrNull(0) ?: "") }
    var option2 by remember { mutableStateOf(flashcard?.options?.getOrNull(1) ?: "") }
    var option3 by remember { mutableStateOf(flashcard?.options?.getOrNull(2) ?: "") }
    var option4 by remember { mutableStateOf(flashcard?.options?.getOrNull(3) ?: "") }
    var correctOption by remember { mutableIntStateOf(flashcard?.correctOptionIndex ?: 0) }

    val isValid = when (selectedType) {
        FlashcardType.FRONT_BACK -> frontText.isNotBlank() && backText.isNotBlank()
        FlashcardType.CLOZE -> clozeText.isNotBlank() && clozeAnswer.isNotBlank()
        FlashcardType.TEXT_INPUT -> frontText.isNotBlank() && backText.isNotBlank()
        FlashcardType.MULTIPLE_CHOICE -> mcQuestion.isNotBlank() && 
                listOf(option1, option2, option3, option4).all { it.isNotBlank() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = StudyShapes.dialogShape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Text(
                    text = if (flashcard == null) "Novo Flashcard" else "Editar Flashcard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Type Selector
                FlashcardTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Content based on type
                when (selectedType) {
                    FlashcardType.FRONT_BACK -> {
                        FrontBackFields(
                            frontText = frontText,
                            backText = backText,
                            onFrontChange = { frontText = it },
                            onBackChange = { backText = it }
                        )
                    }
                    
                    FlashcardType.CLOZE -> {
                        ClozeFields(
                            clozeText = clozeText,
                            clozeAnswer = clozeAnswer,
                            onClozeTextChange = { clozeText = it },
                            onClozeAnswerChange = { clozeAnswer = it }
                        )
                    }
                    
                    FlashcardType.TEXT_INPUT -> {
                        TextInputFields(
                            questionText = frontText,
                            answerText = backText,
                            onQuestionChange = { frontText = it },
                            onAnswerChange = { backText = it }
                        )
                    }
                    
                    FlashcardType.MULTIPLE_CHOICE -> {
                        MultipleChoiceFields(
                            question = mcQuestion,
                            option1 = option1,
                            option2 = option2,
                            option3 = option3,
                            option4 = option4,
                            correctOption = correctOption,
                            onQuestionChange = { mcQuestion = it },
                            onOption1Change = { option1 = it },
                            onOption2Change = { option2 = it },
                            onOption3Change = { option3 = it },
                            onOption4Change = { option4 = it },
                            onCorrectOptionChange = { correctOption = it }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudyButton(
                        onClick = onDismiss,
                        text = "Cancelar",
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    StudyButton(
                        onClick = {
                            val newFlashcard = when (selectedType) {
                                FlashcardType.FRONT_BACK -> Flashcard(
                                    id = flashcard?.id ?: 0,
                                    deckId = deckId,
                                    type = FlashcardType.FRONT_BACK,
                                    front = frontText.trim(),
                                    back = backText.trim()
                                )
                                
                                FlashcardType.CLOZE -> Flashcard(
                                    id = flashcard?.id ?: 0,
                                    deckId = deckId,
                                    type = FlashcardType.CLOZE,
                                    front = clozeText.trim(),
                                    back = "",
                                    clozeText = clozeText.trim(),
                                    clozeAnswer = clozeAnswer.trim()
                                )
                                
                                FlashcardType.TEXT_INPUT -> Flashcard(
                                    id = flashcard?.id ?: 0,
                                    deckId = deckId,
                                    type = FlashcardType.TEXT_INPUT,
                                    front = frontText.trim(),
                                    back = backText.trim()
                                )
                                
                                FlashcardType.MULTIPLE_CHOICE -> Flashcard(
                                    id = flashcard?.id ?: 0,
                                    deckId = deckId,
                                    type = FlashcardType.MULTIPLE_CHOICE,
                                    front = mcQuestion.trim(),
                                    back = "",
                                    options = listOf(
                                        option1.trim(),
                                        option2.trim(),
                                        option3.trim(),
                                        option4.trim()
                                    ),
                                    correctOptionIndex = correctOption
                                )
                            }
                            onSave(newFlashcard)
                        },
                        text = "Salvar",
                        enabled = isValid,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardTypeSelector(
    selectedType: FlashcardType,
    onTypeSelected: (FlashcardType) -> Unit
) {
    Column {
        Text(
            text = "Tipo de Flashcard",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FlashcardTypeChip(
                type = FlashcardType.FRONT_BACK,
                isSelected = selectedType == FlashcardType.FRONT_BACK,
                onClick = { onTypeSelected(FlashcardType.FRONT_BACK) },
                modifier = Modifier.weight(1f)
            )
            
            FlashcardTypeChip(
                type = FlashcardType.CLOZE,
                isSelected = selectedType == FlashcardType.CLOZE,
                onClick = { onTypeSelected(FlashcardType.CLOZE) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FlashcardTypeChip(
                type = FlashcardType.TEXT_INPUT,
                isSelected = selectedType == FlashcardType.TEXT_INPUT,
                onClick = { onTypeSelected(FlashcardType.TEXT_INPUT) },
                modifier = Modifier.weight(1f)
            )
            
            FlashcardTypeChip(
                type = FlashcardType.MULTIPLE_CHOICE,
                isSelected = selectedType == FlashcardType.MULTIPLE_CHOICE,
                onClick = { onTypeSelected(FlashcardType.MULTIPLE_CHOICE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FlashcardTypeChip(
    type: FlashcardType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (text, icon) = when (type) {
        FlashcardType.FRONT_BACK -> "Frente/Verso" to Icons.Default.FlipToFront
        FlashcardType.CLOZE -> "Lacuna" to Icons.Default.TextFormat
        FlashcardType.TEXT_INPUT -> "Digitação" to Icons.Default.Edit
        FlashcardType.MULTIPLE_CHOICE -> "M. Escolha" to Icons.Default.CheckCircle
    }
    
    FilterChip(
        onClick = onClick,
        label = { 
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall
            ) 
        },
        selected = isSelected,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun FrontBackFields(
    frontText: String,
    backText: String,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = frontText,
            onValueChange = onFrontChange,
            label = { Text("Frente") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        OutlinedTextField(
            value = backText,
            onValueChange = onBackChange,
            label = { Text("Verso") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
private fun ClozeFields(
    clozeText: String,
    clozeAnswer: String,
    onClozeTextChange: (String) -> Unit,
    onClozeAnswerChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = clozeText,
            onValueChange = onClozeTextChange,
            label = { Text("Texto com lacuna") },
            placeholder = { Text("Use ___ para marcar a lacuna") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        OutlinedTextField(
            value = clozeAnswer,
            onValueChange = onClozeAnswerChange,
            label = { Text("Resposta da lacuna") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Text(
            text = "💡 Dica: Use ___ no texto para marcar onde ficará a lacuna",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TextInputFields(
    questionText: String,
    answerText: String,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = questionText,
            onValueChange = onQuestionChange,
            label = { Text("Pergunta") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        OutlinedTextField(
            value = answerText,
            onValueChange = onAnswerChange,
            label = { Text("Resposta esperada") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
private fun MultipleChoiceFields(
    question: String,
    option1: String,
    option2: String,
    option3: String,
    option4: String,
    correctOption: Int,
    onQuestionChange: (String) -> Unit,
    onOption1Change: (String) -> Unit,
    onOption2Change: (String) -> Unit,
    onOption3Change: (String) -> Unit,
    onOption4Change: (String) -> Unit,
    onCorrectOptionChange: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = onQuestionChange,
            label = { Text("Pergunta") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        Text(
            text = "Opções de resposta:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
        
        repeat(4) { index ->
            val option = when (index) {
                0 -> option1
                1 -> option2
                2 -> option3
                else -> option4
            }
            
            val onOptionChange = when (index) {
                0 -> onOption1Change
                1 -> onOption2Change
                2 -> onOption3Change
                else -> onOption4Change
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = correctOption == index,
                    onClick = { onCorrectOptionChange(index) }
                )
                
                OutlinedTextField(
                    value = option,
                    onValueChange = onOptionChange,
                    label = { Text("Opção ${index + 1}") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
        
        Text(
            text = "💡 Dica: Marque o botão ao lado da resposta correta",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}