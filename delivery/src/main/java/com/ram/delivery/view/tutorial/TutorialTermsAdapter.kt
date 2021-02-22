package com.ram.delivery.view.tutorial

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.ram.delivery.R
import com.ram.delivery.databinding.ItemTutorialTermsBinding
import com.ram.delivery.model.api.res.ResTerms

class TutorialTermsAdapter(
        private val viewModel: TutorialViewModel
): BaseQuickAdapter<ResTerms, BaseDataBindingHolder<ItemTutorialTermsBinding>>(R.layout.item_tutorial_terms), LoadMoreModule {

    override fun convert(holder: BaseDataBindingHolder<ItemTutorialTermsBinding>, item: ResTerms) {
        holder.dataBinding?.apply {
//            viewModel = this@TutorialTermsAdapter.viewModel
//            data = item

            cbTerms.isChecked = item.isAgree
            if(item.casNo == "M") {
                tvCasDesc.visibility = View.VISIBLE
                btnTerms.visibility = View.GONE
            } else {
                tvCasDesc.visibility = View.GONE
                btnTerms.visibility = View.VISIBLE
            }
        }
    }
}
