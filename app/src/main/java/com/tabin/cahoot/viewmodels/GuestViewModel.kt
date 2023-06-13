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
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket

class GuestViewModel : ViewModel() {

    private val TAG = "GuestViewModel : "

    private lateinit var hostIn: InputStream
    private lateinit var hostOut: OutputStream
    var keepListening = true

    /**
     * Starts a TCP connection with Encoded IP address entered by User.
     * Does nothing if a connection is already established.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun startConnection() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (!rootViewModel.isGuestSocketInitialized) {
                    guestSocket = Socket()
                    rootViewModel.isGuestSocketInitialized = false
                    guestSocket.reuseAddress = true
                }
                if (!guestSocket.isConnected) {
                    Log.d(TAG, "in startConnection, starting connection")
                    val decodedAddress = UtilFuncs.getDecryptedValueOf(Constants.enteredCode)
                    Log.d(TAG, "decodedAddress = $decodedAddress")
                    guestSocket.connect(InetSocketAddress(decodedAddress, 1234))
                    Log.d(TAG, "post connection")
                    hostIn = guestSocket.getInputStream()
                    hostOut = guestSocket.getOutputStream()
                    var dataString: String = ""
                    sendMessage(
                        MessageModel(
                            Constants.EVENT_SUBSCRIBED,
                            0,
                            sentBy = rootViewModel.userData.value.userName,
                            isSystemMessage = true,
                            ownerId = rootViewModel.userData.value.userId
                        ),
                        rootViewModel.userData.value
                    )

                    while (keepListening) {
                        val byteRead = hostIn.read()
                        if (byteRead != -1) {
                            if (byteRead != 23) {
                                dataString = dataString.plus(byteRead.toChar())
                            } else {
                                Log.d(TAG, "guest received message $dataString")
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
                                    if (messageModel.message!!.startsWith(Constants.EVENT_SUBSCRIBED) && userDataJSON.toString()
                                            .isNotEmpty()
                                    ) {
                                        connectedUsers.add(userDataModel)
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
                                        GlobalStateModel.isReady.value = true
                                    }
                                }
                            }
                        } else {
                            //host has disconnected
                            Log.e(
                                TAG,
                                "Host disconnected -> going to " + HomeScreenDestination.route
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
                } else {
                    Log.d(TAG, "connection already established")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in initiating socket connection", e)
                e.printStackTrace()
            }
        }
    }


    fun sendMessage(message: MessageModel, userData: UserModel?) {
        Log.d(TAG, "in sendMessage, GUEST")
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
                hostOut.write(messageBytes)
            } catch (e: Exception) {
                Log.e(TAG, "Exception in sending message", e)
            }
        }
    }

    fun sendTerminationMessage(userData: UserModel?) {
        Log.d(TAG, "in sendMessage, GUEST")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val message = MessageModel(
                    Constants.EVENT_UNSUBSCRIBED,
                    0,
                    isSystemMessage = true,
                    sentBy = userData?.userName ?: "",
                    ownerId = userData?.userId ?: 0
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
                hostOut.write(messageBytes)
                rootViewModel.closeGuestSocket()
                rootViewModel.closeHostSocket()
            } catch (e: Exception) {
                Log.e(TAG, "Exception in sending message", e)
            }
        }
    }

}