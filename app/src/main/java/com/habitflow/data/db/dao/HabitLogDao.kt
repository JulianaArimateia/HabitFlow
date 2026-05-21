package com.habitflow.data.db.dao

import androidx.room.*
import com.habitflow.data.db.entity.HabitLog
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HabitLog): Long

    @Delete
    suspend fun delete(log: HabitLog)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY dataHora DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>>

    @Query("""
        SELECT * FROM habit_logs
        WHERE habitId = :habitId
          AND dataHora >= :startOfDay
          AND dataHora < :endOfDay
          AND concluido = 1
    """)
    suspend fun getCompletedOnDate(habitId: Long, startOfDay: Long, endOfDay: Long): List<HabitLog>

    @Query("""
        SELECT * FROM habit_logs
        WHERE habitId = :habitId
          AND concluido = 1
        ORDER BY dataHora DESC
        LIMIT :limit
    """)
    suspend fun getLastCompleted(habitId: Long, limit: Int): List<HabitLog>

    @Query("""
        SELECT * FROM habit_logs
        WHERE dataHora >= :since
          AND concluido = 1
        ORDER BY dataHora DESC
    """)
    suspend fun getAllCompletedSince(since: Long): List<HabitLog>

    @Query("""
        SELECT * FROM habit_logs
        WHERE dataHora >= :startOfDay
          AND dataHora < :endOfDay
          AND concluido = 1
    """)
    suspend fun getAllCompletedOnDate(startOfDay: Long, endOfDay: Long): List<HabitLog>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND concluido = 1 ORDER BY dataHora DESC")
    suspend fun getAllCompletedForHabit(habitId: Long): List<HabitLog>
}
