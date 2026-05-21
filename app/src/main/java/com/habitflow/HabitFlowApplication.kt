package com.habitflow

import android.app.Application
import com.habitflow.data.db.AppDatabase
import com.habitflow.data.repository.HabitRepository

class HabitFlowApplication : Application() {

    val repository: HabitRepository by lazy {
        val db = AppDatabase.getInstance(this)
        HabitRepository(db.habitDao(), db.habitLogDao())
    }
}
