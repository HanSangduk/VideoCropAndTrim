package com.ram.delivery.utils.extension

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

@Suppress("SpellCheckingInspection")
fun EditText.filterEmoji() {
    this.filters = arrayOf(FilterUtil.EMOJI_FILTER)
}

fun EditText.applyFilter(vararg filters: MyInputFilter) {
    val array = Array<InputFilter>(filters.count()) {
        when (val filterType = filters[it]) {
            is MyInputFilter.Emoji -> FilterUtil.EMOJI_FILTER
            is MyInputFilter.MaxLength -> InputFilter.LengthFilter(filterType.length)
        }
    }
    this.filters = array
}

sealed class MyInputFilter {
    object Emoji : MyInputFilter()
    data class MaxLength(val length: Int = 8) : MyInputFilter()
}

fun EditText.showKeyboard() {
    requestFocus()
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun EditText.hideKeyboard() {
    clearFocus()
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

object FilterUtil {
    var EMOJI_FILTER =
        label@ InputFilter { source: CharSequence, start: Int, end: Int, _: Spanned?, _: Int, _: Int ->
            var index = start
            while (index < end) {
                val type = Character.getType(source[index])
                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                    return@InputFilter ""
                }
                index++
            }
            null
        }
}