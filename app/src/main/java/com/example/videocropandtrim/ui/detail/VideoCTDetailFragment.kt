package com.example.videocropandtrim.ui.detail

import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.bumptech.glide.Glide
import com.example.videocropandtrim.base.ViewBindingHolder
import com.example.videocropandtrim.base.ViewBindingHolderImpl
import com.example.videocropandtrim.databinding.FragmentVideoCropTrimDetailBinding
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.exo.ExoManager
import com.example.videocropandtrim.utils.getFileFolderPath
import com.example.videocropandtrim.utils.logg
import com.google.android.exoplayer2.video.VideoListener
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

class VideoCTDetailFragment: Fragment(), ViewBindingHolder<FragmentVideoCropTrimDetailBinding> by ViewBindingHolderImpl(){

    private val mVideoCropAndTrimViewModel: VideoCropAndTrimViewModel by sharedViewModel()
    val navArgs by navArgs<VideoCTDetailFragmentArgs>()

    private val mOnScrollListener: RecyclerView.OnScrollListener
        by lazy {
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
//                    logg("onScrolled dx: $dx     dy: $dy")
//                    val distanceX = binding?.rvVideoCropAndTrimDetailTimeLine?.calcScrollXDistance()
//                    logg("calcScrollXDistance firstVisibleChildView position * itemWidth - firstVisibleChildView.left:: $distanceX")
                    val timelineStartSec = binding?.rvVideoCropAndTrimDetailTimeLine?.calcScrollXDistance2()
//                    logg("calcScrollXDistance firstVisibleChildView position * itemWidth - firstVisibleChildView.left seeeccc:: $distanceX2")

                    timelineStartSec?.let { binding?.tltVideoCTD?.setTimeLineTemp(it) }

//                isSeeking = false
//                val scrollX: Int = calcScrollXDistance()
//                //达不到滑动的距离
//                if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
//                    isOverScaledTouchSlop = false
//                    return
//                }
//                isOverScaledTouchSlop = true
//                //初始状态,why ? 因为默认的时候有35dp的空白！
//                if (scrollX == -RECYCLER_VIEW_PADDING) {
//                    scrollPos = 0
//                } else {
//                    isSeeking = true
//                    scrollPos =
//                        (mAverageMsPx * (RECYCLER_VIEW_PADDING + scrollX) / THUMB_WIDTH) as Long
//                    mLeftProgressPos = mRangeSeekBarView.getSelectedMinValue() + scrollPos
//                    mRightProgressPos = mRangeSeekBarView.getSelectedMaxValue() + scrollPos
//                    Log.d(
//                        com.iknow.android.widget.VideoTrimmerView.TAG,
//                        "onScrolled >>>> mLeftProgressPos = $mLeftProgressPos"
//                    )
//                    mRedProgressBarPos = mLeftProgressPos
//                    if (mVideoView.isPlaying()) {
//                        mVideoView.pause()
//                        setPlayPauseViewIcon(false)
//                    }
//                    mRedProgressIcon.setVisibility(View.GONE)
//                    seekTo(mLeftProgressPos)
//                    mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos)
//                    mRangeSeekBarView.invalidate()
//                }
//                lastScrollX = scrollX
                }
            }
        }

    private fun RecyclerView.calcScrollXDistance(): Int {
        val layoutManager = layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        logg("calcScrollXDistance position: $position")
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemWidth = firstVisibleChildView!!.width
        logg("calcScrollXDistance firstVisibleChildView: $firstVisibleChildView")
        logg("calcScrollXDistance itemWidth: $itemWidth")
        logg("calcScrollXDistance firstVisibleChildView left: ${firstVisibleChildView.left}")
//        logg("calcScrollXDistance firstVisibleChildView position * itemWidth - firstVisibleChildView.left: ${position * itemWidth - firstVisibleChildView.left}")
        return position * itemWidth - firstVisibleChildView.left + itemWidth
    }

    private fun RecyclerView.calcScrollXDistance2(): Float {
        val layoutManager = layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        logg("calcScrollXDistance position: $position")
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemWidth = firstVisibleChildView!!.width
        logg("calcScrollXDistance firstVisibleChildView: $firstVisibleChildView")
        logg("calcScrollXDistance itemWidth: $itemWidth")
        logg("calcScrollXDistance firstVisibleChildView left: ${firstVisibleChildView.left}")
//        logg("calcScrollXDistance firstVisibleChildView position * itemWidth - firstVisibleChildView.left: ${position * itemWidth - firstVisibleChildView.left}")
        return (position * itemWidth - firstVisibleChildView.left + itemWidth) / itemWidth.toFloat()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = initBinding(FragmentVideoCropTrimDetailBinding.inflate(layoutInflater), this) {

        logg("navArgs: $navArgs")
        logg("mVideoCropAndTrimViewModel.selectVideoUri: ${mVideoCropAndTrimViewModel.selectVideoUri.value}")
        context?.let {
            Glide.with(it)
//                .load(Uri.parse("file://${navArgs.selectedMediaFile.filePath}"))
                .load(Uri.parse("${navArgs.selectedMediaFile.dataURI}"))
                .into(testThumbnail)
        }

        tltVideoCTD.setVideoDuration(navArgs.selectedMediaFile.duration)
        tltVideoCTD.setTemplateDuration(5000L)

        initUI()
        initObersve()
        videoCropAndTrimViewModel = mVideoCropAndTrimViewModel
        mVideoCropAndTrimViewModel.clearselectVideoUri()
    }

    fun FragmentVideoCropTrimDetailBinding.initObersve(){
        lifecycleOwner?.let {
            mVideoCropAndTrimViewModel.selectVideoUri.observe(it, { mediaFile ->
//                logg("@@@@@@@@@@@@@@@@mVideoCropAndTrimViewModel: mediaFile : $mediaFile")
            })

            mVideoCropAndTrimViewModel.selectVideoFrames.observe(it, { selectVideoFrames ->
//                logg("##################mVideoCropAndTrimViewModel: selectVideoFrames : ${selectVideoFrames.size}")
            })
        }
    }

//    var rectf = RectF()

    fun FragmentVideoCropTrimDetailBinding.initUI(){
//        ocvVideoCTDetail.overlayCropViewChangeListener = {
//            logg("overlayCropViewChangeListener rectf: $it")
//            rectf = it
//        }
        ocvVideoCTDetail.setTargetAspectRatio(1f) // todo 흠 이거 ratio를 어케 정해줘야하려나

        rvVideoCropAndTrimDetailTimeLine.addOnScrollListener(mOnScrollListener)
        btnSetting()
        exoSetting()
    }

    private var exoManager: ExoManager? = null

    private fun FragmentVideoCropTrimDetailBinding.exoSetting() {
        exoManager = context?.let { ctx ->
            ExoManager(
                ctx,
                pvVideoCTDetail
            ).apply {
                videoListener = object : VideoListener{
                    override fun onVideoSizeChanged(
                        width: Int,
                        height: Int,
                        unappliedRotationDegrees: Int,
                        pixelWidthHeightRatio: Float
                    ) {
                        super.onVideoSizeChanged(
                            width,
                            height,
                            unappliedRotationDegrees,
                            pixelWidthHeightRatio
                        )
                        logg("onVideoSizeChanged@@@ width: $width   heght: $height")
                        ocvVideoCTDetail.realVideoRectF.set(
                            0f,
                            0f,
                            width.toFloat(),
                            height.toFloat()
                        )
                    }

                    override fun onSurfaceSizeChanged(width: Int, height: Int) {
                        super.onSurfaceSizeChanged(width, height)

                        logg("videoListener width: $width   heght: $height")
//                        if(height > width) return //일단 이거 임시인데 이거 두번째가 타도록 해야할거같은데 흠
                        logg("2222 videoListener width: $width   heght: $height")
                        ocvVideoCTDetail.resizeVideoRectF.set(
                            0f,
                            0f,
                            width.toFloat(),
                            height.toFloat()
                        )

                        //todo 1130 밑에 파라미터를 서버에서 받으면 이걸 넘겨주면서 rect 조절
                        val setTemplateWidth = 540f
                        val setTemplateHeight = 960f
                        /**
                         *  음 이거를 해서 가로 영상이면 가로 영상이 된거에 꽉맞게 넣어주고 그니까 resizeRectWidth가 되고
                         *  반대편에는 그 해당 영상이 resize된 만큼 해주면 될듯
                         *  즉 위에꺼로 얘로 들면
                         *  960 / 607 = 1.581548599670511
                         *  540 / 1.581548599670511 = 341
                         *  이거 공식 대입해서 내일 만들어보자 
                         */

                        ocvVideoCTDetail.setTemplateCropRectF(
                            RectF(
                                0f,
                                0f,
                                setTemplateWidth,
                                setTemplateHeight
                            )
                        )

                        val cropRectViewP: ViewGroup.LayoutParams = ocvVideoCTDetail.layoutParams
                        cropRectViewP.width = width
                        cropRectViewP.height = height
                        ocvVideoCTDetail.layoutParams = cropRectViewP
//                        ocvVideoCTDetail.mCropViewRect.set(0f, 0f, width.toFloat(), height.toFloat())
                    }
                }
                navArgs.selectedMediaFile.dataURI?.let { initializePlayer(it) }
            }
        }
    }

    private val ffmpegFolderPath by lazy {
        context?.let { ctx ->
            getFileFolderPath(ctx)
        }
    }

    fun FragmentVideoCropTrimDetailBinding.btnSetting(){
        mbVideoCropAndTrimDetailNext.setOnClickListener {
            //todo 여기에 str0 이랑 str2랑 작업하기

//            context?.let { ctx ->
//                val id = Uri.parse(navArgs.selectedMediaFile.dataURI).lastPathSegment
//                logg("id: $id")
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                    getFileToFileId(ctx, id)
//                }
//            }

//            navArgs.selectedMediaFile.filePath?.let { it1 -> temp(it1) }
            ffmpegFolderPath?.let { ffmpegFolderPath ->
                val trimmerFileName = "${ffmpegFolderPath}cuttemp_${DateTime().millis}.mp4"

                File(ffmpegFolderPath).apply {
                    if(!isDirectory) mkdir()
                }

                val str0 = "-y -ss ${tltVideoCTD.mRealTimelineStartTimeStr}" +
                        " -i ${navArgs.selectedMediaFile.filePath}" +
                        " -to ${tltVideoCTD.mRealTimelineEndTimeStr}" +
                        " -preset ultrafast -async 1 -strict -2" +
                        " -c copy $trimmerFileName"

//                val str0 = "-y -ss 00:00:00.000" +
//                        " -i ${navArgs.selectedMediaFile.filePath}" +
//                        " -to 00:00:04.000" +
//                        " -preset ultrafast -async 1 -strict -2" +
//                        " -c copy $trimmerFileName"

                val str2 = "-i ${navArgs.selectedMediaFile.filePath}" +
                        " -ss 00:00:00.100" +
                        " -an -vframes 1" +
                        " /storage/emulated/0/.melchiletteruser/video/letterthumbnail_1606265787712.png"

                logg("click str00000 $str0")
                FFmpeg.executeAsync(str0.split(" ").toTypedArray()) { executionId, returnCode ->
                    logg("@@@FFmpeg.executeAsync executionId: $executionId ")
                    logg("@@@@FFmpeg.executeAsync returnCode: $returnCode ")
                    if(returnCode != Config.RETURN_CODE_SUCCESS) return@executeAsync
                    temp(trimmerFileName)
//                    go(trimmerFileName)
                }
            }


        }
    }

    fun FragmentVideoCropTrimDetailBinding.temp(chanageFileName: String){

        val fileName = "${ffmpegFolderPath}temp_${DateTime().millis}.mp4"
        val realCropRectF = ocvVideoCTDetail.getRealSizeCropRectF()

        realCropRectF?.let {
            val str1 = "-i $chanageFileName" +
                    " -r 30 -preset ultrafast" +
                    " -vf crop=${realCropRectF.left}:${realCropRectF.top}:${realCropRectF.right}:${realCropRectF.bottom}" +
                    " $fileName"

            logg("click str $str1")
            FFmpeg.executeAsync(str1.split(" ").toTypedArray()) { executionId, returnCode ->
                logg("FFmpeg.executeAsync executionId: $executionId ")
                logg("FFmpeg.executeAsync returnCode: $returnCode ")
                if(returnCode != Config.RETURN_CODE_SUCCESS) return@executeAsync
                go(fileName)
            }
        }
    }
//    10000 / 1000
//    (template video / (width / oneTakeWidth))
//    (width / oneTakeWidth)
//    1000 1080 94

    fun go(fileName: String){
        findNavController().navigate(
            VideoCTDetailFragmentDirections.actionDetailFragmentToResultFragment(
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


