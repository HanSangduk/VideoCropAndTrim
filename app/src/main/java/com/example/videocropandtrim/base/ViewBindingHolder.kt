package com.example.videocropandtrim.base

import android.view.KeyEvent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.example.videocropandtrim.utils.logg

interface ViewBindingHolder<T : ViewBinding> {
    val binding: T?

    fun initBinding(binding: T, fragment: Fragment, onBound: (T.() -> Unit)?): View

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
//        backKeySetting(binding.root, fragment)
        return binding.root
    }

    private fun backKeySetting(root: View, fragment: Fragment){

        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener { _, keyCode, _ ->
            logg("fragment is SupportFragment: // keycode: $keyCode")
            when(keyCode){
                KeyEvent.KEYCODE_BACK -> {
                    //todo 1123 backey 눌렀을 때 동작 넣음됨
                    true
                }
                else -> false
            }
        }
    }

    override fun requireBinding(block: (T.() -> Unit)?): T =
        binding?.apply { block?.invoke(this) } ?: throw IllegalStateException("Accessing binding outside of Fragment lifecycle: $fragmentName")
}