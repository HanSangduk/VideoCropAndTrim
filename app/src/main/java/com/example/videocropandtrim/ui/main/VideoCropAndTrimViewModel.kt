package com.example.videocropandtrim.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.example.videocropandtrim.model.MediaRepository
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.ui.BaseViewModel
import com.example.videocropandtrim.ui.detail.video.VideoCTDetailFragment
import com.example.videocropandtrim.utils.*
import com.example.videocropandtrim.utils.widget.TimeLineTrimmer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class VideoCropAndTrimViewModel(val context: Context, val mediaRepository: MediaRepository): BaseViewModel(){

    private val _videoList : MutableLiveData<List<MediaFile>> = MutableLiveData()
    val videoList: MutableLiveData<List<MediaFile>> = _videoList

    private val _selectVideoUri : MutableLiveData<MediaFile> = MutableLiveData()
    val selectVideoUri: MutableLiveData<MediaFile> = _selectVideoUri

    private val _selectVideoFrames : MutableLiveData<List<Bitmap>> = MutableLiveData()
    val selectVideoFrames: MutableLiveData<List<Bitmap>> = _selectVideoFrames

    private val _videoFrames : MutableLiveData<List<Pair<String, Long>>> = MutableLiveData()
    val videoFrames: MutableLiveData<List<Pair<String, Long>>> = _videoFrames

    private val _currentImageExif : MutableLiveData<ImageExif> = MutableLiveData(ImageExif.TOP_LEFT)
    val currentImageExif: MutableLiveData<ImageExif> = _currentImageExif

    init {
        mediaRepository.bothVideosAndImages()
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
//        mediaRepository.videos()
//            .buffer(55, TimeUnit.MILLISECONDS, 5535)
//            .with()
//            .doOnSubscribe {
//                logg("doOnSubscribe@.videos doOnNext: $it")
//            }
//            .subscribe {
//                logg("@@subscribe@.videos doOnNext: $it")
//                logg("@@subscribe@.videos _videoList.value: ${_videoList.value}")
//                _videoList.value  = _videoList.value?.plus(it) ?: it
//            }.addTo(disposables)
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
        getVideoFrame2(mediaFile)
//        getVideoFrame(mediaFile)
    }

    fun clearselectVideoUri(){
        _selectVideoUri.postValue(null)
        _currentImageExif.postValue(ImageExif.TOP_LEFT)
    }

    private val shareDisposables : CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun shareFragmentFinished(){
        shareDisposables.clear()
    }

    fun getVideoFrame2(mediaFile: MediaFile){
        mediaFile.dataURI?.let { dataUri ->

            val mediaDurationMilliSec = mediaFile.duration / TimeLineTrimmer.ONE_SECOND_BY_MILLISECOND
            val interval: Long = context.getOneSceneDuration(mediaFile.duration, VideoCTDetailFragment.TEMP_TEMPLATE_DURATION)
            val totalCnt = mediaDurationMilliSec / interval
            val list: MutableList<Pair<String, Long>> = mutableListOf()
            for(i in 0 .. totalCnt){
                list.add(
                    Pair(dataUri , (interval * i))
                )
            }
            _videoFrames.value = list
        }
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
                        frameTime ,
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

    fun rotateRight() {
        _currentImageExif.value?.rotateRight()?.let {
            _currentImageExif.value = it
        }
    }

    fun flipHorizontal() {
        _currentImageExif.value?.flipHorizontal()?.let {
            _currentImageExif.value = it
        }
    }

}