package com.example.videocropandtrim.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.io.IOException
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.text.Typography.degree


fun Bitmap.rotate(degree: Float): Bitmap {
    val rotateMatrix = Matrix()
    rotateMatrix.postRotate(degree)

    return Bitmap.createBitmap(
        this,
        0,
        0,
        width,
        height,
        rotateMatrix,
        false
    )
}


class RTransformation(private val degree: Float): BitmapTransformation() {
    private val ID = "com.example.videocropandtrim"
    private val ID_BYTES: ByteArray = ID.toByteArray(Charset.forName("UTF-8"))
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
//        if (toTransform.width == outWidth && toTransform.height == outHeight) {
//            return toTransform
//        }
//        todo 1209 낼와서 여기 하자 왜 돌아가다 말지
        val exifInt = when(degree){
            90f -> ExifInterface.ORIENTATION_ROTATE_90
            180f -> ExifInterface.ORIENTATION_ROTATE_180
            270f -> ExifInterface.ORIENTATION_ROTATE_270
            else -> ExifInterface.ORIENTATION_NORMAL
        }
        logg("exifInt: $exifInt")
        return TransformationUtils.rotateImageExif(pool, toTransform, exifInt)
    }

    override fun equals(other: Any?): Boolean {
        return other is RTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }
}

fun rotateBitmap(src: String, degree: Float): Bitmap? {
    val bitmap = BitmapFactory.decodeFile(src)
    try {
        val exif = ExifInterface(
            src
        )
        var orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        logg("rotateBitmap src $src")

        orientation = when(degree){
            90f -> ExifInterface.ORIENTATION_ROTATE_90
            180f -> ExifInterface.ORIENTATION_ROTATE_180
            270f -> ExifInterface.ORIENTATION_ROTATE_270
            else -> ExifInterface.ORIENTATION_NORMAL
        }
        logg("rotateBitmap orientation $orientation")

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_UNDEFINED -> return bitmap
            else -> return bitmap
        }
        return try {
            val oriented =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            oriented
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bitmap
}
