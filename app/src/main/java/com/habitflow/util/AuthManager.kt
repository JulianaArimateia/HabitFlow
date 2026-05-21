package com.habitflow.util

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class AuthManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("habitflow_prefs", Context.MODE_PRIVATE)

    fun register(username: String, password: String): Boolean {
        if (prefs.contains("user_$username")) return false
        prefs.edit()
            .putString("user_$username", hash(password))
            .apply()
        return true
    }

    fun login(username: String, password: String): Boolean {
        val stored = prefs.getString("user_$username", null) ?: return false
        return stored == hash(password)
    }

    fun saveSession(username: String) {
        prefs.edit().putString("session_user", username).apply()
    }

    fun clearSession() {
        prefs.edit().remove("session_user").apply()
    }

    fun getLoggedUser(): String? = prefs.getString("session_user", null)

    fun isLoggedIn(): Boolean = getLoggedUser() != null

    private fun hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
