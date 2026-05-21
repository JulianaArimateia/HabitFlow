package com.habitflow.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.HabitFlowApplication
import com.habitflow.data.db.entity.Habit
import com.habitflow.data.remote.WeatherApi
import com.habitflow.data.remote.WeatherData
import com.habitflow.domain.usecase.CalculateStreakUseCase
import com.habitflow.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HabitConsistency(
    val habit: Habit,
    val rate: Float,
    val streak: Int
)

data class DashboardUiState(
    val streak: Int = 0,
    val completedRate: Float = 0f,
    val last7Days: List<Float> = List(7) { 0f },
    val heatmapData: Map<Long, Int> = emptyMap(),
    val habitConsistencies: List<HabitConsistency> = emptyList(),
    val weather: WeatherData? = null,
    val isLoading: Boolean = true
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as HabitFlowApplication
    private val repository = app.repository
    private val streakUseCase = CalculateStreakUseCase(repository)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadData() }
    }

    private suspend fun loadData() {
        val streak = streakUseCase.executeOverall()
        val last7 = streakUseCase.last7DaysCompletion()

        val habits = repository.getAllActiveOnce()
        val habitConsistencies = habits.map { habit ->
            HabitConsistency(
                habit = habit,
                rate = streakUseCase.consistencyRate(habit.id),
                streak = streakUseCase.execute(habit.id)
            )
        }.sortedByDescending { it.rate }

        val overallRate = if (last7.isNotEmpty()) last7.average().toFloat() * 100f else 0f

        val heatmap = buildHeatmap()

        _uiState.update {
            it.copy(
                streak = streak,
                completedRate = overallRate,
                last7Days = last7,
                heatmapData = heatmap,
                habitConsistencies = habitConsistencies,
                isLoading = false
            )
        }
    }

    private suspend fun buildHeatmap(): Map<Long, Int> {
        val days = DateUtils.currentMonthDays()
        val dayMs = 24 * 60 * 60 * 1000L
        val result = mutableMapOf<Long, Int>()
        for (dayStart in days) {
            val logs = repository.getAllCompletedOnDate(dayStart, dayStart + dayMs)
            result[dayStart] = logs.size
        }
        return result
    }

    fun fetchWeather(lat: Double? = null, lon: Double? = null) {
        viewModelScope.launch {
            val weather = if (lat != null && lon != null) WeatherApi.fetch(lat, lon)
                          else WeatherApi.fetch()
            _uiState.update { it.copy(weather = weather) }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch { loadData() }
    }
}
