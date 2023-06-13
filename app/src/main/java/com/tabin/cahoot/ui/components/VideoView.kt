package com.tabin.cahoot.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File

lateinit var exoPlayer: ExoPlayer

@Composable
fun VideoView(videoUriStr: String) {
    val context = LocalContext.current

    exoPlayer = ExoPlayer.Builder(LocalContext.current)
        .build()
        .also { exoPlayer ->
            val mediaItem = MediaItem.Builder()
                .setUri(videoUriStr)
                .build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }

    exoPlayer.playWhenReady = false

    DisposableEffect(
        AndroidView(factory = {
            StyledPlayerView(context).apply {
                player = exoPlayer
            }
        }, modifier = Modifier.fillMaxWidth())
    ) {
        onDispose { exoPlayer.release() }
    }
}

fun playPauseVideo(play: Boolean){
    exoPlayer.playWhenReady = play
}

fun seekTo(timeMillis: Long){
    exoPlayer.seekTo(timeMillis)
}