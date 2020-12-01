package com.example.videocropandtrim.utils.customview

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
import com.example.videocropandtrim.utils.customview.TimeLineTrimmer.TimeLineRectMovingDirection.Companion.getTimeLineRectMovingDirectionByValue
import com.example.videocropandtrim.utils.logg
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class TimeLineTrimmer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {

    companion object{

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

    private var timeLineRectMovingDirection: TimeLineRectMovingDirection = TimeLineRectMovingDirection.HORIZONTAL

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

            timeLineRectMovingDirection = getTimeLineRectMovingDirectionByValue(
                typedArray.getInt(
                R.styleable.TimeLineTrimmer_tlt_timeline_rect_direction,
                TimeLineRectMovingDirection.HORIZONTAL.value)
            )

            initFrame(typedArray)
            logg("init lineStrokeColor: $lineStrokeColor")
            logg("init lineStrokeColor2222: ${lineStrokeColor == ContextCompat.getColor(context, R.color.black)}")
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

        mMaxRectF.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (width - paddingRight).toFloat(),
            (height - paddingBottom).toFloat()
        )

        mTimeLineTrimmerRect.set(
            (width/2 - 150).toFloat(),
            paddingTop.toFloat(),
            (width/2 + 150).toFloat(),
            (height - paddingBottom).toFloat()
        )

        if(changed){
            val pLeft = paddingLeft
            val pTop = paddingTop
            val pRight = width - paddingRight
            val pBottom = height - paddingBottom
            mThisWidth = pRight - pLeft
            mThisHeight = pBottom - pTop
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { lCanvas ->
            drawDimmedLayer(lCanvas)
            drawCropGrid(lCanvas)
        }
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
            TimeLineTrimmerTouchArea.LEFT_TOP -> {}
            TimeLineTrimmerTouchArea.RIGHT_TOP -> {}
            TimeLineTrimmerTouchArea.RIGHT_BOTTOM -> {}
            TimeLineTrimmerTouchArea.LEFT_BOTTOM -> {}
            TimeLineTrimmerTouchArea.IN -> {
                logg("updateTimeLineRect IN x: $x")
                logg("updateTimeLineRect IN mPreviousTouchX: $mPreviousTouchX")
                logg("updateTimeLineRect IN mTempTimeLineRect: $mTempTimeLineRect")
                logg("updateTimeLineRect IN timeLineRectMovingDirection: $timeLineRectMovingDirection")
                when(timeLineRectMovingDirection){
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

                if(isEnableMovingRect(mTempTimeLineRect)){
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

    private fun getEdgePointsFromRect(r: RectF): FloatArray = floatArrayOf(r.left, r.top, r.right, r.top, r.right, r.bottom, r.left, r.bottom)

    private fun updatePoints(){
        mEdgePoints = getEdgePointsFromRect(mTimeLineTrimmerRect)
    }

    private fun getTouchArea(event: MotionEvent): TimeLineTrimmerTouchArea{
//        if(mTimeLineTrimmerRect.contains(event.x, event.y)){
//            return TimeLineTrimmerTouchArea.IN
//        }

        //todo 모서리 작업 해야할
        for(i in 0..7 step 2){
            logg("getTouchArea i: $i")
            val distanceToEdge = sqrt(
                (event.x - (mEdgePoints?.get(i) ?: 0f)).pow(2)
                + (event.y - (mEdgePoints?.get(i+1) ?: 0f)).pow(2)
            )
            if( distanceToEdge < context.convertDpToPx(20) ){
                return when(i){
                    0 ->  TimeLineTrimmerTouchArea.LEFT_TOP
                    1 ->  TimeLineTrimmerTouchArea.RIGHT_TOP
                    2 ->  TimeLineTrimmerTouchArea.RIGHT_BOTTOM
                    else ->  TimeLineTrimmerTouchArea.LEFT_BOTTOM
                }
            }
            logg("getTouchArea distanceToEdge: $distanceToEdge")
        }
        logg("getTouchArea context.convertDpToPx(15): ${context.convertDpToPx(15)}")

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