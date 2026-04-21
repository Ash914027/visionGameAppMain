package com.example.visiongameapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.visiongameapp.databinding.ActivitySignupBinding
import com.example.visiongameapp.utils.ToastHelper

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)
        setupViews()
    }

    private fun setupViews() {
        binding.btnSignup.setOnClickListener { handleSignup() }
        binding.btnBackToLogin.setOnClickListener { navigateToLogin() }
    }

    private fun handleSignup() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val termsAccepted = binding.cbTerms.isChecked

        if (!termsAccepted) {
            ToastHelper.showCustomToast(this, "Please accept the terms.", false)
            return
        }

        if (validateInput(name, email, password, confirmPassword)) {
            setLoading(true)
            userManager.signup(email, password) { success, error ->
                setLoading(false)
                if (success) {
                    ToastHelper.showCustomToast(this, "Account created!", true)
                    navigateToLogin()
                } else {
                    ToastHelper.showCustomToast(this, error ?: "Signup failed", false)
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnSignup.isEnabled = !isLoading
        binding.btnSignup.text = if (isLoading) "" else "Create Account"
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        // Disable other interactions while loading
        binding.btnBackToLogin.isEnabled = !isLoading
        binding.etName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.cbTerms.isEnabled = !isLoading
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        return when {
            name.isEmpty() -> {
                binding.etName.error = "Name required"
                false
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Email required"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Invalid email format"
                false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password required"
                false
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password must be at least 6 characters"
                false
            }
            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Passwords do not match"
                false
            }
            else -> true
        }
    }
}
