package com.tabin.cahoot.utils

class Constants {

    companion object{
        //User roles
        const val USER_HOST = "HOST"
        const val USER_GUEST = "GUEST"

        //Watch States
        const val STATE_IDLE = "IDLE"
        const val STATE_PAUSED = "PAUSED"
        const val STATE_WATCHING = "WATCHING"
        const val STATE_ERROR = "ERROR"
        const val STATE_FINISHED = "FINISHED"

        //Events
        const val EVENT_PAUSED = "PAUSED"
        const val EVENT_RESUMED = "RESUMED"
        const val EVENT_SUBSCRIBED = "SUBSCRIBED"
        const val EVENT_UNSUBSCRIBED = "UNSUBSCRIBED"
        const val EVENT_NEW_MESSAGE = "NEW_MESSAGE"
        const val EVENT_REFRESH_USERS = "REFRESH_USERS"
        const val EVENT_KICK_OUT = "KICK_OUT"
        const val EVENT_READY = "READY"

        const val SHARED_PREF_FILE_NAME = "com.tabin.cahoot.mySharedPref"
        const val SHARED_PREF_KEY_USER_NAME = "userName"
        const val SHARED_PREF_KEY_USER_ID = "userID"
        const val SHARED_PREF_KEY_USER_ROLE = "userRole"
        const val SHARED_PREF_KEY_USER_IN_SYNC = "userInSync"

        //Screens
        const val SCREEN_GUEST_INPUT = "guest_input_screen"
        const val SCREEN_HOST_INPUT = "host_input_screen"

        var enteredCode = ""

    }
}