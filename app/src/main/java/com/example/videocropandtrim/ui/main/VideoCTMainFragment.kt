package com.example.videocropandtrim.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.videocropandtrim.base.ViewBindingHolder
import com.example.videocropandtrim.base.ViewBindingHolderImpl
import com.example.videocropandtrim.databinding.FragmentVideoCropTrimMainBinding
import com.example.videocropandtrim.utils.logg
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoCTMainFragment: Fragment(), ViewBindingHolder<FragmentVideoCropTrimMainBinding> by ViewBindingHolderImpl(){

//    private val mVideoCropAndTrimViewModel: VideoCropAndTrimViewModel by viewModel()
    private val mVideoCropAndTrimViewModel: VideoCropAndTrimViewModel by sharedViewModel()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = initBinding(FragmentVideoCropTrimMainBinding.inflate(layoutInflater), this) {
        videoCropAndTrimViewModel = mVideoCropAndTrimViewModel

//        navController = Navigation.findNavController(root)
        logg("설마이게 또 ???")
        initObersve()
    }
    private val naviController by lazy {
        findNavController()
    }

    private fun FragmentVideoCropTrimMainBinding.initObersve(){
        lifecycleOwner?.let {
            mVideoCropAndTrimViewModel.selectVideoUri.observe(it, { mediaFile ->
                logg("VideoCTMainFragment observe {this@VideoCTMainFragment.hashCode(): ${this@VideoCTMainFragment.hashCode()}")
                logg("VideoCTMainFragment observe {this@VideoCTMainFragment.id(): ${this@VideoCTMainFragment.id}")
                logg("VideoCTMainFragment observe: $mediaFile")
                logg("VideoCTMainFragment observe: ${findNavController() == null}")
                mediaFile?.let {
                    naviController.navigateUp()
                    naviController.navigate(
                        if (mediaFile.mimeType?.contains("video/") == true){
                            VideoCTMainFragmentDirections.actionMainFragmentToDetailFragemnt(
                                mediaFile
                            )
                        }else{
                            VideoCTMainFragmentDirections.actionMainFragmentToImageDetailFragment(
                                mediaFile
                            )
                        }
                    )
                }
            })
        }
    }
}