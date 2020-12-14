package com.example.videocropandtrim.ui.result

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.videocropandtrim.base.ViewBindingHolder
import com.example.videocropandtrim.base.ViewBindingHolderImpl
import com.example.videocropandtrim.databinding.FragmentVideoCropTrimResultBinding
import com.example.videocropandtrim.utils.logg
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.MimeTypes

class VideoCTResultFragment: Fragment(), ViewBindingHolder<FragmentVideoCropTrimResultBinding> by ViewBindingHolderImpl(){

    private val navArgs by navArgs<VideoCTResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = initBinding(FragmentVideoCropTrimResultBinding.inflate(layoutInflater), this) {

//        context?.let {
//            logg("resultFragment: navArgs.resultMediaFile ${navArgs.resultMediaFile}")
//            Glide.with(it)
//                .load(Uri.parse(navArgs.resultMediaFile.dataURI))
//                .into(ivVideoCTResultThumbnail)
//        }

        if(navArgs.resultMediaFile.filePath?.contains(".jpg") == true
            || navArgs.resultMediaFile.filePath?.contains(".png") == true ){
            context?.let {
                Glide.with(it)
                    .load(navArgs.resultMediaFile.dataURI)
                    .into(ivThumbNail)
            }
        }
        initializePlayer()
    }


//    private val playerView: PlayerView? = null //todo 이건 데이터 바인딩으로 할꺼니 패스

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    private fun initializePlayer() {
        context?.let { ctx ->
            if (player == null) {
                val trackSelector = DefaultTrackSelector(ctx)
                trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd()
                )
                player = SimpleExoPlayer.Builder(ctx)
                    .setTrackSelector(trackSelector)
                    .build()
            }

            binding?.pvVideoCTResult?.player = player
            val mediaItem = MediaItem.Builder()
                .setUri(navArgs.resultMediaFile.dataURI)
                .setMimeType(MimeTypes.APPLICATION_MP4)
//                .setMimeType(MimeTypes.APPLICATION_MPD)
                .build()
            player?.setMediaItem(mediaItem)
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
//            player?.addListener(playbackStateListener)
            player?.prepare()
        }
    }

    private fun releasePlayer() {
        player?.let { lPlayer ->
            playbackPosition = lPlayer.currentPosition
            currentWindow = lPlayer.currentWindowIndex
            playWhenReady = lPlayer.playWhenReady
//            player.removeListener(playbackStateListener)
            lPlayer.release()
            player = null
        }
    }

}