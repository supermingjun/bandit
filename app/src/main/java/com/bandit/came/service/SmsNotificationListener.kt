package com.bandit.came.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.bandit.came.matcher.MatchDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification: Notification = sbn?.notification ?: return
        if (sbn.packageName == packageName) return
        if (notification.category != Notification.CATEGORY_MESSAGE) return

        val extras = notification.extras ?: return
        val sender = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: sbn.packageName
        val body = (
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
                ?: extras.getCharSequence(Notification.EXTRA_TEXT)
            )?.toString() ?: return

        val timestamp = sbn.postTime
        CoroutineScope(Dispatchers.IO).launch {
            MatchDispatcher.handle(applicationContext, sender, body, timestamp)
        }
    }
}
