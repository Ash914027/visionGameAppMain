package com.example.visiongameapp.utils

import android.content.Context

class PreferenceManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun isDarkModeEnabled(): Boolean = prefs.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) = prefs.edit().putBoolean("dark_mode", enabled).apply()
    fun areNotificationsEnabled(): Boolean = prefs.getBoolean("notifications", true)
    fun setNotifications(enabled: Boolean) = prefs.edit().putBoolean("notifications", enabled).apply()
}
