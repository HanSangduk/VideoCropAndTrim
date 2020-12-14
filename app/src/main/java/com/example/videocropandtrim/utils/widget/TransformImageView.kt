package com.example.videocropandtrim.utils.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatImageView
import com.example.videocropandtrim.utils.FastBitmapToDrawable

class TransformImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object{
        private const val RECT_CORNER_POINTS_COORDS = 8
        private const val RECT_CENTER_POINT_COORDS = 2
        private const val MATRIX_VALUES_COUNT = 9
    }

    private val TAG = this.javaClass.simpleName

    private val mCurrentImageCorners = FloatArray(RECT_CORNER_POINTS_COORDS)
    private val mCurrentImageCenter = FloatArray(RECT_CENTER_POINT_COORDS)

    private val mMatrixValues = FloatArray(MATRIX_VALUES_COUNT)

    private var mCurrentImageMatrix = Matrix()
    private var mThisWidth = 0
    private  var mThisHeight:Int = 0

    var mTransformImageListener: TransformImageListener? = null

    interface TransformImageListener {
        fun onLoadComplete()
        fun onLoadFailure(e: Exception)
        fun onRotate(currentAngle: Float)
        fun onScale(currentScale: Float)
    }

    private var mInitialImageCorners: FloatArray? = null
    private var mInitialImageCenter: FloatArray? = null

    private var mBitmapDecoded = false
    private var mBitmapLaidOut = false

    private val mMaxBitmapSize = 0

    private val mImageInputPath: String? = null
    private  var mImageOutputPath:kotlin.String? = null
//    private val mExifInfo: ExifInfo? = null


    init {
        scaleType = ScaleType.MATRIX
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType == ScaleType.MATRIX) super.setScaleType(scaleType)
        else Log.e(TAG, "Invalid ScaleType. Only ScaleType.MATRIX can be used")
    }

    override fun setImageBitmap(bm: Bitmap?) {
        setImageDrawable(bm?.let { FastBitmapToDrawable(it) })
    }

    /**
     * @return - current image scale value.
     * [1.0f - for original image, 2.0f - for 200% scaled image, etc.]
     */
    fun getCurrentScale(): Float {
        return getMatrixScale(mCurrentImageMatrix)
    }

    /**
     * This method calculates scale value for given Matrix object.
     */
    fun getMatrixScale(matrix: Matrix): Float {
        return Math.sqrt(
            Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X).toDouble(), 2.0)
                    + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y).toDouble(), 2.0)
        )
            .toFloat()
    }

    /**
     * @return - current image rotation angle.
     */
    fun getCurrentAngle(): Float {
        return getMatrixAngle(mCurrentImageMatrix)
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    fun getMatrixAngle(matrix: Matrix): Float {
        return (-(Math.atan2(
            getMatrixValue(matrix, Matrix.MSKEW_X).toDouble(),
            getMatrixValue(matrix, Matrix.MSCALE_X).toDouble()
        ) * (180 / Math.PI))).toFloat()
    }

    /**
     * This method returns Matrix value for given index.
     *
     * @param matrix     - valid Matrix object
     * @param valueIndex - index of needed value. See [Matrix.MSCALE_X] and others.
     * @return - matrix value for index
     */
    protected fun getMatrixValue(
        matrix: Matrix,
        @IntRange(
            from = 0,
            to = MATRIX_VALUES_COUNT.toLong()
        ) valueIndex: Int
    ): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[valueIndex]
    }

    override fun setImageMatrix(matrix: Matrix?) {
        super.setImageMatrix(matrix)
        mCurrentImageMatrix.set(matrix)
        updateCurrentImagePoints()
    }

    fun getViewBitmap(): Bitmap?{
        drawable
        return null
    }

    /**
     * This method updates current image corners and center points that are stored in
     * [.mCurrentImageCorners] and [.mCurrentImageCenter] arrays.
     * Those are used for several calculations.
     */
    private fun updateCurrentImagePoints() {
        mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners)
        mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter)
    }

}