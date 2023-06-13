package com.tabin.cahoot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.tabin.cahoot.ui.theme.CahootTheme
import com.tabin.cahoot.ui.theme.primary_black
import com.tabin.cahoot.ui.theme.secondary_black
import com.tabin.cahoot.ui.theme.secondary_yellow

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlainPopup(
    text: String?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            focusable = true,
            excludeFromSystemGesture = false,
            usePlatformDefaultWidth = true
        ), content = {
            Box( modifier = Modifier
                .fillMaxWidth()
                .background(primary_black, shape = RoundedCornerShape(12.dp))) {
                Column(
                    modifier = Modifier
                        .padding(15.dp)
                ) {
                    Text(text = text ?: "", color = Color.White)
                    Spacer(modifier = Modifier.height(30.dp))
                    content()
                }
            }
        }, offset = IntOffset(30,30)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlainDialog(
    text: String?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primary_black, shape = RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                Text(text = text ?: "", color = Color.White)
                Spacer(modifier = Modifier.height(30.dp))
                content()
            }
        }

    }
}


@Preview(showBackground = false)
@Composable
fun PopupPreviews() {
    PlainPopup(text = "Text", onDismiss = { /*TODO*/ }) {

    }
}