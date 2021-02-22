package com.ram.delivery.view.main

import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.orhanobut.logger.Logger
import com.ram.delivery.MainActivity
import com.ram.delivery.R
import com.ram.delivery.base.BaseFragment
import com.ram.delivery.databinding.FmtMainBinding
import kotlinx.android.synthetic.main.act_main.*

class MainFragment: BaseFragment<FmtMainBinding>(R.layout.fmt_main) {

    override fun FmtMainBinding.initView() {
        tvFmtMain.text = "alskaejr:"

        (activity as MainActivity).bottomNaviClickListener = { item ->
            Log.d("alskaejr", " ok fmt item: $item")
            Log.d("alskaejr", " ok fmt item: ${findNavController().currentBackStackEntry}")
            Log.d("alskaejr", " ok fmt item: ${findNavController().currentDestination?.id}")
            Log.d("alskaejr", " ok fmt item: ${this@MainFragment.id}")
            Log.d("alskaejr", " ok fmt item: ${findNavController().currentDestination?.id == this@MainFragment.id}")
            Log.d("alskaejr", " ok fmt item: ${findNavController().currentDestination?.label}")

//            findNavController().navigate(
//                R.id.action_mainFragment_to_homeFragment
//            )
        }
    }
}