package com.bandit.came.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bandit.came.data.local.entity.RuleEntity
import com.bandit.came.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val rules by viewModel.rules.collectAsState()
    val monitoring by viewModel.monitoring.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("土匪来了") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.PERMISSION) }) {
                        Icon(Icons.Default.Security, contentDescription = "权限中心")
                    }
                    IconButton(onClick = { navController.navigate(Routes.HISTORY) }) {
                        Icon(Icons.Default.History, contentDescription = "土匪历史")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.ruleEdit()) }) {
                Icon(Icons.Default.Add, contentDescription = "新建规则")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (monitoring) "监控中" else "已暂停",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (monitoring) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Switch(checked = monitoring, onCheckedChange = { viewModel.toggleMonitoring(it) })
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (rules.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "还没有土匪指令\n点击右下角 + 新建规则",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(rules, key = { it.id }) { rule ->
                        RuleCard(
                            rule = rule,
                            onToggle = { enabled -> viewModel.toggleRule(rule, enabled) },
                            onEdit = { navController.navigate(Routes.ruleEdit(rule.id)) },
                            onDelete = { viewModel.deleteRule(rule) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RuleCard(
    rule: RuleEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.keyword,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = buildString {
                        append(rule.matchType.name)
                        append(" · ")
                        append(rule.matchScope.name)
                        append(" · ")
                        append(
                            when {
                                rule.weakEnabled && rule.strongEnabled -> "弱+强提醒"
                                rule.strongEnabled -> "强提醒(闹钟)"
                                else -> "弱提醒(通知)"
                            }
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = rule.enabled, onCheckedChange = onToggle)
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
