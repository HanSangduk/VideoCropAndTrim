package com.ram.delivery.view.address.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ram.delivery.R
import com.ram.delivery.databinding.ViewAddressLoadMoreBinding

class AddressLoadMoreView: BaseLoadMoreView() {
    override fun getLoadComplete(holder: BaseViewHolder): View {
        return holder.getView(R.id.load_more_load_complete_view)
    }

    override fun getLoadEndView(holder: BaseViewHolder): View {
        return holder.getView(R.id.load_more_load_end_view)
    }

    override fun getLoadFailView(holder: BaseViewHolder): View {
        return holder.getView(R.id.load_more_load_fail_view)
    }

    override fun getLoadingView(holder: BaseViewHolder): View {
        return holder.getView(R.id.load_more_loading_view)
    }

    override fun getRootView(parent: ViewGroup): View {
        return ViewAddressLoadMoreBinding.inflate(
                LayoutInflater.from(parent.context)
        ).root
    }
}