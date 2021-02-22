package com.ram.delivery.base

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import com.ram.delivery.R
import com.ram.delivery.model.api.res.ResError
import com.ram.delivery.other.Constants
import com.ram.delivery.other.ErrorRest
import com.ram.delivery.utils.StringUtil
import com.ram.delivery.utils.SystemUtil
import com.ram.delivery.utils.extension.with
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.HttpException

open class BaseViewModel(val context: Context): ViewModel() {

    val disposable by lazy { CompositeDisposable() }
    protected var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun resetDisposable(){
        disposable.clear()
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    protected open fun onRetrieveDataSuccess(data: Any) { }
    protected open fun onRetrieveDataError(resError: ResError?) { }

    val dataObserver: DisposableSingleObserver<Any>
        get() = object : DisposableSingleObserver<Any>() {
            override fun onSuccess(t: Any) {
                onRetrieveDataSuccess(t)
                isLoading.value = false
            }

            override fun onError(e: Throwable) {
                val resError: ResError?
                resError = try {
                    val error: HttpException = e as HttpException
                    val errorBody = error.response()?.errorBody()?.string()
                    StringUtil.getResError(errorBody)
                } catch (ex: Exception) {
                    Logger.d("dataObserver >>>>>>>>>>>>> ${ex.message}")
                    println(ex.printStackTrace())
                    ResError(Constants.REST_CODE_UNKNOWN, ErrorRest.NETWORK_UNKNOWN.code, context.getString(
                        R.string.error_network_state), "", "")
                }
                Logger.d("onError >>>>>>>>>>>>> $resError}")
                onRetrieveDataError(resError)
                isLoading.value = false
            }
        }

    fun addDisposable(single: Single<*>, isLoadingDisposable: Boolean = false, observer: DisposableSingleObserver<*> = dataObserver) {
        if(SystemUtil.isConnected(context)) {
            isLoading.value = isLoadingDisposable

            @Suppress("UNCHECKED_CAST")
            disposable.add(
                single.with()
                    .subscribeWith(observer as SingleObserver<Any>) as Disposable
            )
        }
    }
}