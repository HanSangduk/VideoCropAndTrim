package com.ram.delivery.other

object Command {
    const val PREF_NAME = "MELCHI"
    const val PREF_SIGN_IN = PREF_NAME + "_PREF_SIGN_IN"
    const val PREF_MEMBER_APP_KEY = PREF_NAME + "_PREF_MEMBER_APP_KEY"
    const val PREF_MEMBER_EMAIL = PREF_NAME + "_PREF_MEMBER_EMAIL"
    const val PREF_MEMBER_NAME = PREF_NAME + "_PREF_MEMBER_NAME"
    const val PREF_MEMBER_ADULT_CODE = PREF_NAME + "_PREF_MEMBER_ADULT_CODE"
    const val PREF_MEMBER_PHONE_NO = PREF_NAME + "_PREF_MEMBER_PHONE_NO"
    const val PREF_IS_FIRST_RUN = PREF_NAME + "_PREF_IS_FIRST_RUN"
    const val PREF_POPUP_TODAY_NOT_VIEW = PREF_NAME + "_PREF_POPUP_TODAY_NOT_VIEW"
    const val PREF_LAST_INTRO_IMAGE_PATH = PREF_NAME + "_PREF_LAST_INTRO_IMAGE_PATH"

    const val PARAMETER_X_AUTH_TOKEN = "X-AUTH-TOKEN"
    const val PARAMETER_X_AUTHORIZATION = "Authorization"

    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

    const val AREA_CD = "areaCd"
    const val LEGAL_CD = "legalCd"
    const val AREA_TYPE_CD = "areaTypeCd" // 전국 모든안심톡 = 'G’lobal | 주변매장( 동으로 검색) 'A’round
    const val LAST_NO = "lastNo" // 마지막 조회된 안심톡 게시물 번호
    const val LIMIT = "limit" // 조회 목록 갯수
}
