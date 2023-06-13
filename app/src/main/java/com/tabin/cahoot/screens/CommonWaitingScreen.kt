package com.tabin.cahoot.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.tabin.cahoot.Header
import com.tabin.cahoot.destinations.CommonPlayerScreenDestination
import com.tabin.cahoot.destinations.HomeScreenDestination
import com.tabin.cahoot.models.MessageModel
import com.tabin.cahoot.rootViewModel
import com.tabin.cahoot.ui.components.BandLikeButton
import com.tabin.cahoot.ui.components.SolidButton
import com.tabin.cahoot.ui.components.playPauseVideo
import com.tabin.cahoot.ui.theme.primary_red
import com.tabin.cahoot.ui.theme.primary_yellow
import com.tabin.cahoot.ui.theme.secondary_black
import com.tabin.cahoot.ui.theme.secondary_red
import com.tabin.cahoot.utils.Constants
import com.tabin.cahoot.utils.FileAccessUtils
import com.tabin.cahoot.utils.UtilFuncs
import com.tabin.cahoot.viewmodels.GlobalStateModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.guestViewModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.hostViewModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.navController
import java.util.Calendar


@Destination
@Composable
fun CommonWaitingScreen(navigator: DestinationsNavigator? = null) {

    val vertGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                primary_red,
                secondary_red
            )
        )
    }
    // A surface container using the 'background' color from the theme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = vertGradient
            )
    ) {
        Column {
            Header()
            CommonWaitingContent(navigator)
        }
    }
}

@Composable
fun CommonWaitingContent(navigator: DestinationsNavigator? = null) {
    val connectedMembersList = remember {
        rootViewModel.connectedUsers
    }
    val selectedMovieName = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val selectedMoviePath = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent(), onResult = { uri ->
            selectedMoviePath.value = FileAccessUtils.getRealPath(context, uri)
//            selectedMovieName.value = UtilFuncs.getFileNameFromPath(selectedMoviePath.value ?: "")
            selectedMovieName.value =
                uri?.let { UtilFuncs.getFileNameFromUri(uri = it, context = context) }
        })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("ExampleScreen", "PERMISSION GRANTED")
            launcher.launch("video/mp4")
        } else {
            // Permission Denied: Do something
            Log.d("ExampleScreen", "PERMISSION DENIED")
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            if (selectedMoviePath.value == null) {
                Text(
                    text = "Select a Movie to watch",
                    color = primary_yellow,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
                SolidButton(text = "Open gallery", onClick = {
                    // Check permission
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) -> {
                            // Some works that require permission
                            launcher.launch("video/mp4")
                        }

                        else -> {
                            // Asking for permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                }, textColor = primary_yellow)
            } else {
                Text(
                    text = "You selected:\n" + selectedMovieName.value,
                    color = primary_yellow,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
                SolidButton(text = "Change selection", onClick = {
                    launcher.launch("video/mp4")
                }, textColor = primary_yellow)
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(secondary_black),
            ) {
                LazyColumn(
                    modifier = Modifier.width(250.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(connectedMembersList) {
                        UserCard(userModel = it)
                        if (it != connectedMembersList[connectedMembersList.size - 1]) {
                            Spacer(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(1.dp)
                                    .background(Color(0x2FFFFFFF))
                            )
                        }
                    }
                }
            }
        }

        if (selectedMoviePath.value != null) {
            BandLikeButton(
                text = "Ready!",
                onClick = {
                    if (rootViewModel.userData.value.userRole == Constants.USER_GUEST) {
                        rootViewModel.userData.value =
                            rootViewModel.userData.value.copy(isReady = true)
                        guestViewModel.sendMessage(
                            MessageModel(
                                Constants.EVENT_READY,
                                0,
                                sentBy = rootViewModel.userData.value.userName,
                                isSystemMessage = true,
                                ownerId = rootViewModel.userData.value.userId
                            ), rootViewModel.userData.value
                        )
                        navigator?.navigate(CommonPlayerScreenDestination(uri = selectedMoviePath.value!!))
                    } else {
                        hostViewModel.sendMessage(
                            MessageModel(
                                Constants.EVENT_READY,
                                0,
                                sentBy = rootViewModel.userData.value.userName,
                                ownerId = rootViewModel.userData.value.userId,
                                isSystemMessage = true
                            ), rootViewModel.userData.value
                        )
                        navigator?.navigate(
                            CommonPlayerScreenDestination(
                                uri = selectedMoviePath.value!!,
                                startDefault = true
                            )
                        )
                    }
                },
                textColor = Color.White
            )
        }
    }
}

@Preview
@Composable
fun WaitingPreview() {
    CommonWaitingContent()

}