package com.example.visiongameapp.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.visiongameapp.R
import com.example.visiongameapp.auth.LoginActivity
import com.example.visiongameapp.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onboardingItems = listOf(
            OnboardingItem(
                title = getString(R.string.onboarding_1_title),
                description = getString(R.string.onboarding_1_desc),
                imageRes = R.drawable.ic_eye
            ),
            OnboardingItem(
                title = getString(R.string.onboarding_2_title),
                description = getString(R.string.onboarding_2_desc),
                imageRes = R.drawable.ic_contrast
            ),
            OnboardingItem(
                title = getString(R.string.onboarding_3_title),
                description = getString(R.string.onboarding_3_desc),
                imageRes = R.drawable.baseline_visibility_24
            )
        )

        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        // Add page change listener to update button text on last page
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingItems.size - 1) {
                    binding.btnContinue.text = "Get Started"
                } else {
                    binding.btnContinue.text = "Next"
                }
            }
        })

        binding.btnContinue.setOnClickListener {
            if (binding.viewPager.currentItem < onboardingItems.size - 1) {
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            } else {
                completeOnboarding()
            }
        }
    }

    private fun completeOnboarding() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("onboarding_completed", true).apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
