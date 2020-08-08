package ru.sinura.hackaton.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import ru.sinura.hackaton.BuildConfig
import java.util.*

class NotificationWorker {

    companion object {
        fun scheduleNotification(context: Context) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    Date().time + 1000 * 60 * 60 * 24,
                    PendingIntent.getBroadcast(context, 1, Intent(context, NotificationBroadcast::class.java), 0)
                )
                Log.e("NotificationWorker", "Scheduled notification")
            } else {
                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExact(
                    AlarmManager.RTC_WAKEUP,
                    Date().time + 1000 * 60 * 60 * 24,
                    PendingIntent.getBroadcast(context, 1, Intent(context, NotificationBroadcast::class.java), 0)
                )
                Log.e("NotificationWorker", "Scheduled notification")
            }
        }
    }
}