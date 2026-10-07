package com.bandit.came.matcher

object MatchDedup {

    private const val WINDOW_MS = 10_000L

    private var lastKey: String = ""
    private var lastTime: Long = 0L

    fun tryConsume(sender: String, body: String): Boolean {
        val key = "$sender|$body"
        val now = System.currentTimeMillis()
        synchronized(this) {
            if (key == lastKey && now - lastTime < WINDOW_MS) {
                return false
            }
            lastKey = key
            lastTime = now
            return true
        }
    }
}
