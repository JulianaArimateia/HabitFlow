package com.habitflow.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.habitflow.HabitFlowApplication
import com.habitflow.domain.usecase.SmartNotificationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Reagenda os alarmes após reinicialização do dispositivo (AlarmManager perde os alarmes)
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as HabitFlowApplication
                SmartNotificationUseCase(app.repository, context).scheduleAll()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
