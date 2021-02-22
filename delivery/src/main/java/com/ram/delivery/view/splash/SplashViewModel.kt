package com.ram.delivery.view.splash


import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.logger.Logger
import com.ram.delivery.BuildConfig
import com.ram.delivery.DeliveryApplication
import com.ram.delivery.DeliveryApplication.Companion.locTracker
import com.ram.delivery.R
import com.ram.delivery.base.BaseViewModel
import com.ram.delivery.model.api.BaseApiService
import com.ram.delivery.model.api.JusoApiService
import com.ram.delivery.model.api.KakaoApiService
import com.ram.delivery.model.api.req.ReqAppKey
import com.ram.delivery.model.api.req.ReqSaveDeliv
import com.ram.delivery.model.api.res.ResBase
import com.ram.delivery.model.api.res.ResPopup
import com.ram.delivery.model.api.res.ResTutorial
import com.ram.delivery.model.api.res.toTown
import com.ram.delivery.other.AppUpgradeType
import com.ram.delivery.other.Constants
import com.ram.delivery.utils.PreferencesUtil
import com.ram.delivery.utils.SingleLiveEvent
import com.ram.delivery.utils.SystemUtil
import com.ram.delivery.utils.extension.isGPS
import com.ram.delivery.utils.extension.showToast
import com.ram.delivery.utils.extension.with
import com.ram.delivery.view.splash.SplashActivity.Companion.GO_TO_MAIN
import com.ram.delivery.view.splash.SplashActivity.Companion.GO_TO_TUTORIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val baseApiService: BaseApiService,
    val jusoApiService: JusoApiService,
    val kakaoApiService: KakaoApiService,
    @ApplicationContext context: Context
) : BaseViewModel(context){

    private val _bgImage = MutableLiveData<String>()
    val bgImage: LiveData<String> = _bgImage

    private val _listTutorial = MutableLiveData<List<ResTutorial>>()
    val listTutorial: LiveData<List<ResTutorial>> = _listTutorial

    private val _listMainPopUp = MutableLiveData<List<ResPopup>>()
    val listMainPopUp: LiveData<List<ResPopup>> = _listMainPopUp

//    val introLoadingFinish = MutableLiveData(false)
//
//    var listTutorial = listOf<ResTutorial>()
//    var listMainPopUp = listOf<ResPopup>()
//
//    private val _toastMsg = MutableLiveData<Int>()
//    val toastMsg: LiveData<Int> = _toastMsg
//
//    val majorDialog = SingleLiveEvent<Unit>()
//    val minorDialog = SingleLiveEvent<Unit>()
    val appUpgradeEvent = SingleLiveEvent<AppUpgradeType>()
//
//    private val _checkPermission = MutableLiveData<(Boolean) -> Unit>()
//    val checkPermission: LiveData<(Boolean) -> Unit> = _checkPermission
//
//    private val _navigateToTutorial = MutableLiveData<List<ResTutorial>>()
//    val navigateToTutorial: LiveData<List<ResTutorial>> = _navigateToTutorial
//
//    private val _navigateToMain = MutableLiveData<List<ResPopup>>()
//    val navigateToMain: LiveData<List<ResPopup>> = _navigateToMain
//
    val finish = SingleLiveEvent<Unit>()
    val naviEvent = SingleLiveEvent<Int>()
    val reqPermission = SingleLiveEvent<Unit>()
//
//    private val appKey by lazy { getApplication<MelchiApplication>().getAppKey() }
//
//    private val smartLocation = SmartLocation.with(getApplication()).location().oneFix()
//
//    private val addressRepository by lazy { getApplication<MelchiApplication>().addressRepository }
//
//    private val checkPermissionCallback: (Boolean) -> Unit = { granted ->
////        Logger.d("permission granted = $granted")
//        when (granted) {
//            true -> grantedPermission()
//            else -> reqPermission.call()
//        }
//    }

    private val app by lazy {
        DeliveryApplication.instance
    }

//    private val locTracker by lazy {
//        LocationTracker(context)
//    }

    private fun handleGps() {
        val isGPS = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            false
        }else{
            context.isGPS()
        }
//        val isGPS = app.isGPS()
        when (isGPS) {
            true -> {
                reqCoord2Address(
                    locTracker.latitude.toString(),
                    locTracker.longitude.toString()
                )
            }
            else -> {
                val param = ReqSaveDeliv(
                    addr1 = Constants.DEFAULT_LOCATION_ADDRESS,
                    addr2 = "",
                    jibunAddr = Constants.DEFAULT_LOCATION_JIBUN_ADDRESS,
                    zipNo = Constants.DEFAULT_LOCATION_ZIPCODE,
                    xpos = Constants.DEFAULT_LOCATION_LONGITUDE,
                    ypos = Constants.DEFAULT_LOCATION_LATITUDE,
                    areaInfo = Constants.DEFAULT_LOCATION_DELIAREANO,
                    town = Constants.DEFAULT_LOCATION_TOWN
                )
                reqSaveDeliv(appKey = app.mAppKey, param = param)
            }
        }
    }

    private fun reqSaveDeliv(appKey: String, param: ReqSaveDeliv) {
        baseApiService.reqSaveDeliv(appKey, param)
            .with()
            .subscribeBy {
                gotoMainOrTutorial()
            }
            .addTo(disposable)
    }

    private fun reqCoord2Address(x: String, y: String) {
//        val app = getApplication<MelchiApplication>()

/*
        fun createParam(data: JusoResponse): ReqSaveDeliv {
            val address = data
            return ReqSaveDeliv(
                addr1 = data.roadAddrPart1,
                addr2 = "",
                zipNo = data.zipNo,
                jibunAddr = data.jibunAddr,
                xpos = data.x,
                ypos = data.y,
                areaInfo = address.b_code.plus(address.h_code)
            )

        }
*/


        fun onSuccess(data: ResBase) {
            Logger.d("reqCorrd2Address success = $data")
            gotoMainOrTutorial()
        }

        fun onFailure(t: Throwable) {
            Logger.e(t, t.message.toString())
            reqSaveDeliv(
                appKey = app.mAppKey,
                param = ReqSaveDeliv(
                    addr1 = Constants.DEFAULT_LOCATION_ADDRESS,
                    addr2 = "",
                    zipNo = Constants.DEFAULT_LOCATION_ZIPCODE,
                    jibunAddr = Constants.DEFAULT_LOCATION_JIBUN_ADDRESS,
                    xpos = Constants.DEFAULT_LOCATION_LONGITUDE,
                    ypos = Constants.DEFAULT_LOCATION_LATITUDE,
                    areaInfo = Constants.DEFAULT_LOCATION_DELIAREANO,
                    town = Constants.DEFAULT_LOCATION_TOWN
                )
            )
        }

        Logger.d("x = $x, y = $y")
        kakaoApiService.reqCoord2Address(x, y)
            .flatMap {
                val first = it.documents.first()
                val keyword = first.road_address?.address_name ?: first.address.address_name
                val hashMapOf = hashMapOf<String, Any?>(
                    "confmKey" to "devU01TX0FVVEgyMDIwMDkxMTA5MDEwMTExMDE3MjI=",
                    "currentPage" to 1,
                    "countPerPage" to 10,
                    "keyword" to keyword,
                    "resultType" to "json"
                )
                jusoApiService.getAddress(hashMapOf).map { jusoAddressResponse ->
                    Pair(first = keyword, second = jusoAddressResponse.results.jusoResponse.first())
                }
            }.flatMap {
                val keyword = it.first
                val juso = it.second
                kakaoApiService.reqAddress(keyword).map { kakaoSearch ->
                    val firstAddress = kakaoSearch.documents.first().address
                    val areaCd = firstAddress.b_code.substring(0, 8)
                        .plus(firstAddress.h_code.substring(0, 8))
                    ReqSaveDeliv(
                        addr1 = juso.roadAddrPart1,
                        addr2 = "",
                        zipNo = juso.zipNo,
                        jibunAddr = juso.jibunAddr,
                        xpos = firstAddress.x,
                        ypos = firstAddress.y,
                        areaInfo = areaCd,
                        town = firstAddress.toTown()
                    )
                }
            }.flatMap {
                baseApiService.reqSaveDeliv(app.mAppKey, it)
            }
            .with()
            .subscribe(::onSuccess, ::onFailure).addTo(disposable)
    }


    fun grantedPermission() {
        viewModelScope.launch() {
//            val lastAddress = baseApiService.reqInfoLastAddrAsync(app.mAppKey)
//            if (lastAddress.areaCd.isEmpty()) { // 서버에 저장된 주소가 없다는 것은 처음 첫 접속이다.
//                handleGps()
//            } else { // 서버에 저장된 주소가 있다.
//                gotoMainOrTutorial()
//            }
        }
    }

    private fun gotoMainOrTutorial() {
        naviEvent.value = if(PreferencesUtil.isFirstRun && listTutorial.value?.isNotEmpty() == true) GO_TO_MAIN
        else GO_TO_TUTORIAL

        finish.call()
    }

    init {
        showSplashBg()
        appKeyStart2()
    }

    private fun showSplashBg() {
        val lastImage = PreferencesUtil.lastIntroImgPath
        if (lastImage.isNotEmpty()) {
            _bgImage.value = lastImage
        }
    }

    fun afterPermissionLogic(){
        fun onSuccess(appUpgradeType: AppUpgradeType) {
            Logger.d("onSuccess")
            appUpgradeEvent.value = appUpgradeType
        }

        fun onFailure(t: Throwable) {
            Logger.d("onFailure")
            Logger.e(t, t.message.toString())
            if (t is HttpException) Logger.d(t.response()?.errorBody()?.string())
            context.showToast(R.string.error_network_state_finish)
        }

        val appKey = if(PreferencesUtil.memAppKey.isEmpty().not()) Single.just(PreferencesUtil.memAppKey)
        else {
            fun createReqParam(): ReqAppKey {
                return ReqAppKey(
                    app.fcmRegId,
                    BuildConfig.VERSION_NAME,
                    UUID.randomUUID().toString(),
                    Build.VERSION.SDK_INT.toString(),
                    Constants.REST_DEVICE_OS_TYPE_CODE
                ).apply {
                    Logger.d("reqAppKey params = $this")
                }
            }

            baseApiService.reqAppKey(createReqParam())
                .map {
                    PreferencesUtil.memAppKey = it.appKey
                    Logger.d("it.appKey: ${it.appKey}")
                    it.appKey
                }
        }

        appKey.flatMap(::starter)
            .with()
            .subscribe(::onSuccess, ::onFailure)
            .addTo(disposable)
    }

    private fun appKeyStart2() {
        reqAppKeyAndSave2(PreferencesUtil.memAppKey)
            .flatMap(::reqLoadImageAndSet)
            .with()
            .subscribe({
                reqPermission.call()
            }, {
                Logger.d("okkkkkkkk22222 error: ${it.message}")
                if (it is HttpException) Logger.d(it.response()?.errorBody()?.string())
                reqPermission.call()
            }).addTo(disposable)
    }

    private fun reqAppKeyAndSave2(appKey: String?,): Single<String> {
        Logger.d("okkkkkkkk11111 appKey: $appKey")
        fun createReqParam(): ReqAppKey {
            return ReqAppKey(
                app.fcmRegId,
                BuildConfig.VERSION_NAME,
                UUID.randomUUID().toString(),
                Build.VERSION.SDK_INT.toString(),
                Constants.REST_DEVICE_OS_TYPE_CODE
            ).apply {
                Logger.d("reqAppKey params = $this")
            }
        }

        return if(appKey.isNullOrEmpty().not()) Single.just(appKey)
        else baseApiService.reqAppKey(createReqParam())
            .map {
                PreferencesUtil.memAppKey = it.appKey
                Logger.d("it.appKey: ${it.appKey}")
                it.appKey
            }
    }

    private fun starter(appKey: String): Single<AppUpgradeType> {
        return reqTutorialAndSave(appKey)
            .flatMap(::reqMainPopupAndSave)
            .flatMap(::reqAppVerAndCheck)
    }


    private fun reqLoadImageAndSet(appKey: String): Single<String> {
        return baseApiService.reqLoadImg(appKey).map {
            Logger.d(it.toString())
            when (PreferencesUtil.lastIntroImgPath.isEmpty()) {
                true -> {
                    _bgImage.postValue(it.filePath)
                    PreferencesUtil.lastIntroImgPath = it.filePath
                    Logger.i("load and set image")
                }
                else -> {
                    PreferencesUtil.lastIntroImgPath = it.filePath
                    Logger.i("load image")
                }
            }
            appKey
        }
    }

    private fun reqMainPopupAndSave(appKey: String): Single<String> {
        return baseApiService.reqPopup(appKey)
            .map {
                Logger.d(it.toString())
                _listMainPopUp.value = it
                appKey
            }
    }

    private fun reqTutorialAndSave(appKey: String): Single<String> {
        return baseApiService.reqTutorial(appKey).map { tutorials ->
            Logger.d(tutorials.toString())
            _listTutorial.value = tutorials.sortedBy { it.viewNum }
            _listTutorial.value
        }.map {
            it.forEach {
                val size = SystemUtil.getWindowSize(context)
                Glide.with(context).load(it.filePath)
                    .apply(RequestOptions().override(size.width, size.height))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE).preload()
            }
            appKey
        }
    }

    private fun reqAppVerAndCheck(appKey: String): Single<AppUpgradeType> {
        return baseApiService.reqAppVer(appKey).map {
            Logger.d(it.toString())
            val appVersion = BuildConfig.VERSION_NAME.split(".")
            val curVersion = it.appVer.split(".")
            if(appVersion[0] <= curVersion[0]){
                when {
                    appVersion[0] < curVersion[0] -> {
                        AppUpgradeType.MAJOR
                    }
                    appVersion[1] <= curVersion[1] -> {
                        when {
                            appVersion[1] < curVersion[1] -> {
                                AppUpgradeType.MAJOR
                            }
                            appVersion[2] < curVersion[2] -> {
                                AppUpgradeType.MINER
                            }
                            else -> AppUpgradeType.NONE //음 여기 뭐 주지
                        }
                    }
                    else -> AppUpgradeType.NONE //음 여기 뭐 주지
                }
            }else{
                AppUpgradeType.NONE
            }
        }
    }

    fun onMinorDialogCancelClick() {
        grantedPermission()
    }

}