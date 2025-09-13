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
import com.example.study.data.Flashcard
import java.text.SimpleDateFormat
import java.util.Locale

class FlashcardAdapter(
    private val onItemClick: (Flashcard) -> Unit,
    private val onEditClick: (Flashcard) -> Unit
) : ListAdapter<Flashcard, FlashcardAdapter.FlashcardViewHolder>(FlashcardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flashcard, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val frontText: TextView = itemView.findViewById(R.id.frontText)
        private val backText: TextView = itemView.findViewById(R.id.backText)
        private val nextReviewText: TextView = itemView.findViewById(R.id.nextReviewText)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)

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
                    onEditClick(getItem(position))
                }
            }
        }

        fun bind(flashcard: Flashcard) {
            frontText.text = flashcard.front
            backText.text = flashcard.back
            
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val nextReviewDate = flashcard.nextReviewDate?.let { dateFormat.format(it) } ?: "Não revisado"
            nextReviewText.text = itemView.context.getString(R.string.next_review, nextReviewDate)
        }
    }

    private class FlashcardDiffCallback : DiffUtil.ItemCallback<Flashcard>() {
        override fun areItemsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
            return oldItem == newItem
        }
    }
} 