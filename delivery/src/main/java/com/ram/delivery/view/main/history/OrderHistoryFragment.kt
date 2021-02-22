package com.ram.delivery.view.main.history

import android.util.Log
import com.ram.delivery.R
import com.ram.delivery.base.BaseFragment
import com.ram.delivery.databinding.FmtMainBinding

class OrderHistoryFragment: BaseFragment<FmtMainBinding>(R.layout.fmt_main) {

    override fun FmtMainBinding.initView() {
        Log.d("alskaejr"," ok fmt")
        tvFmtMain.text = "ORDER HISTORY"
    }
}