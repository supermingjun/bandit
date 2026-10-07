package com.bandit.came.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
    val matchType: MatchType = MatchType.CONTAINS,
    val weakEnabled: Boolean = true,
    val strongEnabled: Boolean = false,
    val matchScope: MatchScope = MatchScope.BODY,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MatchType {
    CONTAINS,
    REGEX,
    WILDCARD
}

enum class MatchScope {
    BODY,
    SENDER,
    BOTH
}
