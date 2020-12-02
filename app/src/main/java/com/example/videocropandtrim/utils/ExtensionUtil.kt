package com.example.videocropandtrim.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.collection.LruCache
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.videocropandtrim.R
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

fun DateTime.pepleKnowStr(context: Context, dateFormat: String = "yyyy-MM-dd"): String {
    val now = DateTime.now(DateTimeZone.getDefault())
    val days = Days.daysBetween(this.toLocalDate(), now.toLocalDate()).days
    return when (days) {
        0 -> context.getString(R.string.today)
        1 -> context.getString(R.string.yesterday)
        in 2..10 -> String.format(context.getString(R.string.days_ago), days.toString())
        else -> {
            when (now.year - this.year) {
                0 -> this.toString(context.getString(R.string.this_year_data_format))
                else -> this.toString(context.getString(R.string.this_year_over_data_format))
            }
        }
    }
}

fun Long.formatPepleknowTime(): String {
    var seconds = Math.round(this.toDouble() / 1000000L)
    val hours = TimeUnit.SECONDS.toHours(seconds)
    if (hours > 0) seconds -= TimeUnit.HOURS.toSeconds(hours)
    val minutes = if (seconds > 0) TimeUnit.SECONDS.toMinutes(seconds) else 0
    if (minutes > 0) seconds -= TimeUnit.MINUTES.toSeconds(minutes)

    return if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds).intern()
    else String.format("%02d:%02d", minutes, seconds)
}

fun Long.formatTimeUsec(): String {
    // val millTimes = org.joda.time.Duration.millis(this/1000L)
    val timeMill = DateTime(this.absoluteValue / 1000L, DateTimeZone.UTC)
    return if (this >= 0) {
        "+${timeMill.toString("ss.SS")}"
    } else {
        "-${timeMill.toString("ss.SS")}"
    }
}

fun Any?.toStringNullable(): String? {
    if (this == null) {
        return null
    } else {
        return this.toString()
    }
}

//fun MemoryFile.unzip(outputFolder: File): Int {
//    if (!outputFolder.exists()) {
//        outputFolder.mkdir()
//    } else {
//        outputFolder.deleteAll()
//    }
//
//    var count = 0
//    var fis: InputStream? = null
//    var zis: ZipArchiveInputStream? = null
//    var fos: FileOutputStream? = null
//    try {
//        var buffer = ByteArray(8192)
//        fis = this.inputStream
//        zis = ZipArchiveInputStream(fis, "UTF-8", true) // this supports non-USACII names
//        var entry: ArchiveEntry?
//        while (true) {
//            entry = zis.getNextEntry()
//            if (entry == null) break
//            val file = File(outputFolder, entry.getName())
//            if (entry.isDirectory()) {
//                file.mkdirs()
//            } else {
//                count++
//                file.getParentFile().mkdirs()
//                fos = FileOutputStream(file)
//                var read: Int
//                while (true) {
//                    val read = zis.read(buffer, 0, buffer.size)
//                    if (read == -1) break
//                    fos.write(buffer, 0, read)
//                }
//                fos.close()
//                fos = null
//            }
//        }
//    } finally {
//        try {
//            zis?.close()
//        } catch (e: Exception) {}
//        try {
//            fis?.close()
//        } catch (e: Exception) {}
//        try {
//            fos?.close()
//        } catch (e: Exception) {}
//    }
//    return count
//}

fun File.deleteAll() {
    val childFileList = this.listFiles()
    if (this.exists()) {
        for (childFile in childFileList) {
            if (childFile.isDirectory) {
                childFile.deleteAll() // 하위 디렉토리
            } else {
                childFile.delete() // 하위 파일
            }
        }
        this.delete()
    }
}

val ddddCache = LruCache<Any, Any>(4 * 1024 * 1024)

inline fun <reified T> Observable<T>.memoizedMap() = this.map {}

fun <T> List<T>.upsert(value: T, finder: (T) -> Boolean) =
    indexOfFirst(finder).let { index -> if (index >= 0) copy(index, value) else this + value }

fun <T> List<T>.upserts(value: List<T>, keySelector: (T) -> Any?) =
    this.filter { fisrst ->
        value.indexOfFirst { second -> keySelector(fisrst) == keySelector(second) } > -1
    } + value

