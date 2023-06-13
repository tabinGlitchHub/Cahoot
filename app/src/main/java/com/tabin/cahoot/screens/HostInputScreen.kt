package com.tabin.cahoot.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.tabin.cahoot.Header
import com.tabin.cahoot.R
import com.tabin.cahoot.destinations.HomeScreenDestination
import com.tabin.cahoot.models.UserModel
import com.tabin.cahoot.rootViewModel
import com.tabin.cahoot.ui.animations.AnimatedComposables
import com.tabin.cahoot.ui.components.SolidButton
import com.tabin.cahoot.ui.theme.*
import com.tabin.cahoot.utils.Constants
import com.tabin.cahoot.utils.UtilFuncs
import com.tabin.cahoot.viewmodels.GlobalStateModel.hostViewModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.isReady
import com.tabin.cahoot.viewmodels.GlobalStateModel.navController

@Destination
@Composable
fun HostInputScreen(navigator: DestinationsNavigator? = null) {
    val generatedCode = rememberSaveable {
        mutableStateOf(UtilFuncs.getEncryptedValueOf(UtilFuncs.getIPAddress(false) ?: ""))
    }

    val init = rememberSaveable {
        mutableStateOf(false)
    }

    val connectionStatus = rememberSaveable {
        rootViewModel.connectionState.value
    }

    if (connectionStatus == Constants.STATE_ERROR || connectionStatus == Constants.STATE_FINISHED) {
        Log.d("HostInputScreen", "reset navigation")
        navController.popBackStack(HomeScreenDestination.route, inclusive = false, saveState = false)
    }

    if(!init.value){
        hostViewModel.startConnection()
    }

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
            HostInputContent(navigator, generatedCode)
        }
    }
}

@Composable
fun HostInputContent(
    navigator: DestinationsNavigator? = null,
    generatedCode: MutableState<String>,
) {

    val localContext = LocalContext.current
    val connectedMembersList = rootViewModel.connectedUsers


    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Share the below code with your guests",
            color = primary_yellow,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth(0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))

        val clipboardManager: ClipboardManager = LocalClipboardManager.current

        Box(
            modifier = Modifier.clip(
                RoundedCornerShape(10.dp)
            )
        ) {
            Row(
                modifier = Modifier
                    .background(primary_black)
                    .padding(10.dp)
                    .clickable {
                        println(AnnotatedString((generatedCode.value)))
                        clipboardManager.setText(AnnotatedString((generatedCode.value)))
                        Toast
                            .makeText(localContext, "Code copied to clipboard!", Toast.LENGTH_LONG)
                            .show()
                    }) {
                Text(
                    text = generatedCode.value,
                    color = secondary_white,
                    fontSize = 20.sp,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.width(20.dp))

                Image(painterResource(R.drawable.ic_copy), "Copy to clipboard")

            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        if (connectedMembersList.size == 0) {
            Text(
                text = "Nobody is here :(",
                color = secondary_white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp, 9.dp)
            )
        } else {
            Text(
                text = "Arrived Guests",
                color = secondary_white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp, 9.dp)
            )

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
                        if (it != connectedMembersList.get(connectedMembersList.size - 1)) {
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

        if (connectedMembersList.size > 0 && UtilFuncs.areAllUserReady(connectedMembersList)) {
            Spacer(modifier = Modifier.height(50.dp))

            SolidButton(
                buttonModifier = Modifier.width(120.dp),
                textModifier = Modifier,
                text = "NEXT",
                onClick = { },
                textColor = null
            )
        }
    }
}

@Composable
fun UserCard(userModel: UserModel) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp)
        ) {
            Text(
                text = userModel.userName,
                color = secondary_white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp, 9.dp)
            )

            if(isReady.value){
                AnimatedComposables.TickPopOnceAnimation()
            }else{
                AnimatedComposables.LoadingAnimation(
                    Modifier.padding(10.dp, 0.dp),
                    7.dp,
                    primary_yellow,
                    5.dp,
                    10.dp
                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostPreview() {
    CahootTheme {
        AnimatedComposables.TickPopOnceAnimation()
    }
}