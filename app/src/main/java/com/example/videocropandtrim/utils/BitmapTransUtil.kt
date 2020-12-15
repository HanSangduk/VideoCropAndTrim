package com.example.videocropandtrim.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.util.Log
import java.io.*
import java.lang.Math.max
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

//class BitmapTransUtil {
    private val emptyRect = Rect()

    //    static final RectF EMPTY_RECT_F = new RectF();
    //
    //    /**
    //     * Reusable rectangle for general internal usage
    //     */
    //    static final RectF RECT = new RectF();
    //
    //    /**
    //     * Reusable point for general internal usage
    //     */
    //    static final float[] POINTS = new float[6];
    //
    //    /**
    //     * Reusable point for general internal usage
    //     */
    //    static final float[] POINTS2 = new float[6];
    //    /**
    //     * Used to know the max texture size allowed to be rendered
    //     */
    //    static int mMaxTextureSize;
    //    /**
    //     * used to save bitmaps during state save and restore so not to reload them.
    //     */
    //    static Pair<String, WeakReference<Bitmap>> mStateBitmap;
    //
    //    /**
    //     * Rotate the given image by reading the Exif value of the image (uri).<br>
    //     * If no rotation is required the image will not be rotated.<br>
    //     * New bitmap is created and the old one is recycled.
    //     */
    //    public static RotateBitmapResult rotateBitmapByExif(Bitmap bitmap, Context context, Uri uri) {
    //        try {
    //            File file = getFileFromUri(context, uri);
    //            if (file.exists()) {
    //                ExifInterface ei = new ExifInterface(file.getAbsolutePath());
    //                return rotateBitmapByExif(bitmap, ei);
    //            }
    //        } catch (Exception ignored) {
    //        }
    //        return new RotateBitmapResult(bitmap, 0);
    //    }
    //    /**
    //     * Rotate the given image by given Exif value.<br>
    //     * If no rotation is required the image will not be rotated.<br>
    //     * New bitmap is created and the old one is recycled.
    //     */
    //    public static RotateBitmapResult rotateBitmapByExif(Bitmap bitmap, ExifInterface exif) {
    //        int degrees;
    //        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    //        switch (orientation) {
    //            case ExifInterface.ORIENTATION_ROTATE_90:
    //                degrees = 90;
    //                break;
    //            case ExifInterface.ORIENTATION_ROTATE_180:
    //                degrees = 180;
    //                break;
    //            case ExifInterface.ORIENTATION_ROTATE_270:
    //                degrees = 270;
    //                break;
    //            default:
    //                degrees = 0;
    //                break;
    //        }
    //        return new RotateBitmapResult(bitmap, degrees);
    //    }
    //    /**
    //     * Decode bitmap from stream using sampling to get bitmap with the requested limit.
    //     */
    //    public static BitmapSampled decodeSampledBitmap(Context context, Uri uri, int reqWidth, int reqHeight) {
    //
    //        try {
    //            ContentResolver resolver = context.getContentResolver();
    //
    //            // First decode with inJustDecodeBounds=true to check dimensions
    //            BitmapFactory.Options options = decodeImageForOption(resolver, uri);
    //
    //            // Calculate inSampleSize
    //            options.inSampleSize = Math.max(
    //                    calculateInSampleSizeByReqestedSize(options.outWidth, options.outHeight, reqWidth, reqHeight),
    //                    calculateInSampleSizeByMaxTextureSize(options.outWidth, options.outHeight));
    //
    //            // Decode bitmap with inSampleSize set
    //            Bitmap bitmap = decodeImage(resolver, uri, options);
    //
    //            return new BitmapSampled(bitmap, options.inSampleSize);
    //
    //        } catch (Exception e) {
    //            throw new RuntimeException("Failed to load sampled bitmap: " + uri + "\r\n" + e.getMessage(), e);
    //        }
    //    }
    /**
     * Crop image bitmap from given bitmap using the given points in the original bitmap and the given rotation.<br></br>
     * if the rotation is not 0,90,180 or 270 degrees then we must first crop a larger area of the image that
     * contains the requires rectangle, rotate and then crop again a sub rectangle.
     */
    fun cropBitmap(
        bitmap: Bitmap, points: FloatArray, degreesRotated: Int,
        fixAspectRatio: Boolean, aspectRatioX: Int, aspectRatioY: Int
    ): Bitmap? {

        // get the rectangle in original image that contains the required cropped area (larger for non rectangular crop)
        val rect = getRectFromPoints(
            points,
            bitmap.width,
            bitmap.height,
            fixAspectRatio,
            aspectRatioX,
            aspectRatioY
        )

        // crop and rotate the cropped image in one operation
        val matrix = Matrix()
        matrix.setRotate(
            degreesRotated.toFloat(),
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat()
        )
        var result = Bitmap.createBitmap(
            bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height(),
            matrix,
            true
        )
        if (result == bitmap) {
            // corner case when all bitmap is selected, no worth optimizing for it
            result = bitmap.copy(bitmap.config, false)
        }

        // rotating by 0, 90, 180 or 270 degrees doesn't require extra cropping
        if (degreesRotated % 90 != 0) {

            // extra crop because non rectangular crop cannot be done directly on the image without rotating first
            result = cropForRotatedImage(
                result,
                points,
                rect,
                degreesRotated,
                fixAspectRatio,
                aspectRatioX,
                aspectRatioY
            )
        }
        return result
    }

    /**
     * Crop image bitmap from URI by decoding it with specific width and height to down-sample if required.<br></br>
     * Additionally if OOM is thrown try to increase the sampling (2,4,8).
     */
    fun cropBitmap(
        context: Context, loadedImageUri: Uri, points: FloatArray,
        degreesRotated: Int, orgWidth: Int, orgHeight: Int, fixAspectRatio: Boolean,
        aspectRatioX: Int, aspectRatioY: Int, reqWidth: Int, reqHeight: Int
    ): BitmapSampled {
        var sampleMulti = 1
        while (true) {
            try {
                // if successful, just return the resulting bitmap
                return cropBitmap(
                    context, loadedImageUri, points,
                    degreesRotated, orgWidth, orgHeight, fixAspectRatio,
                    aspectRatioX, aspectRatioY, reqWidth, reqHeight,
                    sampleMulti
                )
            } catch (e: OutOfMemoryError) {
                logg("BitmapUtils, cropBitmap, OutOfMemoryError~!!! sampling retry~!!!")
                // if OOM try to increase the sampling to lower the memory usage
                sampleMulti *= 2
                if (sampleMulti > 16) {
                    throw RuntimeException(
                        """
                        Failed to handle OOM by sampling ($sampleMulti): $loadedImageUri
                        ${e.message}
                        """.trimIndent(), e
                    )
                }
            }
        }
    }

    /**
     * Get left value of the bounding rectangle of the given points.
     */
    fun getRectLeft(points: FloatArray): Float {
        return points[0]
            .coerceAtMost(points[2])
            .coerceAtMost(points[4])
            .coerceAtMost(points[6])
    }

    /**
     * Get top value of the bounding rectangle of the given points.
     */
    fun getRectTop(points: FloatArray): Float {
        return points[1]
            .coerceAtMost(points[3])
            .coerceAtMost(points[5])
            .coerceAtMost(points[7])
    }

    /**
     * Get right value of the bounding rectangle of the given points.
     */
    fun getRectRight(points: FloatArray): Float {
        return points[0]
            .coerceAtLeast(points[2])
            .coerceAtLeast(points[4])
            .coerceAtLeast(points[6])
    }

    /**
     * Get bottom value of the bounding rectangle of the given points.
     */
    fun getRectBottom(points: FloatArray): Float {
        return points[1]
            .coerceAtLeast(points[3])
            .coerceAtLeast(points[5])
            .coerceAtLeast(points[7])
    }
    //    /**
    //     * Get width of the bounding rectangle of the given points.
    //     */
    //    public static float getRectWidth(float[] points) {
    //        return getRectRight(points) - getRectLeft(points);
    //    }
    //
    //    /**
    //     * Get heightof the bounding rectangle of the given points.
    //     */
    //    public static float getRectHeight(float[] points) {
    //        return getRectBottom(points) - getRectTop(points);
    //    }
    //
    //    /**
    //     * Get horizontal center value of the bounding rectangle of the given points.
    //     */
    //    public static float getRectCenterX(float[] points) {
    //        return (getRectRight(points) + getRectLeft(points)) / 2f;
    //    }
    //
    //    /**
    //     * Get verical center value of the bounding rectangle of the given points.
    //     */
    //    public static float getRectCenterY(float[] points) {
    //        return (getRectBottom(points) + getRectTop(points)) / 2f;
    //    }
    /**
     * Get a rectangle for the given 4 points (x0,y0,x1,y1,x2,y2,x3,y3) by finding the min/max 2 points that
     * contains the given 4 points and is a stright rectangle.
     */
    fun getRectFromPoints(
        points: FloatArray,
        imageWidth: Int,
        imageHeight: Int,
        fixAspectRatio: Boolean,
        aspectRatioX: Int,
        aspectRatioY: Int
    ): Rect {
        val left = Math.round(max(0f, getRectLeft(points)))
        val top = Math.round(max(0f, getRectTop(points)))
        val right = Math.round(Math.min(imageWidth.toFloat(), getRectRight(points)))
        val bottom = Math.round(Math.min(imageHeight.toFloat(), getRectBottom(points)))
        val rect = Rect(left, top, right, bottom)
        if (fixAspectRatio) {
            fixRectForAspectRatio(rect, aspectRatioX, aspectRatioY)
        }
        return rect
    }

    /**
     * Fix the given rectangle if it doesn't confirm to aspect ration rule.<br></br>
     * Make sure that width and height are equal if 1:1 fixed aspect ratio is requested.
     */
    fun fixRectForAspectRatio(rect: Rect, aspectRatioX: Int, aspectRatioY: Int) {
        if (aspectRatioX == aspectRatioY && rect.width() != rect.height()) {
            if (rect.height() > rect.width()) {
                rect.bottom -= rect.height() - rect.width()
            } else {
                rect.right -= rect.width() - rect.height()
            }
        }
    }

    /**
     * Write the given bitmap to the given uri using the given compression.
     */
    @Throws(FileNotFoundException::class)
    fun writeBitmapToUri(
        context: Context,
        bitmap: Bitmap,
        uri: Uri?,
        compressFormat: CompressFormat?,
        compressQuality: Int
    ) {
        var outputStream: OutputStream? = null
        try {
            outputStream = context.contentResolver.openOutputStream(uri!!)
            bitmap.compress(compressFormat, compressQuality, outputStream)
        } finally {
            closeSafe(outputStream)
        }
    }

    /**
     * Resize the given bitmap to the given width/height by the given option.<br></br>
     */
    fun resizeBitmap(
        bitmap: Bitmap,
        reqWidth: Int,
        reqHeight: Int,
        options: RequestSizeOptions
    ): Bitmap {
        try {
            if (reqWidth > 0 && reqHeight > 0 && (options === RequestSizeOptions.RESIZE_FIT || options === RequestSizeOptions.RESIZE_INSIDE || options === RequestSizeOptions.RESIZE_EXACT)) {
                var resized: Bitmap? = null
                if (options === RequestSizeOptions.RESIZE_EXACT) {
                    resized = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false)
                } else {
                    val width = bitmap.width
                    val height = bitmap.height
                    val scale =
                        max(width / reqWidth.toFloat(), height / reqHeight.toFloat())
                    if (scale > 1 || options === RequestSizeOptions.RESIZE_FIT) {
                        resized = Bitmap.createScaledBitmap(
                            bitmap,
                            (width / scale).toInt(), (height / scale).toInt(), false
                        )
                    }
                }
                if (resized != null) {
                    if (resized != bitmap) {
                        bitmap.recycle()
                    }
                    return resized
                }
            }
        } catch (e: Exception) {
            Log.w("AIC", "Failed to resize cropped image, return bitmap before resize", e)
        }
        return bitmap
    }

    fun resizeBitmapNew(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val ratioX = newWidth / bitmap.width.toFloat()
        val ratioY = newHeight / bitmap.height.toFloat()
        val middleX = newWidth / 2.0f
        val middleY = newHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap, middleX - bitmap.width / 2, middleY - bitmap.height / 2, Paint(
                Paint.FILTER_BITMAP_FLAG
            )
        )
        return scaledBitmap
    }
    //region: Private methods
    /**
     * Crop image bitmap from URI by decoding it with specific width and height to down-sample if required.
     *
     * @param orgWidth used to get rectangle from points (handle edge cases to limit rectangle)
     * @param orgHeight used to get rectangle from points (handle edge cases to limit rectangle)
     * @param sampleMulti used to increase the sampling of the image to handle memory issues.
     */
    private fun cropBitmap(
        context: Context, loadedImageUri: Uri, points: FloatArray,
        degreesRotated: Int, orgWidth: Int, orgHeight: Int, fixAspectRatio: Boolean,
        aspectRatioX: Int, aspectRatioY: Int, reqWidth: Int, reqHeight: Int, sampleMulti: Int
    ): BitmapSampled {

        // get the rectangle in original image that contains the required cropped area (larger for non rectangular crop)
        val rect = getRectFromPoints(
            points,
            orgWidth,
            orgHeight,
            fixAspectRatio,
            aspectRatioX,
            aspectRatioY
        )
        val width = if (reqWidth > 0) reqWidth else rect.width()
        val height = if (reqHeight > 0) reqHeight else rect.height()
        logg("BitmapUtils, cropBitmap, rect.width() = " + rect.width() + ", rect.height() = " + rect.height())
        logg("BitmapUtils, cropBitmap, reqWidth = $reqWidth, reqHeight = $reqHeight")
        logg("BitmapUtils, cropBitmap, width = $width, height = $height")
        var result: Bitmap? = null
        var sampleSize = 1
        try {
            // decode only the required image from URI, optionally sub-sampling if reqWidth/reqHeight is given.
            val bitmapSampled = decodeSampledBitmapRegion(
                context,
                loadedImageUri,
                rect,
                width,
                height,
                sampleMulti
            )
            result = bitmapSampled.bitmap
            sampleSize = bitmapSampled.sampleSize
        } catch (e: Exception) {
            logg("BitmapUtils, cropBitmap, Exception!!!")
        }
        return if (result != null) {
            try {
                // rotate the decoded region by the required amount
                result = rotateBitmapInt(result, degreesRotated)

                // rotating by 0, 90, 180 or 270 degrees doesn't require extra cropping
                if (degreesRotated % 90 != 0) {

                    // extra crop because non rectangular crop cannot be done directly on the image without rotating first
                    result = cropForRotatedImage(
                        result,
                        points,
                        rect,
                        degreesRotated,
                        fixAspectRatio,
                        aspectRatioX,
                        aspectRatioY
                    )
                }
            } catch (e: OutOfMemoryError) {
                logg("BitmapUtils, cropBitmap, OutOfMemoryError!!!")
                if (result != null) {
                    result.recycle()
                }
                throw e
            }
            BitmapSampled(result, sampleSize)
        } else {
            // failed to decode region, may be skia issue, try full decode and then crop
            cropBitmap(
                context,
                loadedImageUri,
                points,
                degreesRotated,
                fixAspectRatio,
                aspectRatioX,
                aspectRatioY,
                sampleMulti,
                rect,
                width,
                height
            )
        }
    }

    /**
     * Crop bitmap by fully loading the original and then cropping it, fallback in case cropping region failed.
     */
    private fun cropBitmap(
        context: Context, loadedImageUri: Uri, points: FloatArray,
        degreesRotated: Int, fixAspectRatio: Boolean, aspectRatioX: Int, aspectRatioY: Int,
        sampleMulti: Int, rect: Rect, width: Int, height: Int
    ): BitmapSampled {
        var result: Bitmap? = null
        val sampleSize: Int
        try {
            val options = BitmapFactory.Options()
            sampleSize = sampleMulti * calculateInSampleSizeByReqestedSize(
                rect.width(),
                rect.height(),
                width,
                height
            )
            options.inSampleSize = sampleSize
            val fullBitmap = decodeImage(context.contentResolver, loadedImageUri, options)
            if (fullBitmap != null) {
                try {
                    // adjust crop points by the sampling because the image is smaller
                    val points2 = FloatArray(points.size)
                    System.arraycopy(points, 0, points2, 0, points.size)
                    for (i in points2.indices) {
                        points2[i] = points2[i] / options.inSampleSize
                    }
                    result = cropBitmap(
                        fullBitmap,
                        points2,
                        degreesRotated,
                        fixAspectRatio,
                        aspectRatioX,
                        aspectRatioY
                    )
                } finally {
                    if (result != fullBitmap) {
                        fullBitmap.recycle()
                    }
                }
            }
        } catch (e: OutOfMemoryError) {
            result?.recycle()
            throw e
        } catch (e: Exception) {
            throw RuntimeException(
                """
                Failed to load sampled bitmap: $loadedImageUri
                ${e.message}
                """.trimIndent(), e
            )
        }
        return BitmapSampled(result, sampleSize)
    }
    //    /**
    //     * Decode image from uri using "inJustDecodeBounds" to get the image dimensions.
    //     */
    //    private static BitmapFactory.Options decodeImageForOption(ContentResolver resolver, Uri uri) throws FileNotFoundException {
    //        InputStream stream = null;
    //        try {
    //            stream = resolver.openInputStream(uri);
    //            BitmapFactory.Options options = new BitmapFactory.Options();
    //            options.inJustDecodeBounds = true;
    //            BitmapFactory.decodeStream(stream, EMPTY_RECT, options);
    //            options.inJustDecodeBounds = false;
    //            return options;
    //        } finally {
    //            closeSafe(stream);
    //        }
    //    }
    /**
     * Decode image from uri using given "inSampleSize", but if failed due to out-of-memory then raise
     * the inSampleSize until success.
     */
    @Throws(FileNotFoundException::class)
    private fun decodeImage(
        resolver: ContentResolver,
        uri: Uri,
        options: BitmapFactory.Options
    ): Bitmap? {
        do {
            var stream: InputStream? = null
            try {
                stream = resolver.openInputStream(uri)
                return BitmapFactory.decodeStream(stream, emptyRect, options)
            } catch (e: OutOfMemoryError) {
                options.inSampleSize *= 2
            } finally {
                closeSafe(stream)
            }
        } while (options.inSampleSize <= 512)
        throw RuntimeException("Failed to decode image: $uri")
    }

    /**
     * Decode specific rectangle bitmap from stream using sampling to get bitmap with the requested limit.
     *
     * @param sampleMulti used to increase the sampling of the image to handle memory issues.
     */
    private fun decodeSampledBitmapRegion(
        context: Context,
        uri: Uri,
        rect: Rect,
        reqWidth: Int,
        reqHeight: Int,
        sampleMulti: Int
    ): BitmapSampled {
        var stream: InputStream? = null
        var decoder: BitmapRegionDecoder? = null
        try {
            logg("BitmapUtils, decodeSampledBitmapRegion, uri = $uri")
            logg("BitmapUtils, decodeSampledBitmapRegion, reqWidth = $reqWidth, reqHeight = $reqHeight")
            val options = BitmapFactory.Options()
            options.inSampleSize = sampleMulti * calculateInSampleSizeByReqestedSize(
                rect.width(),
                rect.height(),
                reqWidth,
                reqHeight
            )
            stream = context.contentResolver.openInputStream(uri)
            decoder = BitmapRegionDecoder.newInstance(stream, false)
            do {
                try {
                    return BitmapSampled(
                        decoder.decodeRegion(rect, options),
                        options.inSampleSize
                    )
                } catch (e: OutOfMemoryError) {
                    logg("BitmapUtils, decodeSampledBitmapRegion, OutOfMemoryError!!!")
                    options.inSampleSize *= 2
                }
            } while (options.inSampleSize <= 512)
        } catch (e: Exception) {
            throw RuntimeException(
                """
                Failed to load sampled bitmap: $uri
                ${e.message}
                """.trimIndent(), e
            )
        } finally {
            closeSafe(stream)
            decoder?.recycle()
        }
        return BitmapSampled(null, 1)
    }

    /**
     * Special crop of bitmap rotated by not stright angle, in this case the original crop bitmap contains parts
     * beyond the required crop area, this method crops the already cropped and rotated bitmap to the final
     * rectangle.<br></br>
     * Note: rotating by 0, 90, 180 or 270 degrees doesn't require extra cropping.
     */
    private fun cropForRotatedImage(
        bitmap: Bitmap, points: FloatArray, rect: Rect, degreesRotated: Int,
        fixAspectRatio: Boolean, aspectRatioX: Int, aspectRatioY: Int
    ): Bitmap {
        var nBitmap = bitmap
        if (degreesRotated % 90 != 0) {
            var adjLeft = 0
            var adjTop = 0
            var width = 0
            var height = 0
            val rads = Math.toRadians(degreesRotated.toDouble())
            val compareTo =
                if (degreesRotated < 90 || degreesRotated in 181 until 270) rect.left
                else rect.right
            var i = 0
            while (i < points.size) {
                if (points[i] >= compareTo - 1 && points[i] <= compareTo + 1) {
                    adjLeft = abs(sin(rads) * (rect.bottom - points[i + 1]))
                        .toInt()
                    adjTop = abs(cos(rads) * (points[i + 1] - rect.top))
                        .toInt()
                    width = abs((points[i + 1] - rect.top) / sin(rads))
                        .toInt()
                    height = abs((rect.bottom - points[i + 1]) / cos(rads))
                        .toInt()
                    break
                }
                i += 2
            }
            rect[adjLeft, adjTop, adjLeft + width] = adjTop + height
            if (fixAspectRatio) {
                fixRectForAspectRatio(rect, aspectRatioX, aspectRatioY)
            }
            val bitmapTmp = nBitmap
            nBitmap = Bitmap.createBitmap(nBitmap, rect.left, rect.top, rect.width(), rect.height())
            if (bitmapTmp != nBitmap) {
                bitmapTmp.recycle()
            }
        }
        return nBitmap
    }

    /**
     * Calculate the largest inSampleSize value that is a power of 2 and keeps both
     * height and width larger than the requested height and width.
     */
    private fun calculateInSampleSizeByReqestedSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            while (height / 2 / inSampleSize > reqHeight && width / 2 / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    //    /**
    //     * Calculate the largest inSampleSize value that is a power of 2 and keeps both
    //     * height and width smaller than max texture size allowed for the device.
    //     */
    //    private static int calculateInSampleSizeByMaxTextureSize(int width, int height) {
    //        int inSampleSize = 1;
    //        if (mMaxTextureSize == 0) {
    //            mMaxTextureSize = getMaxTextureSize();
    //        }
    //        if (mMaxTextureSize > 0) {
    //            while ((height / inSampleSize) > mMaxTextureSize || (width / inSampleSize) > mMaxTextureSize) {
    //                inSampleSize *= 2;
    //            }
    //        }
    //        return inSampleSize;
    //    }
    //    /**
    //     * Get {@link File} object for the given Android URI.<br>
    //     * Use content resolver to get real path if direct path doesn't return valid file.
    //     */
    //    private static File getFileFromUri(Context context, Uri uri) {
    //
    //        // first try by direct path
    //        File file = new File(uri.getPath());
    //        if (file.exists()) {
    //            return file;
    //        }
    //
    //        // try reading real path from content resolver (gallery images)
    //        Cursor cursor = null;
    //        try {
    //            String[] proj = {MediaStore.Images.Media.DATA};
    //            cursor = context.getContentResolver().query(uri, proj, null, null, null);
    //            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    //            cursor.moveToFirst();
    //            String realPath = cursor.getString(column_index);
    //            file = new File(realPath);
    //        } catch (Exception ignored) {
    //        } finally {
    //            if (cursor != null) {
    //                cursor.close();
    //            }
    //        }
    //
    //        return file;
    //    }
    /**
     * Rotate the given bitmap by the given degrees.<br></br>
     * New bitmap is created and the old one is recycled.
     */
    private fun rotateBitmapInt(bitmap: Bitmap, degrees: Int, isHorizontalRevers: Boolean = true): Bitmap {
        return if (degrees > 0) {
            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat())
            val newBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
            if (newBitmap != bitmap) {
                bitmap.recycle()
            }
//            if(isHorizontalRevers) {
//                val scaleMatrix = Matrix()
//                scaleMatrix.setScale(-1f, 1f)
//                val scaleBitmap =
//                    Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.width, newBitmap.height, scaleMatrix, false)
//                if (scaleBitmap != newBitmap) {
//                    newBitmap.recycle()
//                }
//                logg("여기 오지??????????????")
//                scaleBitmap
//            }else newBitmap
            newBitmap
        } else {
            bitmap
        }
    }
    //    /**
    //     * Get the max size of bitmap allowed to be rendered on the device.<br>
    //     * http://stackoverflow.com/questions/7428996/hw-accelerated-activity-how-to-get-opengl-texture-size-limit.
    //     */
    //    private static int getMaxTextureSize() {
    //        // Safe minimum default size
    //        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;
    //
    //        try {
    //            // Get EGL Display
    //            EGL10 egl = (EGL10) EGLContext.getEGL();
    //            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
    //
    //            // Initialise
    //            int[] version = new int[2];
    //            egl.eglInitialize(display, version);
    //
    //            // Query total number of configurations
    //            int[] totalConfigurations = new int[1];
    //            egl.eglGetConfigs(display, null, 0, totalConfigurations);
    //
    //            // Query actual list configurations
    //            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
    //            egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);
    //
    //            int[] textureSize = new int[1];
    //            int maximumTextureSize = 0;
    //
    //            // Iterate through all the configurations to located the maximum texture size
    //            for (int i = 0; i < totalConfigurations[0]; i++) {
    //                // Only need to check for width since opengl textures are always squared
    //                egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);
    //
    //                // Keep track of the maximum texture size
    //                if (maximumTextureSize < textureSize[0]) {
    //                    maximumTextureSize = textureSize[0];
    //                }
    //            }
    //
    //            // Release
    //            egl.eglTerminate(display);
    //
    //            // Return largest texture size found, or default
    //            return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
    //        } catch (Exception e) {
    //            return IMAGE_MAX_BITMAP_DIMENSION;
    //        }
    //    }
    /**
     * Close the given closeable object (Stream) in a safe way: check if it is null and catch-log
     * exception thrown.
     *
     * @param closeable the closable object to close
     */
    private fun closeSafe(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (ignored: IOException) {
            }
        }
    }
    //endregion
    //region: Inner class: BitmapSampled
    /**
     * Holds bitmap instance and the sample size that the bitmap was loaded/cropped with.
     */
    data class BitmapSampled(
        /**
         * The bitmap instance
         */
        val bitmap: Bitmap?,
        /**
         * The sample size used to lower the size of the bitmap (1,2,4,8,...)
         */
        val sampleSize: Int
    )
    //region: Inner class: RotateBitmapResult
    //    /**
    //     * The result of {@link #rotateBitmapByExif(android.graphics.Bitmap, android.media.ExifInterface)}.
    //     */
    //    public static final class RotateBitmapResult {
    //
    //        /**
    //         * The loaded bitmap
    //         */
    //        public final Bitmap bitmap;
    //
    //        /**
    //         * The degrees the image was rotated
    //         */
    //        public final int degrees;
    //
    //        RotateBitmapResult(Bitmap bitmap, int degrees) {
    //            this.bitmap = bitmap;
    //            this.degrees = degrees;
    //        }
    //    }
    //endregion
