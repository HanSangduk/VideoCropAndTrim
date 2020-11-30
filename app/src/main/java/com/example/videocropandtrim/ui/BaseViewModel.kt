package com.example.videocropandtrim.ui

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable


abstract class BaseViewModel : ViewModel() {
    val disposables : CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCleared() {
        disposables.clear()
        disposables.dispose()
        super.onCleared()
    }
}