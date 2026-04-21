package com.example.visiongameapp.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.visiongameapp.databinding.ActivityLearnMoreBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class LearnMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLearnMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loadAiContent()
    }

    private fun loadAiContent() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvAiContent.visibility = View.GONE
        binding.layoutFallback.visibility = View.GONE

        // Using the API key found in the project configuration
        val apiKey = "AIzaSyA9ikfQfSFJGQsnN3AKrbZDob5Qec-ejis"
        
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )

            val prompt = "Provide a detailed but concise educational summary about:\n" +
                    "1. Visual Acuity (sharpness)\n" +
                    "2. Color Vision (Ishihara)\n" +
                    "3. Contrast Sensitivity\n\n" +
                    "Explain importance for eye health. Use headings and simple language."

            lifecycleScope.launch {
                try {
                    val response = generativeModel.generateContent(prompt)
                    binding.tvAiContent.text = response.text
                    binding.tvAiContent.visibility = View.VISIBLE
                    binding.layoutFallback.visibility = View.GONE
                } catch (e: Exception) {
                    Log.e("LearnMoreActivity", "Error loading AI content", e)
                    // Show styled fallback layout if API fails
                    binding.tvAiContent.visibility = View.GONE
                    binding.layoutFallback.visibility = View.VISIBLE
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            binding.layoutFallback.visibility = View.VISIBLE
        }
    }
}
