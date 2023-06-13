package com.tabin.cahoot.models

data class UserModel(
    val userId: Long,
    val inSync: Boolean = false,
    val userRole: String,
    val userName: String,
    val isReady: Boolean = false
)