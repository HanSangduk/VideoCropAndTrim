package com.example.videocropandtrim.utils

import android.content.Context
import android.util.Log
import com.arthenica.mobileffmpeg.ExecuteCallback
import com.example.videocropandtrim.R

fun Context.getTimelineOneTakeWidth() = (getDeviceWidth() - resources.getDimensionPixelSize(R.dimen.default_timeline_padding) * 2) / 10
