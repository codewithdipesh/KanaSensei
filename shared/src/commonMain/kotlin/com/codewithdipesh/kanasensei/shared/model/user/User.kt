package com.codewithdipesh.kanasensei.shared.model.user

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid : String = "",
    val name : String = "",
    val motivationSource : String = "",
    val createdAt : Long = Clock.System.now().toEpochMilliseconds(),
    val lastLogin : Long = Clock.System.now().toEpochMilliseconds()
)

