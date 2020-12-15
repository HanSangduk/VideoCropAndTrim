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
        val degree = (rota) * 90
        val points = ocvImageCTDetail.getCropRectRealPoints((rota) * 90)

        bitmapCroppingWorkerTask(
            ivTemplateImageEditorTarget, points, degree,
            Uri.parse(navArgs.selectedMediaFile.dataURI), navArgs.selectedMediaFile.width ?: 0, navArgs.selectedMediaFile.height ?: 0,
            requireTemplateWidth, requireTemplateHeight
        )

    }

    private var rota = 0
    private var isHorizontalRevers = false
    private val glide by lazy {
        context?.let { ctx ->
            Glide.with(ctx)
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
                logg("rota: $rota")
//                val degree = 90 * (rota)
//                when(it){
//                    0 -> isHorizontalRevers = !isHorizontalRevers
//                    1 -> {
//                        rota %= 4
//                        rota ++
//                    }
//                }

                when(it){
                    0 -> {
//                        val degree = -90 * (rota)  * if(isHorizontalRevers)-1 else 1
//                        val degree =if(isHorizontalRevers) 360 - 90 * (rota)
//                        else -90 * (rota)  * if(isHorizontalRevers)-1 else 1
//                        -270
                        val degree = when(rota){
                            0, 2, 4 -> -90 * (rota)  * if(isHorizontalRevers)-1 else 1
                            else -> -90 * (rota)  * if(isHorizontalRevers)-1 else 1
                        }
                        isHorizontalRevers = !isHorizontalRevers

//                        val degree = 90 * (rota)
                        logg("Revers degree: $degree")
                        glide
                            ?.asBitmap()
                            ?.load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
                            ?.transform(Rotate(degree))
                            ?.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            ?.into(object : CustomTarget<Bitmap>(){
                                override fun onResourceReady(newBitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                    val tagetBitmap = if(isHorizontalRevers){
                                        val matrix = Matrix()
//                                        if(degree == 90 || degree == 270) matrix.setScale(1f, -1f)
//                                        else matrix.setScale(-1f, 1f)
                                        when(abs(degree)){
                                            0, 180, 360 -> matrix.setScale(-1f, 1f)
                                            else -> matrix.setScale(1f, -1f)
                                        }
//                                        matrix.setScale(-1f, 1f)
                                        Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.width, newBitmap.height, matrix, false)
                                    }else newBitmap
                                    ivTemplateImageEditorTarget.setImageBitmap(tagetBitmap)
                                }
                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })
                    }
                    1 -> {
                        rota %= 4
                        rota ++
                        val degree = 90 * (rota) * if(isHorizontalRevers)-1 else 1
                        logg("degree: $degree")
                        glide
                            ?.asBitmap()
                            ?.load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
                            ?.transform(Rotate(degree))
                            ?.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            ?.into(object : CustomTarget<Bitmap>(){
                                override fun onResourceReady(newBitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                    val tagetBitmap = if(isHorizontalRevers){
                                        val matrix = Matrix()
                                        when(abs(degree)){
                                            0, 180, 360 -> matrix.setScale(-1f, 1f)
                                            else -> matrix.setScale(1f, -1f)
                                        }
//                                        matrix.setScale(-1f, 1f)
                                        Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.width, newBitmap.height, matrix, false)
                                    }else newBitmap

//                                    val matrix = Matrix()
//                                    when(abs(degree)) {
//                                        0, 180, 360 -> matrix.setScale(-1f, 1f)
//                                        else -> matrix.setScale(1f, -1f)
//                                    }
//                                    val tagetBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.width, newBitmap.height, matrix, false)

                                    ivTemplateImageEditorTarget.setImageBitmap(tagetBitmap)
                                    logg("ㅎㅎㅎㅎㅎㅎㅎㅎ????")
                                }
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    logg("ㅎㅎㅎㅎㅎㅎㅎㅎ????2222")
                                }
                            })
                    }
                }
//                glide
//                    ?.asBitmap()
//                    ?.load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
//                    ?.transform(Rotate(degree * if(isHorizontalRevers)-1 else 1))
//                    ?.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                    ?.into(object : CustomTarget<Bitmap>(){
//                        override fun onResourceReady(newBitmap: Bitmap, transition: Transition<in Bitmap>?) {
//                            val tagetBitmap = if(isHorizontalRevers){
//                                val matrix = Matrix()
//                                if(degree == 90 || degree == 270) matrix.setScale(1f, -1f)
//                                else matrix.setScale(-1f, 1f)
//                                Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.width, newBitmap.height, matrix, false)
//                            }else newBitmap
//                            ivTemplateImageEditorTarget.setImageBitmap(tagetBitmap)
//                            logg("ㅎㅎㅎㅎㅎㅎㅎㅎ????")
//                        }
//                        override fun onLoadCleared(placeholder: Drawable?) {
//                            logg("ㅎㅎㅎㅎㅎㅎㅎㅎ????2222")
//                        }
//                    })
//                    ?.into(ivTemplateImageEditorTarget)



            }, {
                logg("throw: ${it.message}")
            }).addTo(disposable)

//        llTemplateImageEditorReverseBtn.setOnClickListener {
//            logg("rota: ${90f * rota}")
//            val degree = 90f * (rota-1)
//            glide
//                ?.asBitmap()
//                ?.load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
//                ?.transform(Rotate(degree.toInt()))
//                ?.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                ?.into(object : CustomTarget<Bitmap>(){
//                    override fun onResourceReady(newBitmap: Bitmap, transition: Transition<in Bitmap>?) {
//                        val matrix = Matrix()
//                        matrix.setScale(-1f, 1f)
//                        val reverseBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.width, newBitmap.height, matrix, false)
//                        ivTemplateImageEditorTarget.setImageBitmap(reverseBitmap)
//                    }
//                    override fun onLoadCleared(placeholder: Drawable?) {}
//                })
//        }
//
//        llTemplateImageEditorRotateBtn.setOnClickListener {
//            logg("rota: ${90f * rota}")
//            val degree = 90f * rota
//
//                glide
//                    ?.asBitmap()
//                    ?.load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
//                    ?.transform(Rotate(degree.toInt()))
//                    ?.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                    ?.into(ivTemplateImageEditorTarget)
//
//
//            rota %= 4
//            rota++
//        }
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



