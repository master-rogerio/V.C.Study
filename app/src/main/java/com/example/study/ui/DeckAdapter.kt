package com.example.study.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.study.R
import com.example.study.data.Deck
import com.example.study.util.ColorUtils
import com.google.android.material.card.MaterialCardView

class DeckAdapter(
    private val onItemClick: (Deck) -> Unit,
    private val onEditClick: (Deck) -> Unit = {}, // Valor padrão vazio
    private val getFlashcardCount: (Long) -> Int
) : ListAdapter<Deck, DeckAdapter.DeckViewHolder>(DeckDiffCallback()) {

    private val flashcardCounts = mutableMapOf<Long, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deck, parent, false)
        return DeckViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deckHeader: TextView = itemView.findViewById(R.id.deckHeader)
        private val deckThemeText: TextView = itemView.findViewById(R.id.deckThemeText)
        private val cardCountText: TextView = itemView.findViewById(R.id.cardCountText)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val cardView: MaterialCardView = itemView as MaterialCardView

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(getItem(position)) // Chama a função (pode ser vazia)
                }
            }
        }

        fun bind(deck: Deck) {
            val deckColor = ColorUtils.getColorFromString(deck.name)
            val lighterColor = ColorUtils.getLighterColor(deckColor)

            deckHeader.text = itemView.context.getString(R.string.deck_header, deck.name)
            deckHeader.setBackgroundColor(deckColor)
            cardView.strokeColor = deckColor
            editButton.setColorFilter(deckColor)
            deckThemeText.text = deck.theme

            val count = flashcardCounts[deck.id] ?: getFlashcardCount(deck.id)
            cardCountText.text = itemView.context.getString(R.string.card_count, count)
        }
    }

    fun updateFlashcardCount(deckId: Long, count: Int) {
        flashcardCounts[deckId] = count
        val position = currentList.indexOfFirst { it.id == deckId }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    private class DeckDiffCallback : DiffUtil.ItemCallback<Deck>() {
        override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean {
            return oldItem == newItem
        }
    }
}