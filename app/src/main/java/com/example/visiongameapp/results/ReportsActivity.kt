package com.example.visiongameapp.results

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityReportsBinding

class ReportsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: Show reports and export options
    }
}
