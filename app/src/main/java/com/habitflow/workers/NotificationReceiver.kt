package com.habitflow.workers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.habitflow.MainActivity
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(KEY_HABIT_ID, -1L)
        val habitName = intent.getStringExtra(KEY_HABIT_NAME) ?: return
        val minutesOfDay = intent.getIntExtra(KEY_MINUTES, -1)
        if (habitId == -1L || minutesOfDay < 0) return

        showNotification(context, habitId.toInt(), habitName)
        scheduleNextDay(context, habitId, habitName, minutesOfDay)
    }

    private fun showNotification(context: Context, id: Int, habitName: String) {
        createChannel(context)
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("HabitFlow 🌱")
            .setContentText("Hora do seu hábito: $habitName!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(id, notification)
    }

    private fun scheduleNextDay(context: Context, habitId: Long, habitName: String, minutesOfDay: Int) {
        val target = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, minutesOfDay / 60)
            set(Calendar.MINUTE, minutesOfDay % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        scheduleAlarm(context, habitId, habitName, minutesOfDay, target.timeInMillis)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Lembretes de Hábitos", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notificações dos seus hábitos" }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val KEY_HABIT_ID = "habit_id"
        const val KEY_HABIT_NAME = "habit_name"
        const val KEY_MINUTES = "minutes_of_day"
        const val CHANNEL_ID = "habitflow_notifications"

        fun scheduleAlarm(context: Context, habitId: Long, habitName: String, minutesOfDay: Int, triggerAtMillis: Long) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(KEY_HABIT_ID, habitId)
                putExtra(KEY_HABIT_NAME, habitName)
                putExtra(KEY_MINUTES, minutesOfDay)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, habitId.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms() ->
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                else ->
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }

        fun cancel(context: Context, habitId: Long) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, habitId.toInt(), intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }
}
