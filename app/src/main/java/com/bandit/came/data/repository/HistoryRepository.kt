package com.bandit.came.data.repository

import com.bandit.came.data.local.dao.HistoryDao
import com.bandit.came.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    fun getRecentHistory(): Flow<List<HistoryEntity>> = historyDao.getRecentHistory()

    suspend fun insert(history: HistoryEntity): Long = historyDao.insert(history)

    suspend fun clearAll() = historyDao.clearAll()

    suspend fun deleteById(id: Long) = historyDao.deleteById(id)
}
