package com.tabin.cahoot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tabin.cahoot.models.MessageModel
import com.tabin.cahoot.rootViewModel
import com.tabin.cahoot.ui.theme.CahootTheme
import com.tabin.cahoot.ui.theme.Purple700
import com.tabin.cahoot.ui.theme.Teal200
import com.tabin.cahoot.ui.theme.primary_black
import com.tabin.cahoot.ui.theme.primary_yellow
import com.tabin.cahoot.ui.theme.secondary_black
import com.tabin.cahoot.ui.theme.secondary_red
import com.tabin.cahoot.ui.theme.secondary_white
import com.tabin.cahoot.ui.theme.secondary_yellow
import com.tabin.cahoot.ui.theme.simple_red

@Composable
fun ChatList(messageList: SnapshotStateList<MessageModel>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = messageList, itemContent = {
            if (it.timeStamp != 0L) {
                TextBubble(
                    isUserOwned = it.ownerId == rootViewModel.userData.value.userId,
                    ownerName = it.sentBy,
                    message = it.message ?: "",
                    movieTimeStamp = it.timeStamp
                )
            }
        })
    }

}

@Composable
fun TextBubble(isUserOwned: Boolean, ownerName: String, message: String, movieTimeStamp: Long) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(isUserOwned.let {
                    if (it) return@let primary_black
                    else return@let simple_red
                })
                .padding(12.dp)
                .align(isUserOwned.let {
                    if (it)
                        return@let Alignment.TopEnd
                    else return@let Alignment.TopStart
                })
        ) {
            Text(text = ownerName, fontSize = 14.sp, color = isUserOwned.let {
                if (!it) return@let primary_black
                else return@let primary_yellow
            }, fontWeight = FontWeight.Bold)
            Text(
                text = message,
                fontSize = 17.sp,
                color = secondary_white,
                modifier = Modifier
                    .widthIn(60.dp, 300.dp)
            )
            Text(
                text = "at " + (movieTimeStamp / 60000).toString() + " mins",
                fontSize = 12.sp,
                color = secondary_yellow,
                modifier = Modifier
                    .widthIn(60.dp, 300.dp)
                    .align(Alignment.End)
            )
        }
    }
}

@Preview
@Composable
fun ChatPreview() {
    CahootTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            TextBubble(true, "Me", "Hello world!", 0)
            TextBubble(
                false,
                "Them",
                "Hello world back at ya, ya scroundchy scum of the eath",
                480000
            )
        }
    }
}