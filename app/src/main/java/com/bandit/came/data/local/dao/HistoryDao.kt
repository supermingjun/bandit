package com.bandit.came.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bandit.came.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY receivedAt DESC LIMIT 200")
    fun getRecentHistory(): Flow<List<HistoryEntity>>

    @Insert
    suspend fun insert(history: HistoryEntity): Long

    @Query("DELETE FROM history")
    suspend fun clearAll()

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
