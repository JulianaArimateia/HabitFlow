package com.habitflow.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object WeatherApi {

    // Padrão: São Paulo — pode ser substituído por coordenadas do GPS
    private const val DEFAULT_LAT = -23.5505
    private const val DEFAULT_LON = -46.6333

    suspend fun fetch(lat: Double = DEFAULT_LAT, lon: Double = DEFAULT_LON): WeatherData? =
        withContext(Dispatchers.IO) {
            try {
                val url = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$lat&longitude=$lon" +
                    "&current=temperature_2m,weather_code" +
                    "&timezone=America%2FSao_Paulo"
                val json = JSONObject(URL(url).readText())
                val current = json.getJSONObject("current")
                val temp = current.getDouble("temperature_2m").toFloat()
                val code = current.getInt("weather_code")
                WeatherData(
                    temperature = temp,
                    weatherCode = code,
                    description = description(code),
                    emoji = emoji(code),
                    habitSuggestion = suggestion(code)
                )
            } catch (e: Exception) {
                null
            }
        }

    private fun description(code: Int) = when (code) {
        0 -> "Céu limpo"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Névoa"
        51, 53, 55 -> "Garoa leve"
        61, 63, 65 -> "Chuva"
        80, 81, 82 -> "Pancadas de chuva"
        95 -> "Trovoada"
        else -> "Tempo variável"
    }

    private fun emoji(code: Int) = when (code) {
        0 -> "☀️"
        1, 2, 3 -> "⛅"
        45, 48 -> "🌫️"
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "🌧️"
        95 -> "⛈️"
        else -> "🌤️"
    }

    private fun suggestion(code: Int) = when (code) {
        0 -> "Dia perfeito para hábitos ao ar livre! 🏃"
        1, 2, 3 -> "Bom dia para se exercitar fora de casa!"
        45, 48 -> "Visibilidade baixa — prefira atividades internas."
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Dia chuvoso — foque em leitura ou meditação 📚"
        95 -> "Fique em casa e aproveite para meditar ou estudar! ⛈️"
        else -> "Mantenha seus hábitos independente do clima!"
    }
}
