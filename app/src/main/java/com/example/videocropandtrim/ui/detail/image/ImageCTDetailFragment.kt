package com.example.videocropandtrim.ui.detail.image

import android.graphics.RectF
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
import com.example.videocropandtrim.base.ViewBindingHolder
import com.example.videocropandtrim.base.ViewBindingHolderImpl
import com.example.videocropandtrim.databinding.FragmentImageCropDetailBinding
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_video_crop_trim_detail.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


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
//        initObersve()
//        videoCropAndTrimViewModel = mVideoCropAndTrimViewModel
        mVideoCropAndTrimViewModel.clearselectVideoUri()

        context?.let {
            Glide.with(it)
                .load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
                .into(ivTemplateImageEditorTarget)

//            logg("flTemplateImageEditorTargetContainer width: ${flTemplateImageEditorTargetContainer.width}")
//            logg("flTemplateImageEditorTargetContainer hhhheight: ${flTemplateImageEditorTargetContainer.height}")

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
                logg("addOnLayoutChangeListener i: $i")
                logg("addOnLayoutChangeListener i2: $i2")
                logg("addOnLayoutChangeListener i3: $i3")
                logg("addOnLayoutChangeListener i4: $i4")
                logg("addOnLayoutChangeListener i5: $i5")
                logg("addOnLayoutChangeListener i6: $i6")
                logg("addOnLayoutChangeListener i7: $i7")
                logg("addOnLayoutChangeListener i8: $i8")
                val left = if (i < 0) 0f else i.toFloat()
                val top = if (i2 < 0) 0f else i2.toFloat()
                val right = if (i < 0) (i3 - i).toFloat() else i3.toFloat()
                val bottom = if (i2 < 0) (i4 - i2).toFloat() else i4.toFloat()
//                ocvImageCTDetail.resizeVideoRectF.set(
//                    left,
//                    top,
//                    right,
//                    bottom
//                )
                ocvImageCTDetail.resizeVideoRectF.set(
                    0f,
                    0f,
                    right - left,
                    bottom - top
                )
                logg("addOnLayoutChangeListener ocvImageCTDetail.resizeVideoRectF: ${ocvImageCTDetail.resizeVideoRectF}")
                val setTemplateWidth = 720f
                val setTemplateHeight = 1080f
//                val setTemplateWidth = 540f
//                val setTemplateHeight = 960f

                ocvImageCTDetail.setTemplateCropRectF(
                    RectF(
                        0f,
                        0f,
                        setTemplateWidth,
                        setTemplateHeight
                    )
                )

//                val cropRectViewP: ViewGroup.LayoutParams = ocvImageCTDetail.layoutParams
//                cropRectViewP.width = i3 - i
//                cropRectViewP.height = i4 - i2
//                logg("addOnLayoutChangeListener cropRectViewP.width:${cropRectViewP.width}")
//                logg("addOnLayoutChangeListener i3 - i:${i3 - i}")
//                logg("addOnLayoutChangeListener cropRectViewP.height:${cropRectViewP.height}")
//                ocvImageCTDetail.layoutParams = cropRectViewP
//                ocvImageCTDetail.postInvalidate()
//                ivTemplateImageEditorTarget.postInvalidate()

            }
        }


        logg("crop navArgs.selectedMediaFile.width: ${navArgs.selectedMediaFile.width}")
        logg("crop navArgs.selectedMediaFile.heighttt: ${navArgs.selectedMediaFile.height}")
        logg("addOnLayoutChangeListener reaaaaaaaaaaaaaaaaaaaaaaaal:")
        ocvImageCTDetail.realVideoRectF.set(
            0f,
            0f,
            navArgs.selectedMediaFile.width?.toFloat() ?: 500f,
            navArgs.selectedMediaFile.height?.toFloat() ?: 500f
        )
    }


    fun FragmentImageCropDetailBinding.initUI() {
        btnSetting()
    }

    fun FragmentImageCropDetailBinding.ImageCrop(){
        val points = ocvImageCTDetail.getCropRectRealPoints((rota - 1) * 90)
//        val points2 = ocvImageCTDetail.getCropRectRealPoints2(0)
        logg("ocvImageCTDetail.getRealSizeCropRectF() left: ${points[0]}")
        logg("ocvImageCTDetail.getRealSizeCropRectF() top: ${points[1]}")
        logg("ocvImageCTDetail.getRealSizeCropRectF() right: ${points[2]}")
        logg("ocvImageCTDetail.getRealSizeCropRectF() bottom: ${points[7]}")
//        logg("ocvImageCTDetail.getRealSizeCropRectF() left: ${points2[0]}")
//        logg("ocvImageCTDetail.getRealSizeCropRectF() top: ${points2[1]}")
//        logg("ocvImageCTDetail.getRealSizeCropRectF() right: ${points2[2]}")
//        logg("ocvImageCTDetail.getRealSizeCropRectF() bottom: ${points2[7]}")

        bitmapCroppingWorkerTask(ivTemplateImageEditorTarget, points)

//        val fileName = "${ffmpegFolderPath}temp_${DateTime().millis}.png"
//        val realCropRectF = ocvImageCTDetail.getRealSizeCropRectF()
//        realCropRectF?.let {
//            val str1 = "-i ${navArgs.selectedMediaFile.filePath}" +
//                    " -r 30 -preset ultrafast" +
//                    " -vf crop=${realCropRectF.left}:${realCropRectF.top}:${realCropRectF.right}:${realCropRectF.bottom}" +
//                    " $fileName"
//
//            logg("click str $str1")
//            FFmpeg.executeAsync(str1.split(" ").toTypedArray()) { executionId, returnCode ->
//                logg("FFmpeg.executeAsync executionId: $executionId ")
//                logg("FFmpeg.executeAsync returnCode: $returnCode ")
//                if(returnCode != Config.RETURN_CODE_SUCCESS) return@executeAsync
//                go(fileName)
//            }
//        }
    }

    private val ffmpegFolderPath by lazy {
        context?.let { ctx ->
            getFileFolderPath(ctx, IMAGE_CROP_FILE_FOLDER_PATH)
        }
    }

    private var rota = 1

    fun FragmentImageCropDetailBinding.btnSetting() {
        tvImageEditCompleteBtn.setOnClickListener {
            ImageCrop()
        }

        llTemplateImageEditorReverseBtn.setOnClickListener {

        }

        llTemplateImageEditorRotateBtn.setOnClickListener {
            context?.let { ctx ->
                logg("rota: ${90f * rota}")
                val degree = 90f * rota

//                navArgs.selectedMediaFile.filePath?.let { it1 ->
//                    Glide.with(ctx)
//                        .load(rotateBitmap(it1, degree = degree) )
//                        .into(ivTemplateImageEditorTarget)
//
//                }

                Glide.with(ctx)
                    .asBitmap()
                    .load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
                    .transform(Rotate(degree.toInt()))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(ivTemplateImageEditorTarget)

                rota %= 4
                rota++
            }

        }

//        llTemplateImageEditorRotateBtn.setOnClickListener {
//            val matrix = Matrix()
//            ivTemplateImageEditorTarget.scaleType = ImageView.ScaleType.MATRIX //required
//
////                    if(rota % 2 == 0){
////                    }
//
//            logg("templateImageEditorTargetContainerRectF: $templateImageEditorTargetContainerRectF")
//            logg("templateImageEditorTargetContainerRectF: ${templateImageEditorTargetContainerRectF.centerX()}")
//            matrix.postRotate(
//                90f * rota,
//                0f,
//                0f
//            )
//
//            ivTemplateImageEditorTarget.imageMatrix = matrix
//            rota %= 4
//            rota++
//            //todo 1207 흠 최대값을 가져와야 뭔가 넣어주고 자시고 할거같은데 바꾸ㅣ고 돌리고
//
////            if(rota % 2 == 0){
////                matrix.postScale(0.5f, 0.2f, (ivTemplateImageEditorTarget.x + ivTemplateImageEditorTarget.width/2f), (ivTemplateImageEditorTarget.y + ivTemplateImageEditorTarget.height/2f))
////                ivTemplateImageEditorTarget.imageMatrix = matrix
////            }
//
////            val lp = ivTemplateImageEditorTarget.layoutParams
////            lp.width = 607
////            lp.height = 1080
////            logg("(1080 / 1920) * 1080: ${((1080 / 1920f) * 1080)}")
////            ivTemplateImageEditorTarget.layoutParams = lp
//
////            val lp = ivTemplateImageEditorTarget.layoutParams
////            lp.width = 1080
////            lp.height = ((1080 / 1920f) * 1080).toInt()
////            logg("(1080 / 1920) * 1080: ${((1080 / 1920f) * 1080)}")
////            ivTemplateImageEditorTarget.layoutParams = lp
//        }
    }

    private val timelineTimerDispose by lazy {
        CompositeDisposable()
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
        timelineTimerDispose.clear()
        timelineTimerDispose.dispose()
        super.onDestroy()
        logg("여길 안타는군??")
    }
}



