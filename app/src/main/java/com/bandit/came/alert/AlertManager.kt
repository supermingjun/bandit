package com.bandit.came.alert

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.bandit.came.BanditApp
import com.bandit.came.R

object AlertManager {

    private var notificationIdCounter = 100

    fun fireWeak(context: Context, sender: String, body: String, keyword: String) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val id = notificationIdCounter++

        val notification = NotificationCompat.Builder(context, BanditApp.CHANNEL_ALERT_WEAK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("土匪来了: $sender")
            .setContentText(body.take(80))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .build()

        manager.notify(id, notification)
    }

    fun fireStrong(context: Context, sender: String, body: String, keyword: String) {
        AlarmRingService.start(context, sender, body, keyword)

        if (Settings.canDrawOverlays(context)) {
            val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
                putExtra(AlarmActivity.EXTRA_SENDER, sender)
                putExtra(AlarmActivity.EXTRA_BODY, body)
                putExtra(AlarmActivity.EXTRA_KEYWORD, keyword)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            runCatching { context.startActivity(alarmIntent) }
        }
    }
}
