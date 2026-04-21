package com.example.visiongameapp.games

import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.database.DatabaseHelper
import com.example.visiongameapp.databinding.ActivityGameResultBinding
import com.example.visiongameapp.main.MainActivity

class GameResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameResultBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        val testType = intent.getStringExtra("testType") ?: "General"
        val score = intent.getIntExtra("score", 0)
        val maxScore = intent.getIntExtra("maxScore", 100)
        val levelReached = intent.getIntExtra("levelReached", 0)
        val mistakes = intent.getIntExtra("mistakes", 0)

        // UI Setup
        binding.tvTestName.text = testType
        binding.tvScore.text = "$score/$maxScore"
        binding.tvLevelReached.text = levelReached.toString()
        binding.tvMistakes.text = mistakes.toString()

        val indication = getDiseaseIndication(testType, score, maxScore)
        binding.tvDiseaseIndication.text = indication

        saveGameResult(testType, score, maxScore, indication)

        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun getDiseaseIndication(testType: String, score: Int, maxScore: Int): String {
        val percentage = (score.toFloat() / maxScore.toFloat()) * 100
        
        if (percentage >= 90) return "Excellent! Your vision shows no signs of issues in this area."
        if (percentage >= 70) return "Good performance. Your results are within the normal range."

        return when (testType) {
            "Visual Acuity" -> "Potential Myopia or Hyperopia detected. We recommend a professional eye exam."
            "Contrast Sensitivity" -> "Lower contrast sensitivity might indicate early signs of cataracts or glaucoma."
            "Color Vision" -> "Possible Color Vision Deficiency detected. Please consult an optometrist for an Ishihara test."
            "Depth Perception" -> "Weak depth perception (Stereopsis) detected. This could be due to eye strain or amblyopia."
            else -> "Your score is below average. If you experience vision fatigue, please consult a specialist."
        }
    }

    private fun saveGameResult(testType: String, score: Int, maxScore: Int, disease: String) {
        dbHelper.insertResult(testType, score, maxScore, disease)
        
        val prefs = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("lastGameType", testType)
        editor.putInt("lastScore", score)
        editor.putInt("lastMaxScore", maxScore)
        editor.apply()
    }
}
