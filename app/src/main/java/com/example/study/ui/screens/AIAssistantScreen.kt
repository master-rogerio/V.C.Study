package com.example.study.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.study.ui.components.*
import com.example.study.ui.theme.*
import kotlinx.coroutines.*

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    
    // Auto scroll to bottom when new message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    // Initial welcome message
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages = listOf(
                ChatMessage(
                    id = "welcome",
                    text = "Olá! Eu sou o Viber.AI, seu assistente inteligente de estudos! 🤖\n\nPosso te ajudar com:\n• Criação de flashcards\n• Explicações de conceitos\n• Dicas de estudo\n• Planejamento de revisões\n• Análise do seu progresso\n\nComo posso te ajudar hoje?",
                    isFromUser = false
                )
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(GradientStart, GradientEnd)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Viber.AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Assistente de Estudos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        messages = listOf(
                            ChatMessage(
                                id = "welcome_${System.currentTimeMillis()}",
                                text = "Conversa limpa! Como posso te ajudar?",
                                isFromUser = false
                            )
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Nova conversa"
                        )
                    }
                }
            )
        },
        bottomBar = {
            StudyBottomNavigation(
                selectedItem = 4,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message
                    )
                }
                
                if (isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }
            
            // Quick actions
            QuickActionsRow(
                onActionSelected = { action ->
                    inputText = action
                }
            )
            
            // Input area
            ChatInputArea(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSendMessage = {
                    if (inputText.isNotBlank()) {
                        val userMessage = ChatMessage(
                            id = "user_${System.currentTimeMillis()}",
                            text = inputText,
                            isFromUser = true
                        )
                        messages = messages + userMessage
                        val currentInput = inputText
                        inputText = ""
                        isLoading = true
                        
                        // Simulate AI response
                        simulateAIResponse(currentInput) { response ->
                            messages = messages + ChatMessage(
                                id = "ai_${System.currentTimeMillis()}",
                                text = response,
                                isFromUser = false
                            )
                            isLoading = false
                        }
                    }
                },
                isLoading = isLoading
            )
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        if (!message.isFromUser) {
            // AI avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (message.isFromUser) 16.dp else 4.dp,
                topEnd = if (message.isFromUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if (message.isFromUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            tonalElevation = 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isFromUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        
        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    var scale by remember { mutableFloatStateOf(1f) }
                    
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(200L * index)
                            scale = 1.5f
                            delay(300)
                            scale = 1f
                            delay(500 - 200L * index)
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size((6 * scale).dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onActionSelected: (String) -> Unit
) {
    val quickActions = listOf(
        "Criar flashcards sobre...",
        "Explicar conceito",
        "Dicas de memorização",
        "Plano de estudos"
    )
    
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Ações rápidas:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        items(quickActions.chunked(2)) { actionPair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actionPair.forEach { action ->
                    StudyChip(
                        text = action,
                        onClick = { onActionSelected(action) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Fill remaining space if odd number
                if (actionPair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ChatInputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Digite sua pergunta...") },
                minLines = 1,
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                enabled = !isLoading
            )
            
            FloatingActionButton(
                onClick = onSendMessage,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar mensagem"
                    )
                }
            }
        }
    }
}

private fun simulateAIResponse(input: String, onResponse: (String) -> Unit) {
    // Simulate AI processing delay
    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
        delay(1500L + (0L..1000L).random()) // 1.5-2.5 seconds
        
        val response = when {
            input.contains("flashcard", ignoreCase = true) -> {
                "Ótima ideia! Para criar flashcards eficazes sobre esse tópico, sugiro:\n\n" +
                "📚 Divida o conteúdo em conceitos pequenos\n" +
                "❓ Use perguntas diretas na frente\n" +
                "✨ Adicione exemplos na resposta\n" +
                "🔄 Inclua conceitos relacionados\n\n" +
                "Que tal começarmos? Sobre qual tópico específico você quer criar flashcards?"
            }
            
            input.contains("explicar", ignoreCase = true) || input.contains("conceito", ignoreCase = true) -> {
                "Claro! Adoro explicar conceitos! 🧠\n\n" +
                "Para te dar a melhor explicação possível, preciso de mais detalhes:\n\n" +
                "• Qual é o conceito específico?\n" +
                "• Qual seu nível atual de conhecimento?\n" +
                "• Você prefere uma explicação mais teórica ou prática?\n\n" +
                "Quanto mais específico você for, melhor posso te ajudar!"
            }
            
            input.contains("memorização", ignoreCase = true) || input.contains("dicas", ignoreCase = true) -> {
                "Aqui estão algumas técnicas poderosas de memorização! 🧠💡\n\n" +
                "🔄 **Repetição Espaçada**: Revise nos intervalos 1, 3, 7, 14 dias\n" +
                "🖼️ **Técnica da Visualização**: Crie imagens mentais\n" +
                "📖 **Método das Histórias**: Conecte informações em narrativas\n" +
                "🏛️ **Palácio da Memória**: Associe a lugares conhecidos\n" +
                "✍️ **Resumos Ativos**: Escreva com suas próprias palavras\n\n" +
                "Qual dessas técnicas você gostaria de explorar mais?"
            }
            
            input.contains("plano", ignoreCase = true) || input.contains("estudos", ignoreCase = true) -> {
                "Vamos criar um plano de estudos personalizado! 📅✨\n\n" +
                "Para montar o melhor plano para você, me conte:\n\n" +
                "⏰ Quanto tempo você tem disponível por dia?\n" +
                "📚 Quais matérias/tópicos quer focar?\n" +
                "🎯 Qual é seu objetivo (prova, concurso, vestibular)?\n" +
                "📈 Qual seu estilo de aprendizagem preferido?\n\n" +
                "Com essas informações, posso criar um cronograma eficiente!"
            }
            
            else -> {
                val responses = listOf(
                    "Interessante! Deixe-me pensar na melhor forma de te ajudar com isso... 🤔\n\nPoderia me dar mais detalhes sobre o que você precisa?",
                    "Ótima pergunta! 💡 Para te dar uma resposta mais precisa, você poderia especificar um pouco mais sobre o contexto?",
                    "Entendi! Vou te ajudar com isso. Você pode me contar mais sobre seus objetivos de estudo nessa área?",
                    "Legal! Essa é uma área muito importante dos estudos. Que tal começarmos identificando seus principais desafios aqui?"
                )
                responses.random()
            }
        }
        
        onResponse(response)
    }
}