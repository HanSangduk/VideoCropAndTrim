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
import com.example.videocropandtrim.utils.logg
import com.example.videocropandtrim.utils.with
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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

//    fun getVideoFrame(mediaFile: MediaFile){
//        val startPosition = 0
//        val endPosition = mediaFile.duration / 1000
//        var totalThumbsCount = endPosition / 1000
//        totalThumbsCount = if(totalThumbsCount > 10) totalThumbsCount else 10
//
//        try {
//            val mediaMetadataRetriever = MediaMetadataRetriever()
//            mediaMetadataRetriever.setDataSource(context, Uri.parse(mediaFile.dataURI))
//            // Retrieve media data use microsecond
//            val interval: Long = (endPosition - startPosition) / (totalThumbsCount - 1)
//            _selectVideoFrames.value = null
//
//            for (i in 0 until totalThumbsCount) {
//                val frameTime: Long = startPosition + interval * i
//                var bitmap: Bitmap? =
//                    mediaMetadataRetriever.getFrameAtTime(
//                        frameTime * 1000,
//                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
//                    )
//                bitmap?.let {
//                    try {
//                        val newBitmap = Bitmap.createScaledBitmap(
//                            it,
//                            89,
//                            113,
//                            false
//                        )
//                        _selectVideoFrames.value = _selectVideoFrames.value?.plus(newBitmap) ?: listOf(newBitmap)
//                    } catch (t: Throwable) {
//                        t.printStackTrace()
//                        null
//                    }
//                }
//            }
//
//            mediaMetadataRetriever.release()
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            logg("total Error: ${e.message}")
//        }
//    }

    fun getVideoFrame(mediaFile: MediaFile){
        logg("")
//        239000
        val startPosition = 0
        val endPosition = mediaFile.duration / 1000
        var totalThumbsCount = endPosition / 1000
        logg("firstCount totalThumbsCount: $totalThumbsCount")
        totalThumbsCount = if(totalThumbsCount > 10) totalThumbsCount else 10
        logg("2222 firstCount totalThumbsCount: $totalThumbsCount")

        try {
//            val mediaMetadataRetriever = MediaMetadataRetriever()
//            mediaMetadataRetriever.setDataSource(context, Uri.parse(mediaFile.dataURI))
            // Retrieve media data use microsecond
            val interval: Long = (endPosition - startPosition) / (totalThumbsCount - 1)
            logg("shootVideoThumbInBackground interval: $interval")
            var frameBitmap: Bitmap? = null
            val asd = Observable.rangeLong(0, totalThumbsCount)
                .subscribeOn(Schedulers.computation())
                .doOnNext {
//                    logg("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ")
//                    logg("long: $it")
                }.map {
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
//                    logg("bitMap: $it")
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
//
//            mediaMetadataRetriever.release()
        } catch (e: Throwable) {
            e.printStackTrace()
            logg("total Error: ${e.message}")
        }
    }


}