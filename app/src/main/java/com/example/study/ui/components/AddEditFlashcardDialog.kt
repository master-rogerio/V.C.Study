package com.example.study.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import androidx.compose.material.icons.filled.Audiotrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFlashcardDialog(
    deckId: Long,
    flashcard: Flashcard?,
    onDismiss: () -> Unit,
    onSave: (Flashcard) -> Unit
) {
    var selectedType by remember { mutableStateOf(flashcard?.type ?: FlashcardType.FRONT_BACK) }

    // Estados para multimídia
    var frontImageUrl by remember { mutableStateOf(flashcard?.frontImageUrl ?: "") }
    var backImageUrl by remember { mutableStateOf(flashcard?.backImageUrl ?: "") }
    var frontAudioUrl by remember { mutableStateOf(flashcard?.frontAudioUrl ?: "") }
    var backAudioUrl by remember { mutableStateOf(flashcard?.backAudioUrl ?: "") }

    // Estados dos campos de texto
    var frontText by remember { mutableStateOf(flashcard?.front ?: "") }
    var backText by remember { mutableStateOf(flashcard?.back ?: "") }
    var clozeText by remember { mutableStateOf(flashcard?.clozeText ?: "") }
    var clozeAnswer by remember { mutableStateOf(flashcard?.clozeAnswer ?: "") }
    var mcQuestion by remember { mutableStateOf(flashcard?.front ?: "") }
    var option1 by remember { mutableStateOf(flashcard?.options?.getOrNull(0) ?: "") }
    var option2 by remember { mutableStateOf(flashcard?.options?.getOrNull(1) ?: "") }
    var option3 by remember { mutableStateOf(flashcard?.options?.getOrNull(2) ?: "") }
    var option4 by remember { mutableStateOf(flashcard?.options?.getOrNull(3) ?: "") }
    var correctOption by remember { mutableIntStateOf(flashcard?.correctOptionIndex ?: 0) }

    val context = LocalContext.current
    var mediaTarget by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            when (mediaTarget) {
                "frontImage" -> frontImageUrl = it.toString()
                "backImage" -> backImageUrl = it.toString()
                "frontAudio" -> frontAudioUrl = it.toString()
                "backAudio" -> backAudioUrl = it.toString()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxHeight(0.9f).fillMaxWidth(0.95f), shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(if (flashcard == null) Icons.Default.Add else Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(8.dp))
                    }
                    Text(if (flashcard == null) "Novo Flashcard" else "Editar Flashcard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }

                FlashcardTypeSelector(selectedType, onTypeSelected = { selectedType = it })

                when (selectedType) {
                    FlashcardType.FRONT_BACK -> FrontBackFields(frontText, backText, frontImageUrl, backImageUrl, frontAudioUrl, backAudioUrl, { frontText = it }, { backText = it }, { mediaTarget = "frontImage"; filePickerLauncher.launch("image/*") }, { mediaTarget = "backImage"; filePickerLauncher.launch("image/*") }, { mediaTarget = "frontAudio"; filePickerLauncher.launch("audio/*") }, { mediaTarget = "backAudio"; filePickerLauncher.launch("audio/*") })
                    FlashcardType.TEXT_INPUT -> TextInputFields(frontText, backText, frontImageUrl, frontAudioUrl, { frontText = it }, { backText = it }, { mediaTarget = "frontImage"; filePickerLauncher.launch("image/*") }, { mediaTarget = "frontAudio"; filePickerLauncher.launch("audio/*") })
                    FlashcardType.MULTIPLE_CHOICE -> MultipleChoiceFields(mcQuestion, option1, option2, option3, option4, correctOption, frontImageUrl, frontAudioUrl, { mcQuestion = it }, { option1 = it }, { option2 = it }, { option3 = it }, { option4 = it }, { correctOption = it }, { mediaTarget = "frontImage"; filePickerLauncher.launch("image/*") }, { mediaTarget = "frontAudio"; filePickerLauncher.launch("audio/*") })
                    FlashcardType.CLOZE -> ClozeFields(clozeText, clozeAnswer, frontImageUrl, frontAudioUrl, { clozeText = it }, { clozeAnswer = it }, { mediaTarget = "frontImage"; filePickerLauncher.launch("image/*") }, { mediaTarget = "frontAudio"; filePickerLauncher.launch("audio/*") })
                }

                Spacer(Modifier.weight(1f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val newFlashcard = when (selectedType) {
                                FlashcardType.FRONT_BACK -> Flashcard(flashcard?.id ?: 0, deckId, selectedType, frontText.trim(), backText.trim(), frontImageUrl.takeIf { it.isNotBlank() }, frontAudioUrl.takeIf { it.isNotBlank() }, backImageUrl.takeIf { it.isNotBlank() }, backAudioUrl.takeIf { it.isNotBlank() })
                                FlashcardType.CLOZE -> Flashcard(flashcard?.id ?: 0, deckId, selectedType, clozeText.trim(), "", frontImageUrl.takeIf { it.isNotBlank() }, frontAudioUrl.takeIf { it.isNotBlank() }, null, null, clozeText = clozeText.trim(), clozeAnswer = clozeAnswer.trim())
                                FlashcardType.TEXT_INPUT -> Flashcard(flashcard?.id ?: 0, deckId, selectedType, frontText.trim(), backText.trim(), frontImageUrl.takeIf { it.isNotBlank() }, frontAudioUrl.takeIf { it.isNotBlank() })
                                FlashcardType.MULTIPLE_CHOICE -> Flashcard(flashcard?.id ?: 0, deckId, selectedType, mcQuestion.trim(), "", frontImageUrl.takeIf { it.isNotBlank() }, frontAudioUrl.takeIf { it.isNotBlank() }, null, null, options = listOf(option1.trim(), option2.trim(), option3.trim(), option4.trim()), correctOptionIndex = correctOption)
                            }
                            onSave(newFlashcard)
                        },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)
                    ) { Text("Salvar") }
                }
            }
        }
    }
}

