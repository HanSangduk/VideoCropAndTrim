package com.ram.delivery.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.ram.delivery.utils.SystemUtil
import io.reactivex.disposables.CompositeDisposable

interface NetworkStateReceiverListener {
    fun networkConnectivityChanged() {}
}

abstract class BaseActivity<B: ViewBinding>(val layoutId: Int): AppCompatActivity(), ActViewBindingHolder<B> by ActViewBindingHolderImpl()  {

    open val activityDisposable = CompositeDisposable()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityManager by lazy { getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    abstract fun B.initVIew()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding(DataBindingUtil.setContentView(this, layoutId),this, lifecycle){
            initVIew()

            if (this is NetworkStateReceiverListener)
                registerConnectivityMonitoring(this)
        }
    }
    override fun onResume() {
        super.onResume()
        if (this is NetworkStateReceiverListener && !isConnected)
            this.networkConnectivityChanged()// call to show no network banner on activity resume
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this is NetworkStateReceiverListener)
            unregisterConnectivityMonitoring()
        with(activityDisposable){
            clear()
            dispose()
        }
    }


    protected val NetworkStateReceiverListener.isConnected: Boolean // can be used by childs
        get() {
            this as BaseActivity<*> // only accessible from child class, so cast i safe here
            return SystemUtil.isConnected(applicationContext)
        }

    private fun registerConnectivityMonitoring(listener: NetworkStateReceiverListener) {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
//                listener.networkConnectivityChanged()
            }

            override fun onLost(network: Network) {
//                listener.networkConnectivityChanged()
            }
        }
        this.networkCallback = networkCallback
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback
        )
    }

    private fun unregisterConnectivityMonitoring() {
        val networkCallback = this.networkCallback ?: return
        connectivityManager.unregisterNetworkCallback(networkCallback)
        this.networkCallback = null
    }
}