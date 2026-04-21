package com.example.visiongameapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivityLoginBinding
import com.example.visiongameapp.main.MainActivity
import com.example.visiongameapp.utils.ToastHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)
        setupViews()
        
        // Check if user is already logged in
        if (userManager.isLoggedIn()) {
            navigateToMain()
        }
    }

    private fun setupViews() {
        binding.btnLogin.setOnClickListener { handleLogin() }
        binding.btnSignup.setOnClickListener { navigateToSignup() }
        binding.tvForgotPassword.setOnClickListener { navigateToForgotPassword() }
        binding.btnGuestMode.setOnClickListener { enterGuestMode() }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (validateInput(email, password)) {
            setLoading(true)
            userManager.login(email, password) { success, error ->
                setLoading(false)
                if (success) {
                    ToastHelper.showCustomToast(this, "Login successful", true)
                    navigateToMain()
                } else {
                    showError(error ?: "Invalid credentials")
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "" else "Login"
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        // Disable other interactions while loading
        binding.btnSignup.isEnabled = !isLoading
        binding.btnGuestMode.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.etEmail.error = "Email required"
                false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password required"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Invalid email format"
                false
            }
            else -> true
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun enterGuestMode() {
        userManager.setGuestMode(true)
        navigateToMain()
    }

    private fun navigateToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToForgotPassword() {
         val intent = Intent(this, ForgotPasswordActivity::class.java)
         startActivity(intent)
    }

    private fun showError(message: String) {
        ToastHelper.showCustomToast(this, message, false)
    }
}
