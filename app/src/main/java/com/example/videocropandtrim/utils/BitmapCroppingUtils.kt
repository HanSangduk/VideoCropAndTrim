package com.example.videocropandtrim.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

fun bitmapCroppingWorkerTask(
    cropImageView: AppCompatImageView, cropPoints: FloatArray, mDegreesRotated : Int = 90,
    mUri: Uri, mOrgWidth: Int, mOrgHeight: Int,
    mReqWidth: Int, mReqHeight: Int,
    mSaveCompressFormat: Bitmap.CompressFormat = CompressFormat.JPEG,
    mSaveCompressQuality: Int = 90,
    mIsPreview: Boolean = false
){

    val mContext = cropImageView.context
    val mFixAspectRatio = true
    val mAspectRatioX = mReqWidth
    val mAspectRatioY = mReqHeight

    val mSaveUri = getOutputUri(context = mContext)

    val disposables = CompositeDisposable() //todo 잠깐 밑에 waring 안나오게 하려고

    Observable.fromCallable {

        val bitmapSampled: BitmapSampled = cropBitmap(
            mContext, mUri, cropPoints, mDegreesRotated, mOrgWidth, mOrgHeight,
            mFixAspectRatio, mAspectRatioX, mAspectRatioY, mReqWidth, mReqHeight
        )
        val sampleSize = bitmapSampled.sampleSize

        bitmapSampled.bitmap?.let {
            writeBitmapToUri(
                mContext,
                it,
                mSaveUri,
                mSaveCompressFormat,
                mSaveCompressQuality
            )
        }

        CroppingResult(
            uri = mSaveUri,
            sampleSize = sampleSize,
            isPreview = mIsPreview
        )

    }.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            it?.let { croppingResult ->

                cropImageView.let { it1 ->
                    Glide.with(mContext)
                        .load(croppingResult.uri)
                        .into(it1)
                }
            }

        }.addTo(disposables)
}

//region: Inner class: Result
data class CroppingResult(
    /**
     * The cropped bitmap
     */
    val bitmap: Bitmap? = null,

    /**
     * The saved cropped bitmap uri
     */
    val uri: Uri? = null,

    /**
     * is the cropping request was to get a bitmap or to save it to uri
     */
    val isSave: Boolean = true,

    /**
     * sample size used creating the crop bitmap to lower its size
     */
    val sampleSize: Int,
    var isPreview: Boolean = false
)