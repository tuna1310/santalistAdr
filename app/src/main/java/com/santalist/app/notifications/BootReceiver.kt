package com.santalist.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.santalist.app.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val peopleWithGifts = db.personDao().getAllPeopleWithGifts().first()
                    for (pwg in peopleWithGifts) {
                        for (gift in pwg.gifts) {
                            if (!gift.status.isCompleted && gift.dueDate != null) {
                                ReminderScheduler.scheduleReminder(
                                    context = context,
                                    gift = gift,
                                    recipientName = pwg.person.name
                                )
                            }
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
