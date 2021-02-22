package com.ram.delivery.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<B : ViewBinding>(val layoutId: Int): Fragment(), ViewBindingHolder<B> by ViewBindingHolderImpl() {
    open val fragmentDisposable = CompositeDisposable()

    abstract fun B.initView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = initBinding(DataBindingUtil.inflate(inflater, layoutId, container, false), this){
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentDisposable.clear()
        fragmentDisposable.dispose()
    }
}