package com.example.visiongameapp.games

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityDepthPerceptionBinding
import com.example.visiongameapp.utils.ToastHelper

class DepthPerceptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDepthPerceptionBinding
    private var score = 0
    private var currentTrial = 1
    private val maxTrials = 10
    private var correctOption = 1
    private var difficulty = 1.0f // Starts at 1.0, decreases to make it harder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepthPerceptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        startTrial()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnOption1.setOnClickListener { checkAnswer(1) }
        binding.btnOption2.setOnClickListener { checkAnswer(2) }
        binding.btnOption3.setOnClickListener { checkAnswer(3) }
        binding.btnOption4.setOnClickListener { checkAnswer(4) }
    }

    private fun startTrial() {
        binding.tvTrial.text = "Trial $currentTrial/$maxTrials"
        correctOption = (1..4).random()
        
        // Generate a new depth pattern with labels A, B, C, D
        val generator = DepthPatternGenerator()
        val bitmap = generator.createBitmap(800, 800, correctOption, difficulty)
        binding.imgPattern.setImageBitmap(bitmap)
    }

    private fun checkAnswer(selectedOption: Int) {
        if (selectedOption == correctOption) {
            score++
            difficulty *= 0.8f // Make it harder (less visual difference)
            ToastHelper.showCustomToast(this, "✅ Correct!", true)
        } else {
            ToastHelper.showCustomToast(this, "❌ Incorrect", false)
        }

        currentTrial++
        if (currentTrial > maxTrials) {
            endGame()
        } else {
            startTrial()
        }
    }

    private fun endGame() {
        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("testType", "Depth Perception")
            putExtra("score", score)
            putExtra("maxScore", maxTrials)
            // levelReached is a scale of 1-10 based on accuracy and difficulty mastered
            val level = if (score == 0) 1 else (score * (1.1f - difficulty) * 10).toInt().coerceIn(1, 10)
            putExtra("levelReached", level)
            putExtra("mistakes", maxTrials - score)
        }
        startActivity(intent)
        finish()
    }

    class DepthPatternGenerator {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun createBitmap(width: Int, height: Int, target: Int, difficulty: Float): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.parseColor("#F5F7FA")) // Match background

            val centerX = width / 2f
            val centerY = height / 2f
            val spacing = width * 0.35f

            val positions = listOf(
                Pair(centerX - spacing / 2, centerY - spacing / 2),
                Pair(centerX + spacing / 2, centerY - spacing / 2),
                Pair(centerX - spacing / 2, centerY + spacing / 2),
                Pair(centerX + spacing / 2, centerY + spacing / 2)
            )

            val labels = listOf("A", "B", "C", "D")

            for (i in 1..4) {
                val pos = positions[i - 1]
                val isTarget = (i == target)
                
                // Shadow displacement based on difficulty to simulate depth (Closer = further shadow)
                val shadowOffset = if (isTarget) 12f * (1f + (0.5f * difficulty)) else 6f
                paint.color = Color.parseColor("#D0D0D0")
                canvas.drawCircle(pos.first + shadowOffset, pos.second + shadowOffset, 75f, paint)

                // Main Sphere
                val sphereColor = if (isTarget) "#34B1E9" else "#2196F3"
                paint.color = Color.parseColor(sphereColor)
                
                // Size variation (Closer = slightly larger)
                val radius = if (isTarget) 75f + (8f * difficulty) else 75f
                canvas.drawCircle(pos.first, pos.second, radius, paint)
                
                // Highlight for 3D effect
                paint.color = Color.WHITE
                paint.alpha = 180
                canvas.drawCircle(pos.first - 20f, pos.second - 20f, 20f, paint)
                paint.alpha = 255
                
                // Draw Label A, B, C, or D
                paint.color = Color.parseColor("#666666")
                paint.textSize = 36f
                paint.isFakeBoldText = true
                canvas.drawText(labels[i-1], pos.first - 12f, pos.second + radius + 50f, paint)
            }

            return bitmap
        }
    }
}
