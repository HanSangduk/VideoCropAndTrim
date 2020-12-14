package com.example.videocropandtrim.ui.main

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.utils.convertSecondsToTime
import com.example.videocropandtrim.utils.logg
import java.util.concurrent.TimeUnit

@BindingAdapter(value = ["bind:set_crop_and_trim_rv_adapter_vm", "bind:set_crop_and_trim_rv_adapter"])
fun bindCropAndTrimRvAdapter(recyclerView: RecyclerView, vm: VideoCropAndTrimViewModel?, mediaFiles: List<MediaFile>?){
    logg("bindCropAndTrimRvAdapter mediaFiles size: ${mediaFiles?.size}")
    logg("bindCropAndTrimRvAdapter mediaFiles vm: ${vm}")
    mediaFiles?.let { lMediaFiles ->
        with(recyclerView){
            adapter?.let {
                if(it is VideoCropAndTrimRvAdapter){
                    it.updateDiffItems(lMediaFiles)
                }
            } ?: run {
                val glm = GridLayoutManager(context, 3 )
                recyclerView.layoutManager = glm
                recyclerView.adapter = VideoCropAndTrimRvAdapter(
                    vm = vm,
                    lMediaFiles,
                    Glide.with(context)
                ).apply {
                    setHasStableIds(true)
                    recyclerView.setHasFixedSize(true)
                }
            }
        }
    }
}

@BindingAdapter(value = ["bind:video_time"])
fun bindVideoTime(tv: TextView, mediaFile: MediaFile?){
    mediaFile?.let {
        if(it.mimeType?.contains("video/") == true){
            tv.text = it.duration.convertSecondsToTime(TimeUnit.MICROSECONDS)
        }else
            tv.visibility = View.GONE
    }
}
