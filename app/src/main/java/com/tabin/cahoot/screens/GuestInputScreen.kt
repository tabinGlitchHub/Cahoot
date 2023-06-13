package com.tabin.cahoot.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.tabin.cahoot.Header
import com.tabin.cahoot.destinations.CommonWaitingScreenDestination
import com.tabin.cahoot.destinations.HomeScreenDestination
import com.tabin.cahoot.rootViewModel
import com.tabin.cahoot.ui.theme.CahootTheme
import com.tabin.cahoot.ui.components.SolidButton
import com.tabin.cahoot.ui.theme.primary_red
import com.tabin.cahoot.ui.theme.primary_yellow
import com.tabin.cahoot.ui.theme.secondary_black
import com.tabin.cahoot.ui.theme.secondary_red
import com.tabin.cahoot.utils.Constants
import com.tabin.cahoot.viewmodels.GlobalStateModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.guestViewModel


@Destination
@Composable
fun GuestInputScreen(navigator: DestinationsNavigator? = null) {

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
            GuestInputContent(navigator)
        }
    }
}

@Composable
fun GuestInputContent(navigator: DestinationsNavigator?) {
    val textInput = rememberSaveable() {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Enter the code shared by the Host",
            color = primary_yellow,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth(0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            BasicTextField(
                modifier = Modifier.width(250.dp),
                value = textInput.value,
                onValueChange = {
//                    if (it.length < 20) {
                        textInput.value = it
                        Constants.enteredCode = it
//                    }
                },
                textStyle = TextStyle(
                    Color.White, fontSize = 18.sp
                ),
                cursorBrush = Brush.verticalGradient(colors = listOf(Color.White, Color.White)),
//                singleLine = true,
                decorationBox = { textField ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                secondary_black
                            )
                            .padding(all = 16.dp)
                            .fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (textInput.value.isEmpty()) {
                            Text(
                                text = "Your code here",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF949494)
                            )
                        }
                        textField()
                    }
                })

            SolidButton(
                buttonModifier = Modifier
                    .width(120.dp)
                    .padding(0.dp, 0.dp, 0.dp, 70.dp),
                text = "CONNECT",
                onClick = {
                    if (Constants.enteredCode.isNotEmpty()) {
                        guestViewModel.startConnection()
                        navigator!!.navigate(CommonWaitingScreenDestination)
                    }
                },
                textColor = null
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun GuestPreview() {
    CahootTheme {
        GuestInputScreen()
    }
}