package com.example.videocropandtrim.ui.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.videocropandtrim.R
import com.example.videocropandtrim.base.BaseRecyclerViewAdapter
import com.example.videocropandtrim.base.ViewBindingHolder
import com.example.videocropandtrim.base.ViewBindingHolderImpl
import com.example.videocropandtrim.databinding.FragmentVideoCropTrimDetailBinding
import com.example.videocropandtrim.databinding.ItemVideoCropAndTrimVideoFrameBinding
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import com.example.videocropandtrim.utils.logg
import kotlin.math.sign

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
