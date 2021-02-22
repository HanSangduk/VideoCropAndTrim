package com.ram.delivery

import android.app.Application
import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.google.firebase.messaging.FirebaseMessaging
import com.ram.delivery.utils.LocationTracker
import com.ram.delivery.utils.PreferencesUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DeliveryApplication: Application() {

    companion object{
        lateinit var instance: DeliveryApplication
            private set

        fun appContext() : Context {
            return instance.applicationContext
        }

        val locTracker by lazy {
            LocationTracker(appContext())
        }
    }
    val mAppKey by lazy {
        PreferencesUtil.memAppKey.ifEmpty {
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNMjAxMjE0MDAwMTY3NyIsImRldmlUcENkIjoiU0EiLCJkZXZpT3NWZXIiOiIiLCJhcHBSZWciOiIiLCJhcHBWZXIiOiIiLCJkZXZpSWQiOiI1NDFkODE4Yy0yOTcxLTRkMGEtYmQ2NS1kMDdhNGMyMGM3MGEiLCJhcHBEaXZDZCI6IjEwIiwiaXNMb2dpbiI6dHJ1ZSwiaWF0IjoxNjEzNTIxMDQzLCJleHAiOjI1NjAyMDU4NDN9.lY3KU2cLKJnK5MTnliMN3mwCjb9_j5vn_6lEBe4bNNU"
        }
    }

    var isBackground = false

    private var _fcmRegId: String = ""
    val fcmRegId : String
        get() = _fcmRegId

    override fun onCreate() {
        super.onCreate()
        Log.d("alskaejr","okokok")
        instance = this

        WebView.setWebContentsDebuggingEnabled(
            !(BuildConfig.FLAVOR == "pro" && !BuildConfig.DEBUG)
        )
//        AndroidThreeTen.init(this)
        PreferencesUtil.init(this)
        firebaseToken()
//        startKoin(applicationContext, melchiModule)

//        KakaoSDK.init(KakaoSDKAdapter())

//        Foreground.init(this)
//        Library.init(this)
    }

    private fun firebaseToken() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                return@addOnCompleteListener
//            }
//
//            task.result.let {
//                _fcmRegId = it
//            }
//        }
    }

//    private class KakaoSDKAdapter : KakaoAdapter() {
//        /**
//         * Session Config에 대해서는 default값들이 존재한다.
//         * 필요한 상황에서만 override해서 사용하면 됨.
//         * @return Session의 설정값.
//         */
//        override fun getSessionConfig(): ISessionConfig {
//            return object : ISessionConfig {
//                override fun getAuthTypes(): Array<AuthType> {
//                    return arrayOf(AuthType.KAKAO_LOGIN_ALL)
//                }
//
//                override fun isUsingWebviewTimer(): Boolean {
//                    return false
//                }
//
//                override fun isSecureMode(): Boolean {
//                    return false
//                }
//
//                override fun getApprovalType(): ApprovalType? {
//                    return ApprovalType.INDIVIDUAL
//                }
//
//                override fun isSaveFormData(): Boolean {
//                    return true
//                }
//            }
//        }
//
//        override fun getApplicationConfig(): IApplicationConfig {
//            return IApplicationConfig { appContext() }
//        }
//    }


    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true
        }
    }
}