@Composable
private fun FrontBackFields(frontText: String, backText: String, frontImageUrl: String?, backImageUrl: String?, frontAudioUrl: String?, backAudioUrl: String?, onFrontChange: (String) -> Unit, onBackChange: (String) -> Unit, onFrontImageSelect: () -> Unit, onBackImageSelect: () -> Unit, onFrontAudioSelect: () -> Unit, onBackAudioSelect: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = frontText, onValueChange = onFrontChange, label = { Text("Frente") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Row { IconButton(onClick = onFrontImageSelect) { Icon(Icons.Default.Image, "Adicionar Imagem") }; IconButton(onClick = onFrontAudioSelect) { Icon(Icons.Default.Audiotrack, "Adicionar Áudio") } } })
        if (!frontImageUrl.isNullOrBlank()) { AsyncImage(frontImageUrl, "Imagem da Frente", modifier = Modifier.height(100.dp).clip(RoundedCornerShape(8.dp))) }
        if (!frontAudioUrl.isNullOrBlank()) { Text("🎤 Áudio da frente selecionado", style = MaterialTheme.typography.bodySmall) }
        OutlinedTextField(value = backText, onValueChange = onBackChange, label = { Text("Verso") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Row { IconButton(onClick = onBackImageSelect) { Icon(Icons.Default.Image, "Adicionar Imagem") }; IconButton(onClick = onBackAudioSelect) { Icon(Icons.Default.Audiotrack, "Adicionar Áudio") } } })
        if (!backImageUrl.isNullOrBlank()) { AsyncImage(backImageUrl, "Imagem do Verso", modifier = Modifier.height(100.dp).clip(RoundedCornerShape(8.dp))) }
        if (!backAudioUrl.isNullOrBlank()) { Text("🎤 Áudio do verso selecionado", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun TextInputFields(questionText: String, answerText: String, questionImageUrl: String?, questionAudioUrl: String?, onQuestionChange: (String) -> Unit, onAnswerChange: (String) -> Unit, onQuestionImageSelect: () -> Unit, onQuestionAudioSelect: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = questionText, onValueChange = onQuestionChange, label = { Text("Pergunta") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Row { IconButton(onClick = onQuestionImageSelect) { Icon(Icons.Default.Image, "Adicionar Imagem") }; IconButton(onClick = onQuestionAudioSelect) { Icon(Icons.Default.Audiotrack, "Adicionar Áudio") } } })
        if (!questionImageUrl.isNullOrBlank()) { AsyncImage(questionImageUrl, "Imagem da Pergunta", modifier = Modifier.height(100.dp).clip(RoundedCornerShape(8.dp))) }
        if (!questionAudioUrl.isNullOrBlank()) { Text("🎤 Áudio da pergunta selecionado", style = MaterialTheme.typography.bodySmall) }
        OutlinedTextField(value = answerText, onValueChange = onAnswerChange, label = { Text("Resposta esperada") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    }
}

@Composable
private fun MultipleChoiceFields(question: String, option1: String, option2: String, option3: String, option4: String, correctOption: Int, questionImageUrl: String?, questionAudioUrl: String?, onQuestionChange: (String) -> Unit, onOption1Change: (String) -> Unit, onOption2Change: (String) -> Unit, onOption3Change: (String) -> Unit, onOption4Change: (String) -> Unit, onCorrectOptionChange: (Int) -> Unit, onQuestionImageSelect: () -> Unit, onQuestionAudioSelect: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = question, onValueChange = onQuestionChange, label = { Text("Pergunta") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Row { IconButton(onClick = onQuestionImageSelect) { Icon(Icons.Default.Image, "Adicionar Imagem") }; IconButton(onClick = onQuestionAudioSelect) { Icon(Icons.Default.Audiotrack, "Adicionar Áudio") } } })
        if (!questionImageUrl.isNullOrBlank()) { AsyncImage(questionImageUrl, "Imagem da Pergunta", modifier = Modifier.height(100.dp).clip(RoundedCornerShape(8.dp))) }
        if (!questionAudioUrl.isNullOrBlank()) { Text("🎤 Áudio da pergunta selecionado", style = MaterialTheme.typography.bodySmall) }
        Text("Opções de resposta:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        val options = listOf(option1, option2, option3, option4)
        val onChanges = listOf(onOption1Change, onOption2Change, onOption3Change, onOption4Change)
        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RadioButton(selected = correctOption == index, onClick = { onCorrectOptionChange(index) })
                OutlinedTextField(value = option, onValueChange = onChanges[index], label = { Text("Opção ${index + 1}") }, modifier = Modifier.weight(1f), singleLine = true)
            }
        }
    }
}

