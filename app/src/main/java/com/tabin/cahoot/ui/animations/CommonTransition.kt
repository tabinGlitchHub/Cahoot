package com.tabin.cahoot.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.tabin.cahoot.appDestination
import com.tabin.cahoot.destinations.CommonWaitingScreenDestination
import com.tabin.cahoot.destinations.GuestInputScreenDestination
import com.tabin.cahoot.destinations.HomeScreenDestination
import com.tabin.cahoot.destinations.HostInputScreenDestination

@OptIn(ExperimentalAnimationApi::class)
object CommonTransition : DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            HostInputScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            GuestInputScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            HostInputScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(700)
                )

            GuestInputScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(700)
                )

            CommonWaitingScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(700)
                )

            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            HostInputScreenDestination ->
                fadeIn(
                    initialAlpha = 0F,
                    animationSpec = tween(700)
                )

            GuestInputScreenDestination ->
                fadeIn(
                    initialAlpha = 0F,
                    animationSpec = tween(700)
                )

            CommonWaitingScreenDestination ->
                fadeIn(
                    initialAlpha = 0F,
                    animationSpec = tween(700)
                )

            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            HostInputScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            GuestInputScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            CommonWaitingScreenDestination ->
                slideOutHorizontally(
                targetOffsetX = { 1000 },
                animationSpec = tween(700)
            )

            else -> null
        }
    }
}