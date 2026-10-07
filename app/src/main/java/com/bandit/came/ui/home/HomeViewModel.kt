package com.bandit.came.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.came.BanditApp
import com.bandit.came.data.local.entity.RuleEntity
import com.bandit.came.service.SmsMonitorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val ruleRepository = (application as BanditApp).ruleRepository

    val rules: StateFlow<List<RuleEntity>> = ruleRepository.getAllRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _monitoring = MutableStateFlow(true)
    val monitoring: StateFlow<Boolean> = _monitoring.asStateFlow()

    fun toggleMonitoring(enabled: Boolean) {
        _monitoring.value = enabled
        val context = getApplication<Application>()
        if (enabled) {
            SmsMonitorService.start(context)
        } else {
            SmsMonitorService.stop(context)
        }
    }

    fun deleteRule(rule: RuleEntity) {
        viewModelScope.launch { ruleRepository.delete(rule) }
    }

    fun toggleRule(rule: RuleEntity, enabled: Boolean) {
        viewModelScope.launch { ruleRepository.update(rule.copy(enabled = enabled)) }
    }
}
