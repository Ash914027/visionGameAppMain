package com.example.visiongameapp.results

data class GameResultModel(
    val id: Int,
    val testType: String,
    val score: Int,
    val maxScore: Int,
    val date: String,
    val diseaseIndication: String
)
