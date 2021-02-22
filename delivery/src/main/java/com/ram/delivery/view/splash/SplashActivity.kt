package com.ram.delivery.view.splash

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.orhanobut.logger.Logger
import com.ram.delivery.MainActivity
import com.ram.delivery.R
import com.ram.delivery.base.BaseActivity
import com.ram.delivery.base.NetworkStateReceiverListener
import com.ram.delivery.databinding.ActSplashBinding
import com.ram.delivery.other.AppUpgradeType
import com.ram.delivery.other.Constants
import com.ram.delivery.other.DialogType
import com.ram.delivery.utils.SystemUtil
import com.ram.delivery.utils.extension.showToast
import com.ram.delivery.utils.showCustomDialog
import com.ram.delivery.view.tutorial.TutorialActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.act_splash.*
import org.jetbrains.anko.startActivity
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActSplashBinding>(R.layout.act_splash),
    EasyPermissions.PermissionCallbacks, NetworkStateReceiverListener {

    companion object {
        const val GO_TO_MAIN = 1
        const val GO_TO_TUTORIAL = 2
    }

    private val listPermissions = listOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CALL_PHONE
    )

    private val viewModel: SplashViewModel by viewModels()

    override fun ActSplashBinding.initVIew() {
        vmObserve()
    }

    private fun vmObserve() {
        val owner = this
        with(viewModel) {
            bgImage.observe(owner, {
                setLogoImage(it)
            })

            finish.observe(owner) {
                finish()
            }

            naviEvent.observe(owner) {
                when (it) {
                    GO_TO_TUTORIAL -> startActivity<TutorialActivity>("Tutorials" to viewModel.listTutorial)
                    GO_TO_MAIN -> startActivity<MainActivity>(
                        "ListMainPopUp" to viewModel.listMainPopUp,
                        "deepLink" to intent.data?.toString()
                    )
                }
            }

            reqPermission.observe(owner) {
                reqPermission()
            }

            appUpgradeEvent.observe(owner) {
                when (it) {
                    AppUpgradeType.MAJOR -> {
                        showCustomDialog(
                            dialogType = DialogType.CONFIRM,
                            title = "",
                            message = getString(R.string.splash_upgrade_major_msg),
                            btnLeftText = getString(R.string.btn_popup_next),
                            btnRightText = getString(R.string.btn_popup_upgrade),
                            cancelable = false
                        ) { isConfirm, _ ->
                            if (isConfirm) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("market://details?id=${packageName}")
                                startActivity(intent)
                                return@showCustomDialog
                            }
                            finish()
                        }
                    }
                    AppUpgradeType.MINER -> {
                        showCustomDialog(
                            dialogType = DialogType.CONFIRM,
                            title = "",
                            message = getString(R.string.splash_upgrade_minor_msg),
                            btnLeftText = getString(R.string.btn_popup_next),
                            btnRightText = getString(R.string.btn_popup_upgrade),
                            cancelable = false
                        ) { isConfirm, _ ->
                            if (isConfirm) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("market://details?id=${packageName}")
                                startActivity(intent)
                                return@showCustomDialog
                            }
                            viewModel.onMinorDialogCancelClick()
                        }
                    }
                    AppUpgradeType.NONE -> grantedPermission()
                }
            }
        }
    }

    private fun reqPermission() {
        Logger.d("reqPermission")
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.toast_message_permission_denied),
            Constants.PERMISSION_REQUEST_CODE,
            *listPermissions.toTypedArray()
        )
    }

    private fun setLogoImage(filePath: String) {
        val size = SystemUtil.getWindowSize(ivSplashBackground.context)
        Glide.with(ivSplashBackground.context)
            .load(filePath)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .override(size.width, size.height)
            .into(ivSplashBackground)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun checkPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, *listPermissions.toTypedArray())
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (checkPermission()) {
            Logger.d("onPermissionsGranted checkPermission true")
            viewModel.afterPermissionLogic()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }

    override fun networkConnectivityChanged() {

        if (isConnected) {
            //show that that the network is back
        } else {
            //showToast(R.string.error_network_state)
            //startActivity<CrashActivity>()
            showToast(R.string.error_network_state)
            Logger.d(getString(R.string.error_network_state))
            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    finish()
                }, 2000)
            }
        }
    }
}