package com.example.videocropandtrim.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.example.videocropandtrim.R
import com.example.videocropandtrim.model.MediaRepository
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.ui.BaseViewModel
import com.example.videocropandtrim.ui.detail.VideoCTDetailFragment
import com.example.videocropandtrim.utils.getOneSceneDuration
import com.example.videocropandtrim.utils.getTimelineWidth
import com.example.videocropandtrim.utils.logg
import com.example.videocropandtrim.utils.widget.TimeLineTrimmer
import com.example.videocropandtrim.utils.with
import com.google.android.exoplayer2.Timeline
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class VideoCropAndTrimViewModel(val context: Context, val mediaRepository: MediaRepository): BaseViewModel(){

    private val _videoList : MutableLiveData<List<MediaFile>> = MutableLiveData()
    val videoList: MutableLiveData<List<MediaFile>>
        get() = _videoList

    private val _selectVideoUri : MutableLiveData<MediaFile> = MutableLiveData()
    val selectVideoUri: MutableLiveData<MediaFile>
        get() = _selectVideoUri

    private val _selectVideoFrames : MutableLiveData<List<Bitmap>> = MutableLiveData()
    val selectVideoFrames: MutableLiveData<List<Bitmap>>
        get() = _selectVideoFrames

    init {
        mediaRepository.videos()
            .buffer(55, TimeUnit.MILLISECONDS, 5535)
            .with()
            .doOnSubscribe {
                logg("doOnSubscribe@.videos doOnNext: $it")
            }
            .subscribe {
                logg("@@subscribe@.videos doOnNext: $it")
                logg("@@subscribe@.videos _videoList.value: ${_videoList.value}")
                _videoList.value  = _videoList.value?.plus(it) ?: it
            }.addTo(disposables)
    }

    fun selectedVideo(mediaFile: MediaFile){
        logg("context == null? ${context == null}")
        _selectVideoUri.value = mediaFile

        val videoUri = Uri.parse(mediaFile.dataURI)
        logg("videoUri: $videoUri")
        logg("22 videoUri: ${videoUri.lastPathSegment.toString()}")
        val rr = Uri.withAppendedPath(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoUri.lastPathSegment.toString()
        )
        logg("33 videoUri: $rr")
        getVideoFrame(mediaFile)
    }

    fun clearselectVideoUri(){
        _selectVideoUri.postValue(null)
    }

    private val shareDisposables : CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun shareFragmentFinished(){
        shareDisposables.clear()
    }

    fun getVideoFrame(mediaFile: MediaFile){
        val mediaDurationMilliSec = mediaFile.duration / TimeLineTrimmer.ONE_SECOND_BY_MILLISECOND

        try {
            val interval: Long = context.getOneSceneDuration(mediaFile.duration, VideoCTDetailFragment.TEMP_TEMPLATE_DURATION)
            val totalCnt = mediaDurationMilliSec / interval
            Observable.rangeLong(0, totalCnt)
                .subscribeOn(Schedulers.computation())
                .map {
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(context, Uri.parse(mediaFile.dataURI))
                    val frameTime: Long = interval * it
                    logg("getVideoFrame need frameTime: ${frameTime}")
                    val bitmap = mediaMetadataRetriever.getFrameAtTime(
                        frameTime * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                    logg("test123 it long: $it  && hash: ${bitmap.hashCode()}")
                    mediaMetadataRetriever.release()
                    bitmap
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it?.let {
                        logg("test123 subscribe it  && hash: ${it.hashCode()}")
                        try {
                            val newBitmap = Bitmap.createScaledBitmap(
                                it,
                                89,
                                113,
                                false
                            )
                            _selectVideoFrames.value = _selectVideoFrames.value?.plus(newBitmap) ?: listOf(newBitmap)
                        } catch (t: Throwable) {
                            t.printStackTrace()
                            null
                        }
                    }
                }.addTo(shareDisposables)
        } catch (e: Throwable) {
            e.printStackTrace()
            logg("total Error: ${e.message}")
        }
    }

    /**
     * version 01
     * 1초 단위 가져오는 용도
     */
    fun getVideoFrameVersion01(mediaFile: MediaFile){
        val startPosition = 0
        val endPosition = mediaFile.duration / 1000
        var totalThumbsCount = endPosition / 1000
        totalThumbsCount = if(totalThumbsCount > 10) totalThumbsCount else 10
        try {
            val interval: Long = (endPosition - startPosition) / (totalThumbsCount - 1)
            var frameBitmap: Bitmap? = null
            val asd = Observable.rangeLong(0, totalThumbsCount)
                .subscribeOn(Schedulers.computation())
                .map {
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(context, Uri.parse(mediaFile.dataURI))
                    val frameTime: Long = startPosition + interval * it
                    val bitmap = mediaMetadataRetriever.getFrameAtTime(
                        frameTime * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )

                    mediaMetadataRetriever.release()
                    bitmap
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it?.let {
                        try {
                            val newBitmap = Bitmap.createScaledBitmap(
                                it,
                                89,
                                113,
                                false
                            )
                            _selectVideoFrames.value = _selectVideoFrames.value?.plus(newBitmap) ?: listOf(newBitmap)
                        } catch (t: Throwable) {
                            t.printStackTrace()
                            null
                        }
                    }
                }
        } catch (e: Throwable) {
            e.printStackTrace()
            logg("total Error: ${e.message}")
        }
    }


}