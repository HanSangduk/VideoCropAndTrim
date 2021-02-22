@file:Suppress("SpellCheckingInspection")

package com.ram.delivery.view.main.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ram.delivery.DeliveryApplication
import com.ram.delivery.base.BaseViewModel
import com.ram.delivery.model.api.BaseApiService
import com.ram.delivery.model.api.JusoApiService
import com.ram.delivery.model.api.SafeTalkApiService
import com.ram.delivery.model.api.res.LastAddressResponse
import com.ram.delivery.model.api.res.ResBanner
import com.ram.delivery.model.api.res.ResError
import com.ram.delivery.model.api.res.SafeTalkBannerResponse
import com.ram.delivery.other.CateTpCd2
import com.ram.delivery.other.JobTypeCd
import com.ram.delivery.other.RunTypeCd
import com.ram.delivery.utils.SingleLiveEvent
import com.ram.delivery.utils.toDisplayAddress
import com.ram.delivery.view.safetalk.SafeTalkBannerModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("UNUSED_PARAMETER")
@HiltViewModel
class HomeViewModel @Inject constructor(
    val baseApiService: BaseApiService,
    val safeTalkApiService: SafeTalkApiService,
    val jusoApiService: JusoApiService,
    @ApplicationContext context: Context
) : BaseViewModel(context) {

    private val app by lazy {
        DeliveryApplication.instance
    }
    private val appKey by lazy {
        app.mAppKey
    }

    val locationSettingEvent = SingleLiveEvent<Unit>()

    /**ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ START Banner ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ*/
    private val _bannerItem: MutableLiveData<List<ResBanner>> = MutableLiveData()
    val bannerItem: LiveData<List<ResBanner>> = _bannerItem

    private val _bannerTotalCnt: MutableLiveData<String> = MutableLiveData("1")
    val bannerTotalCnt: LiveData<String> = _bannerTotalCnt

    private val _bannerCurrentIndex: MutableLiveData<String> = MutableLiveData("1")
    val bannerCurrentIndex: LiveData<String> = _bannerCurrentIndex
    /** ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ|||| END Banner ||||ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ */


    private val _lastAddress: MutableLiveData<String> = MutableLiveData("1")
    val lastAddress: LiveData<String> = _lastAddress

    private val _lastAddressRes: MutableLiveData<LastAddressResponse> = MutableLiveData()
    val lastAddressRes: LiveData<SafeTalkBannerModel> = _lastAddressRes

    private val _safeTalkBannerModel: MutableLiveData<SafeTalkBannerModel> = MutableLiveData()
    val safeTalkBannerModel: LiveData<SafeTalkBannerModel> = _safeTalkBannerModel

//    private val _test: MutableLiveData<String> = MutableLiveData("1")
//    val test: LiveData<String> = _test

//    private val _test: MutableLiveData<String> = MutableLiveData("1")
//    val test: LiveData<String> = _test



    init {
        addDisposable(
            baseApiService.reqInfoLastAddr(appKey)
        )
        addDisposable(
            baseApiService.reqBaskCnt(appKey)
        )

        addDisposable(
            baseApiService.reqBanner(appKey)
        )

        addDisposable(
            baseApiService.reqCategory(
                appKey,
                CateTpCd2.ALL
            )
        )

        addDisposable(
            baseApiService.reqCompany(appKey)
        )

    }

    fun apiCallSafeTalkBanner(lastAddress: LastAddressResponse){
        addDisposable(
            safeTalkApiService.getBanner(
                appKey,
                lastAddress.legalCd
            )
        )
    }

    fun itemClickOrView(resBanner: ResBanner, position: Int, isClick: Boolean = false){
        if(!isClick) _bannerCurrentIndex.value = (position + 1 ).toString()

        addDisposable(
            baseApiService.reqViewClick(
                appKey,
                JobTypeCd.BANNER,
                resBanner.bannNo,
                if(isClick) RunTypeCd.CLICK else RunTypeCd.VIEW
            )
        )
    }

    fun onClickLocation(){
        locationSettingEvent.call()
    }

    override fun onRetrieveDataSuccess(data: Any) {
        Log.d("alskaejr","onRetrieveDataSuccess: data: $data")
        Log.d("alskaejr", "onRetrieveDataSuccess: is ResBanner: ${data is ResBanner}")
        when(data){
            is List<*> -> {
                if(data.isNullOrEmpty()) return

                when(data[0]){
                    is ResBanner -> {
                        _bannerTotalCnt.value = data.size.toString()
                        _bannerItem.value = data as List<ResBanner>
                    }
                }
            }

            is LastAddressResponse -> {
                Log.d("alskaejr", "onRetrieveDataSuccess: LastAddressResponse data: $data")
                _lastAddress.value = data.addr1.toDisplayAddress()
                apiCallSafeTalkBanner(data)
            }

            is SafeTalkBannerResponse -> {
                _safeTalkBannerModel.value = SafeTalkBannerModel(
                    data.talkCount,
                    data.point,
                    _lastAddress.value?.tow
                )
            }
        }
    }

    override fun onRetrieveDataError(resError: ResError?) {
        Log.d("alskaejr","onRetrieveDataSuccess: resError: $resError")
    }
}