package com.example.study.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.study.data.FlashcardType
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect


// Componente para selecionar o tipo de flashcard
@Composable
fun FlashcardTypeSelector(selectedType: FlashcardType, onTypeSelected: (FlashcardType) -> Unit) {
    val types = FlashcardType.values()
    Column {
        Text("Tipo de Flashcard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.take(2).forEach { type ->
                FlashcardTypeChip(type, selectedType == type, { onTypeSelected(type) }, Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.drop(2).forEach { type ->
                FlashcardTypeChip(type, selectedType == type, { onTypeSelected(type) }, Modifier.weight(1f))
            }
        }
    }
}

// O Chip individual usado pelo seletor
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardTypeChip(type: FlashcardType, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (text, icon) = when (type) {
        FlashcardType.FRONT_BACK -> "Frente/Verso" to Icons.Default.FlipToFront
        FlashcardType.CLOZE -> "Lacuna" to Icons.Default.TextFormat
        FlashcardType.TEXT_INPUT -> "Digitação" to Icons.Default.Edit
        FlashcardType.MULTIPLE_CHOICE -> "M. Escolha" to Icons.Default.CheckCircle
    }
    FilterChip(
        onClick = onClick,
        label = { Text(text, style = MaterialTheme.typography.labelMedium) },
        selected = isSelected,
        leadingIcon = { Icon(icon, null, Modifier.size(18.dp)) },
        modifier = modifier
    )
}

// Player de áudio CORRIGIDO
@Composable
fun AudioPlayer(audioUrl: String) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val exoPlayer = remember(audioUrl) {
        try {
            if (audioUrl.isBlank()) {
                hasError = true
                null
            } else {
                val uri = Uri.parse(audioUrl)
                if (uri == null || uri.scheme == null) {
                    Log.e("AudioPlayer", "URI inválida: $audioUrl")
                    hasError = true
                    null
                } else {
                    ExoPlayer.Builder(context).build().apply {
                        try {
                            setMediaItem(MediaItem.fromUri(uri))
                            // Não chamar prepare() aqui - será chamado quando necessário
                        } catch (e: Exception) {
                            Log.e("AudioPlayer", "Erro ao configurar MediaItem", e)
                            hasError = true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Erro ao criar ExoPlayer", e)
            hasError = true
            null
        }
    }

    if (hasError) {
        OutlinedButton(
            onClick = { /* Não fazer nada */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Error, contentDescription = "Erro no áudio")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Erro no áudio")
            }
        }
    } else {
        OutlinedButton(
            onClick = {
                exoPlayer?.let { player ->
                    try {
                        if (isPlaying) {
                            player.pause()
                        } else {
                            if (player.playbackState == ExoPlayer.STATE_IDLE) {
                                player.prepare()
                            }
                            player.play()
                        }
                        isPlaying = !isPlaying
                    } catch (e: Exception) {
                        Log.e("AudioPlayer", "Erro ao controlar reprodução", e)
                        hasError = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Tocar Áudio"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isPlaying) "Pausar" else "Tocar áudio")
            }
        }
    }

    DisposableEffect(audioUrl) {
        onDispose {
            exoPlayer?.release()
        }
    }
}