package com.codewithdipesh.kanasensei.core

expect object AppConfig {
    val telegramBotToken: String
    val chatId: String
    val devicename: String
    val version: String
    val timestamp: String
}