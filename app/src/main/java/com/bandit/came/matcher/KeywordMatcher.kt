package com.bandit.came.matcher

import com.bandit.came.data.local.entity.MatchScope
import com.bandit.came.data.local.entity.MatchType
import com.bandit.came.data.local.entity.RuleEntity

data class MatchResult(
    val rule: RuleEntity,
    val matchedText: String
)

object KeywordMatcher {

    fun match(sender: String, body: String, rules: List<RuleEntity>): List<MatchResult> {
        return rules.mapNotNull { rule -> matchSingle(sender, body, rule) }
    }

    private fun matchSingle(sender: String, body: String, rule: RuleEntity): MatchResult? {
        val targets = when (rule.matchScope) {
            MatchScope.BODY -> listOf(body)
            MatchScope.SENDER -> listOf(sender)
            MatchScope.BOTH -> listOf(body, sender)
        }

        for (target in targets) {
            if (isMatch(target, rule.keyword, rule.matchType)) {
                return MatchResult(rule, target)
            }
        }
        return null
    }

    private fun isMatch(text: String, keyword: String, matchType: MatchType): Boolean {
        return when (matchType) {
            MatchType.CONTAINS -> text.contains(keyword, ignoreCase = true)
            MatchType.REGEX -> runCatching { Regex(keyword).containsMatchIn(text) }.getOrDefault(false)
            MatchType.WILDCARD -> wildcardMatch(text, keyword)
        }
    }

    private fun wildcardMatch(text: String, pattern: String): Boolean {
        val regex = buildString {
            append("^")
            for (ch in pattern) {
                when (ch) {
                    '*' -> append(".*")
                    '?' -> append(".")
                    else -> append(Regex.escape(ch.toString()))
                }
            }
            append("$")
        }
        return runCatching { Regex(regex, RegexOption.IGNORE_CASE).matches(text) }.getOrDefault(false)
    }
}
