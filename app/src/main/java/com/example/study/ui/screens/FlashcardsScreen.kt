package com.example.study.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.ui.components.*
import com.example.study.ui.view.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    deckId: Long,
    deckName: String,
    onNavigateBack: () -> Unit,
    onNavigateToExercise: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlashcardViewModel = viewModel()
) {
    val flashcards by viewModel.getFlashcardsForDeckByCreation(deckId).collectAsState(initial = emptyList())
    var showAddFlashcardDialog by remember { mutableStateOf(false) }
    var flashcardToEdit by remember { mutableStateOf<Flashcard?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Flashcard?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFlashcards = remember(flashcards, searchQuery) {
        if (searchQuery.isBlank()) flashcards
        else flashcards.filter { it.front.contains(searchQuery, ignoreCase = true) || it.back.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = deckName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "${flashcards.size} flashcards", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                actions = {
                    if (flashcards.isNotEmpty()) IconButton(onClick = onNavigateToExercise) { Icon(Icons.Default.PlayArrow, "Iniciar exercício") }
                    IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.MoreVert, "Menu") }
                }
            )
        },
        floatingActionButton = { StudyFAB(onClick = { showAddFlashcardDialog = true }, text = "Novo Card") }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (flashcards.isEmpty()) {
                StudyEmptyState("Nenhum flashcard criado", "Adicione flashcards para começar a estudar este deck", Icons.Default.Quiz, actionText = "Criar Flashcard", onActionClick = { showAddFlashcardDialog = true }, modifier = Modifier.fillMaxSize().padding(16.dp))
            } else {
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                if (filteredFlashcards.isEmpty()) {
                    StudyEmptyState("Nenhum flashcard encontrado", "Tente ajustar sua busca", Icons.Default.SearchOff, modifier = Modifier.fillMaxSize().padding(16.dp))
                } else {
                    FlashcardsList(
                        flashcards = filteredFlashcards,
                        onEditClick = { flashcardToEdit = it },
                        onDeleteClick = { showDeleteDialog = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showAddFlashcardDialog || flashcardToEdit != null) {
        AddEditFlashcardDialog(deckId, flashcardToEdit, { showAddFlashcardDialog = false; flashcardToEdit = null }) { flashcard ->
            if (flashcardToEdit != null) viewModel.update(flashcard) else viewModel.insert(flashcard)
            showAddFlashcardDialog = false
            flashcardToEdit = null
        }
    }

    showDeleteDialog?.let {
        DeleteFlashcardDialog(onConfirm = { viewModel.delete(it); showDeleteDialog = null }, onDismiss = { showDeleteDialog = null })
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(value = query, onValueChange = onQueryChange, modifier = modifier.fillMaxWidth(), placeholder = { Text("Buscar flashcards...") }, leadingIcon = { Icon(Icons.Default.Search, "Buscar") }, trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Clear, "Limpar") } }, singleLine = true)
}

@Composable
private fun FlashcardsList(flashcards: List<Flashcard>, onEditClick: (Flashcard) -> Unit, onDeleteClick: (Flashcard) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(flashcards, key = { it.id }) { flashcard ->
            FlashcardItem(flashcard, onEditClick = { onEditClick(flashcard) }, onDeleteClick = { onDeleteClick(flashcard) })
        }
    }
}

@Composable
private fun FlashcardItem(flashcard: Flashcard, onEditClick: () -> Unit, onDeleteClick: () -> Unit, modifier: Modifier = Modifier) {
    var showMenu by remember { mutableStateOf(false) }
    var isFlipped by remember { mutableStateOf(false) }

    StudyCard(onClick = { isFlipped = !isFlipped }, modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                StudyChip(text = flashcard.type.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }, selected = false)
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.MoreVert, "Menu", modifier = Modifier.size(16.dp)) }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem({ Text("Editar") }, onClick = { onEditClick(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Edit, null) })
                        DropdownMenuItem({ Text("Excluir") }, onClick = { onDeleteClick(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Delete, null) })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            AnimatedContent(targetState = isFlipped, transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }, label = "flip") {
                Column { if (!it) FlashcardFront(flashcard) else FlashcardBack(flashcard) }
            }
        }
    }
}

@Composable
private fun FlashcardFront(flashcard: Flashcard) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        flashcard.frontImageUrl?.let { AsyncImage(it, "Imagem da frente", modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) }
        flashcard.frontAudioUrl?.let { AudioPlayer(it) }
        Text(when (flashcard.type) {
            FlashcardType.CLOZE -> flashcard.clozeText?.replace("___", "______") ?: flashcard.front
            else -> flashcard.front
        }, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun FlashcardBack(flashcard: Flashcard) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        flashcard.backImageUrl?.let { AsyncImage(it, "Imagem do verso", modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) }
        flashcard.backAudioUrl?.let { AudioPlayer(it) }
        when (flashcard.type) {
            FlashcardType.FRONT_BACK, FlashcardType.TEXT_INPUT -> Text(flashcard.back, style = MaterialTheme.typography.bodyLarge)
            FlashcardType.CLOZE -> Text(flashcard.clozeAnswer ?: "", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            FlashcardType.MULTIPLE_CHOICE -> {
                flashcard.options?.forEachIndexed { index, option ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        val isCorrect = index == flashcard.correctOptionIndex
                        Icon(if (isCorrect) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, null, tint = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(option, style = MaterialTheme.typography.bodyMedium, color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontWeight = if (isCorrect) FontWeight.Medium else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteFlashcardDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismiss, icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) }, title = { Text("Excluir flashcard") }, text = { Text("Tem certeza que deseja excluir este flashcard? Esta ação não pode ser desfeita.") }, confirmButton = { StudyButton(onConfirm, text = "Excluir") }, dismissButton = { StudyButton(onDismiss, text = "Cancelar", variant = ButtonVariant.Secondary) })
}