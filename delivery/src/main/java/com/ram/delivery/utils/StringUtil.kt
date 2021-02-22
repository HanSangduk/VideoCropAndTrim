package com.ram.delivery.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.Html
import android.util.Base64
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ram.delivery.R
import com.ram.delivery.model.api.res.ResError
import okhttp3.internal.and
import org.jetbrains.anko.runOnUiThread
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class StringUtil {
    companion object {
        fun getDateFormat(pattern : String) : String {
            val date = Date()
            val fdm = SimpleDateFormat(if (pattern.isNullOrBlank()) "yyyy-MM-dd" else pattern)
            return fdm.format(date)
        }

        fun lpad(str : String, length : Int, prefix : String) : String {
            try {
                val sb = StringBuilder()
                for (i in str.length until length) {
                    sb.append(prefix)
                }
                sb.append(str)
                return sb.toString()
            } catch (e: Exception) {
                return ""
            }
        }

        fun rpad(str : String, length : Int, prefix : String) : String {
            try {
                val sb = StringBuilder()
                sb.append(str)
                for (i in str.length until length) {
                    sb.append(prefix)
                }
                return sb.toString()
            } catch (e: Exception) {
                return ""
            }
        }

        fun fromHtml(str: String): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            } else {
                Html.fromHtml(str).toString()
            }
        }

        fun getToday(format:String = "yyyyMMdd") : String {
            val sdf = SimpleDateFormat(format)
            val calendar = Calendar.getInstance()
            return sdf.format(calendar.time)
        }

        fun dayOfWeek() : Int {
            val now = LocalDate.now()
            return now.dayOfWeek.value
        }

        fun isAdult(birth: String): Boolean {
            if(birth.length != 8) return false
            val birthYear = birth.substring(0, 4).toInt()
            val birthMonth = birth.substring(4, 6).toInt()
            val birthDay = birth.substring(6, 8).toInt()
            val current = Calendar.getInstance()
            val currentYear = current[Calendar.YEAR]
            val currentMonth = current[Calendar.MONTH] + 1
            val currentDay = current[Calendar.DAY_OF_MONTH]
            var age = currentYear - birthYear
            if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay) age--
            return age > 18
        }

        fun getPhoneNumber(context: Context, isHyphen: Boolean = false) : String {
            var telNumber = ""
            val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if(mTelephonyManager != null) {
                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    showToast(context, context.getString(R.string.permission_denied_phone))
                } else if(mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN
                            || mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT){
                    showToast(context, context.getString(R.string.toast_msg_empty_usim))
                } else {
                    telNumber = mTelephonyManager.line1Number
                    telNumber = telNumber.replace("+82", "0")
                }
            }
            return telNumber
        }

        fun addPhoneNumberHyphen(num : String, mask : String = "") : String {
            if(num.isNullOrEmpty()) return num
            return try {
                var formatNum = num.replace("-", "")
                formatNum = if (mask.equals("Y")) {
                    formatNum.replace("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
                }else{
                    formatNum.replace("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
                }
                formatNum
            } catch (ex: Exception) {
                num
            }
        }

        fun toHexString(bytes: ByteArray): String {
            val hexString = StringBuilder()
            for (i in bytes.indices) {
                val hex = Integer.toHexString(0xFF and bytes[i].toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            return hexString.toString()
        }

        fun <T> objToString(obj : T) : String {
            val gson = GsonBuilder()
                .disableHtmlEscaping()
                .serializeNulls()
                .create()
            return gson.toJson(obj)
        }

        fun getResError(json: String?) : ResError {
            return try {
                GsonBuilder()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create()
                    .fromJson(json, ResError::class.java)
            } catch(ex: Exception) {
                throw Exception(ex)
            }
        }

        fun <T> strToObj(json: String, clazz: Class<T>) : T {
            return Gson().fromJson(json, clazz)
        }

        fun encryptSHA256(str: String?) : String {
            var SHA: String? = ""
            SHA = try {
                val sh =
                    MessageDigest.getInstance("SHA-256")
                sh.update(str?.toByteArray())
                val byteData = sh.digest()
                val sb = StringBuffer()
                for (i in byteData.indices) {
                    sb.append(
                        Integer.toString(
                            (byteData[i] and 0xff) + 0x100,
                            16
                        ).substring(1)
                    )
                }
                sb.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                null
            }
            return SHA!!
        }

        fun showToast(context: Context, msg : String) {
            context.runOnUiThread {
                Toast.makeText(context, msg, Toast.LENGTH_LONG)
            }
        }

        fun addComma(digit: Int) : String {
            val df = DecimalFormat("###,###")
            return df.format(digit)
        }

        fun addComma(digit: Int, format: String= "") : String {
            val df = DecimalFormat("###,###")
            val str = df.format(digit)
            return if(format.isNullOrBlank()) str else "$str$format"
        }

        fun addComma(digit: Float, format: String= "") : String {
            val df = DecimalFormat("###,###.##")
            val str = df.format(digit)
            return if(format.isNullOrBlank()) str else "$str$format"
        }

        fun addCommaWon(digit: Int) : String {
            val df = DecimalFormat("###,###")
            return df.format(digit) + " 원"
        }
    }
}


fun String.toDisplayAddress(): String {
    val split = this.split(" ")
    if (split.count() > 1) {
        val buildingNo = split[split.lastIndex]
        val roadName = split[split.lastIndex - 1]
        return roadName.plus(buildingNo)
    }
    throw java.lang.Exception("Invalid address")
}