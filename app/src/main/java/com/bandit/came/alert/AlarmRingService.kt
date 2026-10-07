package com.bandit.came.alert

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.bandit.came.BanditApp
import com.bandit.came.R

class AlarmRingService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopRinging()
            else -> startRinging(intent)
        }
        return START_NOT_STICKY
    }

    private fun startRinging(intent: Intent?) {
        val sender = intent?.getStringExtra(AlarmActivity.EXTRA_SENDER) ?: "未知号码"
        val body = intent?.getStringExtra(AlarmActivity.EXTRA_BODY) ?: ""
        val keyword = intent?.getStringExtra(AlarmActivity.EXTRA_KEYWORD) ?: ""

        val alarmIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra(AlarmActivity.EXTRA_SENDER, sender)
            putExtra(AlarmActivity.EXTRA_BODY, body)
            putExtra(AlarmActivity.EXTRA_KEYWORD, keyword)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pending = PendingIntent.getActivity(
            this, 0, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, BanditApp.CHANNEL_ALERT_STRONG)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("土匪来了！")
            .setContentText("命中规则: $keyword")
            .setStyle(NotificationCompat.BigTextStyle().bigText("发件人: $sender\n$body"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pending, true)
            .setContentIntent(pending)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(
            NOTIFICATION_ID, notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )

        startAlarmSound()
        startVibration()
    }

    private fun startAlarmSound() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: return
        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmRingService, alarmUri)
                isLooping = true
                prepare()
                start()
            }
        }
    }

    private fun startVibration() {
        val vibratorManager = getSystemService(VibratorManager::class.java)
        vibrator = vibratorManager.defaultVibrator
        val pattern = longArrayOf(0, 800, 400, 800, 400, 800)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun stopRinging() {
        mediaPlayer?.apply {
            runCatching { if (isPlaying) stop() }
            release()
        }
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 200
        private const val ACTION_STOP = "com.bandit.came.action.STOP_ALARM"

        fun start(context: Context, sender: String, body: String, keyword: String) {
            val intent = Intent(context, AlarmRingService::class.java).apply {
                putExtra(AlarmActivity.EXTRA_SENDER, sender)
                putExtra(AlarmActivity.EXTRA_BODY, body)
                putExtra(AlarmActivity.EXTRA_KEYWORD, keyword)
            }
            runCatching { context.startForegroundService(intent) }
        }

        fun stop(context: Context) {
            val intent = Intent(context, AlarmRingService::class.java).apply {
                action = ACTION_STOP
            }
            runCatching { context.startService(intent) }
            context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
        }
    }
}
