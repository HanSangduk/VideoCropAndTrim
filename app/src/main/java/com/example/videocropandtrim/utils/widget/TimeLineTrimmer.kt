package com.example.videocropandtrim.utils.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Region
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.videocropandtrim.R
import com.example.videocropandtrim.utils.convertDpToPx
import com.example.videocropandtrim.utils.convertSecondsToTime
import com.example.videocropandtrim.utils.getTimelineOneTakeWidth
import com.example.videocropandtrim.utils.logg
import com.example.videocropandtrim.utils.widget.TimeLineTrimmer.TimeLineRectMovingDirection.Companion.getTimeLineRectMovingDirectionByValue
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * version 1
 * 최대 10초 짜리
 * inner rect moving enable
 */
class TimeLineTrimmer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    companion object{
        const val ONE_SECOND_BY_MILLISECOND = 1000L
    }

    private var lineStrokeColor = 1
    private var leftThumb: Drawable? = null
    private var rightTumb: Drawable? = null

    private var mThisWidth = 0
    private var mThisHeight = 0

    private var mDimmedColor = 0

    private val mFramePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTimeLineTrimmerRect = RectF()
    private val mTempTimeLineRect = RectF()

    private var mTimeLineTrimmerTouchArea = TimeLineTrimmerTouchArea.NONE

    private var mPreviousTouchX = -1f
    private var mPreviousTouchY = -1f

    private var mRectMinSize = 0

    private var mMaxRectF = RectF()

    private var mTimelineTextpaintL = Paint()
    private var mTimelineTextpaintR = Paint()

    private var timeLineRectMovingDirection: TimeLineRectMovingDirection = TimeLineRectMovingDirection.HORIZONTAL

    private val mWhiteColorRes = ContextCompat.getColor(context, R.color.white)

    private var mVideoDurationMilliSec = 0L
    private var mTemplateVideoMaxDurationMilliSec = 10L * ONE_SECOND_BY_MILLISECOND
    private var mVideoOneTakeMilliSec = 0L
    private val mVideoOneTakeWidth by lazy {
        context.getTimelineOneTakeWidth()
    }

    private var mTimelineStartPos = 0f
    var mRealTimelineStartTimeStr = ""
    var mRealTimelineEndTimeStr = ""

    //template에 필요한 영상 초에 따른 rect영역
    private var mTemplateMaxRightPoint: Float? = null

    private var mVideoDurationRatio = 1L

    init {
//        attrs?.let { lAttrs ->
//            val typeArray = context.obtainStyledAttributes(lAttrs, R.styleable.TimeLineTrimmer)

            mRectMinSize = resources.getDimensionPixelSize(R.dimen.default_crop_rect_min_size)

            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeLineTrimmer)
            lineStrokeColor = typedArray.getColor(
                R.styleable.TimeLineTrimmer_tlt_line_stroke_color,
                ContextCompat.getColor(context, R.color.color_default_crop_frame)
            )
            mDimmedColor = typedArray.getColor(
                R.styleable.TimeLineTrimmer_tlt_dimmed_color,
                ContextCompat.getColor(context, R.color.color_default_dimmed)
            )

            leftThumb = typedArray.getDrawable(
                R.styleable.TimeLineTrimmer_tlt_left_guide_line_thumb
            )

            rightTumb = typedArray.getDrawable(
                R.styleable.TimeLineTrimmer_tlt_right_guide_line_thumb
            )

            //text
            mTimelineTextpaintL.strokeWidth = 3f
            mTimelineTextpaintL.setARGB(255, 51, 51, 51)
            mTimelineTextpaintL.textSize = 28f
            mTimelineTextpaintL.isAntiAlias = true
            mTimelineTextpaintL.color = mWhiteColorRes
            mTimelineTextpaintL.textAlign = Paint.Align.LEFT

            mTimelineTextpaintR.strokeWidth = 3f
            mTimelineTextpaintR.setARGB(255, 51, 51, 51)
            mTimelineTextpaintR.textSize = 28f
            mTimelineTextpaintR.isAntiAlias = true
            mTimelineTextpaintR.color = mWhiteColorRes
            mTimelineTextpaintR.textAlign = Paint.Align.RIGHT

            timeLineRectMovingDirection = getTimeLineRectMovingDirectionByValue(
                typedArray.getInt(
                    R.styleable.TimeLineTrimmer_tlt_timeline_rect_direction,
                    TimeLineRectMovingDirection.HORIZONTAL.value
                )
            )

            initFrame(typedArray)
            logg("init lineStrokeColor: $lineStrokeColor")
            logg(
                "init lineStrokeColor2222: ${
                    lineStrokeColor == ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                }"
            )
            typedArray.recycle()
