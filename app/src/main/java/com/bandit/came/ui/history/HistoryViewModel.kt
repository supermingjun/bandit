package com.bandit.came.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.came.BanditApp
import com.bandit.came.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository = (application as BanditApp).historyRepository

    val historyList: StateFlow<List<HistoryEntity>> = historyRepository.getRecentHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearAll() {
        viewModelScope.launch { historyRepository.clearAll() }
    }

    fun delete(id: Long) {
        viewModelScope.launch { historyRepository.deleteById(id) }
    }
}