@Composable
private fun ClozeFields(clozeText: String, clozeAnswer: String, clozeImageUrl: String?, clozeAudioUrl: String?, onClozeTextChange: (String) -> Unit, onClozeAnswerChange: (String) -> Unit, onClozeImageSelect: () -> Unit, onClozeAudioSelect: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = clozeText, onValueChange = onClozeTextChange, label = { Text("Texto com lacuna") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Row { IconButton(onClick = onClozeImageSelect) { Icon(Icons.Default.Image, "Adicionar Imagem") }; IconButton(onClick = onClozeAudioSelect) { Icon(Icons.Default.Audiotrack, "Adicionar Áudio") } } })
        if (!clozeImageUrl.isNullOrBlank()) { AsyncImage(clozeImageUrl, "Imagem do Texto", modifier = Modifier.height(100.dp).clip(RoundedCornerShape(8.dp))) }
        if (!clozeAudioUrl.isNullOrBlank()) { Text("🎤 Áudio do texto selecionado", style = MaterialTheme.typography.bodySmall) }
        OutlinedTextField(value = clozeAnswer, onValueChange = onClozeAnswerChange, label = { Text("Resposta da lacuna") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    }
}

@Composable
private fun TextInputFields(questionText: String, answerText: String, onQuestionChange: (String) -> Unit, onAnswerChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = questionText, onValueChange = onQuestionChange, label = { Text("Pergunta") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = answerText, onValueChange = onAnswerChange, label = { Text("Resposta esperada") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    }
}

@Composable
private fun MultipleChoiceFields(
    question: String, option1: String, option2: String, option3: String, option4: String, correctOption: Int,
    onQuestionChange: (String) -> Unit, onOption1Change: (String) -> Unit, onOption2Change: (String) -> Unit,
    onOption3Change: (String) -> Unit, onOption4Change: (String) -> Unit, onCorrectOptionChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = question, onValueChange = onQuestionChange, label = { Text("Pergunta") }, modifier = Modifier.fillMaxWidth())
        Text("Opções de resposta:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        val options = listOf(option1, option2, option3, option4)
        val onChanges = listOf(onOption1Change, onOption2Change, onOption3Change, onOption4Change)
        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RadioButton(selected = correctOption == index, onClick = { onCorrectOptionChange(index) })
                OutlinedTextField(value = option, onValueChange = onChanges[index], label = { Text("Opção ${index + 1}") }, modifier = Modifier.weight(1f), singleLine = true)
            }
        }
    }
}