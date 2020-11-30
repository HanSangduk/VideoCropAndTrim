package com.example.videocropandtrim.ui.detail

import android.graphics.Bitmap
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videocropandtrim.model.data.MediaFile
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.logg

@BindingAdapter(value = ["bind:set_ct_detail_rv_adapter_vm", "bind:set_ct_detail_rv_adapter"])
fun bindCTDetailRvAdapter(recyclerView: RecyclerView, vm: VideoCropAndTrimViewModel?, videoFrames: List<Bitmap>?){
//    logg("bindCTDetailRvAdapter mediaFiles size: ${videoFrames?.size}")
//    logg("bindCTDetailRvAdapter mediaFiles vm: ${vm}")
    videoFrames?.let { lVideoFrames ->
        with(recyclerView){
            adapter?.let {
                if(it is VideoCTDetailRvAdapter){
                    it.addItems(lVideoFrames)
                }
            } ?: run {
                val lim = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = lim
                recyclerView.adapter = VideoCTDetailRvAdapter(
                    vm = vm,
                    lVideoFrames,
                    Glide.with(context)
                ).apply {
                    setHasStableIds(true)
                    recyclerView.setHasFixedSize(true)
                }
            }
        }
    }
}
