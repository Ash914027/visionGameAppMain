package com.example.visiongameapp.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visiongameapp.R

class SlideshowAdapter(private val games: List<GameInfo>) :
    RecyclerView.Adapter<SlideshowAdapter.SlideViewHolder>() {

    inner class SlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle2)
        val desc: TextView = view.findViewById(R.id.tvDescription2)
        val image: ImageView = view.findViewById(R.id.ivGameImage2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slide, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val game = games[position]
        holder.title.text = game.title
        holder.desc.text = game.description
        holder.image.setImageResource(game.imageRes)
    }

    override fun getItemCount() = games.size
}
data class GameInfo(
    val title: String,
    val description: String,
    val imageRes: Int
)