fun <T> List<T>.copy(i: Int? = null, value: T? = null): List<T> =
    toMutableList().apply { i?.let { set(i, value!!) } }

inline fun <T> List<T>.switch(from: Int, to: Int): List<T> =
    toMutableList().map {
        when {
            this.indexOf(it) == from -> this[to]
            this.indexOf(it) == to -> this[from]
            else -> it
        }
    }

inline fun <T> List<T>.delete(filter: (T) -> Boolean): List<T> =
    toMutableList().apply { removeAt(indexOfFirst(filter)) }

inline fun <T> List<T>.add(item: T, index: Int = 0): List<T> =
    toMutableList().apply { add(index, item) }

fun CoordinatorLayout.showLongSnackbar(@StringRes stringRes: Int) {
    Snackbar.make(this, stringRes, Snackbar.LENGTH_LONG).show()
}

fun ViewGroup.asSequence(): Sequence<View> = (0..childCount).asSequence().map { getChildAt(it) }

inline fun <T> T.whenNull(f: (T) -> T): T {
    return (this ?: f(this))!!
}

inline fun BigDecimal.toRoundInt(): Int {
    this.setScale(8, RoundingMode.HALF_UP)
    return this.toInt()
}

inline fun Int.toBoolean(): Boolean = (this > 0)

fun <T> Flowable<T>.with(): Flowable<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.with(): Single<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.with(): Observable<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> List<T>.addAll(items: List<T>): List<T> {
    val array = arrayListOf<T>()
    array.addAll(this)
    array.addAll(items)
    return array
}

fun Int.toTimeStr(): String = if (this < 10) "0$this" else "$this"

fun Long.toPlayTime(isShowHour: Boolean = false, timePer: Int = 1): String {

    val min = (((this / timePer) % 3600) / 60).toInt().toTimeStr()
    val seconds = (((this / timePer) % 3600) % 60).toInt().toTimeStr()
    val hours = if (isShowHour) "${((this / timePer) / 3600).toInt().toTimeStr()}:" else ""

    return "$hours$min:$seconds"
}

fun DateTime.toPatternString(pattern: String = "yyyy-MM-dd HH:mm") =
    this.toString(DateTimeFormat.forPattern(pattern))

fun Float.roundValue(digit: Double = 2.0): Float {
    val num = 10.0.pow(digit)
    return ((this * num).roundToInt() / num).toFloat()
}

fun Float.percentValue(percent: Float) = (this * percent / 100).roundValue()

fun Int.unitFormat() : String = if(this in 0 until 10) "0$this" else this.toString()

fun Long.convertSecondsToTime(timeUnit: TimeUnit = TimeUnit.MILLISECONDS): String {
    val time = when(timeUnit){
        TimeUnit.MILLISECONDS -> this / 10f.pow(3)
        TimeUnit.MICROSECONDS -> this / 10f.pow(6)
        TimeUnit.MINUTES -> this * 60
        TimeUnit.HOURS -> this * 3600
        else -> this
    }.toLong()
    var timeStr = ""
    var hour = 0
    var min = 0
    var second = 0
    return if(time <= 0) "00:00"
    else {
        min = (time / 60).toInt()
        if(min < 60) {
            second = (time % 60).toInt()
            "00:${min.unitFormat()}:${second.unitFormat()}"
        }else{
            hour = min / 60
            if(hour > 99) "99:59:59"
            else {
                min %= 60
                second = (time - hour * 3600 - min * 60).toInt()
                "${hour.unitFormat()}:${min.unitFormat()}:${second.unitFormat()}"
            }
        }
    }
}



fun Context.convertDpToPx(dp: Float): Int {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return (dp * scale).fastRound()
}

fun Context.convertDpToPx(dp: Int): Int {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return (dp * scale).fastRound()
}


fun Context.convertPxToDp(px: Float): Float {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return px / scale
}


fun Context.convertPxToDp(px: Int): Float {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return px / scale
}

fun Context.getDeviceWidth(): Int {
    return resources.displayMetrics.widthPixels
}
fun Context.getDeviceHeight(): Int {
    return resources.displayMetrics.heightPixels
}