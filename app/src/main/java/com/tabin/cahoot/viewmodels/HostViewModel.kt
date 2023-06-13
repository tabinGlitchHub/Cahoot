package com.tabin.cahoot.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tabin.cahoot.destinations.CommonWaitingScreenDestination
import com.tabin.cahoot.destinations.HomeScreenDestination
import com.tabin.cahoot.models.MessageModel
import com.tabin.cahoot.models.UserModel
import com.tabin.cahoot.rootViewModel
import com.tabin.cahoot.utils.Constants
import com.tabin.cahoot.utils.UtilFuncs
import com.tabin.cahoot.viewmodels.GlobalStateModel.connectedUsers
import com.tabin.cahoot.viewmodels.GlobalStateModel.guestSocket
import com.tabin.cahoot.viewmodels.GlobalStateModel.hostSocket
import com.tabin.cahoot.viewmodels.GlobalStateModel.isReady
import com.tabin.cahoot.viewmodels.GlobalStateModel.messageTimeLine
import com.tabin.cahoot.viewmodels.GlobalStateModel.navController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket


class HostViewModel : ViewModel() {

    private val TAG = "HostViewModel : "

    private lateinit var guestIn: InputStream
    private lateinit var guestOut: OutputStream
    var keepListening = true

    @OptIn(DelicateCoroutinesApi::class)
    fun startConnection() {
        //initialize lists
        try {
            if (!rootViewModel.isHostSocketInitialized) {
                hostSocket = ServerSocket(1234)
                rootViewModel.isHostSocketInitialized = true
                hostSocket.reuseAddress = true
                Log.d(TAG, "starting server")
            } else {
                Log.d(TAG, "server already initiated, isBound? " + hostSocket.isBound)
            }

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    guestSocket = hostSocket.accept()
                    Log.d(TAG, "A guest connected")
                    guestIn = guestSocket.getInputStream()
                    guestOut = guestSocket.getOutputStream()
                    var dataString: String = ""
                    while (keepListening) {
                        val byteRead = guestIn.read()
                        if (byteRead != -1) {
                            if (byteRead != 23) {
                                dataString = dataString.plus(byteRead.toChar())
                            } else {
                                Log.d(TAG, "host received message $dataString")
                                val mainJSON = JSONObject(dataString)
                                dataString = ""
                                val messageJSON = JSONObject(mainJSON.getString("message"))
                                val userDataJSON = JSONObject(mainJSON.getString("user"))
                                val gson = Gson()
                                val messageModel =
                                    gson.fromJson(messageJSON.toString(), MessageModel::class.java)
                                val userDataModel =
                                    gson.fromJson(userDataJSON.toString(), UserModel::class.java)
                                if (messageModel.timeStamp != 0L) {
                                    messageTimeLine.plus(messageModel)
                                } else {
                                    //Handle EVENT MESSAGES
                                    if (messageModel.message == Constants.EVENT_SUBSCRIBED && userDataJSON.toString()
                                            .isNotEmpty()
                                    ) {
                                        connectedUsers.add(userDataModel)
                                        sendMessage(
                                            MessageModel(
                                                Constants.EVENT_SUBSCRIBED,
                                                0,
                                                ownerId = rootViewModel.userData.value.userId,
                                                sentBy = rootViewModel.userData.value.userName,
                                                isSystemMessage = true
                                            ),
                                            rootViewModel.userData.value
                                        )
                                    } else if (messageModel.message == Constants.EVENT_UNSUBSCRIBED) {
//                                        rootViewModel.closeHostSocket()
//                                        rootViewModel.closeGuestSocket()
                                        rootViewModel.connectedUsers.clear()
                                        UtilFuncs.runOnUiThread {
                                            navController.popBackStack(
                                                HomeScreenDestination.route,
                                                inclusive = false,
                                                saveState = false
                                            )
                                        }
                                        break;
                                    } else if (messageModel.message == Constants.EVENT_READY) {
                                        //TODO: Fix ready status not updating on Host end
                                        connectedUsers[0] = userDataModel.copy()
                                        isReady.value = true
                                        UtilFuncs.runOnUiThread {
                                            navController.navigate(
                                                CommonWaitingScreenDestination.route
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            //Guests have disconnected
                            Log.e(
                                TAG,
                                "Guest disconnected -> going to " + HomeScreenDestination.route
                            )
//                            rootViewModel.closeHostSocket()
//                            rootViewModel.closeGuestSocket()
                            rootViewModel.connectedUsers.clear()
                            UtilFuncs.runOnUiThread {
                                navController.popBackStack(
                                    HomeScreenDestination.route,
                                    inclusive = false,
                                    saveState = false
                                )
                            }
                            break;
                        }
                    }
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in initiating socket connection", e)
        }
    }

    fun sendMessage(message: MessageModel, userData: UserModel?) {
        Log.d(TAG, "in sendMessage, HOST")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val gson = Gson()
                val mainJSON = JSONObject()
                val messageJSON = gson.toJson(message)
                val userDataJSON = gson.toJson(userData)
                if (userData != null) mainJSON.put("user", userDataJSON)
                mainJSON.put("message", messageJSON)
                Log.d(TAG, "Sending message : $mainJSON")
                var messageBytes = mainJSON.toString().toByteArray()
                //Append ETB byte to denote end of transmission
                messageBytes = messageBytes.plus(23)
                guestOut.write(messageBytes)
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Exception in sending message", e)
            }
        }
    }

    fun sendTerminationMessage(userData: UserModel?) {
        Log.d(TAG, "in sendMessage, HOST")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val message = MessageModel(
                    Constants.EVENT_UNSUBSCRIBED, 0,
                    ownerId = userData?.userId ?: 0,
                    sentBy = userData?.userName ?: "",
                    isSystemMessage = true
                )
                val gson = Gson()
                val mainJSON = JSONObject()
                val messageJSON = gson.toJson(message)
                val userDataJSON = gson.toJson(userData)
                if (userData != null) mainJSON.put("user", userDataJSON)
                mainJSON.put("message", messageJSON)
                Log.d(TAG, "Sending message : $mainJSON")
                var messageBytes = mainJSON.toString().toByteArray()
                //Append ETB byte to denote end of transmission
                messageBytes = messageBytes.plus(23)
                guestOut.write(messageBytes)
                rootViewModel.closeGuestSocket()
                rootViewModel.closeHostSocket()
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Exception in sending message", e)
            }
        }
    }

}