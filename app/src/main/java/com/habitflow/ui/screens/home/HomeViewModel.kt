package com.habitflow.ui.screens.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.HabitFlowApplication
import com.habitflow.data.db.entity.Habit
import com.habitflow.data.remote.QuoteApi
import com.habitflow.data.remote.QuoteData
import com.habitflow.domain.usecase.SmartNotificationUseCase
import com.habitflow.util.AuthManager
import com.habitflow.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HabitWithStatus(
    val habit: Habit,
    val completedToday: Boolean,
    val completedAt: String? = null
)

data class HomeUiState(
    val habits: List<HabitWithStatus> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val userName: String = "",
    val quote: QuoteData? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as HabitFlowApplication
    private val repository = app.repository
    private val authManager = AuthManager(application)
    private val notifUseCase = SmartNotificationUseCase(repository, application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(userName = authManager.getLoggedUser() ?: "") }
        loadHabits()
        viewModelScope.launch { fetchQuote() }
        viewModelScope.launch {
            try { notifUseCase.scheduleAll() } catch (e: Exception) {
                Log.w("HomeViewModel", "Failed to schedule notifications on start", e)
            }
        }
    }

    private suspend fun fetchQuote() {
        val quote = QuoteApi.fetchToday()
        _uiState.update { it.copy(quote = quote) }
    }

    private fun loadHabits() {
        viewModelScope.launch {
            repository.getAllActiveHabits()
                .catch { e ->
                    Log.e("HomeViewModel", "Error loading habits", e)
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { habits ->
                    try {
                        val withStatus = buildHabitStatus(habits)
                        _uiState.update {
                            it.copy(
                                habits = withStatus,
                                completedCount = withStatus.count { h -> h.completedToday },
                                totalCount = withStatus.size,
                                isLoading = false,
                                error = null
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error processing habits", e)
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
        }
    }

    private suspend fun buildHabitStatus(habits: List<Habit>): List<HabitWithStatus> {
        val result = mutableListOf<HabitWithStatus>()
        for (habit in habits) {
            val completed = try {
                repository.isCompletedToday(habit.id)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error checking completion for habit ${habit.id}", e)
                false
            }
            val completedAt = if (completed) {
                try {
                    val logs = repository.getLastNCompleted(habit.id, 1)
                    logs.firstOrNull()?.let { DateUtils.formatTime(it.dataHora) }
                } catch (e: Exception) {
                    null
                }
            } else null
            result.add(HabitWithStatus(habit, completed, completedAt))
        }
        return result
    }

    fun toggleCheckIn(habit: Habit) {
        viewModelScope.launch {
            try {
                val isCompleted = repository.isCompletedToday(habit.id)
                if (isCompleted) {
                    repository.removeCheckIn(habit.id)
                } else {
                    repository.checkIn(habit.id)
                    try { notifUseCase.scheduleAll() } catch (e: Exception) {
                        Log.w("HomeViewModel", "Failed to schedule notifications", e)
                    }
                }
                // habit_logs change doesn't trigger the habits Flow, so refresh manually
                val refreshed = buildHabitStatus(_uiState.value.habits.map { it.habit })
                _uiState.update {
                    it.copy(
                        habits = refreshed,
                        completedCount = refreshed.count { h -> h.completedToday },
                        totalCount = refreshed.size
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error toggling check-in", e)
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            try { repository.deleteHabit(habitId) } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting habit", e)
            }
        }
    }

    fun logout() {
        authManager.clearSession()
    }
}
