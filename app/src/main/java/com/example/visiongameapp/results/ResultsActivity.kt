package com.example.visiongameapp.results

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityResultsBinding

class ResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: Show test results
    }
}
