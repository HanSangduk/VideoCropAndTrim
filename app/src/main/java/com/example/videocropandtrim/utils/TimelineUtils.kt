package com.example.videocropandtrim.utils

import android.content.Context
import com.example.videocropandtrim.R

fun Context.getTimelineOneTakeWidth(visibleCnt: Int = 10) = (getDeviceWidth() - resources.getDimensionPixelSize(R.dimen.default_timeline_padding) * 2) / visibleCnt

fun Context.getTimelineWidth() = (getDeviceWidth() - resources.getDimensionPixelSize(R.dimen.default_timeline_padding) * 2)


fun Context.getOneSceneDuration(videoDuration: Long, templateNeedDuration: Long): Long{
    val oneFrameWidth = resources.getDimensionPixelSize(R.dimen.default_video_thumb_width)
    val cnt = getTimelineWidth()/oneFrameWidth
    val cntFloat = getTimelineWidth() / oneFrameWidth.toFloat()
    val needPage = videoDuration / templateNeedDuration
    logg("getVideoFrame mediaFile.mediaDurationMilliSec: $videoDuration")
    logg("getVideoFrame one frame width: $oneFrameWidth")
    logg("getVideoFrame recyclerview width: ${getTimelineWidth()}")
    logg("getVideoFrame need frame cnt: $cnt")
    logg("getVideoFrame need frame cntFloat: $cntFloat")
    logg("getVideoFrame need frame needPage: $needPage")
    logg("getVideoFrame need totalCnt: ${needPage * cnt}")
    logg("getVideoFrame need totalCntFloat: ${needPage * cntFloat}")

    val totalCnt = (needPage * cntFloat).toInt()

    logg("getVideoFrame need interval: ${videoDuration / totalCnt}")
    logg("getVideoFrame need int interval: ${videoDuration / totalCnt.toInt()}")
    return videoDuration / totalCnt
}