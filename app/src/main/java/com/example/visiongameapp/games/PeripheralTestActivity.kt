package com.example.visiongameapp.games

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityPeripheralTestBinding
import kotlin.random.Random

class PeripheralTestActivity : AppCompatActivity() {

  private lateinit var binding: ActivityPeripheralTestBinding

  private var score = 0
  private var currentTrial = 1
  private val maxTrials = 10

  private var startTime: Long = 0
  private val handler = Handler(Looper.getMainLooper())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityPeripheralTestBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // 👉 Click on stimulus to record reaction
    binding.ivStimulus.setOnClickListener {
      recordReaction()
    }

    startTrial()
  }

  private fun startTrial() {
    binding.tvTrialPeripheral.text = "Trial $currentTrial"

    // Hide stimulus first
    binding.ivStimulus.visibility = View.INVISIBLE

    // Random delay before showing stimulus
    val delay = Random.nextLong(1000, 2000)

    handler.postDelayed({
      showStimulus()
    }, delay)
  }

  private fun showStimulus() {
    binding.ivStimulus.visibility = View.VISIBLE

    // Random left/right position
    val isLeft = Random.nextBoolean()
    binding.ivStimulus.translationX = if (isLeft) -300f else 300f

    startTime = System.currentTimeMillis()
  }

  private fun recordReaction() {
    val reactionTime = System.currentTimeMillis() - startTime

    // Show reaction time
    binding.tvReactionPeripheral.text = "Reaction Time: $reactionTime ms"

    // Simple scoring
    if (reactionTime < 800) {
      score++
    }

    binding.ivStimulus.visibility = View.INVISIBLE

    currentTrial++

    if (currentTrial > maxTrials) {
      endGame()
    } else {
      startTrial()
    }
  }

  private fun endGame() {
    val intent = Intent(this, GameResultActivity::class.java).apply {
      putExtra("testType", "Peripheral Vision")
      putExtra("score", score)
      putExtra("maxScore", maxTrials)
      putExtra("levelReached", maxTrials)
      putExtra("mistakes", maxTrials - score)
    }

    startActivity(intent)
    finish()
  }
}