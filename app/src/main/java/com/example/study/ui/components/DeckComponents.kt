package com.example.study.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.study.data.Deck
import com.example.study.ui.theme.FlashCardsTheme
import com.example.study.util.ColorUtils

@Composable
fun DeckListItem(
    deck: Deck,
    flashcardCount: Int,
    onItemClick: (Deck) -> Unit
) {
    val deckColor = Color(ColorUtils.getColorFromString(deck.name))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(deck) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Header do Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(deckColor)
                    .padding(16.dp)
            ) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Corpo do Card
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = deck.theme,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$flashcardCount cartões",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeckListItemPreview() {
    FlashCardsTheme {
        DeckListItem(
            deck = Deck(id = 1, name = "Biologia", theme = "Citologia"),
            flashcardCount = 42,
            onItemClick = {}
        )
    }
}