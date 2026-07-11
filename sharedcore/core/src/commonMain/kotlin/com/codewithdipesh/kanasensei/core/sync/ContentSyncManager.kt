package com.codewithdipesh.kanasensei.core.sync

interface ContentSyncManager {
    suspend fun syncChaptersAndLessons(): Boolean
    suspend fun hasLocalContent(): Boolean
}
