package com.bandit.came.ui.permission

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    navController: NavHostController,
    viewModel: PermissionViewModel = viewModel()
) {
    var refreshKey by remember { mutableStateOf(0) }

    val smsGranted = remember(refreshKey) { viewModel.hasSmsPermission() }
    val notificationGranted = remember(refreshKey) { viewModel.hasNotificationPermission() }
    val exactAlarmGranted = remember(refreshKey) { viewModel.hasExactAlarmPermission() }
    val overlayGranted = remember(refreshKey) { viewModel.canDrawOverlays() }
    val fullScreenGranted = remember(refreshKey) { viewModel.canUseFullScreenIntent() }
    val dndGranted = remember(refreshKey) { viewModel.hasDndAccess() }
    val listenerEnabled = remember(refreshKey) { viewModel.isNotificationListenerEnabled() }
    val batteryIgnored = remember(refreshKey) { viewModel.isIgnoringBatteryOptimizations() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { refreshKey++ }

    LaunchedEffect(Unit) { refreshKey++ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("权限中心") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PermissionItem(
                name = "接收短信",
                description = "实时监听普通短信",
                granted = smsGranted,
                onGrant = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.RECEIVE_SMS))
                }
            )

            PermissionItem(
                name = "通知使用权",
                description = "读取网络短信等消息通知内容",
                granted = listenerEnabled,
                onGrant = { viewModel.openNotificationListenerSettings() },
                refreshKey = refreshKey
            )

            PermissionItem(
                name = "通知权限",
                description = "发送弱提醒通知",
                granted = notificationGranted,
                onGrant = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                }
            )

            PermissionItem(
                name = "悬浮窗",
                description = "强提醒时直接弹出闹钟界面",
                granted = overlayGranted,
                onGrant = { viewModel.openOverlaySettings() },
                refreshKey = refreshKey
            )

            PermissionItem(
                name = "精确闹钟",
                description = "创建强提醒闹钟",
                granted = exactAlarmGranted,
                onGrant = { viewModel.openExactAlarmSettings() },
                refreshKey = refreshKey
            )

            PermissionItem(
                name = "全屏通知",
                description = "锁屏时全屏弹出闹钟",
                granted = fullScreenGranted,
                onGrant = { viewModel.openFullScreenIntentSettings() },
                refreshKey = refreshKey
            )

            PermissionItem(
                name = "勿扰权限",
                description = "勿扰模式下仍可响铃",
                granted = dndGranted,
                onGrant = { viewModel.openDndSettings() },
                refreshKey = refreshKey
            )

            PermissionItem(
                name = "忽略电池优化",
                description = "保证后台持续运行不被杀",
                granted = batteryIgnored,
                onGrant = { viewModel.openBatteryOptimizationSettings() },
                refreshKey = refreshKey
            )

            PermissionItem(
                name = "自启动",
                description = "开机后自动恢复监控服务（澎湃OS）",
                granted = null,
                onGrant = { viewModel.openAutoStartSettings() }
            )

            PermissionItem(
                name = "小米其他权限",
                description = "后台弹出界面等澎湃OS权限，强提醒必须允许后台弹出界面",
                granted = null,
                onGrant = { viewModel.openMiuiExtraPermissions() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { refreshKey++ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("刷新状态")
            }
        }
    }
}

@Composable
private fun PermissionItem(
    name: String,
    description: String,
    granted: Boolean?,
    onGrant: () -> Unit,
    refreshKey: Int = 0
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when (granted) {
                true -> Text("✅ 已授权", color = Color(0xFF4CAF50))
                false -> Button(onClick = onGrant) { Text("授权") }
                null -> OutlinedButton(onClick = onGrant) { Text("去设置") }
            }
        }
    }
}
