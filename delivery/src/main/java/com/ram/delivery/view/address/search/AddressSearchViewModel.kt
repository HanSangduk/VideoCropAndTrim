package com.ram.delivery.view.address.search

import android.content.Context
import android.view.inputmethod.EditorInfo
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.module.BaseLoadMoreModule
import com.orhanobut.logger.Logger
import com.ram.delivery.DeliveryApplication
import com.ram.delivery.R
import com.ram.delivery.base.BaseViewModel
import com.ram.delivery.model.api.BaseApiService
import com.ram.delivery.model.api.JusoApiService
import com.ram.delivery.model.api.KakaoApiService
import com.ram.delivery.model.api.res.*
import com.ram.delivery.model.sub.toResAddressList
import com.ram.delivery.other.Constants
import com.ram.delivery.utils.SingleLiveEvent
import com.ram.delivery.utils.StringUtil.Companion.showToast
import com.ram.delivery.utils.extension.showToast
import com.ram.delivery.utils.extension.with
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.sentry.Sentry
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddressSearchViewModel @Inject constructor(
        val baseApi: BaseApiService,
        val jusoApi: JusoApiService,
        val kakaoMapApi: KakaoApiService,
        @ApplicationContext context: Context
) : BaseViewModel(context) {


    val onSuccessEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val saveDelivEvent: SingleLiveEvent<ResAddress> = SingleLiveEvent()
    val setupEvent: SingleLiveEvent<KakaoAddressDocument> = SingleLiveEvent()
    val setupCurEvent: SingleLiveEvent<ResAddress> = SingleLiveEvent()
    val addressHistoryItemClickEvent: SingleLiveEvent<ResDeliv> = SingleLiveEvent()
    val addressSearchItemClickEvent: SingleLiveEvent<ResAddress> = SingleLiveEvent()

    val closeEvent: SingleLiveEvent<Unit> = SingleLiveEvent()

    var delItemPos = -1
    val delItemEvent: SingleLiveEvent<Int> = SingleLiveEvent()

    val searchWord = ObservableField("")
    val searchTitle = ObservableField("")

    val inputClearBtnVisible = ObservableBoolean(false)
    val isHistory = ObservableBoolean(false)
    val isSearch = ObservableBoolean(false)
    val isEmptyList = ObservableBoolean(true)
    val isRefreshing = ObservableBoolean(false)
    val historyList = ObservableArrayList<ResDeliv>()

    val searchList = ObservableArrayList<ResAddress>()

    val addressSearchPage = ObservableField(0)
    private val addressSearchCurPageCnt = ObservableField(0)
    private val addressSearchTotalPageCnt = ObservableField(0)
    private val addressCurrentSearchText = ObservableField("")

    private val _progressbar = MutableLiveData(false)
    val progressbar: LiveData<Boolean> = _progressbar

    private val _enableBtn = MutableLiveData(true)
    val enableBtn: LiveData<Boolean> = _enableBtn

    private val _navigateToAddressSetup = MutableLiveData<ResAddress>()
    val navigateToAddressSetup: LiveData<ResAddress> = _navigateToAddressSetup

    private var locationDispose = CompositeDisposable()
    private val timeoutDisposable = CompositeDisposable()

    private val app: DeliveryApplication by lazy {
        DeliveryApplication.instance
    }

//    private val smartLocation: SmartLocation.LocationControl by lazy {
//        SmartLocation.with(getApplication()).location().oneFix()
//    }

    init {
        baseApi.reqDeliv(app.mAppKey)
                .with()
                .subscribe({
                    if (it.isNotEmpty()) isHistory.set(true)
                    historyList.clear()
                    historyList.addAll(it)
                }, {
                    Logger.d("baseApi.reqDeliv error ${it.message}")
                }).addTo(disposable)
    }

    fun onSearchWordClearClick() {
        searchWord.set("")
    }

    fun onEditorAction(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            search(searchWord.get())
        }
        return false
    }

    fun onTextChanged(s: CharSequence) {
        inputClearBtnVisible.set(s.isNotEmpty())
    }

    private fun search(text: String?) {
        if (text.isNullOrBlank()) {
            showToast(
                context,
                context.getString(R.string.toast_msg_empty_search_text)
            )
        } else {
            isSearch.set(true)
            isHistory.set(false)
            searchTitle.set(text)
            addressSearchPage.set(0)
            getAddress(text)
        }
    }

    private fun getAddress(text: String, currentPage: Int = ((addressSearchPage.get() as Int) + 1), countPerPage: Int = 20, loadMoreModule: BaseLoadMoreModule? = null) {
        addressCurrentSearchText.set(text)

        jusoApi.getAddress(getJusoQueryMap(text, currentPage, countPerPage))
                .flatMap { jusoAddressRes ->
                    addressSearchPage.set(jusoAddressRes.results.commonResponse.currentPage.toInt())
                    addressSearchCurPageCnt.set(jusoAddressRes.results.commonResponse.currentPage.toInt() * jusoAddressRes.results.commonResponse.countPerPage.toInt())
                    addressSearchTotalPageCnt.set(jusoAddressRes.results.commonResponse.totalCount.toInt())
                    jusoAddressRes.results.commonResponse.totalCount //1923
                    jusoAddressRes.results.commonResponse.currentPage  //1
                    jusoAddressRes.results.commonResponse.countPerPage //10
                    kakaoMapApi.reqAddress(text)
                            .map { jusoAddressRes.results.jusoResponse.toResAddressList() }
                }.with()
                .subscribe({
                    if(currentPage == 1) {
                        isEmptyList.set(
                            it.isEmpty()
                        )
                        searchList.clear()
                    }
                    searchList.addAll(it)

                    if(it.isNullOrEmpty()){
                        return@subscribe
                    }
                    isHistory.set(false)
                    isSearch.set(true)

                    if((addressSearchCurPageCnt.get() ?: 0) >= (addressSearchTotalPageCnt.get() ?: 0)) loadMoreModule?.loadMoreEnd()
                    else loadMoreModule?.loadMoreComplete()

                }, { throwable ->
                    Logger.d("kakaoMapApi error? ${throwable.message}")
                    loadMoreModule?.let {
                        it.isEnableLoadMore = true
                        it.loadMoreFail()
                    }
                }).addTo(disposable)
    }



    fun addressPageLoadMore(loadMoreModule: BaseLoadMoreModule){
        addressCurrentSearchText.get()?.let {
            getAddress(
                    text = it,
                    loadMoreModule = loadMoreModule
            )
        }
    }

    fun getJusoQueryMap(text: String, currentPage: Int = ((addressSearchPage.get() as Int) + 1), countPerPage: Int = 20) = hashMapOf<String, Any?>(
        "confmKey" to context.getString(R.string.key_juso),
        "currentPage" to currentPage,
        "countPerPage" to countPerPage,
        "keyword" to text,
        "resultType" to "json"
    )

    fun currentLocationSearch(){
        startTimeoutTimer()
        _enableBtn.value = false
        _progressbar.value = true

        DeliveryApplication.locTracker.let { loc ->
            fun flatMapper(data: ResKakaoCoordAddress): Single<ResAddress> {
                val keyword = data.documents.first().road_address?.address_name
                    ?: data.documents.first().address.address_name

                return jusoApi.getAddress(getJusoQueryMap(keyword))
                    .map {
                        it.results.jusoResponse.toResAddressList(
                            xPos = loc.longitude.toString(), yPos = loc.latitude.toString()
                        ).first()
                    }
            }

            fun onError(throwable: Throwable) {
                timeoutDisposable.clear()
                if (throwable is HttpException) {
                    Logger.e(
                        throwable,
                        throwable.message(),
                        throwable.response()?.errorBody()?.string()
                    )
                }
                Sentry.captureException(throwable)
                context.showToast(context.getString(R.string.error_network_state))
            }

            fun onSuccess(data: ResAddress) {
                _navigateToAddressSetup.value = data
                timeoutDisposable.clear()
                Logger.d(data.toString())
            }

            kakaoMapApi.reqCoord2Address(loc.longitude.toString(), loc.latitude.toString())
                .flatMap(::flatMapper)
                .with()
                .doOnSuccess { hideLoading() }
                .doOnError { hideLoading() }
                .subscribe(::onSuccess, ::onError)
                .addTo(locationDispose)
        }
    }

    private fun startTimeoutTimer() {
        Single.timer(5000, TimeUnit.MILLISECONDS)
            .with()
            .subscribeBy {
                if (_progressbar.value!!) {
                    locationDispose.clear()
                    hideLoading()
                    context.showToast(context.getString(R.string.error_network_state))
                }
            }.addTo(timeoutDisposable)
    }

    fun onClickDeleteAll() {
        if (historyList.isNotEmpty()) {
            delItemPos = 99999
            addDisposable(
                baseApi.reqDelAllSrhAddr(app.mAppKey)
            )
        }
    }

    fun onClickItemDelete(item: ResDeliv) {
        delItemPos = item.deliSeq
        addDisposable(
            baseApi.reqDelSrhAddr(
                app.mAppKey,
                item.deliSeq
            )
        )
    }

    private fun hideLoading() {
        _enableBtn.value = true
        _progressbar.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timeoutDisposable.clear()
    }


    override fun onRetrieveDataSuccess(data: Any) {
        when (data) {
            is ResKakaoAddressSearch -> {
                setupEvent.value = data.documents[0]
            }

            is ResLocationSearch -> {
                if (data.results.common.errorCode == "0") {
                    val address = data.results.juso[0]
                }
            }

            is ResBase -> {
                if (data.code == 20000) {
                    if (delItemPos == 99999) {
                        historyList.clear()
                        isHistory.set(false)
                    }else{
                        historyList.remove(
                            historyList.find { it.deliSeq == delItemPos }
                        )
                        if(historyList.isEmpty())
                            isHistory.set(false)
                    }
                    delItemEvent.value = delItemPos
                    delItemPos = -1
                } else if (data.code == 20011) {
                    saveDelivEvent.value = ResAddress(
                        Constants.DEFAULT_LOCATION_STREET_ADDRESS,
                        1,
                        Constants.DEFAULT_LOCATION_DELIAREANO,
                        Constants.DEFAULT_LOCATION_JIBUN_ADDRESS,
                        Constants.DEFAULT_LOCATION_LONGITUDE,
                        Constants.DEFAULT_LOCATION_LATITUDE,
                        Constants.DEFAULT_LOCATION_ZIPCODE,
                        null,
                        null,
                        null,
                        null,
                        Constants.DEFAULT_LOCATION_DETAIL_ADDRESS
                    )
                }
                if (data.message.isNotEmpty())
                    context.showToast(data.message)
            }
            else -> {
            }
        }
    }

    override fun onRetrieveDataError(resError: ResError?) {
        resError?.message?.let { context.showToast(it) }
    }

    fun addressHistoryClick(resDeliv: ResDeliv){
        addressHistoryItemClickEvent.value = resDeliv
    }
    fun addressSearchClick(resAddress: ResAddress){
        addressSearchItemClickEvent.value = resAddress
    }
}
