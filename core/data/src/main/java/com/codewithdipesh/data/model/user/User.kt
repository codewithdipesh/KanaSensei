package com.codewithdipesh.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid : String = "",
    val name : String = "",
    val motivationSource : String = "",
    val createdAt : Long = System.currentTimeMillis(),
    val lastLogin : Long = System.currentTimeMillis()
)

