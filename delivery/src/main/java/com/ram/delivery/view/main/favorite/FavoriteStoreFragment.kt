package com.ram.delivery.view.main.favorite

import android.util.Log
import com.ram.delivery.R
import com.ram.delivery.base.BaseFragment
import com.ram.delivery.databinding.FmtMainBinding

class FavoriteStoreFragment: BaseFragment<FmtMainBinding>(R.layout.fmt_main) {

    override fun FmtMainBinding.initView() {
        Log.d("alskaejr"," ok fmt")
        tvFmtMain.text = "Favorite Store"
    }
}