//        }
    }

    private fun initFrame(typeArray: TypedArray) {
        val strokeWidth = typeArray.getDimensionPixelSize(
            R.styleable.TimeLineTrimmer_tlt_line_stroke_size,
            resources.getDimensionPixelSize(R.dimen.default_crop_frame_stoke_width)
        )
        mFramePaint.strokeWidth = strokeWidth.toFloat()
        mFramePaint.color = lineStrokeColor
        mFramePaint.style = Paint.Style.STROKE
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        logg("00onLayout changed: $changed")
        logg("11onLayout paddingLeft: $paddingLeft")
        logg("22onLayout paddingLeft: $paddingTop")
        logg("33onLayout paddingLeft: ${width - paddingRight}")
        logg("44onLayout paddingLeft: ${height - paddingBottom}")
        logg("44onLayout width: ${width}")

        val pLeft = paddingLeft
        val pTop = paddingTop
        val pRight = width - paddingRight
        val pBottom = height - paddingBottom

        mMaxRectF.set(
            (pLeft).toFloat(),
            pTop.toFloat(),
            (pRight).toFloat(),
            (pBottom).toFloat()
        )

        mTimeLineTrimmerRect.set(
            mMaxRectF.left,
            mMaxRectF.top,
            mTemplateMaxRightPoint ?: mMaxRectF.right,
            mMaxRectF.bottom
        )
        logg("mVideoOneTakeWidth mTimeLineTrimmerRect@@ : $mTimeLineTrimmerRect")

        if(changed){
            mThisWidth = pRight - pLeft
            mThisHeight = pBottom - pTop
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { lCanvas ->
            drawDimmedLayer(lCanvas)
            drawCropGrid(lCanvas)
            drawTimelineText(lCanvas)
        }
    }

    private fun drawTimelineText(lCanvas: Canvas) {
        logg("setTimeLineTemp 그럼 여기 오나?: $mTimeLineTrimmerRect" )
        logg("setTimeLineTemp mTimeLineTrimmerRect.left?: ${mTimeLineTrimmerRect.left}")
        logg("setTimeLineTemp 222 paddingLeft?: ${paddingLeft}")
        logg("setTimeLineTemp 222 mTempTimeLineRect.left - paddingLeft?: ${(mTimeLineTrimmerRect.left - paddingLeft)}")
        logg("setTimeLineTemp 222 mTempTimeLineRect.left - paddingLeft?: ${mTimeLineTrimmerRect.left.toInt() - paddingLeft}")
        val plusTime = (mTimeLineTrimmerRect.left - paddingLeft) / mVideoOneTakeWidth * 1000
        logg("setTimeLineTemp 333 mTimeLineTrimmerRect.left?: ${plusTime}")
        logg("setTimeLineTemp 444 mTimeLineTrimmerRect.left?: ${mTimelineStartPos}")
        val realTime = (mTimelineStartPos + plusTime).toLong()

        logg("setTimeLineTemp 555 mTimeLineTrimmerRect.left?: ${realTime}")
        logg("setTimeLineTemp 666 mTimeLineTrimmerRect.mVideoOneTakeMilliSec?: ${mVideoOneTakeMilliSec}")
        logg("setTimeLineTemp 777 mTimeLineTrimmerRect.left?: ${mVideoDurationRatio}")
        mRealTimelineStartTimeStr = realTime.convertSecondsToTime()
        mRealTimelineEndTimeStr = (realTime + mTemplateVideoMaxDurationMilliSec).convertSecondsToTime()
        lCanvas.drawText(
            mRealTimelineStartTimeStr,
            mTimeLineTrimmerRect.left,
            resources.getDimension(R.dimen.timeline_time_text_area) / 2,
            mTimelineTextpaintL
        )
        lCanvas.drawText(
            mRealTimelineEndTimeStr,
            mTimeLineTrimmerRect.right,
            resources.getDimension(R.dimen.timeline_time_text_area) / 2,
            mTimelineTextpaintR
        )

        logg("mTimeLineTrimmerRect.left: ${mTimeLineTrimmerRect}")
    }

    //todo 밑에 setVideoDuration와 함께 받도록 수정하거나 그런거 해야할거같은데 데이터 구조가 어케 될지 모르니까 일단 대기
    fun setTemplateDuration(duration: Long){
        //todo 얘도 10*6승으로 올지는 모르니까 오는거 보고 수정 필요할 수도
        mTemplateVideoMaxDurationMilliSec = duration

        mTemplateMaxRightPoint = (paddingLeft + mVideoOneTakeWidth * mVideoDurationRatio * (mTemplateVideoMaxDurationMilliSec/ ONE_SECOND_BY_MILLISECOND)).toFloat()
    }

    fun setVideoDuration(duration: Long){
//        val second = (duration/ 10f.pow(6)).toLong()

        mVideoDurationMilliSec = (duration/ 10f.pow(3)).toLong()
        if(mVideoDurationMilliSec > (10 * ONE_SECOND_BY_MILLISECOND)){
            mVideoOneTakeMilliSec = ONE_SECOND_BY_MILLISECOND
            mVideoDurationRatio = 1L
        }else{
            mVideoOneTakeMilliSec = mVideoDurationMilliSec / 10
            mVideoDurationRatio = ONE_SECOND_BY_MILLISECOND / mVideoOneTakeMilliSec
        }
//        mVideoOneTakeMilliSec = if(mVideoDurationMilliSec > (10 * ONE_SECOND_BY_MILLISECOND)) ONE_SECOND_BY_MILLISECOND else mVideoDurationMilliSec / 10
//        mVideoDurationRatio =
        logg("time: ${mVideoDurationMilliSec.convertSecondsToTime(TimeUnit.MICROSECONDS)}")

    }


    /**
     * todo 1201 이제 남은거
     * 1. 타임라인 템플릿 총 시간 받는거
     * 2. 타임라인 rect 이동할 때 시간 바뀌는거
     * 3. 비디오 시간이 10초 미만일 때 계산해주늑너 필요
     * 4. 그리고 만약 timeline check rect가 움직일 때 이에 따른 start도 바꿔줘야 하는거 아니니
     * 5. 그리고 이게 마무리 된다면 해당 시간초로 cropㅎ ㅏ자
     */
    fun setTimeLineTemp(startSec: Float){
//        600
//        1000 / 500

        logg("setTimeLineTemp startSec: $startSec")
        logg("setTimeLineTemp startSec: ${startSec * mVideoOneTakeMilliSec}")
        mTimelineStartPos = startSec * mVideoOneTakeMilliSec
        postInvalidate()

    }

    /**
     * This method draws dimmed area around the crop bounds.
     *
     * @param canvas - valid canvas object
     */
    private fun drawDimmedLayer(canvas: Canvas) { //start 11 11 //start 14 14
        canvas.save()

        canvas.clipRect(mTimeLineTrimmerRect, Region.Op.DIFFERENCE)

        canvas.drawColor(mDimmedColor)
        canvas.restore()
    }

    /**
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    private fun drawCropGrid(canvas: Canvas) { //start 12 12 //start 15 15
        logg("55onLayout drawCropGrid: ${mTimeLineTrimmerRect}")
        canvas.drawRect(mTimeLineTrimmerRect, mFramePaint)
        updatePoints()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y

        if(event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN){
            mTimeLineTrimmerTouchArea = getTouchArea(event)
            logg("onTouch down getTouchArea(event): $mTimeLineTrimmerTouchArea")
            if(mTimeLineTrimmerTouchArea == TimeLineTrimmerTouchArea.NONE){
                mPreviousTouchX = -1f
                mPreviousTouchY = -1f
            }else{
                mPreviousTouchX = x
                mPreviousTouchY = y
            }
            return mTimeLineTrimmerTouchArea != TimeLineTrimmerTouchArea.NONE
        }

        if(event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_MOVE){
            if(event.pointerCount == 1 && mTimeLineTrimmerTouchArea != TimeLineTrimmerTouchArea.NONE){
                //todo something
                logg("moveee prev x: $x")
                x = min(max(x, paddingLeft.toFloat()), width - paddingRight.toFloat())
                y = min(max(y, paddingTop.toFloat()), height - paddingBottom.toFloat())
                logg("moveee x: $x")
                logg("moveee width - paddingRight.toFloat(): ${width - paddingRight.toFloat()}")
                updateTimeLineRect(x, y)
                mPreviousTouchX = x
                mPreviousTouchY = y
            }
            return true
        }

        if(event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP){
            mTimeLineTrimmerTouchArea = TimeLineTrimmerTouchArea.NONE
            logg("onTouch down getTouchArea(event): $mTimeLineTrimmerTouchArea")
        }


        return false
    }

    private fun isEnableMovingRect(movingRectF: RectF): Boolean{
        logg("isEnableMovingRect movingRectF: ${movingRectF}")
        logg("@@isEnableMovingRect mMaxRectF: ${mMaxRectF}")
        return movingRectF.left >= mMaxRectF.left
                && movingRectF.top >= mMaxRectF.top
                && movingRectF.right <= mMaxRectF.right
                && movingRectF.bottom <= mMaxRectF.bottom
    }

    private fun updateTimeLineRect(x: Float, y: Float) {
//        val pureTemplateRatio = mMaxCropRectF.width() / mMaxCropRectF.height()
        mTempTimeLineRect.set(mTimeLineTrimmerRect)
        when(mTimeLineTrimmerTouchArea){
            TimeLineTrimmerTouchArea.LEFT_TOP -> {
            }
            TimeLineTrimmerTouchArea.RIGHT_TOP -> {
            }
            TimeLineTrimmerTouchArea.RIGHT_BOTTOM -> {
            }
            TimeLineTrimmerTouchArea.LEFT_BOTTOM -> {
            }
            TimeLineTrimmerTouchArea.IN -> {
                logg("22 mTimeLineTrimmerRect.left: ${mTimeLineTrimmerRect}")
                logg("updateTimeLineRect IN x: $x")
                logg("updateTimeLineRect IN mPreviousTouchX: $mPreviousTouchX")
                logg("updateTimeLineRect IN mTempTimeLineRect: $mTempTimeLineRect")
                logg("updateTimeLineRect IN timeLineRectMovingDirection: $timeLineRectMovingDirection")
                when (timeLineRectMovingDirection) {
                    TimeLineRectMovingDirection.ALL -> mTempTimeLineRect.offset(
                        x - mPreviousTouchX,
                        y - mPreviousTouchY
                    )
                    TimeLineRectMovingDirection.HORIZONTAL -> mTempTimeLineRect.offset(
                        x - mPreviousTouchX,
                        0f
                    )
                    TimeLineRectMovingDirection.VERTICAL -> mTempTimeLineRect.offset(
                        0f,
                        y - mPreviousTouchY
                    )
                }
//                mTempTimeLineRect.offset(
//                    x - mPreviousTouchX,
//                    0f
//                )
                logg("updateTimeLineRect IN 222mTempTimeLineRect: $mTempTimeLineRect")

                if (isEnableMovingRect(mTempTimeLineRect)) {
                    mTimeLineTrimmerRect.set(mTempTimeLineRect)
                    updatePoints()
                    postInvalidate()
                }
                return
            }
            else -> {}
        }

        //일단은 줄이거나 넓히는 기능없이 움직이기만 가능하게 작업중이라 주석
//        val changeHeight = mTempTimeLineRect.height() in (mRectMinSize.toFloat() / pureTemplateRatio).. mMaxCropRectF.height()
//                && mTempTimeLineRect.top >= 0
//                && mTempTimeLineRect.bottom <= resizeVideoRectF.height()
//        val changeWidth = mTempTimeLineRect.width() in mRectMinSize.toFloat() .. mMaxCropRectF.width()
//                && mTempTimeLineRect.left >= 0
//                && mTempTimeLineRect.right <= resizeVideoRectF.width()
//
//        if (changeHeight && changeWidth) {
//            mTimeLineTrimmerRect.set(mTempTimeLineRect)
//            updateGridPoints()
//            postInvalidate()
//        }
    }

    private var mEdgePoints: FloatArray? = null

    private fun getEdgePointsFromRect(r: RectF): FloatArray = floatArrayOf(
        r.left,
        r.top,
        r.right,
        r.top,
        r.right,
        r.bottom,
        r.left,
        r.bottom
    )

    private fun updatePoints(){
        mEdgePoints = getEdgePointsFromRect(mTimeLineTrimmerRect)
    }

    private fun getTouchArea(event: MotionEvent): TimeLineTrimmerTouchArea{
        for(i in 0..7 step 2){
            val distanceToEdge = sqrt(
                (event.x - (mEdgePoints?.get(i) ?: 0f)).pow(2)
                        + (event.y - (mEdgePoints?.get(i + 1) ?: 0f)).pow(2)
            )
            if( distanceToEdge < context.convertDpToPx(20) ){
                return when(i){
                    0 -> TimeLineTrimmerTouchArea.LEFT_TOP
                    1 -> TimeLineTrimmerTouchArea.RIGHT_TOP
                    2 -> TimeLineTrimmerTouchArea.RIGHT_BOTTOM
                    else ->  TimeLineTrimmerTouchArea.LEFT_BOTTOM
                }
            }
        }

        return if(mTimeLineTrimmerRect.contains(event.x, event.y)) TimeLineTrimmerTouchArea.IN
        else TimeLineTrimmerTouchArea.NONE
    }

    enum class TimeLineTrimmerTouchArea{
        NONE,
        IN,
        LEFT_TOP,
        RIGHT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM
    }

    enum class TimeLineRectMovingDirection(val value: Int){
        ALL(0),
        VERTICAL(1),
        HORIZONTAL(2);

        companion object {
            fun getTimeLineRectMovingDirectionByValue(value: Int = HORIZONTAL.value): TimeLineRectMovingDirection {
                for (direction in values()) {
                    if (direction.value == value) return direction
                }
                return HORIZONTAL
            }
        }
    }
}