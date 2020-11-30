package com.example.videocropandtrim.model

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.example.videocropandtrim.model.data.MEDIA_TYPE_IMAGE
import com.example.videocropandtrim.model.data.MEDIA_TYPE_VIDEO
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.utils.rx.RxCursorIterable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import java.io.File


class MediaRepository(val context: Context){

    private var bothVideoAndImageProjection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DURATION,
        MediaStore.Files.FileColumns.WIDTH,
        MediaStore.Files.FileColumns.HEIGHT,
        "_data",
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Images.Thumbnails.DATA,
        MediaStore.Video.Thumbnails.DATA,
        MediaStore.Images.Thumbnails.WIDTH,
        MediaStore.Video.Thumbnails.WIDTH
    )

    fun bothVideosAndImages(sortOrder: String? = null) : Observable<MediaFile> {
        val mSortOrder = sortOrder ?: "${MediaStore.Files.FileColumns._ID} DESC"
        val selection =
            (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        val contentResolver = context.contentResolver
        val mediaCursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            bothVideoAndImageProjection,
            selection,
            null,
            mSortOrder
        )

        return if (mediaCursor != null) {
            Observable.fromIterable(RxCursorIterable.from(mediaCursor))
        } else {
            Observable.empty<Cursor>()
        }
            .subscribeOn(Schedulers.io())
            .flatMap {
                val mimeType = it.getStringOrNull(
                    it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                ) ?: "image/__unknown__"
                if(mimeType.contains("svg") || mimeType.contains("webp") ||
                    mimeType.contains("x-adobe-dng") ||
                    mimeType.contains("heif") ||
                    mimeType.contains("vnd.wap.wbmp") ||
                    mimeType.contains("heic")) return@flatMap Observable.never<MediaFile>()

                val filedId =
                    it.getInt(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)) // 46
                val displayName =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)) // ml_4062_2018_7_30_9-49-261923276655.jpg
                val createTime =
                    DateTime(it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)) * 1000) //2018-07-30T09:49:27.000+09:00

                val filePath =
                    it.getString(it.getColumnIndexOrThrow("_data")) ///storage/emulated/0/DCIM/ml_4062_2018_7_30_9-49-261923276655.jpg
                val fileFolderName = File(filePath).parent
                val width = it.getInt(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)) // 46
                val height = it.getInt(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)) // 46
                val sourceType = if(mimeType.contains("image/")) MEDIA_TYPE_IMAGE else MEDIA_TYPE_VIDEO
                val duration = if(sourceType == MEDIA_TYPE_IMAGE) 4500L
                else it.getLong(it.getColumnIndex(MediaStore.Files.FileColumns.DURATION)) ?: 0L ///storage/emulated/0/DCIM/ml_4062_2018_7_30_9-49-261923276655.jpg
                //content://media/external/images/thumbnails/
                val thumbUri = Uri.withAppendedPath(
                    if (sourceType == MEDIA_TYPE_VIDEO) MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    else MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    filedId.toString()
                )
                val legacyThumbnailUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    val legacyThumbnailColumName =  if(sourceType == MEDIA_TYPE_VIDEO) {
                        MediaStore.Video.Thumbnails.DATA
                    }
                    else {
                        MediaStore.Images.Thumbnails.DATA
                    }
                    it.getString(it.getColumnIndexOrThrow(legacyThumbnailColumName))
                }
                else
                    null


                Observable.just(
                    MediaFile(
                        _id = filedId,
                        filePath = filePath,
                        fileName = displayName,
                        dataURI = thumbUri.toString(),
                        sourceType = sourceType,
                        fileFolderName = fileFolderName,
                        duration = duration * 1000L,
                        width = width,
                        height = height,
                        createTime = createTime,
                        legacyThumbnailUri = legacyThumbnailUri
                    )
                )
            }
    }

    private val vidoeProjection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.DATE_MODIFIED,
        "_data",
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.WIDTH,
        MediaStore.Video.Media.HEIGHT,
        MediaStore.Video.Media.SIZE
    )

    fun videos() : Observable<MediaFile> {
        val contentResolver = context.contentResolver
        val videoCursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            vidoeProjection,
            null,
            null,
            MediaStore.Video.Media.DATE_ADDED + " DESC"
        )
        return if(videoCursor != null) {
            Observable.fromIterable(RxCursorIterable.from(videoCursor))
        }
        else {
            Observable.empty()
        }
            .subscribeOn(Schedulers.io())
            .map {
                val filedId =
                    it.getInt(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)) // 46
                val displayName =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)) // ml_4062_2018_7_30_9-49-261923276655.jpg
                val createTime = DateTime(it.getLong(it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)) * 1000) //2018-07-30T09:49:27.000+09:00
                val filePath =
                    it.getString(it.getColumnIndexOrThrow("_data")) ///storage/emulated/0/DCIM/ml_4062_2018_7_30_9-49-261923276655.jpg
                val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)) * 1000L
                val fileFolderName = File(filePath).parent
                val width = it.getInt(it.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
                val height = it.getInt(it.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))
                val size = it.getInt(it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                //content://media/external/images/thumbnails/
                val thumbUri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    filedId.toString()
                )

                MediaFile(
                    _id = filedId,
                    filePath = filePath,
                    fileName = displayName,
                    dataURI = thumbUri.toString(),
                    sourceType = MEDIA_TYPE_VIDEO,
                    fileFolderName = fileFolderName,
                    duration = duration,
                    width = width,
                    height = height,
                    createTime = createTime
                )
            }
            .filter {
                it.duration > 0
            }
    }

    private val imgeProjection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_MODIFIED,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT,
        "_data"
    )

    fun images(folderName: String = "") : Observable<MediaFile> {
        val contentResolver = context.contentResolver
        val imageCursor =  contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imgeProjection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        return  if(imageCursor != null) {
            Observable.fromIterable(RxCursorIterable.from(imageCursor))
        } else {
            Observable.empty<Cursor>()
        }
            .subscribeOn(Schedulers.io())
            .map {
                val filedId = it.getInt(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)) // 46
                val displayName = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)) // ml_4062_2018_7_30_9-49-261923276655.jpg
                val createTime = DateTime(it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)) * 1000) //2018-07-30T09:49:27.000+09:00
                val filePath  = it.getString(it.getColumnIndexOrThrow("_data")) ///storage/emulated/0/DCIM/ml_4062_2018_7_30_9-49-261923276655.jpg
                val fileFolderName = File(filePath).parent
                val width = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)) // 46
                val height = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)) // 46

                //content://media/external/images/media/46
                val imgUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    filedId.toString()
                )
                MediaFile(
                    _id = filedId,
                    filePath = filePath,
                    dataURI = imgUri.toString(),
                    fileName = displayName,
                    sourceType = MEDIA_TYPE_IMAGE,
                    fileFolderName = fileFolderName,
                    duration = 4500L * 1000L,
                    width = width,
                    height = height,
                    createTime = createTime
                )
            }
            .filter {
                !(folderName != "" && it.fileFolderName != folderName) && !(it.fileName?.contains(
                    ".svg",
                    ignoreCase = true
                ) ?: true)
            }
    }

