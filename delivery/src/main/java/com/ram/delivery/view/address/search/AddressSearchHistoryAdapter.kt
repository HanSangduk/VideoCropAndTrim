package com.ram.delivery.view.address.search

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.ram.delivery.R
import com.ram.delivery.databinding.ItemAddressHistoryListBinding
import com.ram.delivery.model.api.res.ResDeliv

class AddressSearchHistoryAdapter(
        private val viewModel: AddressSearchViewModel
): BaseQuickAdapter<ResDeliv, BaseDataBindingHolder<ItemAddressHistoryListBinding>>(R.layout.item_address_history_list) {

    override fun getItemId(position: Int): Long {
        if(position >= getDefItemCount()) return -1
        return data[position].deliAreaNo.toLong()
    }
    override fun convert(holder: BaseDataBindingHolder<ItemAddressHistoryListBinding>, item: ResDeliv) {
        holder.dataBinding?.apply {
//            data = item
//            viewModel = this@AddressSearchHistoryAdapter.viewModel
        }
    }
}
