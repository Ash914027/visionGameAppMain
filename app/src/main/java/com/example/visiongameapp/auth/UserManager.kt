package com.example.visiongameapp.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val KEY_GUEST_MODE = "guest_mode"

    // Signup with Firebase
    fun signup(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setGuestMode(false)
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Login with Firebase
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setGuestMode(false)
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Guest mode
    fun setGuestMode(isGuest: Boolean) {
        sharedPref.edit().putBoolean(KEY_GUEST_MODE, isGuest).apply()
    }

    fun isGuest(): Boolean {
        return sharedPref.getBoolean(KEY_GUEST_MODE, false)
    }

    // Logout
    fun logout() {
        auth.signOut()
        sharedPref.edit().clear().apply()
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null || isGuest()
    }
}
