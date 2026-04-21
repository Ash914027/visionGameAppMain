package com.example.visiongameapp.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.R
import com.example.visiongameapp.auth.LoginActivity
import com.example.visiongameapp.auth.UserManager
import com.example.visiongameapp.databinding.ActivitySplashBinding
import com.example.visiongameapp.main.MainActivity
import com.example.visiongameapp.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)

        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim)
        binding.logoImage.startAnimation(logoAnim)
        binding.taglineText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_tagline_anim))

        Handler(Looper.getMainLooper()).postDelayed({
            checkDestination()
        }, 2000)
    }

    private fun checkDestination() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val onboardingCompleted = sharedPref.getBoolean("onboarding_completed", false)

        val intent = when {
            !onboardingCompleted -> Intent(this, OnboardingActivity::class.java)
            userManager.isLoggedIn() -> Intent(this, MainActivity::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}
