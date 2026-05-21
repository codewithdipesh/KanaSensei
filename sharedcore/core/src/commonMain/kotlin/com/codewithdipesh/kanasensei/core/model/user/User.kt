package com.codewithdipesh.kanasensei.core.model.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid : String = "",
    val name : String = "",
    val motivationSource : String = "",
    val createdAt : String = "",
    val lastLogin : String = ""
)
