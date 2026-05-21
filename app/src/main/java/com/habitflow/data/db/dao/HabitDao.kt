package com.habitflow.data.db.dao

import androidx.room.*
import com.habitflow.data.db.entity.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Query("UPDATE habits SET ativo = 0 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT * FROM habits WHERE ativo = 1 ORDER BY dataCriacao ASC")
    fun getAllActive(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): Habit?

    @Query("SELECT * FROM habits WHERE ativo = 1 ORDER BY dataCriacao ASC")
    suspend fun getAllActiveOnce(): List<Habit>
}
