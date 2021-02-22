package com.ram.delivery.utils

import android.app.Activity
import android.os.Build
import android.widget.Toast
import com.ram.delivery.R

class BackPressCloseHandler(context : Activity) {
    private var activity : Activity = context
    private var backKeyPressedTime : Long = 0
    private var toast : Toast? = null

    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.moveTaskToBack(true);
            //activity.finish();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                activity.finishAndRemoveTask();
            } else {
                activity.finish();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            toast?.cancel();
        }
    }

    private fun showGuide() {
        toast = Toast.makeText(
            activity, activity.getString(R.string.toast_back_button_exit),
            Toast.LENGTH_SHORT
        )
        toast?.show()
    }
}