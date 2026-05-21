package com.habitflow.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val categoria: String,
    val frequencia: String,       // "DIARIO" | "ESPECIFICO"
    val diasSemana: String = "",  // "1,2,3" = Dom,Seg,Ter (Calendar.DAY_OF_WEEK values)
    val horarioPreferencial: String, // "HH:mm"
    val dataCriacao: Long = System.currentTimeMillis(),
    val ativo: Boolean = true
)
