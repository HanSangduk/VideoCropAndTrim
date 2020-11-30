package com.example.videocropandtrim.utils

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File

const val FFMPEG_FILE_FOLDER_PATH = "/ffmpeg/"

@RequiresApi(Build.VERSION_CODES.Q)
fun getFileToFileId(ctx: Context?, fileId: String?): File?{
    val mediaCursor = ctx?.contentResolver?.query(
        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
        arrayOf("_data"),
        "${MediaStore.Files.FileColumns._ID}=$fileId",
        null,
        null
    )
    return if(mediaCursor?.moveToFirst() == true){
        logg(
            "mediaCursor?.getString(mediaCursor.getColumnIndexOrThrow(\"_data\")): ${
                mediaCursor?.getString(
                    mediaCursor.getColumnIndexOrThrow(
                        "_data"
                    )
                )
            }"
        )
        val mediaFilePath = mediaCursor.getString(mediaCursor.getColumnIndexOrThrow("_data"))
        File(mediaFilePath)
    }else
        null
}

fun getFileFolderPath(ctx: Context, folderName: String = FFMPEG_FILE_FOLDER_PATH) = "${ctx.filesDir}/$folderName"