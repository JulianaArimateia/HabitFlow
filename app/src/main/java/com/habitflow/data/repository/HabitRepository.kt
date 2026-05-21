package com.habitflow.data.repository

import com.habitflow.data.db.dao.HabitDao
import com.habitflow.data.db.dao.HabitLogDao
import com.habitflow.data.db.entity.Habit
import com.habitflow.data.db.entity.HabitLog
import com.habitflow.util.DateUtils
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val logDao: HabitLogDao
) {
    fun getAllActiveHabits(): Flow<List<Habit>> = habitDao.getAllActive()

    suspend fun getAllActiveOnce(): List<Habit> = habitDao.getAllActiveOnce()

    suspend fun getHabitById(id: Long): Habit? = habitDao.getById(id)

    suspend fun saveHabit(habit: Habit): Long = habitDao.insert(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    suspend fun deleteHabit(id: Long) = habitDao.softDelete(id)

    suspend fun checkIn(habitId: Long): HabitLog {
        val log = HabitLog(habitId = habitId)
        logDao.insert(log)
        return log
    }

    suspend fun removeCheckIn(habitId: Long) {
        val (start, end) = DateUtils.todayRange()
        val logs = logDao.getCompletedOnDate(habitId, start, end)
        logs.lastOrNull()?.let { logDao.delete(it) }
    }

    suspend fun isCompletedToday(habitId: Long): Boolean {
        val (start, end) = DateUtils.todayRange()
        return logDao.getCompletedOnDate(habitId, start, end).isNotEmpty()
    }

    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> = logDao.getLogsForHabit(habitId)

    suspend fun getLastNCompleted(habitId: Long, n: Int): List<HabitLog> =
        logDao.getLastCompleted(habitId, n)

    suspend fun getAllCompletedSince(since: Long): List<HabitLog> =
        logDao.getAllCompletedSince(since)

    suspend fun getAllCompletedOnDate(startOfDay: Long, endOfDay: Long): List<HabitLog> =
        logDao.getAllCompletedOnDate(startOfDay, endOfDay)

    suspend fun getAllCompletedForHabit(habitId: Long): List<HabitLog> =
        logDao.getAllCompletedForHabit(habitId)
}
