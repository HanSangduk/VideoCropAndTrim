package com.ram.delivery.utils

import android.content.Context
import android.graphics.Point
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.Size
import android.view.WindowManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.DecimalFormat

class SystemUtil {
    companion object {
        fun isConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            var result = false
            if (activeNetwork != null) {
                result = activeNetwork.isConnectedOrConnecting
            }
            return result
        }

        fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
            val file = File(fileUri.path)
            val requestFile: RequestBody = RequestBody.create(
                "multipart/form-data".toMediaType(),
                file
            )
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }

        fun prepareFilePart(partName: String, filesPath: ArrayList<String>): List<MultipartBody.Part> {
            val filesToUpload = mutableListOf<MultipartBody.Part>()
            for(i in filesPath.indices) {
                var fieldName = "fieId" + (i+1)
                filesToUpload.add( prepareFilePart(fieldName, Uri.parse(filesPath[i])) )
            }
            return filesToUpload
        }

        fun chkAuthTime(sndAuthTime: Long) : Boolean {
            if(sndAuthTime == 0L) return true
            val diff = (System.currentTimeMillis() - sndAuthTime) / 1000
            if(diff > 59) return true
            return false
        }

        fun checkLocationServicesStatus(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            return (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        }

        fun dpToPx(context:Context, dp: Float): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi / 160f)
        }

        fun pxToDp(context:Context, px: Float): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return px / (metrics.densityDpi / 160f)
        }

        fun getWindowSize(context:Context): Size {
            val size = Point()
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(size)
            return Size(size.x, size.y)
        }

        fun getStatusBarSize(context:Context): Int {
            var statusBarHeight = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            return statusBarHeight
        }
    }
}