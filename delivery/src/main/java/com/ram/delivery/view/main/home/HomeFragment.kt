package com.ram.delivery.view.main.home

import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ram.delivery.R
import com.ram.delivery.base.BaseFragment
import com.ram.delivery.databinding.FmtHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<FmtHomeBinding>(R.layout.fmt_home) {

    private val homeVM: HomeViewModel by viewModels()
//    private val homeVM: HomeViewModel by activityViewModels()

    override fun FmtHomeBinding.initView() {
        Log.d("alskaejr","homeFragment initView")

        vpSetting()
//        homeVM.start()
        viewModel = homeVM
    }

    private fun FmtHomeBinding.vpSetting() {


    }
}
