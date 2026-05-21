package com.habitflow.data.remote

data class WeatherData(
    val temperature: Float,
    val weatherCode: Int,
    val description: String,
    val emoji: String,
    val habitSuggestion: String
)
