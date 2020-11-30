package com.example.videocropandtrim.di

import com.example.videocropandtrim.model.MediaRepository
import org.koin.dsl.module


val repositoryModule = module {
    single {
        MediaRepository(
            get()
        )
    }
}
