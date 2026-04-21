package com.example.visiongameapp.main.adapters

data class GameItem(
    val name: String,
    val description: String,
    val iconRes: Int,
    val activityClass: Class<*>
)
