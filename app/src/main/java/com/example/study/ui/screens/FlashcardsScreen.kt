package com.example.study.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.study.AIQuizActivity
import coil.compose.AsyncImage
import com.example.study.data.Flashcard
import com.example.study.data.FlashcardType
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
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
    val context = LocalContext.current // Obter o contexto para iniciar a Activity
    val flashcards by viewModel.getFlashcardsForDeckByCreation(deckId).collectAsState(initial = emptyList())
    var showAddFlashcardDialog by remember { mutableStateOf(false) }
    var flashcardToEdit by remember { mutableStateOf<Flashcard?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Flashcard?>(null) }
    var showQualityDialog by remember { mutableStateOf<Flashcard?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFlashcards = remember(flashcards, searchQuery) {
        if (searchQuery.isBlank()) {
            flashcards
        } else {
            flashcards.filter { flashcard ->
                flashcard.front.contains(searchQuery, ignoreCase = true) ||
                        flashcard.back.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = deckName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${flashcards.size} flashcards",
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
                    if (flashcards.isNotEmpty()) {
                        // BOTÃO MODIFICADO PARA INICIAR O QUIZ COM IA
                        IconButton(onClick = {
                            val intent = Intent(context, AIQuizActivity::class.java).apply {
                                putExtra("quizTheme", deckName) // Usa o nome do deck como tema
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Iniciar Quiz com IA"
                            )
                        }
                    }
                    IconButton(onClick = { /* TODO: Menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            StudyFAB(
                onClick = { showAddFlashcardDialog = true },
                icon = Icons.Default.Add,
                expanded = true,
                text = "Novo Card",
                contentDescription = "Adicionar flashcard"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (flashcards.isNotEmpty()) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                StudyProgressCard(
                    flashcards = flashcards,
                    onStartStudy = { // Ação do botão de estudo também foi atualizada
                        val intent = Intent(context, AIQuizActivity::class.java).apply {
                            putExtra("quizTheme", deckName)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filteredFlashcards.isEmpty()) {
                if (flashcards.isEmpty()) {
                    StudyEmptyState(
                        title = "Nenhum flashcard criado",
                        subtitle = "Adicione flashcards para começar a estudar este deck",
                        icon = Icons.Default.Quiz,
                        actionText = "Criar Flashcard",
                        onActionClick = { showAddFlashcardDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                } else {
                    StudyEmptyState(
                        title = "Nenhum flashcard encontrado",
                        subtitle = "Tente ajustar sua busca ou criar um novo flashcard",
                        icon = Icons.Default.SearchOff,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            } else {
                FlashcardsList(
                    flashcards = filteredFlashcards,
                    onFlashcardClick = { flashcard -> showQualityDialog = flashcard },
                    onEditClick = { flashcard -> flashcardToEdit = flashcard },
                    onDeleteClick = { flashcard -> showDeleteDialog = flashcard },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Add/Edit Flashcard Dialog
    if (showAddFlashcardDialog || flashcardToEdit != null) {
        AddEditFlashcardDialog(
            deckId = deckId,
            flashcard = flashcardToEdit,
            onDismiss = {
                showAddFlashcardDialog = false
                flashcardToEdit = null
            },
            onSave = { flashcard ->
                if (flashcardToEdit != null) {
                    viewModel.update(flashcard)
                } else {
                    viewModel.insert(flashcard)
                }
                showAddFlashcardDialog = false
                flashcardToEdit = null
            }
        )
    }

    showQualityDialog?.let { flashcard ->
        QualityDialog(
            flashcard = flashcard,
            onQualitySelected = { quality ->
                val updatedFlashcard = viewModel.calculateNextReview(flashcard, quality)
                viewModel.update(updatedFlashcard)
                showQualityDialog = null
            },
            onDismiss = { showQualityDialog = null }
        )
    }

    showDeleteDialog?.let { flashcard ->
        DeleteFlashcardDialog(
            onConfirm = {
                viewModel.delete(flashcard)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
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
        placeholder = { Text("Buscar flashcards...") },
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
private fun StudyProgressCard(
    flashcards: List<Flashcard>,
    onStartStudy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dueFlashcards = flashcards.filter {
        it.nextReviewDate?.time ?: 0 <= System.currentTimeMillis()
    }
    val progress = if (flashcards.isNotEmpty()) {
        (flashcards.size - dueFlashcards.size).toFloat() / flashcards.size
    } else 0f

    StudyCard(
        modifier = modifier
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
                        text = "Progresso do Deck",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${dueFlashcards.size} cards para revisar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (dueFlashcards.isNotEmpty()) {
                    StudyButton(
                        onClick = onStartStudy,
                        text = "Estudar",
                        icon = Icons.Default.PlayArrow
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StudyProgressBar(
                progress = progress,
                showPercentage = true
            )
        }
    }
}

@Composable
private fun FlashcardsList(
    flashcards: List<Flashcard>,
    onFlashcardClick: (Flashcard) -> Unit,
    onEditClick: (Flashcard) -> Unit,
    onDeleteClick: (Flashcard) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(flashcards, key = { it.id }) { flashcard ->
            FlashcardItem(
                flashcard = flashcard,
                onClick = { onFlashcardClick(flashcard) },
                onEditClick = { onEditClick(flashcard) },
                onDeleteClick = { onDeleteClick(flashcard) }
            )
        }
    }
}

@Composable
private fun FlashcardItem(
    flashcard: Flashcard,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var isFlipped by remember { mutableStateOf(false) }

    StudyCard(
        onClick = { isFlipped = !isFlipped },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                FlashcardTypeChip(type = flashcard.type)

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Revisar") },
                            onClick = {
                                onClick()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Quiz, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                onEditClick()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Excluir") },
                            onClick = {
                                onDeleteClick()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = isFlipped,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "flashcard_flip"
            ) { flipped ->
                if (!flipped) {
                    FlashcardFront(flashcard = flashcard)
                } else {
                    FlashcardBack(flashcard = flashcard)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFlipped) "Verso" else "Frente",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                StudyButton(
                    onClick = onClick,
                    text = "Revisar",
                    variant = ButtonVariant.Secondary
                )
            }
        }
    }
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

@Composable
private fun FlashcardFront(flashcard: Flashcard) {
    Column {
        flashcard.frontImageUrl?.let { AsyncImage(it, "Imagem da frente", modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) }
        flashcard.frontAudioUrl?.let { AudioPlayer(it) }
        when (flashcard.type) {
            FlashcardType.FRONT_BACK, FlashcardType.TEXT_INPUT -> {
                Text(
                    text = flashcard.front,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }
            FlashcardType.CLOZE -> {
                Text(
                    text = flashcard.clozeText?.replace("___", "______") ?: flashcard.front,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }
            FlashcardType.MULTIPLE_CHOICE -> {
                Text(
                    text = flashcard.front,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun FlashcardBack(flashcard: Flashcard) {
    Column {
        flashcard.backImageUrl?.let { AsyncImage(it, "Imagem do verso", modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) }
        flashcard.backAudioUrl?.let { AudioPlayer(it) }
        when (flashcard.type) {
            FlashcardType.FRONT_BACK, FlashcardType.TEXT_INPUT -> {
                Text(
                    text = flashcard.back,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }
            FlashcardType.CLOZE -> {
                Text(
                    text = flashcard.clozeAnswer ?: "Resposta não definida",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            FlashcardType.MULTIPLE_CHOICE -> {
                flashcard.options?.forEachIndexed { index, option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (index == flashcard.correctOptionIndex) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Default.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = if (index == flashcard.correctOptionIndex) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (index == flashcard.correctOptionIndex) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = if (index == flashcard.correctOptionIndex) {
                                FontWeight.Medium
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityDialog(
    flashcard: Flashcard,
    onQualitySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Como você se saiu?")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QualityButton(
                    text = "Muito difícil",
                    subtitle = "Não consegui lembrar",
                    color = MaterialTheme.colorScheme.error,
                    onClick = { onQualitySelected(1) }
                )
                QualityButton(
                    text = "Difícil",
                    subtitle = "Lembrei com dificuldade",
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    onClick = { onQualitySelected(2) }
                )
                QualityButton(
                    text = "Normal",
                    subtitle = "Lembrei após pensar",
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { onQualitySelected(3) }
                )
                QualityButton(
                    text = "Fácil",
                    subtitle = "Lembrei facilmente",
                    color = SuccessColor.copy(alpha = 0.7f),
                    onClick = { onQualitySelected(4) }
                )
                QualityButton(
                    text = "Muito fácil",
                    subtitle = "Lembrei instantaneamente",
                    color = SuccessColor,
                    onClick = { onQualitySelected(5) }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            StudyButton(
                onClick = onDismiss,
                text = "Cancelar",
                variant = ButtonVariant.Secondary
            )
        }
    )
}

@Composable
private fun QualityButton(
    text: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = StudyShapes.buttonShape,
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DeleteFlashcardDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Excluir flashcard")
        },
        text = {
            Text("Tem certeza que deseja excluir este flashcard? Esta ação não pode ser desfeita.")
        },
        confirmButton = {
            StudyButton(
                onClick = onConfirm,
                text = "Excluir"
            )
        },
        dismissButton = {
            StudyButton(
                onClick = onDismiss,
                text = "Cancelar",
                variant = ButtonVariant.Secondary
            )
        }
    )
}

@Composable
private fun AddEditFlashcardDialog(
    deckId: Long,
    flashcard: Flashcard?,
    onDismiss: () -> Unit,
    onSave: (Flashcard) -> Unit
) {
    com.example.study.ui.components.AddEditFlashcardDialog(
        deckId = deckId,
        flashcard = flashcard,
        onDismiss = onDismiss,
        onSave = onSave
    )
}