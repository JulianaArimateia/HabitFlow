package com.habitflow.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitflow.data.db.dao.HabitDao
import com.habitflow.data.db.dao.HabitLogDao
import com.habitflow.data.db.entity.Habit
import com.habitflow.data.db.entity.HabitLog

@Database(
    entities = [Habit::class, HabitLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habitflow.db"
                ).build().also { INSTANCE = it }
            }
    }
}
