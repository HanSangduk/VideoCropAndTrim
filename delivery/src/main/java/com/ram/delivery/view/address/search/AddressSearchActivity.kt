package com.ram.delivery.view.address.search

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.orhanobut.logger.Logger
import com.ram.delivery.R
import com.ram.delivery.base.BaseActivity
import com.ram.delivery.databinding.ActAddressSearchBinding
import com.ram.delivery.model.api.res.ResAddress
import com.ram.delivery.other.Constants
import com.ram.delivery.other.DialogType
import com.ram.delivery.utils.StringUtil.Companion.showToast
import com.ram.delivery.utils.SystemUtil
import com.ram.delivery.utils.customDialogShow
import com.ram.delivery.utils.extension.filterEmoji
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class AddressSearchActivity :
    BaseActivity<ActAddressSearchBinding>(R.layout.act_address_search),
    EasyPermissions.PermissionCallbacks {

    companion object {
        const val DIALOG_LOCATION = 1
    }

    private val viewModel: AddressSearchViewModel by viewModels()

    private val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Logger.d("activityResult = ${it.data}")
    }

    override fun ActAddressSearchBinding.initVIew() {

//        viewModel = this@AddressSearchActivity.viewModel
        etSearch.filterEmoji()
        btnSetting()
        observe()
    }

    private fun ActAddressSearchBinding.observe() {
//        viewModel?.apply {
//
//            setupCurEvent.observe(this@AddressSearchActivity) {
//                startActivityForResult<AddressSetupActivity>(
//                    Constants.ACTIVITY_CALL_ADDRESS_SETUP,
//                    "ResAddress" to it
//                )
//            }
//            setupEvent.observe(this@AddressSearchActivity, {
//                val resAddress = ResAddress(
//                    it.road_address?.address_name ?: "",
//                    0,
//                    it.address.b_code.substring(0, 8),
//                    it.address.address_name,
//                    it.x,
//                    it.y,
//                    "",
//                    "",
//                    "",
//                    "",
//                    "",
//                    ""
//                )
//
//                startActivityForResult<AddressSetupActivity>(
//                    Constants.ACTIVITY_CALL_ADDRESS_SETUP,
//                    "ResAddress" to resAddress
//                )
//            })
//
//            saveDelivEvent.observe(this@AddressSearchActivity){
//                val intent = Intent()
//                intent.putExtra("ResAddress", it)
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//            }
//            closeEvent.observe(this@AddressSearchActivity, {
//                finish()
//            })
//
//            progressbar.observe(this@AddressSearchActivity) {
//                pbAddressSearch.isVisible = it
//            }
//
//            enableBtn.observe(this@AddressSearchActivity) {
//                layoutBtn.isEnabled = it
//            }
//
//            navigateToAddressSetup.observe(this@AddressSearchActivity) {
//                Intent(this@AddressSearchActivity, AddressSetupActivity::class.java).apply {
//                    putExtra("ResAddress", it)
//                    activityResult.launch(this)
//                }
//            }
//
//            addressHistoryItemClickEvent.observe(this@AddressSearchActivity) { item ->
//                val resAddress = ResAddress(
//                    item.addr1,
//                    item.deliSeq,
//                    item.deliAreaNo,
//                    item.oldAddr,
//                    item.xpos,
//                    item.ypos,
//                    item.zipNo,
//                    "",
//                    "",
//                    "",
//                    "",
//                    item.addr2
//                )
//                startActivityForResult<AddressSetupActivity>(
//                    Constants.ACTIVITY_CALL_ADDRESS_SETUP,
//                    "ResAddress" to resAddress
//                )
//            }
//
//            addressSearchItemClickEvent.observe(this@AddressSearchActivity) {
//                startActivityForResult<AddressSetupActivity>(
//                    Constants.ACTIVITY_CALL_ADDRESS_SETUP,
//                    "ResAddress" to it
//                )
//            }
//        }
    }

    private fun setCurrentLocation(){
        if (SystemUtil.checkLocationServicesStatus(this)) {
            checkRunTimePermission()
        } else {
            showDialogForLocationServiceSetting()
        }
    }

    private fun ActAddressSearchBinding.btnSetting() {
        btnAddressSearchClose.setOnClickListener {
            finish()
        }

        with(layoutBtn) {
            isEnabled = true

            setOnClickListener {
                setCurrentLocation()
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        this.customDialogShow(
            DialogType.CONFIRM,
            getString(R.string.pop_msg_off_location_title),
            getString(R.string.pop_msg_off_location_setup),
            getString(R.string.btn_close_text),
            getString(R.string.btn_popup_location_setup),
            customLayoutId = R.layout.custom_popup_dialog,
        ){
            if(it){
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                return@customDialogShow
            }
        }
    }

    private fun checkRunTimePermission() {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            viewModel.currentLocationSearch()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "",
                Constants.PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                DIALOG_LOCATION -> {
                    setCurrentLocation()
                }
                Constants.ACTIVITY_CALL_ADDRESS_SETUP -> {
                    val resAddress = data?.getParcelableExtra<ResAddress>("ResAddress")
                    val intent = Intent()
                    intent.putExtra("ResAddress", resAddress)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        showToast(this, getString(R.string.toast_message_permission_denied))
    }

}