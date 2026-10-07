package com.bandit.came.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.bandit.came.matcher.MatchDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val sender = messages.first().originatingAddress ?: "未知号码"
        val body = messages.joinToString("") { it.messageBody ?: "" }
        val timestamp = messages.first().timestampMillis

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                MatchDispatcher.handle(context, sender, body, timestamp)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
