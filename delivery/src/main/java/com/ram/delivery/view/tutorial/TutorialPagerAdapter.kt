package com.ram.delivery.view.tutorial

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.diff.BrvahAsyncDifferConfig
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.ram.delivery.R
import com.ram.delivery.databinding.ItemTutorialPagerBinding
import com.ram.delivery.model.api.res.ResTutorial
import com.ram.delivery.utils.SystemUtil

class TutorialPagerAdapter() : BaseQuickAdapter<ResTutorial, BaseDataBindingHolder<ItemTutorialPagerBinding>>(
    R.layout.item_tutorial_pager){

    init {
        setDiffConfig(BrvahAsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<ResTutorial>() {
            override fun areItemsTheSame(oldItem: ResTutorial, newItem: ResTutorial): Boolean = oldItem.filePath == newItem.filePath
            override fun areContentsTheSame(oldItem: ResTutorial, newItem: ResTutorial): Boolean = (oldItem.viewNum == newItem.viewNum)
        }).build())
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemTutorialPagerBinding>,
        item: ResTutorial
    ) {

        val size = SystemUtil.getWindowSize(context)
        Glide.with(holder.itemView)
            .load(item.filePath)
            .override(size.width, size.height)
            .into(holder.itemView as ImageView)

        if(holder.absoluteAdapterPosition == getDefItemCount() - 1){
            (holder.itemView as ImageView).setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.black_alpha_60
                )
            )
        }
    }
}