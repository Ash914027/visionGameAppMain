package com.example.visiongameapp.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.visiongameapp.databinding.ItemGameBinding

class GameAdapter(
    private val games: List<GameItem>,
    private val onGameClick: (GameItem) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount(): Int = games.size

    inner class GameViewHolder(private val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GameItem) {
            binding.gameName.text = item.name
            binding.gameDescription.text = item.description
            binding.gameIcon.setImageResource(item.iconRes)
            
            // Trigger game when the card is clicked
            binding.root.setOnClickListener { onGameClick(item) }
            
            // Explicitly handle the "Play Game" button click
            binding.btnPlayGame.setOnClickListener { onGameClick(item) }
            
            // Show last score if available (this assumes GameItem has a way to store it or we fetch it)
            // For now, visibility is handled in XML or by the Activity logic
        }
    }
}
