@file:Suppress("SpellCheckingInspection")

package com.ram.delivery.model.api

import com.google.gson.JsonElement
import com.ram.delivery.model.api.req.SafeTalkNotificationRequest
import com.ram.delivery.model.api.req.SafetalkCommentRequest
import com.ram.delivery.model.api.req.SafetalkSaveCommentRequest
import com.ram.delivery.model.api.req.SafetalkSaveRequest
import com.ram.delivery.model.api.res.*
import com.ram.delivery.other.Command
import io.reactivex.Single
import retrofit2.http.*

interface SafeTalkApiService {
    @GET("/api/order/safetalk/talk-list")
    fun getSafeTalks(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query(Command.LEGAL_CD) legalCd: String,
        @Query(Command.AREA_TYPE_CD) areaTypeCd: String,
        @Query(Command.LAST_NO) lastNo: Int,
        @Query(Command.LIMIT) limit: Int,
    ): Single<SafeTalkListResponse> // 안심톡 리스트

    @GET("/api/order/safetalk/mytalk-list")
    fun getMySafeTalks(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("lastNo") lastNo: Int, // 마지막 조회된 안심톡 rowNo
        @Query("limit") limit: Int,  // 조회 목록 갯수
        @Query("type") type: String, //내가 쓴,알림 설정한,댓글 작성한 안심톡 모두 보기 : A, 내가 쓴 안심톡 : M
    ): Single<SafeTalkListResponse> // 나의 안심톡 리스트

    @GET("/api/order/safetalk/mytalk-info")
    fun getMySafeTalk(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("safetalkNo") safetalkNo: Int, //나의 안심톡 게시글 번호
    ): Single<SafeTalkDetailResponse> // 나의 안심톡 상세

    @GET("/api/order/safetalk/talk-info")
    fun getSafeTalk(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("safetalkNo") safetalkNo: Int,
    ): Single<SafeTalkDetailResponse> // 안심톡 상세 내용

    @POST("/api/order/safetalk/talk-remove")
    fun delSafeTalk(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
    ): Single<JsonElement> // 안심톡 상세 삭제

    @POST("/api/order/safetalk/talk-save")
    fun regSafeTalk(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body body: SafetalkSaveRequest,
    ): Single<ResBase> // 안심톡 등록

    @GET("/api/order/safetalk/comment-list")
    fun getReplies(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("lastNo") lastNo: Int,
        @Query("limit") limit: Int,
        @Query("safetalkNo") safetalkNo: Int,
    ): Single<SafetalkCommentListResponse> // 안심톡 댓글 리스트

    @POST("/api/order/safetalk/comment-remove")
    fun delReply(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body body: SafetalkCommentRequest,
    ): Single<ResBase> // 안심톡 댓글 삭제

    @POST("/api/order/safetalk/comment-save")
    fun regReply(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body body: SafetalkSaveCommentRequest,
    ): Single<ResBase> // 안심톡 댓글 등록

    @POST("/api/order/safetalk/mytalk-comment-save")
    fun regReplyMyTalk(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body body: SafetalkSaveCommentRequest,
    ): Single<ResBase> // 나의 안심톡 댓글 등록

    @GET("/api/order/safetalk/link-banner")
    fun getBanner(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query(Command.LEGAL_CD) legalCd: String,
    ): Single<SafeTalkBannerResponse> // 배너

    @POST("/api/order/safetalk/noti-reg")
    fun regNotification(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body body: SafeTalkNotificationRequest,
    ): Single<ResBase> // 알림 등록

    @POST("/api/order/safetalk/noti-remove")
    fun delNotification(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body body: SafeTalkNotificationRequest,
    ): Single<ResBase> // 알림 해제

}