package com.example.videocropandtrim.ui.detail

import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.videocropandtrim.R
import com.example.videocropandtrim.base.BaseRecyclerViewAdapter
import com.example.videocropandtrim.databinding.ItemVideoCropAndTrimVideoFrameBinding
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.logg
import kotlin.math.pow

class VideoCTDetailRvAdapter(
    vm: VideoCropAndTrimViewModel? = null,
    items: List<Pair<String, Long>> = emptyList(),
    val glide: RequestManager
): BaseRecyclerViewAdapter<
        Pair<String, Long>,
        VideoCropAndTrimViewModel,
        ItemVideoCropAndTrimVideoFrameBinding>(
    R.layout.item_video_crop_and_trim_video_frame,
    items = items,
    vm = vm
){
    override fun getItemIdR(position: Int): Long {
        logg("getItemIdR pos: $position      items?.get(position)?.second: ${items?.get(position)?.second}")
        return items?.get(position)?.second ?: -1
    }

    private val requestOptions =  RequestOptions().format(DecodeFormat.PREFER_RGB_565)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    override fun bindItem(
        bindingView: ItemVideoCropAndTrimVideoFrameBinding,
        position: Int,
        item: Pair<String, Long>,
        viewType: Int,
        payloads: MutableList<Any>?
    ) {

        glide
            .load(item.first)
            .apply(
                requestOptions.override(
                    0,
                    0).frame(((item.second / 1500) * 15 * 10f.pow(5)).toLong())
            ).into(bindingView.ivVideoFrameThumbnail)


//        glide
//            .load(item)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(binding.ivVideoFrameThumbnail)
    }
}
