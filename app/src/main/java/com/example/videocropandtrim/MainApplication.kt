package com.example.videocropandtrim

import android.app.Application
import android.content.Context
import android.os.Looper
import com.example.videocropandtrim.di.ServiceLocator
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers

class MainApplication: Application(){

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        ServiceLocator.init(this)
        setupRxAndroidPlugin()
    }

    private fun setupRxAndroidPlugin() {
        val defaultMainSchedulers = AndroidSchedulers.from(Looper.getMainLooper(), true)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            defaultMainSchedulers
        }
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            defaultMainSchedulers
        }
    }
}