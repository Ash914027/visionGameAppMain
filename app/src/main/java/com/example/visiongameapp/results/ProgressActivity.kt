package com.example.visiongameapp.results

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visiongameapp.database.DatabaseHelper
import com.example.visiongameapp.databinding.ActivityProgressBinding
import com.example.visiongameapp.utils.ToastHelper
import java.text.SimpleDateFormat
import java.util.*

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProgressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarProgress)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        binding.rvProgress.layoutManager = LinearLayoutManager(this)
        adapter = ProgressAdapter(mutableListOf()) { result ->
            showDeleteConfirmation(result)
        }
        binding.rvProgress.adapter = adapter
    }

    private fun showDeleteConfirmation(result: GameResultModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Record")
            .setMessage("Are you sure you want to delete this test result?")
            .setPositiveButton("Delete") { _, _ ->
                val deleted = dbHelper.deleteResult(result.id)
                if (deleted > 0) {
                    ToastHelper.showCustomToast(this, "Result deleted", true)
                    loadData()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadData() {
        val dbResults = dbHelper.getAllResults()
        
        if (dbResults.isEmpty()) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvProgress.visibility = View.GONE
            binding.cardChart.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.rvProgress.visibility = View.VISIBLE
            binding.cardChart.visibility = View.VISIBLE
            
            // Map Database data to UI Model
            val uiResults = dbResults.map { 
                GameResultModel(
                    it.id,
                    it.testType,
                    it.score,
                    it.maxScore,
                    it.date,
                    it.diseaseIndication
                )
            }
            
            adapter.updateData(uiResults)
            setupChart(uiResults)
        }
    }

    private fun setupChart(results: List<GameResultModel>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        // Prepare data for the chart (Showing last 7 tests for example)
        val chartData = results.take(7).reversed().map {
            val date = try {
                sdf.parse(it.date)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
            val percentage = if (it.maxScore > 0) (it.score.toFloat() / it.maxScore * 100) else 0f
            DataPoint(date, percentage, it.testType)
        }

        binding.progressChart.setData(chartData)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
