package com.ram.delivery.utils.extension

import android.content.Context
import android.location.LocationManager
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.ram.delivery.R
import org.jetbrains.anko.layoutInflater

fun Context.showToast(msg : String, view: Int = R.layout.custom_toast) : Toast {
    val toastView = layoutInflater.inflate(view, null)
    val textView = toastView.findViewById<TextView>(R.id.txtCustomToastMsg)
    textView.text = msg

    return Toast(this).apply {
        setGravity(Gravity.CENTER, 0, 1)
        duration = Toast.LENGTH_SHORT
        setView(toastView)
        show()
    }
}

fun Context.showToast(msg : Int, view: Int = R.layout.custom_toast) = showToast(getString(msg), view)

fun Context.isGPS(): Boolean =
    (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        .isProviderEnabled(LocationManager.GPS_PROVIDER)