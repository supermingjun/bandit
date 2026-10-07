package com.bandit.came.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bandit.came.service.SmsMonitorService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            SmsMonitorService.start(context)
        }
    }
}
