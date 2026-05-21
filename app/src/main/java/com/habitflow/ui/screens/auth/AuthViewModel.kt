package com.habitflow.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.habitflow.util.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = AuthManager(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos")
            return
        }
        if (authManager.login(username.trim(), password)) {
            authManager.saveSession(username.trim())
            _uiState.value = AuthUiState.Success
        } else {
            _uiState.value = AuthUiState.Error("Usuário ou senha inválidos")
        }
    }

    fun register(username: String, password: String, confirm: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos")
            return
        }
        if (password != confirm) {
            _uiState.value = AuthUiState.Error("As senhas não coincidem")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Senha deve ter ao menos 6 caracteres")
            return
        }
        val ok = authManager.register(username.trim(), password)
        if (ok) {
            authManager.saveSession(username.trim())
            _uiState.value = AuthUiState.Success
        } else {
            _uiState.value = AuthUiState.Error("Usuário já existe")
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
