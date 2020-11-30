package com.example.videocropandtrim.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

const val MEDIA_TYPE_IMAGE : Int = 0
const val MEDIA_TYPE_VIDEO : Int = 1
const val MEDIA_TYPE_AUDIO : Int = 2
const val MEDIA_EMPTY : Int = 3
const val MEDIA_HEADER : Int = 4

@Parcelize
data class MediaFile(
        val _id: Int? = null,
        val fileName: String? = null,
        val filePath: String? = null,
        val dataURI: String? = null,
        val webDataURI: String? = null,
        val sourceType: Int? = null,
        val fileFolderName: String? = null,
        val duration: Long = 4500L,
        val repeatDuration: Long? = null,
        val width: Int? = null,
        val height: Int? = null,
        val artist: String? = null,
        val title: String? = null,
        val audioThumb: String? = null,
        val isPlay: Boolean = false,
        val dateModified : Long? = null,
        val orientation : Int? = null,
        val legacyThumbnailUri : String? = null,
         @Transient val createTime: DateTime? = null
) : Parcelable
