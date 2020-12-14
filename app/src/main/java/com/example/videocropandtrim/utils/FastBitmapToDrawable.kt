package com.example.videocropandtrim.utils

import android.graphics.*
import android.graphics.drawable.Drawable

class FastBitmapToDrawable(bitmap: Bitmap): Drawable() {

    private val mPaint = Paint(Paint.FILTER_BITMAP_FLAG)

   var mBitmap: Bitmap? = null
   var mAlpha = 0
   var mWidth = 0
   var mHeight:Int = 0

    init {
        mAlpha = 255
        setBitmap(bitmap)
    }

    override fun draw(canvas: Canvas) {
        mBitmap?.let { bitmap ->
            if(!bitmap.isRecycled) canvas.drawBitmap(bitmap, null, bounds, mPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private fun setBitmap(bitmap: Bitmap?) {
        bitmap?.let { lBitmap ->
            mWidth = lBitmap.width
            mHeight = lBitmap.height
        } ?: run {
            mHeight = 0
            mWidth = mHeight
        }
    }
}