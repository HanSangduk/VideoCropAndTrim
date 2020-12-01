package com.example.videocropandtrim.utils.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import com.example.videocropandtrim.R
import com.example.videocropandtrim.utils.getCenterFromRect
import com.example.videocropandtrim.utils.getCornersFromRect
import com.example.videocropandtrim.utils.logg
import java.math.RoundingMode
import java.text.DecimalFormat

class OverlayCropView
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(
    context,
    attrs,
    defStyleAttr
)
{

    companion object{
        const val FREESTYLE_CROP_MODE_DISABLE = 0
        const val FREESTYLE_CROP_MODE_ENABLE = 1
        const val FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH = 2

        const val DEFAULT_SHOW_CROP_FRAME = true
        const val DEFAULT_SHOW_CROP_GRID = true
        const val DEFAULT_CIRCLE_DIMMED_LAYER = false
        const val DEFAULT_FREESTYLE_CROP_MODE = FREESTYLE_CROP_MODE_ENABLE
        const val DEFAULT_CROP_GRID_ROW_COUNT = 2
        const val DEFAULT_CROP_GRID_COLUMN_COUNT = 2
    }

    private val mCropViewRect = RectF()
    private val mTempRect = RectF()

    var mThisWidth = 0
    var mThisHeight:Int = 0
    var mCropGridCorners: FloatArray? = null
    var mCropGridCenter: FloatArray? = null

    var mCropGridRowCount = 0
    var mCropGridColumnCount:Int = 0
    var mTargetAspectRatio = 0f
    var mGridPoints: FloatArray? = null
    var mShowCropFrame = false
    var mShowCropGrid:kotlin.Boolean = false
    var mCircleDimmedLayer = false
    var mDimmedColor = 0
    var mCircularPath = Path()
    var mDimmedStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mCropGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mCropFramePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mCropFrameCornersPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    @FreestyleMode
    var mFreestyleCropMode = DEFAULT_FREESTYLE_CROP_MODE
    var mPreviousTouchX = -1f
    var mPreviousTouchY:kotlin.Float = -1f
    var mCurrentTouchCornerIndex = -1
    var mTouchPointThreshold = 0
    var mCropRectMinSize = 0
    var mCropRectCornerTouchAreaLineLength = 0

//    private val mCallback: OverlayViewChangeListener? = null
    var overlayCropViewChangeListener: ((rectF: RectF) -> Unit)? = null

    val realVideoRectF = RectF()
    val resizeVideoRectF = RectF()

    private var mShouldSetupCropBounds = false

    /**
     * true = 자유롭게 cropRectF resize 가능
     * false = ratio 에 맞춰서 cropRectF resize 가능
     */
    private var isFreeCrop = false

    init {
        mTouchPointThreshold = resources.getDimensionPixelSize(R.dimen.default_crop_rect_corner_touch_threshold)
        mCropRectMinSize = resources.getDimensionPixelSize(R.dimen.default_crop_rect_min_size)
        mCropRectCornerTouchAreaLineLength = resources.getDimensionPixelSize(R.dimen.default_crop_rect_corner_touch_area_line_length)

        attrs?.let { lAttrs ->
            val a = context.obtainStyledAttributes(lAttrs, R.styleable.OverlayCropView)

            mCircleDimmedLayer = a.getBoolean(
                R.styleable.OverlayCropView_circle_dimmed_layer,
                DEFAULT_CIRCLE_DIMMED_LAYER
            )
            mDimmedColor = a.getColor(
                R.styleable.OverlayCropView_dimmed_color,
                ContextCompat.getColor(context, R.color.color_default_dimmed)
            )
            mDimmedStrokePaint.color = mDimmedColor
            mDimmedStrokePaint.style = Paint.Style.STROKE
            mDimmedStrokePaint.strokeWidth = 1f

            initCropFrameStyle(a)
            mShowCropFrame = a.getBoolean(
                R.styleable.OverlayCropView_show_frame,
                DEFAULT_SHOW_CROP_FRAME
            )

            initCropGridStyle(a)
            mShowCropGrid = a.getBoolean(
                R.styleable.OverlayCropView_show_grid,
                DEFAULT_SHOW_CROP_GRID
            )

            a.recycle()
        }
    }

    /**
     * This method setups Paint object for the crop bounds.
     */
    private fun initCropFrameStyle(a: TypedArray) { //start 33
        val cropFrameStrokeSize = a.getDimensionPixelSize(
            R.styleable.OverlayCropView_frame_stroke_size,
            resources.getDimensionPixelSize(R.dimen.default_crop_frame_stoke_width)
        )
        val cropFrameColor = a.getColor(
            R.styleable.OverlayCropView_frame_color,
            ContextCompat.getColor(context, R.color.color_default_crop_frame)
        )
        mCropFramePaint.strokeWidth = cropFrameStrokeSize.toFloat()
        mCropFramePaint.color = cropFrameColor
        mCropFramePaint.style = Paint.Style.STROKE
        mCropFrameCornersPaint.strokeWidth = cropFrameStrokeSize * 3.toFloat()
        mCropFrameCornersPaint.color = cropFrameColor
        mCropFrameCornersPaint.style = Paint.Style.STROKE
    }

    /**
     * This method setups Paint object for the crop guidelines.
     */
    private fun initCropGridStyle(a: TypedArray) { //start 44
        val cropGridStrokeSize = a.getDimensionPixelSize(
            R.styleable.OverlayCropView_grid_stroke_size,
            resources.getDimensionPixelSize(R.dimen.default_crop_grid_stoke_width)
        )
        val cropGridColor = a.getColor(
            R.styleable.OverlayCropView_grid_color,
            ContextCompat.getColor(context, R.color.color_default_crop_grid)
        )
        mCropGridPaint.strokeWidth = cropGridStrokeSize.toFloat()
        mCropGridPaint.color = cropGridColor
        mCropGridRowCount = a.getInt(
            R.styleable.OverlayCropView_grid_row_count,
            DEFAULT_CROP_GRID_ROW_COUNT
        )
        mCropGridColumnCount = a.getInt(
            R.styleable.OverlayCropView_grid_column_count,
            DEFAULT_CROP_GRID_COLUMN_COUNT
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            val nLeft = paddingLeft
            val nTop = paddingTop
            val nRight = width - paddingRight
            val nBottom = height - paddingBottom
            mThisWidth = nRight - nLeft
            mThisHeight = nBottom - nTop
            if (mShouldSetupCropBounds) {
                mShouldSetupCropBounds = false
                setTargetAspectRatio(mTargetAspectRatio)
            }
        }
    }

    /**
     * This method sets aspect ratio for crop bounds.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    fun setTargetAspectRatio(targetAspectRatio: Float) { //start 66
        mTargetAspectRatio = targetAspectRatio
        if (mThisWidth > 0) {
            setupCropBounds()
            postInvalidate()
        } else {
            mShouldSetupCropBounds = true
        }
    }

//    fun setRealVideoRectF(rectF: RectF){
//        realVideoRectF
//        mCropViewRect.set(rectF)
//        updateGridPoints()
//        postInvalidate()
//    }

    private val mTemplateRectF = RectF()
    private val mMaxCropRectF = RectF()

    private fun setTemplateRectF(templateRectF: RectF): RectF{
        mTemplateRectF.set(templateRectF)
        val templateRatio = templateRectF.width() / templateRectF.height()
        val resizeWidth = resizeVideoRectF.width()
        val resizeHeight = resizeVideoRectF.height()
        val resizeRatio = resizeWidth / resizeHeight
        val rTemplateRectF = when {
            templateRatio > 1 -> {
                val transRatio = templateRectF.width() / resizeWidth
                val height = templateRectF.height() / transRatio
                val pivotHeight = resizeHeight/2
                RectF(0f, pivotHeight - height/2, resizeWidth, pivotHeight + height/2 )
            }
            templateRatio == 1f -> {
                val pivotHeight = resizeHeight/2
                val pivotWidth = resizeWidth/2
                val tansRatioHeight = templateRectF.width() / resizeWidth
                val height = templateRectF.height() / tansRatioHeight
                val tansRatioWidth = templateRectF.height() / resizeHeight
                val width = templateRectF.width() / tansRatioWidth
                when {
                    resizeRatio > 1 -> RectF( pivotWidth - width/2, 0f, pivotWidth + width/2, resizeHeight )
                    resizeRatio < 1 -> RectF( 0f, pivotHeight - height/2, resizeWidth, pivotHeight + height/2 )
                    else -> RectF(0f, 0f, resizeWidth, resizeHeight )
                }
            }
            else -> {
                val transRatio = templateRectF.height() / resizeHeight
                logg("transRatio: $transRatio")
                val width = templateRectF.width() / transRatio
                logg("resizeWidth: $resizeWidth")
                logg("width: $width")
                val pivotWidth = resizeWidth/2
                logg("pivotWidth: $pivotWidth")
                logg("pivotWidth - width: ${pivotWidth - width}")
                RectF(pivotWidth - width/2, 0f, pivotWidth + width/2, resizeHeight )
            }
        }

        rTemplateRectF.set(
            if(rTemplateRectF.right > resizeVideoRectF.right) 0f else rTemplateRectF.left,
            if(rTemplateRectF.bottom > resizeVideoRectF.bottom) 0f else rTemplateRectF.top,
            if(rTemplateRectF.right > resizeVideoRectF.right) resizeVideoRectF.right else rTemplateRectF.right,
            if(rTemplateRectF.bottom > resizeVideoRectF.bottom) resizeVideoRectF.bottom else rTemplateRectF.bottom
        )

        return rTemplateRectF
    }

    /**
     * teamplate size 나오면 넣어주는 곳
     */
    fun setTemplateCropRectF(templateRectF: RectF){
        mMaxCropRectF.set(setTemplateRectF(templateRectF))
        logg("mMaxCropRectF: $mMaxCropRectF")
        setCropViewRectf(mMaxCropRectF)
    }

    private fun setCropViewRectf(rectF: RectF){
        logg("realVideoRectF: ${realVideoRectF.width()}")
        if(realVideoRectF.width() < 1) return
        mCropViewRect.set(rectF)
//        resizeVideoRectF.set(rectF)
        updateGridPoints()
        postInvalidate()
    }

    /**
     * left => width
     * top => height
     * right => x 좌표
     * bottom => y 좌표
     */
    fun getRealSizeCropRectF(): RectF?{
        if(resizeVideoRectF.width() < 1 || resizeVideoRectF.height() < 1) return null
        val widthRatio = realVideoRectF.width() / resizeVideoRectF.width()
        val heightRatio = realVideoRectF.height() / resizeVideoRectF.height()

        return RectF(
            mCropViewRect.width() * widthRatio,
            mCropViewRect.height() * heightRatio,
            mCropViewRect.left * widthRatio,
            mCropViewRect.top * heightRatio
        )
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * [.mCropViewRect] is used to draw crop bounds - uses padding.
     */
    fun setupCropBounds() { //start 77
        val height = (mThisWidth / mTargetAspectRatio).toInt()
        if (height > mThisHeight) {
            val width = (mThisHeight * mTargetAspectRatio).toInt()
            val halfDiff = (mThisWidth - width) / 2
            mCropViewRect[paddingLeft + halfDiff.toFloat(), paddingTop.toFloat(), paddingLeft + width + halfDiff.toFloat()] =
                paddingTop + mThisHeight.toFloat()
        } else {
            val halfDiff = (mThisHeight - height) / 2
            mCropViewRect[paddingLeft.toFloat(), paddingTop + halfDiff.toFloat(), paddingLeft + mThisWidth.toFloat()] =
                paddingTop + height + halfDiff.toFloat()
        }
        logg("setupCropBounds overlayCropViewChangeListener rectf: $mCropViewRect")
        overlayCropViewChangeListener?.invoke(mCropViewRect)

        updateGridPoints()
    }

    private fun updateGridPoints() { //start 88
        mCropGridCorners = getCornersFromRect(mCropViewRect)
        mCropGridCenter = getCenterFromRect(mCropViewRect)
        mGridPoints = null
        mCircularPath.reset()
        mCircularPath.addCircle(
            mCropViewRect.centerX(), mCropViewRect.centerY(),
            Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2f, Path.Direction.CW
        )
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
    protected fun drawDimmedLayer(canvas: Canvas) { //start 11 11 //start 14 14
        canvas.save()
        if (mCircleDimmedLayer) {
            canvas.clipPath(mCircularPath, Region.Op.DIFFERENCE)
        } else {
            canvas.clipRect(mCropViewRect, Region.Op.DIFFERENCE)
        }
        canvas.drawColor(mDimmedColor)
        canvas.restore()
        if (mCircleDimmedLayer) { // Draw 1px stroke to fix antialias
            canvas.drawCircle(
                mCropViewRect.centerX(), mCropViewRect.centerY(),
                Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2f, mDimmedStrokePaint
            )
        }
    }

    /**
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    protected fun drawCropGrid(canvas: Canvas) { //start 12 12 //start 15 15
        if (mShowCropGrid) {
            if (mGridPoints == null && !mCropViewRect.isEmpty) {
                mGridPoints = FloatArray(mCropGridRowCount * 4 + mCropGridColumnCount * 4)
                var index = 0
                for (i in 0 until mCropGridRowCount) {
                    mGridPoints!![index++] = mCropViewRect.left
                    mGridPoints!![index++] =
                        mCropViewRect.height() * ((i.toFloat() + 1.0f) / (mCropGridRowCount + 1).toFloat()) + mCropViewRect.top
                    mGridPoints!![index++] = mCropViewRect.right
                    mGridPoints!![index++] =
                        mCropViewRect.height() * ((i.toFloat() + 1.0f) / (mCropGridRowCount + 1).toFloat()) + mCropViewRect.top
                }
                for (i in 0 until mCropGridColumnCount) {
                    mGridPoints!![index++] =
                        mCropViewRect.width() * ((i.toFloat() + 1.0f) / (mCropGridColumnCount + 1).toFloat()) + mCropViewRect.left
                    mGridPoints!![index++] = mCropViewRect.top
                    mGridPoints!![index++] =
                        mCropViewRect.width() * ((i.toFloat() + 1.0f) / (mCropGridColumnCount + 1).toFloat()) + mCropViewRect.left
                    mGridPoints!![index++] = mCropViewRect.bottom
                }
            }
            if (mGridPoints != null) {
                canvas.drawLines(mGridPoints!!, mCropGridPaint)
            }
        }
        if (mShowCropFrame) {
            canvas.drawRect(mCropViewRect, mCropFramePaint)
        }
        if (mFreestyleCropMode != FREESTYLE_CROP_MODE_DISABLE) {
            canvas.save()
            mTempRect.set(mCropViewRect)
            mTempRect.inset(
                mCropRectCornerTouchAreaLineLength.toFloat(),
                -mCropRectCornerTouchAreaLineLength.toFloat()
            )
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE)
            mTempRect.set(mCropViewRect)
            mTempRect.inset(
                -mCropRectCornerTouchAreaLineLength.toFloat(),
                mCropRectCornerTouchAreaLineLength.toFloat()
            )
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE)
            canvas.drawRect(mCropViewRect, mCropFrameCornersPaint)
            canvas.restore()
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mCropViewRect.isEmpty || mFreestyleCropMode == FREESTYLE_CROP_MODE_DISABLE) {
            return false
        }
        var x = event.x
        var y = event.y
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
            mCurrentTouchCornerIndex = getCurrentTouchIndex(x, y)
            val shouldHandle = mCurrentTouchCornerIndex != -1
            if (!shouldHandle) {
                mPreviousTouchX = -1f
                mPreviousTouchY = -1f
            } else if (mPreviousTouchX < 0) {
                mPreviousTouchX = x
                mPreviousTouchY = y
            }
            return shouldHandle
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_MOVE) {
            if (event.pointerCount == 1 && mCurrentTouchCornerIndex != -1) {
                x = Math.min(Math.max(x, paddingLeft.toFloat()), width - paddingRight.toFloat())
                y = Math.min(Math.max(y, paddingTop.toFloat()), height - paddingBottom.toFloat())
                updateCropViewRect(x, y)
                mPreviousTouchX = x
                mPreviousTouchY = y
                return true
            }
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            mPreviousTouchX = -1f
            mPreviousTouchY = -1f
            mCurrentTouchCornerIndex = -1
            logg("action up overlayCropViewChangeListener rectf: $mCropViewRect")
            overlayCropViewChangeListener?.invoke(mCropViewRect)
        }
        return false
    }

    private fun isEnableMovingRectF(currentRectF: RectF): Boolean{
        logg("isEnableMovingRectF currentRectF: $currentRectF")
        logg("@@ isEnableMovingRectF mCropViewRect: $resizeVideoRectF")
        return currentRectF.left >= resizeVideoRectF.left
                && currentRectF.top >= resizeVideoRectF.top
                && currentRectF.right <= resizeVideoRectF.right
                && currentRectF.bottom <= resizeVideoRectF.bottom
    }

    /**
     * * The order of the corners is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     */
    private fun updateCropViewRect(touchX: Float, touchY: Float){
        if(isFreeCrop)
            updateFreeCropViewRect(touchX, touchY)
        else
            updateRatioCropViewRect(touchX, touchY)
    }

    private fun updateFreeCropViewRect(touchX: Float, touchY: Float) {
        mTempRect.set(mCropViewRect)
        when (mCurrentTouchCornerIndex) {
            0 -> mTempRect[touchX, touchY, mCropViewRect.right] =
                mCropViewRect.bottom
            1 -> mTempRect[mCropViewRect.left, touchY, touchX] = mCropViewRect.bottom
            2 -> mTempRect[mCropViewRect.left, mCropViewRect.top, touchX] = touchY
            3 -> mTempRect[touchX, mCropViewRect.top, mCropViewRect.right] = touchY
            4 -> {
                mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY)
                if (isEnableMovingRectF(mTempRect)) {
                    mCropViewRect.set(mTempRect)
                    updateGridPoints()
                    postInvalidate()
                }
                return
            }
        }

        val changeHeight = mTempRect.height() >= mCropRectMinSize
        val changeWidth = mTempRect.width() >= mCropRectMinSize
        mCropViewRect[
                if (changeWidth) mTempRect.left else mCropViewRect.left,
                if (changeHeight) mTempRect.top else mCropViewRect.top,
                if (changeWidth) mTempRect.right else mCropViewRect.right
        ] = if (changeHeight) mTempRect.bottom else mCropViewRect.bottom
        if (changeHeight || changeWidth) {
            updateGridPoints()
            postInvalidate()
        }
    }

    private fun updateRatioCropViewRect(touchX: Float, touchY: Float) {
        val pureTemplateRatio = mMaxCropRectF.width() / mMaxCropRectF.height()
        mTempRect.set(mCropViewRect)
        when (mCurrentTouchCornerIndex) {
            0 -> mTempRect[touchX, mCropViewRect.bottom - (mCropViewRect.right - touchX) / pureTemplateRatio, mCropViewRect.right] = mCropViewRect.bottom
            1 -> mTempRect[mCropViewRect.left, mCropViewRect.bottom - (touchX - mCropViewRect.left) / pureTemplateRatio, touchX] = mCropViewRect.bottom
            2 -> mTempRect[mCropViewRect.left, mCropViewRect.top, touchX] = mCropViewRect.top + (touchX - mCropViewRect.left) / pureTemplateRatio
            3 -> mTempRect[touchX, mCropViewRect.top, mCropViewRect.right] = mCropViewRect.top + (mCropViewRect.right - touchX)/pureTemplateRatio
            4 -> {
                mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY)
                if (isEnableMovingRectF(mTempRect)) {
                    mCropViewRect.set(mTempRect)
                    updateGridPoints()
                    postInvalidate()
                }
                return
            }
        }

        val changeHeight = mTempRect.height() in (mCropRectMinSize.toFloat() / pureTemplateRatio).. mMaxCropRectF.height()
                && mTempRect.top >= 0
                && mTempRect.bottom <= resizeVideoRectF.height()
        val changeWidth = mTempRect.width() in mCropRectMinSize.toFloat() .. mMaxCropRectF.width()
                && mTempRect.left >= 0
                && mTempRect.right <= resizeVideoRectF.width()

        if (changeHeight && changeWidth) {
            mCropViewRect.set(mTempRect)
            updateGridPoints()
            postInvalidate()
        }
    }

    /**
     * * The order of the corners in the float array is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     *
     * @return - index of corner that is being dragged
     */
    private fun getCurrentTouchIndex(touchX: Float, touchY: Float): Int {
        var closestPointIndex = -1
        var closestPointDistance = mTouchPointThreshold.toDouble()
        var i = 0
        while (i < 8) {
            val distanceToCorner = Math.sqrt(
                Math.pow(touchX - mCropGridCorners!![i].toDouble(), 2.0)
                        + Math.pow(touchY - mCropGridCorners!![i + 1].toDouble(), 2.0)
            )
            if (distanceToCorner < closestPointDistance) {
                closestPointDistance = distanceToCorner
                closestPointIndex = i / 2
            }
            i += 2
        }
        return if (mFreestyleCropMode == FREESTYLE_CROP_MODE_ENABLE && closestPointIndex < 0 && mCropViewRect.contains(
                touchX,
                touchY
            )
        ) {
            4
        } else closestPointIndex

//        for (int i = 0; i <= 8; i += 2) {
//
//            double distanceToCorner;
//            if (i < 8) { // corners
//                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2)
//                        + Math.pow(touchY - mCropGridCorners[i + 1], 2));
//            } else { // center
//                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCenter[0], 2)
//                        + Math.pow(touchY - mCropGridCenter[1], 2));
//            }
//            if (distanceToCorner < closestPointDistance) {
//                closestPointDistance = distanceToCorner;
//                closestPointIndex = i / 2;
//            }
//        }
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        FREESTYLE_CROP_MODE_DISABLE,
        FREESTYLE_CROP_MODE_ENABLE,
        FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH
    )
    annotation class FreestyleMode
}