package com.ram.delivery.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.ram.delivery.model.api.res.ResSignIn
import com.ram.delivery.other.Command

object PreferencesUtil {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(Command.PREF_NAME, Context.MODE_PRIVATE)
        Log.d("alskaejr","prefs = $prefs")
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isFirstRun : Boolean
        get() = prefs.getBoolean(Command.PREF_IS_FIRST_RUN, true)
        set(value) = prefs.edit() {
            it.putBoolean(Command.PREF_IS_FIRST_RUN, value).apply()
        }

    var isSignIn : Boolean
        get() = prefs.getBoolean(Command.PREF_SIGN_IN, false)
        set(value) = prefs.edit() {
            it.putBoolean(Command.PREF_SIGN_IN, value).apply()
        }

    var memAppKey : String
        get() = prefs.getString(Command.PREF_MEMBER_APP_KEY, "") ?: ""
        set(value) = prefs.edit() {
            it.putString(Command.PREF_MEMBER_APP_KEY, value).apply()
        }

    var memEmail : String?
        get() = prefs.getString(Command.PREF_MEMBER_EMAIL, "")
        set(value) = prefs.edit() {
            it.putString(Command.PREF_MEMBER_EMAIL, value).apply()
        }

    var memName : String?
        get() = prefs.getString(Command.PREF_MEMBER_NAME, "")
        set(value) = prefs.edit() {
            it.putString(Command.PREF_MEMBER_NAME, value).apply()
        }

    var memAdultCd : String?
        get() = prefs.getString(Command.PREF_MEMBER_ADULT_CODE, "")
        set(value) = prefs.edit() {
            it.putString(Command.PREF_MEMBER_ADULT_CODE, value).apply()
        }

    var phoneNo : String?
        get() = prefs.getString(Command.PREF_MEMBER_PHONE_NO, "")
        set(value) = prefs.edit() {
            it.putString(Command.PREF_MEMBER_PHONE_NO, value).apply()
        }

    var popupTodayNotView : Int
        get() = prefs.getInt(Command.PREF_POPUP_TODAY_NOT_VIEW, 0)
        set(value) = prefs.edit() {
            it.putInt(Command.PREF_POPUP_TODAY_NOT_VIEW, value).apply()
        }

    var lastIntroImgPath : String
        get() = prefs.getString(Command.PREF_LAST_INTRO_IMAGE_PATH, "")?: ""
        set(value) = prefs.edit {
            it.putString(Command.PREF_LAST_INTRO_IMAGE_PATH, value).apply()
        }


    fun setSignIn(data: ResSignIn) {
        isSignIn = true
        memAppKey = data.appKey
        memEmail = data.email
        memName = data.name
        memAdultCd = data.adtAuthCd
        phoneNo = data.phoneNo
    }

    fun setSignOut() {
        isSignIn = false
        memAppKey = ""
        memEmail = ""
        memName = ""
        memAdultCd = ""
        phoneNo = ""
    }

}