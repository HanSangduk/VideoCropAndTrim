package com.ram.delivery.base

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding

interface ViewBindingHolder<T : ViewBinding> {
    val binding: T?

    fun initBinding(binding: T, fragment: Fragment, onBound: (T.() -> Unit)?): View

    fun requireBinding(block: (T.() -> Unit)? = null ): T
}

interface ActViewBindingHolder<T : ViewBinding> {
    val binding: T?

    fun initBinding(binding: T, fragment: Activity, lifecycle: Lifecycle?, onBound: (T.() -> Unit)?): View

    fun requireBinding(block: (T.() -> Unit)? = null ): T
}

class ViewBindingHolderImpl<T: ViewBinding>: ViewBindingHolder<T>, LifecycleObserver {
    override var binding: T? = null
    var lifecycle: Lifecycle? = null

    private lateinit var fragmentName: String

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyView(){
        lifecycle?.removeObserver(this)
        (binding as ViewDataBinding).lifecycleOwner = null
        lifecycle = null
        binding = null
    }

    override fun initBinding(binding: T, fragment: Fragment, onBound: (T.() -> Unit)?): View {
        this.binding = binding
        lifecycle = fragment.viewLifecycleOwner.lifecycle
        lifecycle?.addObserver(this)

        (binding as ViewDataBinding).lifecycleOwner = fragment

        fragmentName = fragment::class.simpleName ?: "N/A"
        onBound?.invoke(binding)
        return binding.root
    }

//    private fun backKeySetting(root: View, fragment: Fragment){
//
//        root.isFocusableInTouchMode = true
//        root.requestFocus()
//        root.setOnKeyListener { _, keyCode, _ ->
//            when(keyCode){
//                KeyEvent.KEYCODE_BACK -> {
//                    if(fragment is SupportFragment)
//                        fragment.pop()
//                    true
//                }
//                else -> false
//            }
//        }
//    }

    override fun requireBinding(block: (T.() -> Unit)?): T =
        binding?.apply { block?.invoke(this) } ?: throw IllegalStateException("Accessing binding outside of Fragment lifecycle: $fragmentName")
}
class ActViewBindingHolderImpl<T: ViewBinding>: ActViewBindingHolder<T>, LifecycleObserver {
    override var binding: T? = null
    var lifecycle: Lifecycle? = null

    private lateinit var actName: String

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyView(){
        lifecycle?.removeObserver(this)
        (binding as ViewDataBinding).lifecycleOwner = null
        lifecycle = null
        binding = null
    }

    override fun initBinding(binding: T, activity: Activity, lifecycle: Lifecycle?, onBound: (T.() -> Unit)?): View {
        this.binding = binding
        this.lifecycle = lifecycle
        lifecycle?.addObserver(this)

        (binding as ViewDataBinding).lifecycleOwner = activity as LifecycleOwner

        actName = activity::class.simpleName ?: "N/A"
        onBound?.invoke(binding)
        return binding.root
    }

    override fun requireBinding(block: (T.() -> Unit)?): T =
        binding?.apply { block?.invoke(this) } ?: throw IllegalStateException("Accessing binding outside of Fragment lifecycle: $actName")
}