package com.example.videocropandtrim.ui.detail

import android.graphics.Bitmap
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.videocropandtrim.R
import com.example.videocropandtrim.base.BaseRecyclerViewAdapter
import com.example.videocropandtrim.databinding.ItemVideoCropAndTrimVideoFrameBinding
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.getDeviceWidth
import com.example.videocropandtrim.utils.getTimelineOneTakeWidth
import com.example.videocropandtrim.utils.logg

class VideoCTDetailRvAdapter(
    vm: VideoCropAndTrimViewModel? = null,
    items: List<Bitmap> = emptyList(),
    val glide: RequestManager
): BaseRecyclerViewAdapter<
        Bitmap,
        VideoCropAndTrimViewModel,
        ItemVideoCropAndTrimVideoFrameBinding>(
    R.layout.item_video_crop_and_trim_video_frame,
    items = items,
    vm = vm
){

    fun addItems(addItems: List<Bitmap>){
        val oldItemsSize = items?.let {
            it.size - 1
        } ?: 0
        items = addItems
//        logg("oldItemsSize: ${oldItemsSize}")
//        logg("addItems: ${addItems.size}")
//        logg(" items: ${items?.size}")
        notifyItemInserted(addItems.size - 1)
//        notifyItemRangeChanged(oldItemsSize, addItems.size - 1)
    }


    override fun onBindCreateViewHolder(
        binding: ItemVideoCropAndTrimVideoFrameBinding,
        parent: ViewGroup,
        viewType: Int
    ) {
        val layoutParams = binding.ivVideoFrameThumbnail.layoutParams
        logg("onBindCreateViewHolder parent.context.getDeviceWidth(): ${parent.context.getDeviceWidth()}")
        logg("onBindCreateViewHolder parent.context.getDeviceWidth(): ${parent.height}")
        logg("onBindCreateViewHolder parent.context.getDeviceWidth(): ${parent.measuredHeight}")
        layoutParams.width = parent.context.getTimelineOneTakeWidth()
//        layoutParams.width = (parent.context.getDeviceWidth() - parent.context.resources.getDimensionPixelSize(R.dimen.default_timeline_padding) * 2) / 10
        layoutParams.height = parent.measuredHeight - parent.context.resources.getDimensionPixelSize(R.dimen.timeline_time_text_area)
        logg("calcScrollXDistance parent.context.getTimelineOneTakeWidth`(): ${parent.context.getTimelineOneTakeWidth()}")
        binding.ivVideoFrameThumbnail.layoutParams = layoutParams
    }

    override fun bindItem(
        bindingView: ItemVideoCropAndTrimVideoFrameBinding,
        position: Int,
        item: Bitmap,
        viewType: Int,
        payloads: MutableList<Any>?
    ) {

        glide
            .load(item)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivVideoFrameThumbnail)
    }
}
