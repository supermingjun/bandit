package com.bandit.came.matcher

import android.content.Context
import com.bandit.came.BanditApp
import com.bandit.came.alert.AlertManager
import com.bandit.came.data.local.entity.HistoryEntity

object MatchDispatcher {

    suspend fun handle(context: Context, sender: String, body: String, timestamp: Long) {
        if (!MatchDedup.tryConsume(sender, body)) return

        val app = context.applicationContext as BanditApp
        val rules = app.ruleRepository.getEnabledRules()
        val results = KeywordMatcher.match(sender, body, rules)
        if (results.isEmpty()) return

        val weak = results.any { it.rule.weakEnabled }
        val strong = results.any { it.rule.strongEnabled }
        if (!weak && !strong) return

        val top = results.first()
        app.historyRepository.insert(
            HistoryEntity(
                sender = sender,
                bodySnippet = body.take(100),
                matchedRuleId = top.rule.id,
                matchedKeyword = top.rule.keyword,
                weakFired = weak,
                strongFired = strong,
                receivedAt = timestamp
            )
        )

        if (weak) AlertManager.fireWeak(context, sender, body, top.rule.keyword)
        if (strong) AlertManager.fireStrong(context, sender, body, top.rule.keyword)
    }
}
