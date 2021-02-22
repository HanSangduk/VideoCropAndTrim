package com.ram.delivery.model.api.req

import com.google.gson.annotations.SerializedName


data class SafeTalkNotificationRequest(
    val safetalkNo: Int
)


data class SafetalkSaveCommentRequest(
    val content: String,
    val safetalkNo: Int
)


data class SafetalkCommentRequest(
    val commentNo: Int,
    val safetalkNo: Int
)


data class SafetalkSaveRequest(
    val legalCd: String?,
    val cateNo: String,
    val content: String,
    val imgfNo: String,
    val point: Int,
    val shopNm: String,
    val shopNo: String,
    val title: String,
    val writerTypeCd: String
)