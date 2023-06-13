package com.tabin.cahoot.screens

import android.content.res.Configuration
import android.net.Uri
import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.tabin.cahoot.models.MessageModel
import com.tabin.cahoot.rootViewModel
import com.tabin.cahoot.ui.components.ChatList
import com.tabin.cahoot.ui.components.PlainPopup
import com.tabin.cahoot.ui.components.VideoView
import com.tabin.cahoot.ui.components.playPauseVideo
import com.tabin.cahoot.ui.theme.CahootTheme
import com.tabin.cahoot.ui.theme.Shapes
import com.tabin.cahoot.ui.theme.secondary_black
import com.tabin.cahoot.utils.Constants
import com.tabin.cahoot.utils.UtilFuncs
import com.tabin.cahoot.viewmodels.GlobalStateModel.guestViewModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.isReady
import kotlinx.coroutines.CoroutineScope

@Destination
@Composable
fun CommonPlayerScreen(
    navigator: DestinationsNavigator? = null,
    uri: String,
    startDefault: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val messageInput = rememberSaveable {
        mutableStateOf("")
    }
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            VideoView(videoUriStr = uri)

            ChatList(
                messageList = rootViewModel.messageTimeLine, modifier = Modifier
                    .fillMaxSize()
                    .padding(7.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(secondary_black)
            )
            BasicTextField(value = messageInput.value,
                onValueChange = { messageInput.value = it },
                textStyle = TextStyle(
                    Color.White, fontSize = 18.sp
                ),
                cursorBrush = Brush.verticalGradient(colors = listOf(Color.White, Color.White)),
                singleLine = true,
                decorationBox = { textField ->
                    Box(
                        modifier = Modifier
                            .padding(10.dp, 5.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                secondary_black
                            )
                            .padding(all = 16.dp)
                            .fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (messageInput.value == "") {
                                Text(
                                    text = "Your Message",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF949494)
                                )
                            }
                            Button(onClick = { /*TODO*/ }) {
                                Text(text = "Send")
                            }
                        }
                        textField()
                    }
                })
//        if (!isReady.value) {
            if (false) {
                PlainPopup(
                    text = "Movie will start when the others are settled in and READY!",
                    onDismiss = {}) {}
            } else {
                playPauseVideo(startDefault)
            }
        }
    }
}

@Preview
@Composable
fun PlayerScreenPreview() {
    CahootTheme() {
        CommonPlayerScreen(uri = "")
    }
}