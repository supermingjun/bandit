package com.bandit.came.ui.rule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.came.BanditApp
import com.bandit.came.data.local.entity.MatchScope
import com.bandit.came.data.local.entity.MatchType
import com.bandit.came.data.local.entity.RuleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RuleEditViewModel(application: Application) : AndroidViewModel(application) {

    private val ruleRepository = (application as BanditApp).ruleRepository

    private val _keyword = MutableStateFlow("")
    val keyword: StateFlow<String> = _keyword.asStateFlow()

    private val _matchType = MutableStateFlow(MatchType.CONTAINS)
    val matchType: StateFlow<MatchType> = _matchType.asStateFlow()

    private val _weakEnabled = MutableStateFlow(true)
    val weakEnabled: StateFlow<Boolean> = _weakEnabled.asStateFlow()

    private val _strongEnabled = MutableStateFlow(false)
    val strongEnabled: StateFlow<Boolean> = _strongEnabled.asStateFlow()

    private val _matchScope = MutableStateFlow(MatchScope.BODY)
    val matchScope: StateFlow<MatchScope> = _matchScope.asStateFlow()

    private val _enabled = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private var editingId: Long = -1L

    fun loadRule(ruleId: Long) {
        if (ruleId <= 0) return
        viewModelScope.launch {
            ruleRepository.getRuleById(ruleId)?.let { rule ->
                editingId = rule.id
                _keyword.value = rule.keyword
                _matchType.value = rule.matchType
                _weakEnabled.value = rule.weakEnabled
                _strongEnabled.value = rule.strongEnabled
                _matchScope.value = rule.matchScope
                _enabled.value = rule.enabled
            }
        }
    }

    fun updateKeyword(value: String) { _keyword.value = value }
    fun updateMatchType(value: MatchType) { _matchType.value = value }
    fun updateWeakEnabled(value: Boolean) { _weakEnabled.value = value }
    fun updateStrongEnabled(value: Boolean) { _strongEnabled.value = value }
    fun updateMatchScope(value: MatchScope) { _matchScope.value = value }
    fun updateEnabled(value: Boolean) { _enabled.value = value }

    fun canSave(): Boolean = _keyword.value.isNotBlank() && (_weakEnabled.value || _strongEnabled.value)

    fun save(onDone: () -> Unit) {
        if (!canSave()) return

        viewModelScope.launch {
            val rule = RuleEntity(
                id = if (editingId > 0) editingId else 0,
                keyword = _keyword.value.trim(),
                matchType = _matchType.value,
                weakEnabled = _weakEnabled.value,
                strongEnabled = _strongEnabled.value,
                matchScope = _matchScope.value,
                enabled = _enabled.value
            )
            if (editingId > 0) {
                ruleRepository.update(rule)
            } else {
                ruleRepository.insert(rule)
            }
            onDone()
        }
    }
}
