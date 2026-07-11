package com.codewithdipesh.kanasensei.core.util

import java.time.Instant

fun nowIso(): String = Instant.now().toString()

fun Long.epochMillisToIso(): String = Instant.ofEpochMilli(this).toString()
