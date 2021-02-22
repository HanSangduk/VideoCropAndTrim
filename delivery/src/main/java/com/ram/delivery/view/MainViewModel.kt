package com.ram.delivery.view

import android.content.Context
import android.os.Build
import android.util.Log
import com.ram.delivery.BuildConfig
import com.ram.delivery.base.BaseViewModel
import com.ram.delivery.model.api.BaseApiService
import com.ram.delivery.model.api.JusoApiService
import com.ram.delivery.model.api.req.ReqAppKey
import com.ram.delivery.other.Constants
import com.ram.delivery.utils.extension.with
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxkotlin.addTo
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val baseApiService: BaseApiService,
    val jusoApiService: JusoApiService,
    @ApplicationContext context: Context
) : BaseViewModel(context){

    fun talk(){
        Log.d("alskaejr","안와???1232131231")
    }
    init {
        Log.d("alskaejr","안와???")
        val req = ReqAppKey(
            "getApplication<MelchiApplication>().fcmRegId",
            BuildConfig.VERSION_NAME,
            UUID.randomUUID().toString(),
            Build.VERSION.SDK_INT.toString(),
            Constants.REST_DEVICE_OS_TYPE_CODE
        )
//        val req = ReqAppKey(
//            getApplication<MelchiApplication>().fcmRegId,
//            BuildConfig.VERSION_NAME,
//            UUID.randomUUID().toString(),
//            Build.VERSION.SDK_INT.toString(),
//            Constants.REST_DEVICE_OS_TYPE_CODE
//        )
        baseApiService.reqAppKey(
            req
        ).with()
            .subscribe({
                Log.d("alskaejr","baseApiService.reqAppKey ok it: $it")
            }, {
                Log.d("alskaejr","baseApiService.reqAppKey error it: ${it.message}")
            }).addTo(disposable)

        val hashMapOf = hashMapOf<String, Any?>(
            "confmKey" to "devU01TX0FVVEgyMDIwMDkxMTA5MDEwMTExMDE3MjI=",
            "currentPage" to 1,
            "countPerPage" to 10,
            "keyword" to "devU01TX0FVVEgyMDIwMDkxMTA5MDEwMTExMDE3MjI=",
            "resultType" to "json"
        )
        jusoApiService.getAddress(
            hashMapOf
        ).with().subscribe({
            Log.d("alskaejr","getAddress ok it: $it")
        }, {
            Log.d("alskaejr","getAddress error it: ${it.message}")
        }).addTo(disposable)

    }

}