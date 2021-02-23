package com.ram.delivery.view.safetalk

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.ram.delivery.R
import com.ram.delivery.databinding.ItemSafetalkBannerBinding
import com.ram.delivery.other.SAFE_TALK_ARGS
import kotlinx.android.parcel.Parcelize

class SafeTalkBannerViewHolder private constructor(private val binding: ItemSafetalkBannerBinding) :
    RecyclerView.ViewHolder(binding.root), BannerViewHolder<SafeTalkBannerModel> {

    override fun onBindViewHolder(data: SafeTalkBannerModel) {



        val location = itemView.context.getString(R.string.safe_talk_home_banner_location, data.town)
        binding.tvShopTalkBannerLocation.text = location

        val starts: LayerDrawable = binding.rbSafeTalkBanner.progressDrawable as LayerDrawable
        starts.getDrawable(2).colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(
                this.itemView.context,
                R.color.color_e21836
            ), BlendModeCompat.SRC_ATOP)

        binding.rbSafeTalkBanner.rating = data.point.toFloat()

//        34c759 clean

//        binding.root.setOnClickListener {
//            Intent(it.context, SafeTalksActivity::class.java).apply {
//                RxBusBehavior.publish(SafeTalksArgs(near = false, safeTalkNo = -1))
//                putExtra(SAFE_TALK_ARGS, SafeTalksArgs(near = false, safeTalkNo = -1))
//                it.context.startActivity(this)
//            }
//        }
    }

    companion object {
        fun create(parent: ViewGroup): SafeTalkBannerViewHolder {
            return SafeTalkBannerViewHolder(
                ItemSafetalkBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

interface BannerViewHolder<T> {
fun onBindViewHolder(data: T)
}

@Parcelize
data class SafeTalksArgs(
    val near: Boolean = false,
    val safeTalkNo:Int = -1,
    val isMySafeTalk:Boolean = false
) : Parcelable


data class SafeTalkBannerModel(
    val talkCount: Int,
    val point: Int,
    val town: String,
)