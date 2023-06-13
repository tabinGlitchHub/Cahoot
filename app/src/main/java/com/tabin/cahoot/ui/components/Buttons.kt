package com.tabin.cahoot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tabin.cahoot.ui.theme.CahootTheme
import com.tabin.cahoot.ui.theme.secondary_yellow

@Composable
fun SolidButton(
    buttonModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    textColor: Color?,
    buttonBackgroundColor: Color = secondary_yellow
) {
    Button(
        onClick = onClick, modifier = buttonModifier, colors = ButtonDefaults.buttonColors(
            buttonBackgroundColor
        )
    ) {
        Text(text = text, modifier = textModifier, color = textColor ?: Color(0xFFFFFFFF))
    }
}

@Composable
fun BandLikeButton(
    buttonModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    textColor: Color?,
    buttonBackgroundColor: Color = secondary_yellow
) {
    Box(
        modifier = buttonModifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onClick)
            .background(buttonBackgroundColor)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, modifier = textModifier, color = textColor ?: Color(0xFFFFFFFF))
    }
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
    CahootTheme {
        BandLikeButton(text = "test", onClick = { println("test") }, textColor = null)
    }
}