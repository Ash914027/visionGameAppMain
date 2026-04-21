package com.example.visiongameapp.games

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityContrastTestBinding
import kotlin.random.Random

class ContrastTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContrastTestBinding

    private var currentLevel = 1
    private var score = 0
    private val maxLevels = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContrastTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGame()
        createLevel()
    }

    private fun setupGame() {

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Score display
        binding.tvScore.text = "Score: $score"

        // Hint button
        binding.btnHint.setOnClickListener {
            showFeedback("Hint: Look carefully for a slightly different shade 👀", false)
        }
    }

    private fun createLevel() {
        binding.gameContainer.removeAllViews()

        // ✅ Improvement 1: Easier grid for level 1
        val gridSize = if (currentLevel == 1) 3 else 4
        val totalItems = gridSize * gridSize

        val backgroundGray = 128

        // ✅ Improvement 2: Smooth difficulty scaling
        val diff = when (currentLevel) {
            1 -> 80
            2 -> 60
            3 -> 45
            4 -> 35
            5 -> 25
            6 -> 18
            7 -> 12
            8 -> 8
            9 -> 5
            else -> 3
        }

        // ✅ Improvement 3: Random lighter/darker
        val isLighter = Random.nextBoolean()
        val targetGray = if (isLighter) {
            (backgroundGray + diff).coerceIn(0, 255)
        } else {
            (backgroundGray - diff).coerceIn(0, 255)
        }

        val targetIndex = Random.nextInt(totalItems)

        for (i in 0 until totalItems) {
            val isTarget = i == targetIndex
            val gray = if (isTarget) targetGray else backgroundGray

            val circle = createCircle(gray, isTarget)
            binding.gameContainer.addView(circle)
        }

        binding.tvLevel.text = "Level $currentLevel - Find the different circle"

        // Resize AFTER adding views
        binding.gameContainer.post {
            val totalSize = binding.gameContainer.width
            val circleSize = totalSize / gridSize - 20

            for (i in 0 until binding.gameContainer.childCount) {
                val child = binding.gameContainer.getChildAt(i)

                val params = GridLayout.LayoutParams()
                params.width = circleSize
                params.height = circleSize
                params.setMargins(10, 10, 10, 10)

                child.layoutParams = params
            }
        }
    }

    private fun createCircle(grayValue: Int, isTarget: Boolean): View {
        val circle = View(this)

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.rgb(grayValue, grayValue, grayValue))
        }

        circle.background = drawable

        if (isTarget) {
            circle.setOnClickListener { correctAnswer() }
        } else {
            circle.setOnClickListener { wrongAnswer() }
        }

        return circle
    }

    private fun correctAnswer() {
        score++
        showFeedback("Found it! ✓", true)
        nextLevel()
    }

    private fun wrongAnswer() {
        showFeedback("Try again ✗", false)
    }

    private fun showFeedback(message: String, isCorrect: Boolean) {
        binding.tvFeedback.text = message
        binding.tvFeedback.setTextColor(
            if (isCorrect) Color.GREEN else Color.RED
        )
        binding.tvFeedback.visibility = View.VISIBLE

        binding.tvFeedback.postDelayed({
            binding.tvFeedback.visibility = View.INVISIBLE
        }, 700)

        binding.tvScore.text = "Score: $score"
    }

    private fun nextLevel() {
        currentLevel++

        if (currentLevel <= maxLevels) {
            createLevel()
        } else {
            endGame()
        }
    }

    private fun endGame() {
        val intent = Intent(this, GameResultActivity::class.java)

        intent.putExtra("gameName", "Contrast Sensitivity")
        intent.putExtra("score", score)
        intent.putExtra("level", currentLevel - 1)

        startActivity(intent)
        finish()
    }
}