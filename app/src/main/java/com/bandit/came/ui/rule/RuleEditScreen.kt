package com.bandit.came.ui.rule

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bandit.came.data.local.entity.MatchScope
import com.bandit.came.data.local.entity.MatchType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleEditScreen(
    navController: NavHostController,
    ruleId: Long,
    viewModel: RuleEditViewModel = viewModel()
) {
    LaunchedEffect(ruleId) { viewModel.loadRule(ruleId) }

    val keyword by viewModel.keyword.collectAsState()
    val matchType by viewModel.matchType.collectAsState()
    val weakEnabled by viewModel.weakEnabled.collectAsState()
    val strongEnabled by viewModel.strongEnabled.collectAsState()
    val matchScope by viewModel.matchScope.collectAsState()
    val enabled by viewModel.enabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ruleId > 0) "编辑规则" else "新建规则") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = keyword,
                onValueChange = { viewModel.updateKeyword(it) },
                label = { Text("关键词 / 正则 / 通配符") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("匹配方式", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MatchType.entries.forEach { type ->
                    FilterChip(
                        selected = matchType == type,
                        onClick = { viewModel.updateMatchType(type) },
                        label = {
                            Text(
                                when (type) {
                                    MatchType.CONTAINS -> "包含"
                                    MatchType.REGEX -> "正则"
                                    MatchType.WILDCARD -> "通配符"
                                }
                            )
                        }
                    )
                }
            }

            Text("匹配范围", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MatchScope.entries.forEach { scope ->
                    FilterChip(
                        selected = matchScope == scope,
                        onClick = { viewModel.updateMatchScope(scope) },
                        label = {
                            Text(
                                when (scope) {
                                    MatchScope.BODY -> "正文"
                                    MatchScope.SENDER -> "发件人"
                                    MatchScope.BOTH -> "两者"
                                }
                            )
                        }
                    )
                }
            }

            Text("提醒级别（可多选）", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("弱提醒（通知栏）", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = weakEnabled, onCheckedChange = { viewModel.updateWeakEnabled(it) })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("强提醒（全屏闹钟）", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = strongEnabled, onCheckedChange = { viewModel.updateStrongEnabled(it) })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("启用", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = enabled, onCheckedChange = { viewModel.updateEnabled(it) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.save { navController.popBackStack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = keyword.isNotBlank() && (weakEnabled || strongEnabled)
            ) {
                Text("保存")
            }
        }
    }
}
