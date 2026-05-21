package com.habitflow.ui.screens.habit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.HabitFlowApplication
import com.habitflow.data.db.entity.Habit
import com.habitflow.domain.usecase.SmartNotificationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HabitFormState(
    val nome: String = "",
    val categoria: String = "Saúde",
    val frequencia: String = "DIARIO",
    val diasSemana: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val horario: String = "07:30",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class AddEditHabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as HabitFlowApplication).repository
    private val notifUseCase = SmartNotificationUseCase(repo, application)

    private val _formState = MutableStateFlow(HabitFormState())
    val formState: StateFlow<HabitFormState> = _formState

    fun loadHabit(habitId: Long) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            val habit = repo.getHabitById(habitId)
            if (habit != null) {
                val days = habit.diasSemana.split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toSet()
                _formState.update {
                    it.copy(
                        nome = habit.nome,
                        categoria = habit.categoria,
                        frequencia = habit.frequencia,
                        diasSemana = days.ifEmpty { setOf(1, 2, 3, 4, 5, 6, 7) },
                        horario = habit.horarioPreferencial,
                        isLoading = false
                    )
                }
            } else {
                _formState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNomeChange(v: String)      = _formState.update { it.copy(nome = v, error = null) }
    fun onCategoriaChange(v: String) = _formState.update { it.copy(categoria = v) }
    fun onFrequenciaChange(v: String) = _formState.update { it.copy(frequencia = v) }
    fun onHorarioChange(v: String)   = _formState.update { it.copy(horario = v) }

    fun toggleDay(day: Int) {
        _formState.update { state ->
            val days = state.diasSemana.toMutableSet()
            if (days.contains(day)) days.remove(day) else days.add(day)
            state.copy(diasSemana = days)
        }
    }

    fun save(habitId: Long?) {
        val s = _formState.value
        if (s.nome.isBlank()) {
            _formState.update { it.copy(error = "Informe o nome do hábito") }
            return
        }
        if (s.frequencia == "ESPECIFICO" && s.diasSemana.isEmpty()) {
            _formState.update { it.copy(error = "Selecione ao menos um dia") }
            return
        }
        viewModelScope.launch {
            val habit = Habit(
                id = habitId ?: 0,
                nome = s.nome.trim(),
                categoria = s.categoria,
                frequencia = s.frequencia,
                diasSemana = s.diasSemana.sorted().joinToString(","),
                horarioPreferencial = s.horario
            )
            repo.saveHabit(habit)
            try { notifUseCase.scheduleAll() } catch (e: Exception) {
                Log.w("AddEditHabitVM", "Failed to schedule notifications", e)
            }
            _formState.update { it.copy(isSaved = true) }
        }
    }
}
