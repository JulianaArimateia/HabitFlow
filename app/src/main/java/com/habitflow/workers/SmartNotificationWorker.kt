package com.habitflow.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitflow.HabitFlowApplication
import com.habitflow.MainActivity
import com.habitflow.domain.usecase.SmartNotificationUseCase

class SmartNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_HABIT_ID   = "habit_id"
        const val KEY_HABIT_NAME = "habit_name"
        private const val CHANNEL_ID = "habitflow_notifications"
    }

    override suspend fun doWork(): Result {
        val habitId   = inputData.getLong(KEY_HABIT_ID, -1L)
        val habitName = inputData.getString(KEY_HABIT_NAME) ?: "Hábito"

        if (habitId == -1L) return Result.failure()

        createChannel()
        showNotification(habitId.toInt(), habitName)

        val app = context.applicationContext as HabitFlowApplication
        SmartNotificationUseCase(app.repository, context).scheduleAll()

        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Lembretes de Hábitos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações inteligentes dos seus hábitos"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(id: Int, habitName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("HabitFlow")
            .setContentText("Hora do seu hábito: $habitName!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(id, notification)
    }
}
