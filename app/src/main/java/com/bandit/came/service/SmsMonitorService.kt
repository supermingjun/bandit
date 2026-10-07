package com.bandit.came.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bandit.came.BanditApp
import com.bandit.came.MainActivity
import com.bandit.came.R

class SmsMonitorService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, BanditApp.CHANNEL_MONITOR)
            .setContentTitle(getString(R.string.notification_monitor_title))
            .setContentText(getString(R.string.notification_monitor_text))
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1

        fun start(context: Context) {
            val intent = Intent(context, SmsMonitorService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, SmsMonitorService::class.java))
        }
    }
}
