package com.ram.delivery.view.main.home

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.diff.BrvahAsyncDifferConfig
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.ram.delivery.R
import com.ram.delivery.databinding.ItemImageViewBinding
import com.ram.delivery.model.api.res.ResBanner

class HomeBannerVpAdapter(
    val glide: RequestManager
) : BaseQuickAdapter<ResBanner, BaseDataBindingHolder<ItemImageViewBinding>>(R.layout.item_image_view){

    init {
        setDiffConfig(BrvahAsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<ResBanner>() {
            override fun areItemsTheSame(oldItem: ResBanner, newItem: ResBanner): Boolean = oldItem.bannNo == newItem.bannNo
            override fun areContentsTheSame(oldItem: ResBanner, newItem: ResBanner): Boolean = (oldItem.viewNum == newItem.viewNum)
        }).build())
    }

    override fun convert(holder: BaseDataBindingHolder<ItemImageViewBinding>, item: ResBanner) {
        holder.dataBinding?.ivItem?.apply {
            glide
                .load(item.sbannImgPath)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this)
        }
    }

}