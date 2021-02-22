package com.ram.delivery.view.main.home

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.ram.delivery.model.api.res.ResBanner

@BindingAdapter(value = ["bind:vp_home_banner_adapter", "bind:vp_home_banner_adapter_vm"])
fun bindVpAdapter(view: ViewPager2, items: List<ResBanner>?, vm: HomeViewModel){
    view.adapter?.let {
        if(it is HomeBannerVpAdapter){
            it.setDiffNewData(items?.toMutableList())
        }
    } ?: run {
        val adapter = HomeBannerVpAdapter(Glide.with(view.context))
        view.adapter = adapter
        adapter.setDiffNewData(items?.toMutableList())
    }

    items?.let {
        view.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Logger.d("bannnnner registerOnPageChangeCallback position: $position")
                super.onPageSelected(position)
                vm.itemClickOrView(it[position], position)
            }
        })
    }
}
