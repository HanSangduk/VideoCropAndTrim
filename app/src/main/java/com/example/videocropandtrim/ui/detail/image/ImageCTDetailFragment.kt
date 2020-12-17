package com.example.videocropandtrim.ui.detail.image

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.videocropandtrim.base.ViewBindingHolder
import com.example.videocropandtrim.base.ViewBindingHolderImpl
import com.example.videocropandtrim.databinding.FragmentImageCropDetailBinding
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.*
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_video_crop_trim_detail.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class ImageCropDetailFragment : Fragment(),
    ViewBindingHolder<FragmentImageCropDetailBinding> by ViewBindingHolderImpl() {

    private val mVideoCropAndTrimViewModel: VideoCropAndTrimViewModel by sharedViewModel()
    val navArgs by navArgs<ImageCropDetailFragmentArgs>()

    private val templateImageEditorTargetContainerRectF = RectF()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = initBinding(FragmentImageCropDetailBinding.inflate(layoutInflater), this) {

        logg("navArgs: $navArgs")
        logg("mVideoCropAndTrimViewModel.selectVideoUri: ${mVideoCropAndTrimViewModel.selectVideoUri.value}")

        initUI()
        initObserve()
        mVideoCropAndTrimViewModel.clearselectVideoUri()

        context?.let {
            Glide.with(it)
                .load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
                .into(ivTemplateImageEditorTarget)

            ctlTemplateImageEditorTargetContainer.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                if (i3 - i == 0 || i4 - i2 == 0) return@addOnLayoutChangeListener
                templateImageEditorTargetContainerRectF.set(
                    i.toFloat(),
                    i2.toFloat(),
                    i3.toFloat(),
                    i4.toFloat()
                )
            }

            ivTemplateImageEditorTarget.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
                if (i3 - i == 0 || i4 - i2 == 0) return@addOnLayoutChangeListener

                val left = if (i < 0) 0f else i.toFloat()
                val top = if (i2 < 0) 0f else i2.toFloat()
                val right = if (i < 0) (i3 - i).toFloat() else i3.toFloat()
                val bottom = if (i2 < 0) (i4 - i2).toFloat() else i4.toFloat()

                ocvImageCTDetail.resizeVideoRectF.set(
                    0f,
                    0f,
                    right - left,
                    bottom - top
                )

                ocvImageCTDetail.setTemplateCropRectF(
                    RectF(
                        0f,
                        0f,
                        requireTemplateWidth.toFloat(),
                        requireTemplateHeight.toFloat()
                    )
                )
            }
        }

        ocvImageCTDetail.realVideoRectF.set(
            0f,
            0f,
            navArgs.selectedMediaFile.width?.toFloat() ?: 500f,
            navArgs.selectedMediaFile.height?.toFloat() ?: 500f
        )
    }

    //todo 1214 server에서 넘어오는 값으로 해야함
    val requireTemplateWidth = 540
    val requireTemplateHeight = 960

    fun FragmentImageCropDetailBinding.initUI() {
        btnSetting()
    }

    fun FragmentImageCropDetailBinding.ImageCrop(){
//        val degree = (rota) * 90
//        val points = ocvImageCTDetail.getCropRectRealPoints((rota) * 90)

        mVideoCropAndTrimViewModel.currentImageExif.value?.let {
            logg("crop image exif: $it")
//            val points = ocvImageCTDetail.getCropRectRealPoints(it.rotate)
            val points = ocvImageCTDetail.getCropRectExifRealPoints(it)
            bitmapExifCroppingWorkerTask(
                ivTemplateImageEditorTarget, points, it,
                Uri.parse(navArgs.selectedMediaFile.dataURI), navArgs.selectedMediaFile.width ?: 0, navArgs.selectedMediaFile.height ?: 0,
                requireTemplateWidth, requireTemplateHeight
            )
        }

    }

    private val glide by lazy {
        context?.let { ctx ->
            Glide.with(ctx)
        }
    }


    private fun FragmentImageCropDetailBinding.initObserve() {
        lifecycleOwner?.let {
            mVideoCropAndTrimViewModel.currentImageExif.observe(it) { exif ->
                logg("currentImageExif: $exif")

                glide
                    ?.asBitmap()
                    ?.load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
                    ?.transform(Rotate(exif.rotate))
                    ?.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    ?.into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            newBitmap: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val tagetBitmap = if (exif.isHorizontalRevers) {
                                val matrix = Matrix()
                                matrix.setScale(-1f, 1f)
                                Bitmap.createBitmap(
                                    newBitmap,
                                    0,
                                    0,
                                    newBitmap.width,
                                    newBitmap.height,
                                    matrix,
                                    false
                                )
                            } else newBitmap
                            ivTemplateImageEditorTarget.setImageBitmap(tagetBitmap)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }

    }


    fun FragmentImageCropDetailBinding.btnSetting() {
        tvImageEditCompleteBtn.setOnClickListener {
            ImageCrop()
        }

        Observable.merge(
            listOf(
                llTemplateImageEditorReverseBtn.clicks().map{0},
                llTemplateImageEditorRotateBtn.clicks().map{1}
            )
        ).throttleLatest(333, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                when(it){
                    0 -> mVideoCropAndTrimViewModel.flipHorizontal()
                    1 -> mVideoCropAndTrimViewModel.rotateRight()
                }
            }, {
                logg("throw: ${it.message}")
            }).addTo(disposable)
    }

    fun go(fileName: String) {
        findNavController().navigate(
            ImageCropDetailFragmentDirections.actionDetailFragmentToResultFragment(
                MediaFile(
                    dataURI = "file://$fileName",
                    filePath = fileName
                )
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        logg("여길 안타는군??")
    }
}