//}

enum class RequestSizeOptions {
    /**
     * No resize/sampling is done unless required for memory management (OOM).
     */
    NONE,

    /**
     * Only sample the image during loading (if image set using URI) so the smallest of the image
     * dimensions will be between the requested size and x2 requested size.<br></br>
     * NOTE: resulting image will not be exactly requested width/height
     * see: [Loading Large
 * Bitmaps Efficiently](http://developer.android.com/training/displaying-bitmaps/load-bitmap.html).
     */
    SAMPLING,

    /**
     * Resize the image uniformly (maintain the image's aspect ratio) so that both
     * dimensions (width and height) of the image will be equal to or **less** than the
     * corresponding requested dimension.<br></br>
     * If the image is smaller than the requested size it will NOT change.
     */
    RESIZE_INSIDE,

    /**
     * Resize the image uniformly (maintain the image's aspect ratio) to fit in the given width/height.<br></br>
     * The largest dimension will be equals to the requested and the second dimension will be smaller.<br></br>
     * If the image is smaller than the requested size it will enlarge it.
     */
    RESIZE_FIT,

    /**
     * Resize the image to fit exactly in the given width/height.<br></br>
     * This resize method does NOT preserve aspect ratio.<br></br>
     * If the image is smaller than the requested size it will enlarge it.
     */
    RESIZE_EXACT
}



