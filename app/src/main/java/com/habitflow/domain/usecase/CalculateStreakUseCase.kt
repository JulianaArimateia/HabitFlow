package com.habitflow.domain.usecase

import com.habitflow.data.repository.HabitRepository
import com.habitflow.util.DateUtils
import java.util.Calendar

class CalculateStreakUseCase(private val repository: HabitRepository) {

    suspend fun execute(habitId: Long): Int {
        val logs = repository.getAllCompletedForHabit(habitId)
        if (logs.isEmpty()) return 0

        val completedDays = logs
            .map { DateUtils.startOfDay(it.dataHora) }
            .toSortedSet(reverseOrder())

        val dayMs = 24 * 60 * 60 * 1000L
        var streak = 0
        var checkDay = DateUtils.startOfDay(System.currentTimeMillis())

        while (completedDays.contains(checkDay)) {
            streak++
            checkDay -= dayMs
        }
        return streak
    }

    suspend fun executeOverall(): Int {
        val habits = repository.getAllActiveOnce()
        if (habits.isEmpty()) return 0

        val dayMs = 24 * 60 * 60 * 1000L
        var streak = 0
        var checkDay = DateUtils.startOfDay(System.currentTimeMillis())

        while (true) {
            val start = checkDay
            val end = checkDay + dayMs
            val logs = repository.getAllCompletedOnDate(start, end)
            if (logs.isEmpty()) break
            streak++
            checkDay -= dayMs
        }
        return streak
    }

    suspend fun consistencyRate(habitId: Long): Float {
        val habit = repository.getHabitById(habitId) ?: return 0f
        val dayMs = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        val created = habit.dataCriacao
        val totalDays = ((now - created) / dayMs).toInt().coerceAtLeast(1)

        val logs = repository.getAllCompletedForHabit(habitId)
        val completedDays = logs.map { DateUtils.startOfDay(it.dataHora) }.toSet()
        return (completedDays.size.toFloat() / totalDays * 100f).coerceIn(0f, 100f)
    }

    suspend fun last7DaysCompletion(): List<Float> {
        val habits = repository.getAllActiveOnce()
        val dayMs = 24 * 60 * 60 * 1000L
        val result = mutableListOf<Float>()

        for (daysAgo in 6 downTo 0) {
            val (start, end) = DateUtils.dayRange(daysAgo)
            val logs = repository.getAllCompletedOnDate(start, end)
            val completedIds = logs.map { it.habitId }.toSet()
            val habitsForDay = habits.filter { habit ->
                if (habit.frequencia == "DIARIO") true
                else {
                    val cal = Calendar.getInstance().apply { timeInMillis = start }
                    val dow = cal.get(Calendar.DAY_OF_WEEK).toString()
                    habit.diasSemana.split(",").contains(dow)
                }
            }
            val rate = if (habitsForDay.isEmpty()) 0f
            else habitsForDay.count { it.id in completedIds }.toFloat() / habitsForDay.size
            result.add(rate)
        }
        return result
    }
}
