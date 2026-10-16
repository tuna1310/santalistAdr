package com.santalist.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.santalist.app.data.local.AppDatabase
import com.santalist.app.data.local.SampleDataLoader
import com.santalist.app.notifications.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SantaListApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Create Notification Channel
        createNotificationChannel()

        // Seed sample data in background coroutine if first launch
        val db = AppDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            SampleDataLoader.seedInitialDataIfNeeded(this@SantaListApplication, db)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderScheduler.CHANNEL_ID,
                ReminderScheduler.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming gift deadlines"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