//todo 1215 image rotate exif 만들었으니 이거 사용해서 돌리자
enum class ImageExif(val rotate: Int, val isHorizontalRevers: Boolean = false) {
    TOP_LEFT(0 ), // = 1,      // Default
    TOP_RIGHT(0, true), // = 2,  //flip horizontal   // Reflected across y-axis
    BOTTOM_RIGHT(180), // = 3,  // Rotated 180
    BOTTOM_LEFT(180, true), // = 4, // 180 ->  flip horizontal  // Reflected across x-axis
    LEFT_TOP(90, true), // = 5,  // 90 ->  flip horizontal    // Reflected across x-axis, Rotated 90 CCW
    RIGHT_TOP(270), // = 6,     // Rotated 270
    RIGHT_BOTTOM(270, true), // = 7, // 270 -> flip horizontal // Reflected across x-axis, Rotated 90 CW
    LEFT_BOTTOM(90), //  = 8   // Rotated 90 CCW
}


fun ImageExif.flipHorizontal(): ImageExif {
    return when (this) {
        ImageExif.TOP_LEFT  ->  ImageExif.TOP_RIGHT
        ImageExif.TOP_RIGHT ->   ImageExif.TOP_LEFT
        ImageExif.BOTTOM_RIGHT  ->  ImageExif.BOTTOM_LEFT
        ImageExif.BOTTOM_LEFT   ->  ImageExif.BOTTOM_RIGHT
        ImageExif.LEFT_TOP  ->  ImageExif.LEFT_BOTTOM
        ImageExif.RIGHT_TOP ->  ImageExif.RIGHT_BOTTOM
        ImageExif.RIGHT_BOTTOM  ->  ImageExif.RIGHT_TOP
        ImageExif.LEFT_BOTTOM   ->  ImageExif.LEFT_TOP
    }
}

fun  ImageExif.rotateRight(): ImageExif {
    return when (this) {
        ImageExif.TOP_LEFT -> ImageExif.LEFT_BOTTOM
        ImageExif.TOP_RIGHT -> ImageExif.RIGHT_BOTTOM //flip horizontal
        ImageExif.BOTTOM_RIGHT -> ImageExif.RIGHT_TOP           //  180
        ImageExif.BOTTOM_LEFT -> ImageExif.LEFT_TOP         // 180 ->  flip horizontal
        ImageExif.LEFT_TOP -> ImageExif.TOP_RIGHT            // 90 ->  flip horizontal
        ImageExif.RIGHT_TOP -> ImageExif.TOP_LEFT              // 270
        ImageExif.RIGHT_BOTTOM -> ImageExif.BOTTOM_LEFT        // 270 -> flip horizontal
        ImageExif.LEFT_BOTTOM -> ImageExif.BOTTOM_RIGHT            // 90
    }
}