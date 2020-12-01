package com.example.videocropandtrim.utils

import android.util.Log
import com.arthenica.mobileffmpeg.ExecuteCallback

fun logg(msg: String){
    Log.d("alskaejr", msg)
}

fun Float.fastRound(): Int = if(this > 0) (this + 0.5).toInt() else (this - 0.5).toInt()


//private fun executeBinary() {
//    logg("TrimmerActivity, executeBinary()")
//    showProgressbar()
//    val commend: Array<String> = mFFmpegWorkList.get(0).split(" ".toRegex()).toTypedArray()
//    for (cmd in commend) {
//        logg("TrimmerActivity, executeBinary, cmd = $cmd")
//    }
//    try {
//        FFmpeg.executeAsync(commend, label@ ExecuteCallback { executionId1, returnCode ->
//            if (returnCode === RETURN_CODE_SUCCESS) {
//                Log.i(Config.TAG, "Async command execution completed successfully.")
//                mIsCommendSuccess = true
//            } else {
//                Log.i(
//                    Config.TAG,
//                    java.lang.String.format(
//                        "Async command execution failed with rc=%d.",
//                        returnCode
//                    )
//                )
//                mIsCommendSuccess = false
//            }
//            if (mFFmpegWorkList.get(0) == check_video_info_commend) {
//                if (mNoAudio) {
//                    logg("TrimmerActivity, executeBinary, onFinish(), info check, 오디오 스트림이 없는 동영상파일로 간주~!")
//                    maekCommendForNOAudio()
//                } else {
//                    logg("TrimmerActivity, executeBinary, onFinish(), info check, 오디오 스트림이 있는 동영상파일로 간주~!")
//                    maekCommendForNormal(false)
//                }
//                executeBinary()
//            } else {
//                if (mIsCommendSuccess) {
//                    var is_thumb = false
//                    logg(
//                        "TrimmerActivity, executeBinary, onFinish(), mFFmpegWorkList.get(0) = " + mFFmpegWorkList.get(
//                            0
//                        )
//                    )
//                    if (mFFmpegWorkList.get(0).contains("-an -vframes 1")) {
//                        is_thumb = true
//                    }
//                    mFFmpegWorkList.removeAt(0)
//                    val work_list_size: Int = mFFmpegWorkList.size
//                    logg("TrimmerActivity, executeBinary, onFinish(), work_list_size = $work_list_size")
//                    if (work_list_size > 0) executeBinary() else {
//                        //commend의 마지막은 썸네일 따기로 정함!
//                        logg("TrimmerActivity, executeBinary, onFinish(), mThumbnailProcess = $mThumbnailProcess, is_thumb = $is_thumb")
//                        if (!mThumbnailProcess && is_thumb) {
//                            mThumbnailProcess = true
//                            getThumbnail()
//                        }
//                        dismissProgressbar()
//                    }
//                } else {
//                    failProcessDeleteFiles()
//                    if (mVideoPath != null) {
//                        if (mVideoPath.contains(" ") ||
//                            mVideoPath.contains(">") ||
//                            mVideoPath.contains(":") ||
//                            mVideoPath.contains("\"") ||
//                            mVideoPath.contains("<") ||
//                            mVideoPath.contains("*") ||
//                            mVideoPath.contains("?") ||
//                            mVideoPath.contains("\\") ||
//                            mVideoPath.contains("|")
//                        ) {
//                            showSimpleDialogForFinish(getString(R.string.letter_video_editor_activity_string09))
//                            return@label
//                        }
//                    }
//                    if (mOutputVideoFilePath_Temp02 != null) {
//                        if (mOutputVideoFilePath_Temp02.contains(" ") ||
//                            mOutputVideoFilePath_Temp02.contains(">") ||
//                            mOutputVideoFilePath_Temp02.contains(":") ||
//                            mOutputVideoFilePath_Temp02.contains("\"") ||
//                            mOutputVideoFilePath_Temp02.contains("<") ||
//                            mOutputVideoFilePath_Temp02.contains("*") ||
//                            mOutputVideoFilePath_Temp02.contains("?") ||
//                            mOutputVideoFilePath_Temp02.contains("\\") ||
//                            mOutputVideoFilePath_Temp02.contains("|")
//                        ) {
//                            showSimpleDialogForFinish(getString(R.string.letter_video_editor_activity_string09))
//                            return@label
//                        }
//                    }
//                    showSimpleDialogForFinish(getString(R.string.letter_video_editor_activity_string08))
//                }
//            }
//        } as ExecuteCallback?)
//    } catch (e: Exception) {
//        logg("TrimmerActivity, executeBinary, FFmpegCommandAlreadyRunningException, e.toString() = $e")
//    }
//}