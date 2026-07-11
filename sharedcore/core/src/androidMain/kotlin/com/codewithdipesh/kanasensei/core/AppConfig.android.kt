package com.codewithdipesh.kanasensei.core

import android.os.Build

actual object AppConfig {

    private var internalTelegramBotToken: String = ""
    private var internalChatId: String = ""

    fun initialize(telegramBotToken: String, chatId: String) {
        internalTelegramBotToken = telegramBotToken
        internalChatId = chatId
    }

    actual val telegramBotToken: String
        get() = internalTelegramBotToken
    actual val chatId: String
        get() = internalChatId

    actual val devicename: String
        get() = "${Build.MANUFACTURER} ${Build.MODEL}"
    actual val version: String
        get() = "${Build.VERSION.RELEASE}"
    actual val timestamp: String
        get() = "${System.currentTimeMillis()}"
}
