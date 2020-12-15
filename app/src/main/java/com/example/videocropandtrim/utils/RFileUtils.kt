package com.example.videocropandtrim.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File

const val FFMPEG_FILE_FOLDER_PATH = "/ffmpeg/"
const val IMAGE_CROP_FILE_FOLDER_PATH = "/imageCrop/"

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
                mediaCursor.getString(
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

//fun getFileFolderPath(ctx: Context, folderName: String = FFMPEG_FILE_FOLDER_PATH) = "${ctx.filesDir}/$folderName"
fun getFileFolderPath(ctx: Context, folderName: String = FFMPEG_FILE_FOLDER_PATH): String {
    val dirPath = "${ctx.filesDir}/$folderName"
    File(dirPath).apply {
        if(!isDirectory) mkdir()
    }
    return dirPath
}

fun getOutputUri(context: Context, folderName: String =IMAGE_CROP_FILE_FOLDER_PATH): Uri {
    return Uri.fromFile(
        File.createTempFile(
            "cropped_image_${System.currentTimeMillis()}",
            ".jpg",
            File(getFileFolderPath(context, folderName))
        )
    )
}