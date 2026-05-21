package com.habitflow.domain.usecase

import android.content.Context
import com.habitflow.data.repository.HabitRepository
import com.habitflow.util.DateUtils
import com.habitflow.workers.NotificationReceiver
import java.util.Calendar

class SmartNotificationUseCase(
    private val repository: HabitRepository,
    private val context: Context
) {
    suspend fun scheduleAll() {
        val habits = repository.getAllActiveOnce()
        habits.forEach { habit ->
            val logs = repository.getLastNCompleted(habit.id, 7)
            val minutesOfDay = if (logs.size >= 2) {
                DateUtils.averageMinutesOfDay(logs.map { it.dataHora })
            } else {
                val (h, m) = DateUtils.parseHHmm(habit.horarioPreferencial)
                h * 60 + m
            }
            scheduleForHabit(habit.id, habit.nome, minutesOfDay)
        }
    }

    private fun scheduleForHabit(habitId: Long, habitName: String, minutesOfDay: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, minutesOfDay / 60)
            set(Calendar.MINUTE, minutesOfDay % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        NotificationReceiver.scheduleAlarm(context, habitId, habitName, minutesOfDay, target.timeInMillis)
    }
}