//    private val audioProjection =
//        arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.ALBUM,
//            MediaStore.Audio.Media.TITLE,
//            "_data",
//            MediaStore.Audio.Media.DURATION,
//            MediaStore.Audio.Media.MIME_TYPE,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.DATE_ADDED,
//            MediaStore.Audio.AlbumColumns.ALBUM_ID
//        )
//
//    fun audios(folderName: String = "") : Flowable<AudioFile> {
//        val contentResolver = context.contentResolver
//        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"
//        val audioCursor = contentResolver.query(
//            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            audioProjection,
//            null,
//            null,
//            sortOrder
//        )
//
//        return if(audioCursor != null) {
//            Flowable.fromIterable(RxCursorIterable.from(audioCursor))
//        } else {
//            Flowable.empty<Cursor>()
//        }.subscribeOn(Schedulers.io())
//            .map {
//                val filedId = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)) // 46
//                val createTime = DateTime(it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)) * 1000) //2018-07-30T09:49:27.000+09:00
//                val filePath = it.getString(it.getColumnIndexOrThrow("_data")) ///storage/emulated/0/DCIM/ml_4062_2018_7_30_9-49-261923276655.jpg
//                val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
//                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))    //bensound-anewbeginning
//                val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))    //<unknown>
//
//                val album = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))    //bensound
//                val mimeType = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE))    // audio/mpeg , audio/aac, audio/x-wav, audio/ogg, audio/flac
//
//                val albumId = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.AlbumColumns.ALBUM_ID))    // audio/mpeg , audio/aac, audio/x-wav, audio/ogg, audio/flac
////                val albumART = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.AlbumColumns.ALBUM_ART))    // audio/mpeg , audio/aac, audio/x-wav, audio/ogg, audio/flac
//
//                val fileFolderName = File(filePath).parent
//
//                //content://media/external/images/media/46
//                val dataUri = Uri.withAppendedPath(
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    filedId.toString()
//                )
//
//                val audioThumb = Uri.withAppendedPath(
//                    Uri.parse("content://media/external/audio/albumart"),
//                    albumId.toString()
//                )
//
//                AudioFile(
//                    mediaFile = MediaFile(
//                        _id = filedId,
//                        filePath = filePath,
//                        dataURI = dataUri.toString(),
//                        sourceType = MEDIA_TYPE_AUDIO,
//                        fileFolderName = fileFolderName,
//                        duration = duration,
//                        artist = artist,
//                        title = title,
//                        audioThumb = audioThumb.toString(),
//                        createTime = createTime
//                    )
//                )
//            }
//    }
}