package com.example.visiongameapp.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityForgotPasswordBinding
import com.example.visiongameapp.utils.ToastHelper

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)
        setupViews()
    }

    private fun setupViews() {
        binding.btnResetPassword.setOnClickListener { handleResetPassword() }
    }

    private fun handleResetPassword() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.etEmail.error = "Email required"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return
        }
        // TODO: Implement password reset logic
        ToastHelper.showCustomToast(this, "Password reset link sent!", true)
    }
}
