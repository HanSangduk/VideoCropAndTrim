package com.ram.delivery.view.main.cart

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ram.delivery.R
import com.ram.delivery.base.BaseFragment
import com.ram.delivery.databinding.FmtMainBinding
import com.ram.delivery.view.main.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment: BaseFragment<FmtMainBinding>(R.layout.fmt_main) {

    private val homeVM: HomeViewModel by activityViewModels()

    override fun FmtMainBinding.initView() {
        Log.d("alskaejr"," ok fmt cart")
        tvFmtMain.text = "Cart"
    }
}