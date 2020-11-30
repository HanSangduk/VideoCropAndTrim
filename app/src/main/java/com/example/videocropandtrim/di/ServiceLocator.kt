package com.example.videocropandtrim.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object ServiceLocator {

    fun init(context: Context) {
        startKoin {
            androidContext(context)
            modules(
                listOf(
                    repositoryModule,
                    videoCropAndTrimModule,
                )
            )
        }
    }
}