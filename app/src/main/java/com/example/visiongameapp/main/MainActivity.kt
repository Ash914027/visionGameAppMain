package com.example.visiongameapp.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.visiongameapp.R
import com.example.visiongameapp.databinding.ActivityMainBinding
import com.example.visiongameapp.main.adapters.GameAdapter
import com.example.visiongameapp.main.adapters.GameItem
import com.example.visiongameapp.games.*
import com.example.visiongameapp.auth.UserManager
import com.example.visiongameapp.auth.LoginActivity
import com.example.visiongameapp.database.DatabaseHelper
import com.example.visiongameapp.results.ProgressActivity
import com.example.visiongameapp.utils.ToastHelper
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userManager: UserManager
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPrefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("isDarkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        userManager = UserManager(this)
        dbHelper = DatabaseHelper(this)
        
        setupDrawer()
        setupNavigation()
        setupGamesGrid()
        updateStats()
        setupListeners()
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // Setup Header Data
        val headerView = binding.navView.getHeaderView(0)
        val tvNavName = headerView.findViewById<TextView>(R.id.tvNavName)
        val tvNavEmail = headerView.findViewById<TextView>(R.id.tvNavEmail)

        val user = userManager.getCurrentUser()
        if (user != null) {
            tvNavName.text = user.displayName ?: "User"
            tvNavEmail.text = user.email ?: "No Email"
            binding.tvWelcome.text = "Welcome, ${user.displayName ?: "User"}"
        } else {
            binding.tvWelcome.text = "Welcome!"
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_progress -> {
                startActivity(Intent(this, ProgressActivity::class.java))
            }
            R.id.nav_logout -> {
                logoutUser()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    private fun updateStats() {
        val stats = dbHelper.getStats()
        binding.tvTotalGames.text = stats.totalGames.toString()
        binding.tvStreak.text = "${stats.streak}d"
        binding.tvAvgPercentage.text = "${stats.avgPercentage}%"

        val lastResults = dbHelper.getAllResults()
        if (lastResults.isNotEmpty()) {
            val last = lastResults[0]
            binding.tvLastPlayedGame.text = last.testType
            binding.tvLastPlayedScore.text = "${last.score}/${last.maxScore}"
        } else {
            binding.tvLastPlayedGame.text = "No games yet"
            binding.tvLastPlayedScore.text = "--/--"
        }
    }

    private fun setupListeners() {
        binding.btnLearnGames.setOnClickListener {
            startActivity(Intent(this, LearnMoreActivity::class.java))
        }

        binding.fabQuickStart.setOnClickListener {
            val lastResults = dbHelper.getAllResults()
            if (lastResults.isNotEmpty()) {
                val lastGameType = lastResults[0].testType
                val gameClass = getGameClassByName(lastGameType)
                
                ToastHelper.showCustomToast(this, "Resuming: $lastGameType", true)
                
                if (lastGameType == "Visual Acuity") {
                    val intent = Intent(this, PreTestActivity::class.java).apply {
                        putExtra("GAME_CLASS", gameClass.name)
                    }
                    startActivity(intent)
                } else {
                    startActivity(Intent(this, gameClass))
                }
            } else {
                // Default to Visual Acuity if no games played
                val intent = Intent(this, PreTestActivity::class.java).apply {
                    putExtra("GAME_CLASS", LetterAcuityActivity::class.java.name)
                }
                startActivity(intent)
                ToastHelper.showCustomToast(this, "Starting Visual Acuity", true)
            }
        }
    }

    private fun getGameClassByName(name: String): Class<*> {
        return when (name) {
            "Visual Acuity" -> LetterAcuityActivity::class.java
            "Contrast Sensitivity" -> ContrastTestActivity::class.java
            "Color Vision" -> ColorVisionActivity::class.java
            "Peripheral Vision" -> PeripheralTestActivity::class.java
            "Depth Perception" -> DepthPerceptionActivity::class.java
            else -> LetterAcuityActivity::class.java
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_dark_mode -> {
                toggleDarkMode()
                true
            }
            R.id.action_logout -> {
                logoutUser()
                true
            }
            R.id.action_settings -> {
                ToastHelper.showCustomToast(this, "Settings clicked", true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleDarkMode() {
        val sharedPrefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("isDarkMode", false)
        val editor = sharedPrefs.edit()

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putBoolean("isDarkMode", false)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putBoolean("isDarkMode", true)
        }
        editor.apply()
        recreate()
    }

    private fun logoutUser() {
        userManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.progress -> {
                    startActivity(Intent(this, ProgressActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupGamesGrid() {
        val games = listOf(
            GameItem("Visual Acuity", "Progressive letter recognition",
                R.drawable.visualacuity, LetterAcuityActivity::class.java),
            GameItem("Contrast Sensitivity", "Find fading circles",
                R.drawable.contrast, ContrastTestActivity::class.java),
            GameItem("Color Vision", "Ishihara-style number detection",
                R.drawable.colorvision, ColorVisionActivity::class.java),
            GameItem("Peripheral Vision", "Reaction time test",
                R.drawable.peripheral, PeripheralTestActivity::class.java),
            GameItem("Depth Perception", "3D pattern recognition",
                R.drawable.peripheral, DepthPerceptionActivity::class.java)
        )

        val adapter = GameAdapter(games) { item ->
            if (item.name == "Visual Acuity") {
                val intent = Intent(this, LetterAcuityActivity::class.java).apply {
                    putExtra("GAME_CLASS", item.activityClass.name)
                }
                startActivity(intent)
            } else {
                startActivity(Intent(this, item.activityClass))
            }
        }
        binding.gamesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.gamesRecyclerView.adapter = adapter
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
