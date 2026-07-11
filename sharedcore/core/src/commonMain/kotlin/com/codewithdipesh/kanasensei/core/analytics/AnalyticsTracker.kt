package com.codewithdipesh.kanasensei.core.analytics

interface AnalyticsTracker {
    fun trackScreen(screenName: String)
    fun logEvent(name: String, params: Map<String, Any?> = emptyMap())
    fun setUserId(userId: String?)
    fun setUserProperty(name: String, value: String)
    fun setCustomKey(key: String, value: Any)
    fun logNonFatal(exception: Throwable, message: String? = null)
}
