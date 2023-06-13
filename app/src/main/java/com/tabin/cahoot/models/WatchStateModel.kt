package com.tabin.cahoot.models

import com.tabin.cahoot.utils.Constants

data class WatchStateModel(
    val watchState: String = Constants.STATE_IDLE,
    val timestamp: Long,
    val event: String? = null
)