package com.bandit.came.data.repository

import com.bandit.came.data.local.dao.RuleDao
import com.bandit.came.data.local.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

class RuleRepository(private val ruleDao: RuleDao) {

    fun getAllRules(): Flow<List<RuleEntity>> = ruleDao.getAllRules()

    suspend fun getEnabledRules(): List<RuleEntity> = ruleDao.getEnabledRules()

    suspend fun getRuleById(id: Long): RuleEntity? = ruleDao.getRuleById(id)

    suspend fun insert(rule: RuleEntity): Long = ruleDao.insert(rule)

    suspend fun update(rule: RuleEntity) = ruleDao.update(rule)

    suspend fun delete(rule: RuleEntity) = ruleDao.delete(rule)

    suspend fun deleteById(id: Long) = ruleDao.deleteById(id)
}
