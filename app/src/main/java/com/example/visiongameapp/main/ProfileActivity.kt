package com.example.visiongameapp.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.auth.LoginActivity
import com.example.visiongameapp.auth.UserManager
import com.example.visiongameapp.database.DatabaseHelper
import com.example.visiongameapp.databinding.ActivityProfileBinding
import com.example.visiongameapp.results.ProgressActivity
import com.example.visiongameapp.utils.ToastHelper

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userManager: UserManager
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)
        dbHelper = DatabaseHelper(this)

        setupButtons()
        loadUserProfileData()
        loadUserStats()
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnViewProgress.setOnClickListener {
            startActivity(Intent(this, ProgressActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            userManager.logout()
            ToastHelper.showCustomToast(this, "Logged out successfully", true)
            
            // Navigate to LoginActivity and clear task stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserProfileData() {
        if (userManager.isGuest()) {
            binding.tvUserName.text = "Guest User"
            binding.tvUserEmail.text = "Playing as Guest"
            return
        }

        val currentUser = userManager.getCurrentUser()


        if (currentUser != null) {
            val email = currentUser.email ?: ""

            // 1. Get the part before the '@'
            // 2. Replace dots, underscores, or hyphens with spaces
            // 3. Capitalize each word
            val nameFromEmail = email.substringBefore('@')
                .split(".", "_", "-")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

            // Use displayName if available, otherwise use the name generated from email
            binding.tvUserName.text = if (!currentUser.displayName.isNullOrBlank()) {
                currentUser.displayName
            } else if (nameFromEmail.isNotEmpty()) {
                nameFromEmail
            } else {
                "User"
            }

            binding.tvUserEmail.text = if (email.isNotEmpty()) email else "No Email"

        }
        else {
            binding.tvUserName.text = "Guest"
            binding.tvUserEmail.text = "No email available"
            ToastHelper.showCustomToast(this, "User data not found.", false)
        }
    }

    private fun loadUserStats() {
        val results = dbHelper.getAllResults()
        if (results.isNotEmpty()) {
            val totalGames = results.size
            val avgScore = results.map { it.score }.average().toInt()
            
            binding.tvTotalGames.text = "$totalGames\nGames"
            binding.tvAvgScore.text = "$avgScore%\nScore"
            // You can also calculate max level or other stats if needed
            binding.tvMaxLevel.text = "Lv ${results.size / 5 + 1}\nLevel"
        } else {
            binding.tvTotalGames.text = "0\nGames"
            binding.tvAvgScore.text = "0%\nScore"
            binding.tvMaxLevel.text = "1\nLevel"
        }
    }
}
