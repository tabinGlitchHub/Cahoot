package com.tabin.cahoot.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.tabin.cahoot.models.MessageModel
import com.tabin.cahoot.models.UserModel
import com.tabin.cahoot.models.WatchStateModel
import com.tabin.cahoot.rootViewModel
import com.tabin.cahoot.utils.Constants.Companion.STATE_IDLE
import java.net.ServerSocket
import java.net.Socket

object GlobalStateModel : ViewModel() {
    private val TAG = "RootViewModel : "

    val userData = mutableStateOf<UserModel>(
        UserModel(
            userId = 0,
            inSync = false,
            userName = "",
            userRole = ""
        )
    )

    lateinit var hostSocket: ServerSocket
    lateinit var guestSocket: Socket
    var isGuestSocketInitialized = ::guestSocket.isInitialized
    var isHostSocketInitialized = ::hostSocket.isInitialized

    val watchTimeLine = mutableStateListOf<WatchStateModel>()
    val messageTimeLine = mutableStateListOf<MessageModel>()
    val connectedUsers = mutableStateListOf<UserModel>()

    //temporary use only
    val isReady = mutableStateOf(false)

    val connectionState = mutableStateOf<String>(STATE_IDLE)

    val guestViewModel: GuestViewModel = GuestViewModel()
    val hostViewModel: HostViewModel = HostViewModel()

    lateinit var navController: NavController

    fun updateUserDataModel(
        id: Long = userData.value.userId,
        inSync: Boolean = userData.value.inSync,
        isReady: Boolean = userData.value.isReady,
        userRole: String = userData.value.userRole,
        userName: String = userData.value.userName
    ) {
        userData.value = UserModel(id, inSync, userRole, userName, isReady)
    }

    fun closeHostSocket() {
        if (isHostSocketInitialized && !hostSocket.isClosed) hostSocket.close()
    }

    fun closeGuestSocket() {
        if (isGuestSocketInitialized && !guestSocket.isClosed) guestSocket.close()
    }
}