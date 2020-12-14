package com.example.videocropandtrim.ui.detail.video

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videocropandtrim.R
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.logg
import com.example.videocropandtrim.utils.widget.DefaultRvItemDecoration

@BindingAdapter(value = ["bind:set_ct_detail_rv_adapter_vm", "bind:set_ct_detail_rv_adapter"])
fun bindCTDetailRvAdapter(recyclerView: RecyclerView, vm: VideoCropAndTrimViewModel?, videoFrames: List<Pair<String, Long>>?){
    logg("bindingView bindCTDetailRvAdapter videoFrames: $videoFrames")
    videoFrames?.let { lVideoFrames ->
        with(recyclerView){
            adapter?.let {
//                if(it is VideoCTDetailRvAdapter){
//                    it.addItems(lVideoFrames)
//                }
            } ?: run {
                logg("bindingView bindCTDetailRvAdapter videoFrames: ${videoFrames.size}")
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
                recyclerView.addItemDecoration(
                    DefaultRvItemDecoration(
                        resources.getDimensionPixelSize(R.dimen.default_timeline_padding),
                    )
                )
            }
        }
    }
}
//
//@BindingAdapter(value = ["bind:set_ct_detail_rv_adapter_vm", "bind:set_ct_detail_rv_adapter"])
//fun bindCTDetailRvAdapter(recyclerView: RecyclerView, vm: VideoCropAndTrimViewModel?, videoFrames: List<Bitmap>?){
//    videoFrames?.let { lVideoFrames ->
//        with(recyclerView){
//            adapter?.let {
//                if(it is VideoCTDetailRvAdapter){
//                    it.addItems(lVideoFrames)
//                }
//            } ?: run {
//                val lim = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//
//                recyclerView.layoutManager = lim
//                recyclerView.adapter = VideoCTDetailRvAdapter(
//                    vm = vm,
//                    lVideoFrames,
//                    Glide.with(context)
//                ).apply {
//                    setHasStableIds(true)
//                    recyclerView.setHasFixedSize(true)
//                }
//                recyclerView.addItemDecoration(
//                    DefaultRvItemDecoration(
//                        resources.getDimensionPixelSize(R.dimen.default_timeline_padding),
//                    )
//                )
//            }
//        }
//    }
//}
