package com.example.videocropandtrim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.videocropandtrim.databinding.ActivityMainBinding
import com.example.videocropandtrim.ui.main.VideoCropAndTrimViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private val videoCropAndTrimViewModel: VideoCropAndTrimViewModel by viewModel()
private val mVideoCropAndTrimViewModel: VideoCropAndTrimViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)



        val navi = Navigation.findNavController(this, fmVideoCTMain.id)
        NavigationUI.setupActionBarWithNavController(this, navi)

//        toolbar.setNavigationOnClickListener { navigation.navigateUp() }
        binding.lifecycleOwner = this
    }
}
