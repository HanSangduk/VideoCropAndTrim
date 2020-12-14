package com.example.videocropandtrim.ui.detail.video

import android.R.attr.*
import android.animation.ValueAnimator
import android.graphics.RectF
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.videocropandtrim.R
import com.example.videocropandtrim.base.ViewBindingHolder
import com.example.videocropandtrim.base.ViewBindingHolderImpl
import com.example.videocropandtrim.databinding.FragmentVideoCropTrimDetailBinding
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.convertSecondsToTime
import com.example.videocropandtrim.utils.exo.ExoManager
import com.example.videocropandtrim.utils.getFileFolderPath
import com.example.videocropandtrim.utils.getTimelineWidth
import com.example.videocropandtrim.utils.logg
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoListener
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import java.util.concurrent.TimeUnit


class VideoCTDetailFragment: Fragment(), ViewBindingHolder<FragmentVideoCropTrimDetailBinding> by ViewBindingHolderImpl(){

    companion object{
        //todo 임시용 나중에 서버에서 받아온 값으로 넘겨주자
        const val TEMP_TEMPLATE_DURATION = 2000L
    }

    private val mVideoCropAndTrimViewModel: VideoCropAndTrimViewModel by sharedViewModel()
    val navArgs by navArgs<VideoCTDetailFragmentArgs>()

    private var exoManager: ExoManager? = null

    private val mOnScrollListener: RecyclerView.OnScrollListener
        by lazy {
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    /**
                     * private static final int STATE_IDLE = 0;
                     * private static final int STATE_IN_PROGRESS_MANUAL_DRAG = 1;
                     * private static final int STATE_IN_PROGRESS_SMOOTH_SCROLL = 2;
                     * private static final int STATE_IN_PROGRESS_IMMEDIATE_SCROLL = 3;
                     * private static final int STATE_IN_PROGRESS_FAKE_DRAG = 4;
                     */
                    logg("rvScroll newState: $newState")
                    when(newState){
                        0 -> {
//                            startTimelineProgressBar()
                            exoManager?.start()
//                            timerStart()
                        }
                        else -> {
                            pauseTimelineProgressBar()
//                            timerPause()
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
//                    logg("rvScroll onScrolled dx: $dx     //     dy: $dy")
                    val timelineStartSec = binding?.rvVideoCropAndTrimDetailTimeLine?.calcScrollXDistance2()
                    logg("rvScroll drawTimelineText timelineStartSec: $timelineStartSec")
                    timelineStartSec?.let { binding?.tltVideoCTD?.setTimeLineTemp(it) }
                }
            }
        }

    private fun RecyclerView.calcScrollXDistance2(): Float {
        val layoutManager = layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemWidth = firstVisibleChildView!!.width
        return (position * itemWidth - firstVisibleChildView.left + itemWidth) / itemWidth.toFloat()
    }

    var rota = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = initBinding(FragmentVideoCropTrimDetailBinding.inflate(layoutInflater), this) {

        logg("navArgs: $navArgs")
        logg("mVideoCropAndTrimViewModel.selectVideoUri: ${mVideoCropAndTrimViewModel.selectVideoUri.value}")

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

    fun FragmentVideoCropTrimDetailBinding.initUI(){

        rvVideoCropAndTrimDetailTimeLine.addOnScrollListener(mOnScrollListener)
        TimeLineTrimmerSetting()
        btnSetting()
        exoSetting()
    }

    private fun FragmentVideoCropTrimDetailBinding.TimeLineTrimmerSetting() {
        with(tltVideoCTD){
            setVideoAndTemplateDuration(
                navArgs.selectedMediaFile.duration,
                TEMP_TEMPLATE_DURATION
            )

            timeLineChnageTimeCallback = { startTime, endTime ->
                val seekTime = if(startTime > 500) startTime - 500 else startTime
                exoManager?.seekTo(
                    exoPos = seekTime
                )
            }
        }
    }
    private var mProgressAnimator: ValueAnimator? = null

    fun startTimelineProgressBar(){
        context?.let { ctx ->

            binding?.apply {
                vProgressBarVideoTimeLine.visibility = View.VISIBLE
                val params = vProgressBarVideoTimeLine.layoutParams as ConstraintLayout.LayoutParams
                val start = ctx.resources.getDimensionPixelSize(R.dimen.default_timeline_padding)
                val end = ctx.getTimelineWidth() + start
                mProgressAnimator = ValueAnimator.ofInt(start, end).setDuration(
                    TEMP_TEMPLATE_DURATION
                )
                mProgressAnimator?.interpolator = LinearInterpolator()
                mProgressAnimator?.addUpdateListener { animation ->

                    params.leftMargin = animation.animatedValue as Int
                    vProgressBarVideoTimeLine.layoutParams = params

                }
                mProgressAnimator?.start()
            }
        }
    }

    private fun pauseTimelineProgressBar() {
        binding?.vProgressBarVideoTimeLine?.visibility = View.GONE
        binding?.vProgressBarVideoTimeLine?.clearAnimation()
//        binding?.vProgressBarVideoTimeLine?.removeCallbacks() //todo listener 넣으면 추가해주자
        mProgressAnimator?.cancel()
    }

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
                        logg("111 여길 와 근데 ???? ${width}")
                        logg("111 여길 와 근데 height ???? ${height}")
                        ocvVideoCTDetail.realVideoRectF.set(
                            0f,
                            0f,
                            width.toFloat(),
                            height.toFloat()
                        )
                    }

