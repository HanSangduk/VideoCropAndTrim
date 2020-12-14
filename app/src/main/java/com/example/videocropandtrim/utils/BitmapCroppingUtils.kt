package com.example.videocropandtrim.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Looper
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

//fun bitmapCroppingWorkerTask(
//    cropImageView: AppCompatImageView, uri: Uri, cropPoints: FloatArray,
//    degreesRotated: Int, orgWidth: Int, orgHeight: Int,
//    fixAspectRatio: Boolean, aspectRatioX: Int, aspectRatioY: Int,
//    reqWidth: Int, reqHeight: Int, options: RequestSizeOptions,
//    saveUri: Uri, saveCompressFormat: CompressFormat, saveCompressQuality: Int, is_preview: Boolean
//){
fun bitmapCroppingWorkerTask(
    cropImageView: AppCompatImageView, cropPoints: FloatArray,
){
//    val mContext = cropImageView.context
//    val mUri = uri //Uri.parse("content://media/external/images/media/3956")
//    val mCropPoints = cropPoints
//    val mDegreesRotated = degreesRotated //90~
//    val mFixAspectRatio = fixAspectRatio
//    val mAspectRatioX = aspectRatioX
//    val mAspectRatioY = aspectRatioY
//    val mOrgWidth = orgWidth
//    val mOrgHeight = orgHeight
//    val mReqWidth = reqWidth
//    val mReqHeight = reqHeight
//    val mReqSizeOptions = options
//    val mSaveUri = saveUri
//    val mSaveCompressFormat = saveCompressFormat
//    val mSaveCompressQuality = saveCompressQuality
//    val mIsPreview = is_preview

    val mPhotoViewReference = WeakReference<AppCompatImageView>(cropImageView)
    val mContext = cropImageView.context
    val mUri = Uri.parse("content://media/external/images/media/3956")
    val left = 1005.4535f
    val top = 679.1075f
    val right = 1314.3184f
    val bottom = 1080f
    val mCropPoints = cropPoints
//    val mCropPoints = floatArrayOf(
//        left,
//        top,
//        right,
//        top,
//        right,
//        bottom,
//        left,
//        bottom
//    )
    val mDegreesRotated = 90
    val mFixAspectRatio = true
    val mAspectRatioX = 720
    val mAspectRatioY = 1080
    val mOrgWidth = 1920
    val mOrgHeight = 1080
    val mReqWidth = 720
    val mReqHeight = 1080
    val mReqSizeOptions = RequestSizeOptions.RESIZE_EXACT
    val mSaveUri = Uri.parse("file:///storage/emulated/0/.melchiletteruser/image/letter_cropped_${System.currentTimeMillis()}.jpg")
    val mSaveCompressFormat = CompressFormat.JPEG
    val mSaveCompressQuality = 90
    val mIsPreview = false

    val disposables = CompositeDisposable() //todo 잠깐 밑에 waring 안나오게 하려고

    Observable.fromCallable {

        logg("BitmapCropping 111 mainThread?? ${Looper.myLooper() == Looper.getMainLooper()}")

        var sampleSize = 1
        val bitmapSampled: BitmapSampled = cropBitmap(
            mContext, mUri, mCropPoints, mDegreesRotated, mOrgWidth, mOrgHeight,
            mFixAspectRatio, mAspectRatioX, mAspectRatioY, mReqWidth, mReqHeight
        )
        val bitmap = bitmapSampled.bitmap
        sampleSize = bitmapSampled.sampleSize
        logg("")
        bitmap?.let {
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
            logg("BitmapCroppingWorkerTask result: $it")
            it?.let { croppingResult ->

                cropImageView.let { it1 ->
                    Glide.with(mContext)
                        .load(croppingResult.uri)
                        .into(it1)
                }
            }

        }.addTo(disposables)
}


//fun BitmapCroppingWorkerTask(
//    cropImageView: AppCompatImageView
//) {
////    val mPhotoViewReference = WeakReference<AppCompatImageView>(cropImageView)
////    val mContext = cropImageView.getContext()
////    val mUri = uri
////    val mCropPoints = cropPoints
////    val mDegreesRotated = degreesRotated
////    val mFixAspectRatio = fixAspectRatio
////    val mAspectRatioX = aspectRatioX
////    val mAspectRatioY = aspectRatioY
////    val mOrgWidth = orgWidth
////    val mOrgHeight = orgHeight
////    val mReqWidth = reqWidth
////    val mReqHeight = reqHeight
////    val mReqSizeOptions = options
////    val mSaveUri = saveUri
////    val mSaveCompressFormat = saveCompressFormat
////    val mSaveCompressQuality = saveCompressQuality
////    val mIsPreview = is_preview
//
//    val mPhotoViewReference = WeakReference<AppCompatImageView>(cropImageView)
//    val mContext = cropImageView.context
//    val mUri = Uri.parse("content://media/external/images/media/3956")
//    val left = 1005.4535f
//    val top = 679.1075f
//    val right = 1314.3184f
//    val bottom = 1080f
//    val mCropPoints = floatArrayOf(
//        left,
//        top,
//        right,
//        top,
//        right,
//        bottom,
//        left,
//        bottom
//    )
//    val mDegreesRotated = 90
//    val mFixAspectRatio = true
//    val mAspectRatioX = 1400
//    val mAspectRatioY = 1080
//    val mOrgWidth = 1920
//    val mOrgHeight = 1080
//    val mReqWidth = 1400
//    val mReqHeight = 1080
//    val mReqSizeOptions = RequestSizeOptions.RESIZE_EXACT
//    val mSaveUri = Uri.parse("file:///storage/emulated/0/.melchiletteruser/image/letter_cropped_${System.currentTimeMillis()}.jpg")
//    val mSaveCompressFormat = CompressFormat.JPEG
//    val mSaveCompressQuality = 90
//    val mIsPreview = false
//
//    val disposables = CompositeDisposable() //todo 잠깐 밑에 waring 안나오게 하려고
//
//    Observable.fromCallable {
//
//        logg("BitmapCropping 111 mainThread?? ${Looper.myLooper() == Looper.getMainLooper()}")
//
//        var sampleSize = 1
//        val bitmapSampled: BitmapSampled = cropBitmap(
//            mContext, mUri, mCropPoints, mDegreesRotated, mOrgWidth, mOrgHeight,
//            mFixAspectRatio, mAspectRatioX, mAspectRatioY, mReqWidth, mReqHeight
//        )
//        val bitmap = bitmapSampled.bitmap
//        sampleSize = bitmapSampled.sampleSize
//        logg("")
//        bitmap?.let {
//            writeBitmapToUri(
//                mContext,
//                it,
//                mSaveUri,
//                mSaveCompressFormat,
//                mSaveCompressQuality
//            )
//        }
//
//        CroppingResult(
//            uri = mSaveUri,
//            sampleSize = sampleSize,
//            isPreview = mIsPreview
//        )
//    }.subscribeOn(Schedulers.computation())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe {
//            logg("BitmapCroppingWorkerTask result: $it")
//            it?.let { croppingResult ->
////                var completeCalled = false
////                val cropImageView: AppCompatImageView? = mPhotoViewReference.get()
////
////                if (cropImageView != null) {
////                    completeCalled = true
//////                    cropImageView.onImageCroppingAsyncComplete(croppingResult)
////                }
////
////                if (!completeCalled && croppingResult.bitmap != null) {
////                    // fast release of unused bitmap
////                    croppingResult.bitmap.recycle()
////                }
//
//                cropImageView.let { it1 ->
//                    Glide.with(mContext)
//                        .load(croppingResult.uri)
//                        .into(it1)
//                }
//            }
//
//        }.addTo(disposables)
//}

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