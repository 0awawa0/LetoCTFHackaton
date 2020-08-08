package ru.sinura.hackaton.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import ru.sinura.hackaton.R
import ru.sinura.hackaton.main.MainActivity
import ru.sinura.hackaton.main.ui.news.NewsFragment
import ru.sinura.hackaton.main.ui.news.NewsModel
import ru.sinura.hackaton.repo.Repo


class NotificationBroadcast: BroadcastReceiver() {

    val repo = Repo.getInstance()

    override fun onReceive(context: Context?, intent: Intent?) {

        val responseCallback = object : NewsFragment.NewsResponse {
            override fun onSuccess(news: Array<NewsModel>) {
                showNotification(context!!, news[0].header)
            }

            override fun onError() {}
        }
        Log.e("NotificationBroadcast", "BroadcastReceived")
        repo.newsResponse = responseCallback
        repo.getNews()
        NotificationWorker.scheduleNotification(context!!)
    }

    fun showNotification(context: Context, message: String) {
        createNotificationChannel(context)

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            0
        )
        val notification = NotificationCompat.Builder(context, "enigma_channel")
            .setContentTitle("Новости")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_news)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            .build()

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, notification)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "enigma_channel",
                "news_notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(notificationChannel)
        }
    }
}