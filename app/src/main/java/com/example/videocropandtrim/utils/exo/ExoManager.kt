package com.example.videocropandtrim.utils.exo

import android.content.Context
import com.example.videocropandtrim.utils.logg
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.video.VideoListener

class ExoManager(
    val context: Context,
    val playerView: PlayerView,
    var playbackStateListener: Player.EventListener? = null
){
    private var player: SimpleExoPlayer? = null
    var videoListener: VideoListener? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    fun initializePlayer(playUri: String) {
        if (player == null) {
            val trackSelector = DefaultTrackSelector(context)
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = SimpleExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .build()
        }

        videoListener?.let { player?.addVideoListener(it) }

        playerView.player = player
        val mediaItem = MediaItem.Builder()
            .setUri(playUri)
            .setMimeType(MimeTypes.APPLICATION_MP4)
//                .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
        player?.setMediaItem(mediaItem)
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        playbackStateListener?.let { player?.addListener(it) }
        player?.prepare()


    }

    fun releasePlayer() {
        player?.let { lPlayer ->
            playbackPosition = lPlayer.currentPosition
            currentWindow = lPlayer.currentWindowIndex
            playWhenReady = lPlayer.playWhenReady
            playbackStateListener?.let { player?.removeListener(it) }
            lPlayer.release()
            player = null
        }
    }
}