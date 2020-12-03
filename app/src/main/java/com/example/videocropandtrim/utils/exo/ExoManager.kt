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


    private val defaultExoListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            logg("onPlayerStateChanged exoCurrentPosition: ${player?.currentPosition}")
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
        }
    }

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
        playbackStateListener?.let { player?.addListener(it) } ?: run { player?.addListener(defaultExoListener) }

        playerView.player = player
        val mediaItem = MediaItem.Builder()
            .setUri(playUri)
            .setMimeType(MimeTypes.APPLICATION_MP4)
//                .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
        player?.setMediaItem(mediaItem)
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare()
    }

    fun pausePlayer(){
        player?.let { lPlayer ->
            lPlayer.pause()
        }
    }
    fun getCurrentTime(): Long? = player?.currentPosition

    fun seekTo(windowIndex: Int = currentWindow, exoPos: Long){
        player?.let { lPlayer ->
            lPlayer.pause()
            lPlayer.seekTo(windowIndex, exoPos)
        }
    }

    fun start(){
        player?.let { lPlayer ->
            lPlayer.play()
        }
    }

    fun releasePlayer() {
        player?.let { lPlayer ->
            lPlayer.stop()
            playbackPosition = lPlayer.currentPosition
            currentWindow = lPlayer.currentWindowIndex
            playWhenReady = lPlayer.playWhenReady
            playbackStateListener?.let { player?.removeListener(it) }  ?: run { player?.removeListener(defaultExoListener) }
            videoListener?.let { player?.removeVideoListener(it) }
            lPlayer.release()
            player = null
        }
    }
}