package com.habitflow.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

object QuoteApi {

    suspend fun fetchToday(): QuoteData? = withContext(Dispatchers.IO) {
        try {
            val json = JSONArray(URL("https://zenquotes.io/api/today").readText())
            val obj = json.getJSONObject(0)
            val originalText = obj.getString("q")
            val author = obj.getString("a")
            val translated = translate(originalText) ?: originalText
            QuoteData(text = translated, author = author)
        } catch (e: Exception) {
            null
        }
    }

    private fun translate(text: String): String? = try {
        val encoded = URLEncoder.encode(text, "UTF-8")
        val url = "https://api.mymemory.translated.net/get?q=$encoded&langpair=en|pt-BR"
        val json = JSONObject(URL(url).readText())
        json.getJSONObject("responseData")
            .getString("translatedText")
            .takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        null
    }
}
