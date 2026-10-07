package com.bandit.came.alert

import android.app.KeyguardManager
import android.app.NotificationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bandit.came.ui.theme.BanditTheme

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        turnScreenOn()

        val sender = intent.getStringExtra(EXTRA_SENDER) ?: "未知号码"
        val body = intent.getStringExtra(EXTRA_BODY) ?: ""
        val keyword = intent.getStringExtra(EXTRA_KEYWORD) ?: ""

        setContent {
            BanditTheme {
                AlarmScreen(
                    sender = sender,
                    body = body,
                    keyword = keyword,
                    onDismiss = { dismissAlarm() }
                )
            }
        }
    }

    private fun turnScreenOn() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager.requestDismissKeyguard(this, null)
    }

    private fun dismissAlarm() {
        AlarmRingService.stop(this)
        getSystemService(NotificationManager::class.java).cancel(AlarmRingService.NOTIFICATION_ID)
        finish()
    }

    companion object {
        const val EXTRA_SENDER = "extra_sender"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_KEYWORD = "extra_keyword"
    }
}

@Composable
private fun AlarmScreen(
    sender: String,
    body: String,
    keyword: String,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "土匪来了！",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "命中关键词: $keyword",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "发件人",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = sender,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "短信内容",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("关闭闹钟", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
