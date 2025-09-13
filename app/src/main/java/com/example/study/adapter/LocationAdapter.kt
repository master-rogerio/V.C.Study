package com.example.study.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.study.data.UserLocation
import com.example.study.databinding.ItemLocationBinding
import java.util.Locale

class LocationAdapter(
    private val context: Context,
    private val onDeleteClick: (UserLocation) -> Unit
) : ListAdapter<UserLocation, LocationAdapter.LocationViewHolder>(LocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LocationViewHolder(private val binding: ItemLocationBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(location: UserLocation) {
            // Remove "Ambientes" prefix if present in the location name
            val displayName = location.name.replace("^Ambientes\\s*", "").trim()
            binding.locationName.text = displayName
            
            binding.locationCoordinates.text = String.format(
                Locale.getDefault(),
                context.getString(com.example.study.R.string.location_coordinates),
                location.latitude,
                location.longitude
            )
            
            // Set icon based on iconName
            val iconResId = context.resources.getIdentifier(
                location.iconName, 
                "drawable", 
                context.packageName
            )
            
            if (iconResId != 0) {
                binding.locationIcon.setImageResource(iconResId)
            }
            
            binding.deleteButton.setOnClickListener {
                onDeleteClick(location)
            }
        }
    }

    class LocationDiffCallback : DiffUtil.ItemCallback<UserLocation>() {
        override fun areItemsTheSame(oldItem: UserLocation, newItem: UserLocation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserLocation, newItem: UserLocation): Boolean {
            return oldItem == newItem
        }
    }
}