package com.example.visiongameapp.games

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityLetterAcuityBinding
import com.example.visiongameapp.utils.GameResult

class LetterAcuityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLetterAcuityBinding
    private var currentLevel = 1
    private var score = 0
    private var mistakes = 0
    private val maxLevels = 15
    private val letters = arrayOf("E", "F", "P", "T", "O", "Z", "L", "D", "C", "R", "N", "H")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetterAcuityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupGame()
        startLevel()
    }

    private fun setupGame() {
        binding.progressBar.max = maxLevels
        binding.btnOption1.setOnClickListener { checkAnswer(0) }
        binding.btnOption2.setOnClickListener { checkAnswer(1) }
        binding.btnOption3.setOnClickListener { checkAnswer(2) }
        binding.btnOption4.setOnClickListener { checkAnswer(3) }
    }

    private fun startLevel() {
        val textSize = calculateTextSize()
        val targetLetter = letters.random()
        binding.tvLevel.text = "Level $currentLevel"
        binding.progressBar.progress = currentLevel
        binding.tvTargetLetter.text = targetLetter
        binding.tvTargetLetter.textSize = textSize
        val options = generateOptions(targetLetter)
        binding.btnOption1.text = options[0]
        binding.btnOption2.text = options[1]
        binding.btnOption3.text = options[2]
        binding.btnOption4.text = options[3]
        binding.root.tag = targetLetter
    }

    private fun calculateTextSize(): Float {
        val baseSize = 100f
        val reduction = (currentLevel - 1) * 8f
        return (baseSize - reduction).coerceAtLeast(20f)
    }

    private fun generateOptions(correctLetter: String): Array<String> {
        val options = mutableListOf<String>()
        options.add(correctLetter)
        while (options.size < 4) {
            val randomLetter = letters.random()
            if (!options.contains(randomLetter)) {
                options.add(randomLetter)
            }
        }
        return options.shuffled().toTypedArray()
    }

    private fun checkAnswer(selectedIndex: Int) {
        val selectedLetter = when (selectedIndex) {
            0 -> binding.btnOption1.text.toString()
            1 -> binding.btnOption2.text.toString()
            2 -> binding.btnOption3.text.toString()
            else -> binding.btnOption4.text.toString()
        }
        val correctLetter = binding.root.tag as String
        if (selectedLetter == correctLetter) {
            score++
            showFeedback("Correct! ✓", true)
            nextLevel()
        } else {
            mistakes++
            showFeedback("Incorrect ✗", false)
            if (mistakes >= 3) {
                endGame()
            } else {
                nextLevel()
            }
        }
    }

    private fun showFeedback(message: String, isCorrect: Boolean) {
        binding.tvFeedback.text = message
        binding.tvFeedback.setTextColor(if (isCorrect) Color.GREEN else Color.RED)
        binding.tvFeedback.visibility = View.VISIBLE
        Handler().postDelayed({ binding.tvFeedback.visibility = View.INVISIBLE }, 1000)
    }

    private fun nextLevel() {
        currentLevel++
        if (currentLevel <= maxLevels) {
            Handler().postDelayed({ startLevel() }, 1500)
        } else {
            endGame()
        }
    }
    private fun endGame() {
        val intent = Intent(this, GameResultActivity::class.java)
        intent.putExtra("testType", "Visual Acuity")
        intent.putExtra("score", score)
        intent.putExtra("maxScore", maxLevels)
        intent.putExtra("levelReached", currentLevel - 1)
        intent.putExtra("mistakes", mistakes)
        startActivity(intent)
        finish()
    }

}
