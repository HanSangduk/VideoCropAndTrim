package com.ram.delivery.model.api.res

import com.google.gson.annotations.SerializedName


data class SafeTalkBannerResponse(
    val talkCount: Int,
    val point: Int
)

data class SafetalkCommentListResponse(
    val commentList: List<Comment>
)

data class Comment(
    val addDt: String,
    val commentNo: Int,
    val content: String,
    val isMyComment: Boolean,
    val isWriter: Boolean
)

data class SafeTalkDetailResponse(
    val addDt: String,
    val areaCd: String,
    val legalDispNm: String,
    val cateNo: String,
    val cateNm: String = "ㅋㅋ",
    val commentCount: Int,
    val content: String,
    val imgfNo: String,
    val isWriter: Boolean,
    val isAddedNoti: Boolean,
    val photoList: List<SafeTalkDetailPhotoresponse>,
    val point: Int,
    val readCount: Int,
    val safetalkNo: Int,
    val shopNm: String,
    val shopNo: String,
    val title: String,
    val writerTypeCd: String
)

data class SafeTalkDetailPhotoresponse(
    val fileSeq: Int,
    val rviewImgfNoUrl: String
)


data class SafeTalkListResponse(
    val hasLegend: Boolean,
    val safetalkList: List<SafetalkResponse>,
)

data class SafetalkResponse(
    val addDt: String,
    val legalCd: String,
    val legalDispNm: String,
    val cateNm: String,
    val cateNo: String,
    val commentCount: Int,
    val content: String,
    val imgfNo: String?,
    val isAddedNoti: Boolean,
    val isWriter: Boolean,
    val photoList: List<SafeTalksPhotoResponse>?,
    val point: Int,
    val readCount: Int,
    val safetalkNo: Int,
    val shopNm: String,
    val shopNo: String,
    val title: String,
    val writerTypeCd: String,
    val squareBracketsCd: String,
    val isNew: Boolean,

    )

data class SafeTalksPhotoResponse(
    val fileSeq: Int,
    val rviewImgfNoUrl: String,
)