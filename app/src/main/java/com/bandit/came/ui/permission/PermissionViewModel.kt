package com.bandit.came.ui.permission

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.bandit.came.service.SmsNotificationListener

class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    fun hasSmsPermission(): Boolean =
        hasPermission(android.Manifest.permission.RECEIVE_SMS)

    fun hasNotificationPermission(): Boolean =
        hasPermission(android.Manifest.permission.POST_NOTIFICATIONS)

    fun hasExactAlarmPermission(): Boolean {
        val manager = getApplication<Application>()
            .getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        return manager.canScheduleExactAlarms()
    }

    fun canDrawOverlays(): Boolean =
        Settings.canDrawOverlays(getApplication())

    fun canUseFullScreenIntent(): Boolean {
        val manager = getApplication<Application>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        return manager.canUseFullScreenIntent()
    }

    fun hasDndAccess(): Boolean {
        val manager = getApplication<Application>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        return manager.isNotificationPolicyAccessGranted
    }

    fun isNotificationListenerEnabled(): Boolean {
        val context = getApplication<Application>()
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        val myComponent = ComponentName(context, SmsNotificationListener::class.java)
        return enabledListeners.contains(myComponent.flattenToString())
    }

    fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = getApplication<Application>()
            .getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(getApplication<Application>().packageName)
    }

    fun openBatteryOptimizationSettings() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openExactAlarmSettings() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openFullScreenIntentSettings() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openOverlaySettings() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openDndSettings() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openNotificationListenerSettings() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openAutoStartSettings() {
        val context = getApplication<Application>()
        val intent = Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        }
        runCatching { context.startActivity(intent) }.onFailure {
            openMiuiExtraPermissions()
        }
    }

    fun openMiuiExtraPermissions() {
        val context = getApplication<Application>()
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
            setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            putExtra("extra_pkgname", context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }.onFailure {
            openAppDetailSettings()
        }
    }

    fun openAppDetailSettings() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            getApplication(), permission
        ) == PackageManager.PERMISSION_GRANTED
}
