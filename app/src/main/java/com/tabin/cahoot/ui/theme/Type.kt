package com.tabin.cahoot.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tabin.cahoot.R


val RaleWay = FontFamily(
    Font(R.font.raleway_black, weight = FontWeight.Black),
    Font(R.font.raleway_bold, weight = FontWeight.Bold),
    Font(R.font.raleway_semibold, weight = FontWeight.SemiBold),
    Font(R.font.raleway_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.raleway_light, weight = FontWeight.Light),
    Font(R.font.raleway_extralight, weight = FontWeight.ExtraLight),
    Font(R.font.raleway_regular, weight = FontWeight.Normal),
    Font(R.font.raleway_thin, weight = FontWeight.Thin),
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = RaleWay,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = RaleWay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
)