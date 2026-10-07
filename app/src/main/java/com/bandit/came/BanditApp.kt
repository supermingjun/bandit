package com.bandit.came

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import com.bandit.came.data.local.BanditDatabase
import com.bandit.came.data.repository.HistoryRepository
import com.bandit.came.data.repository.RuleRepository
import com.bandit.came.service.SmsMonitorService

class BanditApp : Application() {

    val database: BanditDatabase by lazy { BanditDatabase.getInstance(this) }
    val ruleRepository: RuleRepository by lazy { RuleRepository(database.ruleDao()) }
    val historyRepository: HistoryRepository by lazy { HistoryRepository(database.historyDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
        SmsMonitorService.start(this)
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val monitorChannel = NotificationChannel(
            CHANNEL_MONITOR,
            getString(R.string.notification_channel_monitor),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setShowBadge(false)
        }

        val weakAlertChannel = NotificationChannel(
            CHANNEL_ALERT_WEAK,
            getString(R.string.notification_channel_alert_weak),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            enableLights(true)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            )
        }

        val strongAlertChannel = NotificationChannel(
            CHANNEL_ALERT_STRONG,
            getString(R.string.notification_channel_alert_strong),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 800, 400, 800)
            enableLights(true)
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
        }

        manager.createNotificationChannels(listOf(monitorChannel, weakAlertChannel, strongAlertChannel))
    }

    companion object {
        const val CHANNEL_MONITOR = "bandit_monitor"
        const val CHANNEL_ALERT_WEAK = "bandit_alert_weak_v2"
        const val CHANNEL_ALERT_STRONG = "bandit_alert_strong_v2"

        lateinit var instance: BanditApp
            private set
    }
}
