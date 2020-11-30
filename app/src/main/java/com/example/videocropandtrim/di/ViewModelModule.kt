package com.example.videocropandtrim.di

import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val videoCropAndTrimModule = module {
    viewModel { VideoCropAndTrimViewModel(get(), get()) }
}