                    override fun onSurfaceSizeChanged(width: Int, height: Int) {
                        super.onSurfaceSizeChanged(width, height)
                        logg("여길 와 근데 ???? ${width}")
                        logg("여길 와 근데 height ???? ${height}")
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
                }// VideoListener

                playbackStateListener = object : Player.EventListener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        logg("exo onPlayerStateChanged playWhenReady: $playWhenReady")
                        if(playWhenReady && playbackState == Player.STATE_READY){
                            startTimelineProgressBar()
                            timerStart()
                        }
                        else timelineTimerDispose.clear()
//                        else timerPause()

                        logg("exo onPlayerStateChanged playbackState: $playbackState")
                    }
                }

                navArgs.selectedMediaFile.dataURI?.let { initializePlayer(it) }
            }// exoManager apply
        }
    }

    private val ffmpegFolderPath by lazy {
        context?.let { ctx ->
            getFileFolderPath(ctx)
        }
    }

    fun FragmentVideoCropTrimDetailBinding.btnSetting(){
        mbVideoCropAndTrimDetailNext.setOnClickListener {

            ffmpegFolderPath?.let { ffmpegFolderPath ->
                val trimmerFileName = "${ffmpegFolderPath}cuttemp_${DateTime().millis}.mp4"

                val str0 = "-y -ss ${tltVideoCTD.mRealTimelineStartTimeMilliSec.convertSecondsToTime()}" +
                        " -i ${navArgs.selectedMediaFile.filePath}" +
                        " -to ${(tltVideoCTD.mRealTimelineEndTimeMilliSec - tltVideoCTD.mRealTimelineStartTimeMilliSec).convertSecondsToTime()}" +
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
                    videoCrop(trimmerFileName)
//                    go(trimmerFileName)
                }
            }
        }
    }

    private val timelineTimerDispose by lazy {
        CompositeDisposable()
    }

    private fun timerStart(){
//        timerPause()

        Flowable.interval(0, 50, TimeUnit.MILLISECONDS)
//            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                exoManager?.getCurrentTime()
            }
            .distinctUntilChanged { old, new ->
                logg("old: $old")




                val newTime = (new / 100) * 100
                val roundEndTimeMilliSec = ((binding?.tltVideoCTD?.mRealTimelineEndTimeMilliSec ?: 0) / 100 ) * 100
                logg("new: $new")
                logg("newTime: $newTime")
                logg("exoplayer timeline max time: ${binding?.tltVideoCTD?.mRealTimelineEndTimeMilliSec}")
                logg("roundEndTimeMilliSec: $roundEndTimeMilliSec")
                logg("new != roundEndTimeMilliSec: ${newTime != roundEndTimeMilliSec}")


                //todo true가 되면 subscrib 안탄다 개꿀 그걸 이용하자
                //처음 한번은 타네???
//                true
                newTime != roundEndTimeMilliSec
            }
            .subscribe {
                logg("2222mainThread?? ${Looper.myLooper() == Looper.getMainLooper()}")
                logg("asd subscribe: $it")
                it?.let {
                    val newTime = (it / 100) * 100
                    val roundEndTimeMilliSec = ((binding?.tltVideoCTD?.mRealTimelineEndTimeMilliSec ?: 0) / 100 ) * 100
                    logg("newTime == roundEndTimeMilliSec: ${it == roundEndTimeMilliSec}")
                    if(newTime == roundEndTimeMilliSec) timerPause()
                }
            }.addTo(timelineTimerDispose)
    }
    private fun timerPause(){
        timelineTimerDispose.clear()
        exoManager?.pausePlayer()
//        timelineTimerDispose.dispose()
    }

    fun FragmentVideoCropTrimDetailBinding.videoCrop(chanageFileName: String){

        val fileName = "${ffmpegFolderPath}temp_${DateTime().millis}.mp4"
        val realCropRectF = ocvVideoCTDetail.getffmpegConvertToRealSizeCropRectF()
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

    fun hideNavigationBar(){
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

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
        timelineTimerDispose.clear()
        timelineTimerDispose.dispose()
        super.onDestroy()
        mVideoCropAndTrimViewModel.shareFragmentFinished()
        exoManager?.releasePlayer()
        logg("여길 안타는군??")
    }
}



