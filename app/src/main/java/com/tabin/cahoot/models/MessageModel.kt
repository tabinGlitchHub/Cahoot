package com.tabin.cahoot.models

data class MessageModel(
    val message: String? = null,
    val timeStamp: Long,
    val sentBy: String,
    val ownerId: Long = 0,
    val isSystemMessage: Boolean = false
)
