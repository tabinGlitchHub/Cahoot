package com.tabin.cahoot.ui.animations


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabin.cahoot.R
import com.tabin.cahoot.ui.theme.primary_yellow
import kotlinx.coroutines.delay

object AnimatedComposables {
    @Composable
    fun LoadingAnimation(
        modifier: Modifier = Modifier,
        circleSize: Dp = 25.dp,
        circleColor: Color = primary_yellow,
        spaceBetween: Dp = 10.dp,
        travelDistance: Dp = 20.dp
    ) {
        val circles = listOf(
            remember { Animatable(initialValue = 0f) },
            remember { Animatable(initialValue = 0f) },
            remember { Animatable(initialValue = 0f) }
        )

        circles.forEachIndexed { index, circle ->
            LaunchedEffect(key1 = circle) {
                delay(index * 100L)
                circle.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 1200
                            0.0f at 0 with LinearOutSlowInEasing
                            1.0f at 300 with LinearOutSlowInEasing
                            0.0f at 600 with LinearOutSlowInEasing
                            0.0f at 1200 with LinearOutSlowInEasing
                        },
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }

        val circleValues = circles.map { it.value }
        val distance = with(LocalDensity.current) { travelDistance.toPx() }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spaceBetween)
        ) {
            circleValues.forEach { value ->
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .graphicsLayer {
                            translationY = -value * distance
                        }
                        .background(
                            color = circleColor,
                            shape = CircleShape
                        )
                )
            }
        }

    }

    @Composable
    fun TickPopOnceAnimation(
        modifier: Modifier = Modifier,
    ) {
        val circle = remember { Animatable(initialValue = 0f) }
        val check = remember { Animatable(initialValue = 0f) }

        LaunchedEffect(key1 = circle) {
            circle.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500, easing = FastOutLinearInEasing
                )
            )
        }

        LaunchedEffect(key1 = check) {
            check.animateTo(
                targetValue = 1f,
                animationSpec = snap(450)
            )
        }

        Box(
            modifier = modifier,
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .graphicsLayer {
                        scaleX = circle.value
                        scaleY = circle.value
                    }
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            )

            Image(
                painterResource(R.drawable.ic_tick),
                "animated check mark",
                modifier = Modifier
                    .offset(2.dp, 2.dp).graphicsLayer {
                        alpha = check.value
                    }
            )
        }

    }
}