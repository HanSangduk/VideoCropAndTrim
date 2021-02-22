package com.ram.delivery

import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.orhanobut.logger.Logger
import com.ram.delivery.base.BaseActivity
import com.ram.delivery.databinding.ActMainBinding
import com.ram.delivery.view.main.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity<ActMainBinding>(R.layout.act_main) {

    var bottomNaviClickListener: ((item: MenuItem) -> Unit)? = null
    private val homeVM: HomeViewModel by viewModels()


    private val onNavigationItemSelectedListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            Log.d("alskaejr","1111111 ite,: $item")


            val naviHostFragment = supportFragmentManager.findFragmentById(R.id.fmtActMain)
            naviHostFragment?.childFragmentManager?.fragments?.forEach {
                Log.d("alskaejr","it: $it")
            }
            val currentFragment = naviHostFragment?.childFragmentManager?.fragments?.first()
            val resId = when (item.itemId) {
                R.id.navigation_favorite -> R.id.action_homeFragment_to_favoriteStoreFragment
                R.id.navigation_cart -> R.id.action_homeFragment_to_cartFragment
                R.id.navigation_order_history -> R.id.action_homeFragment_to_orderHistoryFragment
                R.id.navigation_myorder -> R.id.action_homeFragment_to_myOrderFragment
                else -> null
            }
            currentFragment?.let {
                Log.d("alskaejr","okkkk it: $it")
                it.findNavController().navigateUp()
                resId?.let { id ->
                    it.findNavController().navigate(id)
                }
            }

//            bottomNaviClickListener?.invoke(item)
            true
        }
    }

    private val onNavigationItemReselectedListener by lazy {
        BottomNavigationView.OnNavigationItemReselectedListener { item ->
            Logger.d("22222bottom navi item: $item")
        }
    }

    override fun ActMainBinding.initVIew() {

        Log.d("alskaejr","0000")
        Logger.d("333333 initVIew§")
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigationView.setOnNavigationItemReselectedListener(onNavigationItemReselectedListener)
    }
}