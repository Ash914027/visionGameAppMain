package com.example.visiongameapp.games

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.R
import com.example.visiongameapp.databinding.ActivityColorVisionBinding
import com.example.visiongameapp.utils.ToastHelper

class ColorVisionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColorVisionBinding

    private var score = 0
    private var currentPlate = 0


    data class Question(
        val question: String,
        val answer: String,
        val options: List<String>,
        val imageRes: Int
    )


    private val questions = listOf(
        Question("What number do you see?", "12",
            listOf("12", "8", "6", "29"),
            R.drawable.ishihara12),

        Question("Identify the number in the dots", "8",
            listOf("3", "12", "8", "74"),
            R.drawable.plate_8),

        Question("Which number is visible?", "6",
            listOf("5", "6", "16", "45"),
            R.drawable.ishihara5),

        Question("Find the hidden number", "29",
            listOf("74", "29", "15", "57"),
            R.drawable.ishara29),

        Question("What number stands out?", "35",
            listOf("7", "57", "35", "8"),
            R.drawable.ishara35),

        Question("Guess the number", "5",
            listOf("3", "8", "5", "12"),
            R.drawable.ishihara5),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorVisionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Click Listeners
        binding.btnOptionA.setOnClickListener { checkAnswer(binding.btnOptionA.text.toString()) }
        binding.btnOptionB.setOnClickListener { checkAnswer(binding.btnOptionB.text.toString()) }
        binding.btnOptionC.setOnClickListener { checkAnswer(binding.btnOptionC.text.toString()) }
        binding.btnOptionD.setOnClickListener { checkAnswer(binding.btnOptionD.text.toString()) }


        startQuestion()
    }

    private fun startQuestion() {
        if (currentPlate < questions.size) {

            val question = questions[currentPlate]

            // Progress
            binding.tvProgress.text = "${currentPlate + 1}/${questions.size}"

            // ✅ Show Image
            binding.ivPlate.setImageResource(question.imageRes)

            // Options
            binding.btnOptionA.text = question.options[0]
            binding.btnOptionB.text = question.options[1]
            binding.btnOptionC.text = question.options[2]
            binding.btnOptionD.text = question.options[3]
        }
    }

    private fun checkAnswer(selectedAnswer: String) {

        val correctAnswer = questions[currentPlate].answer

        if (selectedAnswer == correctAnswer) {
            score++
            ToastHelper.showCustomToast(this, "✅ Correct!", true)
        } else {
            ToastHelper.showCustomToast(this, "❌ Wrong! Correct: $correctAnswer", false)
        }

        currentPlate++

        if (currentPlate >= questions.size) {
            endGame()
        } else {
            startQuestion()
        }
    }

    private fun endGame() {

        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("testType", "Color Vision")
            putExtra("score", score)
            putExtra("maxScore", questions.size)
            putExtra("levelReached", currentPlate)
            putExtra("mistakes", questions.size - score)
        }

        startActivity(intent)
        finish()
    }
}
