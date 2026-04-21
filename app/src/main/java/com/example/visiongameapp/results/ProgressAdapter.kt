package com.example.visiongameapp.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.visiongameapp.databinding.ItemProgressBinding

class ProgressAdapter(
    private var results: MutableList<GameResultModel>,
    private val onDeleteClick: (GameResultModel) -> Unit
) : RecyclerView.Adapter<ProgressAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemProgressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = results[position]

        holder.binding.tvTestType.text = result.testType
        holder.binding.tvDate.text = result.date
        
        val percentage = if (result.maxScore > 0) (result.score.toFloat() / result.maxScore * 100).toInt() else 0
        holder.binding.tvScore.text = "$percentage%"
        holder.binding.tvScoreFraction.text = "(${result.score}/${result.maxScore})"
        
        if (result.diseaseIndication.isNotEmpty()) {
            holder.binding.tvIndication.visibility = View.VISIBLE
            holder.binding.tvIndication.text = "Insight: ${result.diseaseIndication}"
        } else {
            holder.binding.tvIndication.visibility = View.GONE
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(result)
        }
    }

    override fun getItemCount(): Int = results.size

    fun updateData(newResults: List<GameResultModel>) {
        this.results = newResults.toMutableList()
        notifyDataSetChanged()
    }
}
