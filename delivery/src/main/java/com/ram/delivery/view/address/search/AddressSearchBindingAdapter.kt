package com.ram.delivery.view.address.search

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.diff.BrvahAsyncDifferConfig
import com.ram.delivery.R
import com.ram.delivery.model.api.res.ResDeliv


@BindingAdapter(value = ["bind:adapter_addressLatelyHistory_vm", "bind:adapter_addressLatelyHistory_list"])
fun bindAdapterAddressLateLyHistory(rv: RecyclerView, vm: AddressSearchViewModel, list: List<ResDeliv>?){

    rv.adapter?.let {
        if(it is AddressSearchHistoryAdapter)
            it.setDiffNewData(list = list?.toMutableList())
    } ?: run {
        val adapter = AddressSearchHistoryAdapter(vm)
        adapter.apply {
            setHasStableIds(true)
            setDiffConfig(BrvahAsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<ResDeliv>() {
                override fun areItemsTheSame(oldItem: ResDeliv, newItem: ResDeliv): Boolean =
                        oldItem.deliAreaNo == newItem.deliAreaNo

                override fun areContentsTheSame(oldItem: ResDeliv, newItem: ResDeliv): Boolean =
                        (oldItem.xpos == newItem.xpos && oldItem.ypos == newItem.ypos)

            }).build())
            setEmptyView(R.layout.item_empty_list)
        }
        with(rv){
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        adapter.setDiffNewData(list?.toMutableList())
    }
}

//@BindingAdapter(value = ["bind:adapter_addressSearch_vm", "bind:adapter_addressSearch_list"])
//fun bindAdapterAddressSearch(rv: RecyclerView, vm: AddressSearchViewModel, list: List<ResAddress>?) {
//    rv.adapter?.let {
//        if(it is AddressSearchResultAdapter){
//            if(vm.addressSearchPage.get() == 1) it.setList(list)
//            else list?.let { it1 -> it.addData(it1) }
//        }
//    } ?: run {
//        val adapter = AddressSearchResultAdapter(vm)
//        adapter.apply {
//            loadMoreModule.loadMoreView = AddressLoadMoreView()
//            loadMoreModule.setOnLoadMoreListener {
//                vm.addressPageLoadMore(loadMoreModule)
//            }
//            loadMoreModule.isAutoLoadMore = true
//            loadMoreModule.isEnableLoadMoreIfNotFullPage = false
//            setEmptyView(R.layout.item_empty_list)
//        }
//        with(rv){
//            this.adapter = adapter
//            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//        }
//        adapter.setDiffNewData(list?.toMutableList())
//    }
//}

