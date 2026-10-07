package com.bandit.came.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,
    val bodySnippet: String,
    val matchedRuleId: Long,
    val matchedKeyword: String,
    val weakFired: Boolean,
    val strongFired: Boolean,
    val receivedAt: Long
)
