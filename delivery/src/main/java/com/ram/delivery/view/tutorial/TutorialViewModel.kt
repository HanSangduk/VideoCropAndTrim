package com.ram.delivery.view.tutorial

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.ram.delivery.DeliveryApplication
import com.ram.delivery.base.BaseViewModel
import com.ram.delivery.model.api.BaseApiService
import com.ram.delivery.model.api.req.TermsItem
import com.ram.delivery.model.api.res.ResTerms
import com.ram.delivery.model.api.res.ResTutorial
import com.ram.delivery.other.TermsType
import com.ram.delivery.utils.PreferencesUtil
import com.ram.delivery.utils.SingleLiveEvent
import com.ram.delivery.utils.extension.showToast
import com.ram.delivery.utils.extension.with
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    val baseApiService: BaseApiService,
    @ApplicationContext context: Context
) : BaseViewModel(context){

    val adapterEvent: SingleLiveEvent<Unit> = SingleLiveEvent()
    val termsViewEvent: SingleLiveEvent<ResTerms> = SingleLiveEvent()
    val startEvent: SingleLiveEvent<Unit> = SingleLiveEvent()

    var isBtnEnable = MutableLiveData<Boolean>(false)
    var isBottomVisibility = MutableLiveData<Boolean>(false)

    var tutorialList: ArrayList<ResTutorial>? = null

    val termsList = ObservableArrayList<ResTerms>()
    val listMutableLiveData = MutableLiveData<List<ResTerms>>()

    private var isCheckAll = false

    private val app by lazy {
        DeliveryApplication.instance
    }

    init {
        baseApiService.reqTerms(
            app.mAppKey,
            TermsType.SETUP.code
        ).with()
        .subscribe({
            listMutableLiveData.value = it
        }, {
            context.showToast("${it?.message}")
        }).addTo(disposable)

//        addDisposable(restClient.reqTerms(getApplication<MelchiApplication>().getAppKey(), TermsType.SETUP.code), dataObserver)
    }

    fun onClickTermsView(item: ResTerms) {
        termsViewEvent.value = item
    }

    fun onClickAgreeAll() {
        isCheckAll = !isCheckAll
        for(term: ResTerms in termsList) {
            term.isAgree = isCheckAll
        }
        adapterEvent.call()
        chkRequired()
    }

    fun onClickTermsAgree(item: ResTerms) {
        item.isAgree = !item.isAgree
        chkRequired()
    }

    fun onClickStart() {
        val agreeTerms = ArrayList<TermsItem>()
        for(term: ResTerms in termsList) {
            val termsItem = TermsItem(term.casNo, if(term.isAgree) "Y" else "N")
            agreeTerms.add(termsItem)
        }
        baseApiService.reqSaveTerms(
            app.mAppKey,
            agreeTerms,
            TermsType.SETUP.code
        ).with()
            .subscribe({
                PreferencesUtil.isFirstRun = false
                startEvent.call()
            }, {
                context.showToast("${it?.message}")
            }).addTo(disposable)
    }

    private fun chkRequired() {
        var isRequired = true
        for(term: ResTerms in termsList) {
            if(term.casNo != "M") {
                if(!term.isAgree) {
                    isRequired = false
                    break
                }
            }
        }
        isBtnEnable.value = isRequired
    }

    fun updateData(list: List<ResTerms>) {
        termsList.clear()
        termsList.addAll(list)
    }

}