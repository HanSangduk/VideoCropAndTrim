package com.ram.delivery.other

import com.google.gson.annotations.SerializedName

enum class AppDivCd(var code: String) {
    ORDER("10"),
    RECEIVE("20")
}

enum class DeviCd(var code: String) {
    ANDROID("SA"),
    IPHONE("SI")
}

// 개선한, 카테고리타입
enum class CateTpCd2 {
    @SerializedName("B") BASE,
    @SerializedName("E") PLAN,
    @SerializedName("A") ALL
}


// 작업 10 자주묻는질문, 20 배너 (최초 배너 리스트 전달시 자동 첫번째거 노출 카운트처리함), 30 팝업(최초 배너 리스트 전달시 자동 첫번째거 노출 카운트처리함)
enum class JobTypeCd {
    @SerializedName("10") QUESTION,
    @SerializedName("20") BANNER,
    @SerializedName("30") POPUP,
}

// runType : 실행 : 10 클릭, 20 노출
enum class RunTypeCd {
    @SerializedName("10") CLICK,
    @SerializedName("20") VIEW,
}

enum class EsseCd(var code: String) {
    REQUIRED("10"),
    OPTION("20")
}

// 검색어구분
enum class SepCd(var code: String) {
    ADDRESS("10"),
    SEARCH("20")
}

// 작업구분
enum class WorkTp(var code: String) {
    REGIST("10"),
    DELETE("20")
}

// 링크구분
enum class LinkCd(var code: String) {
    URL("30"),
    PATH("110")
}

enum class DialogType {
    ALERT,
    CONFIRM
}

enum class PayOrderType {
    ORDER_ZERO,
    ORDER_SUCCESS,
    ORDER_FAIL
}

enum class AppUpgradeType {
    MAJOR,
    MINER,
    NONE
}

enum class MenuModifyType {
    PLUS,
    MINUS
}

enum class PayloadType {
    NOW_VIEW,
    NOW_ORDER
}

enum class TermsType(val code: String) {
    SETUP("10"),
    HOME_FOOTER("20"),
    PAYMENT("30"),
    MEMBER("40")
}

enum class MainTermsType(val code: String, val title: String) {
    BIZ_INFO("10", "사업자정보확인"),
    USE_TERMS("20", "이용약관"),
    USE_FIN("30", "전자금융거래 이용약관"),
    PRIVACY_INFO("40", "개인정보 처리방침")
}

enum class ClickViewType(val code: String) {
    CLICK("C"),
    VIEW("V")
}

enum class SetType(val code: Int) {
    ORDER(10),
    REVIEW(20),
    BOSS(30),
    INQUIRY(40),
    NOTICE(50),
    EVENT(60)
}

enum class SrhArrayCd(val code: String) {
    DISTINCE("10"),
    RATING("20"),
    REVIEW("30"),
    MINIMUM_AMT("40"),
    DELIVERY_COST("50"),
    POPULARITY("60")
}

enum class MyInfoSaveCd(val code: String) {
    NICKNAME("10"),
    RECEIVE_EMAIL("20"),
    RECEIVE_SMS("30"),
    PHONE_NO("40"),
    ADULT_AUTH("50"),
    CHANGE_PASSWORD("60")
}

enum class ReviewSortCd(val code: String) {
    LATEST("10"),
    OLD("20"),
    RATING_HIGH("30"),
    RATING_LOW("40")
}

enum class MenuSaleCd(val code: String) {
    SAILING("10"),
    SOLDOUT("20"),
    STOP_SALE("30")
}

// 주문타입
enum class OrderType(var code: String) {
    CART("10"),
    MENU("20")
}

// 주문내역상태
enum class OrderStatusType(var code: String) {
    READY("10"),
    ACCEPT("20"),
    COMPLETE("30"),
    CANCEL("90")
}

// 로그타입
enum class LogTpCd(var code: String) {
    APP_LOADING("10"),
    HOME("20"),
    FAVORITE("30"),
    CART("40"),
    ORDER_HISTORY("50"),
    MY_ORDER("60"),
    MENU_SHARING("70"),
    SHOP_SHARING("80")
}

// 노출 및 클릭 카운트 처리
enum class JobType(var code: String) {
    ASK("10"),
    BANNER("20"),
    POPUP("30")
}

enum class PushCd(val code: String) {
    NOTICE_DETAIL       ("10"),
    EVENT_DETAIL        ("20"),
    INAPP_URL           ("30"),
    GNB_HOME            ("40"),
    GNB_FAVORITE        ("50"),
    GNB_CART            ("60"),
    GNB_ORDER_HISTORY   ("70"),
    GNB_MY_ORDER        ("80"),
    MY_ORDER_COUPON     ("90"),
    MY_ORDER_REVIEW     ("100"),
    NON_ACTION          ("110")
}

enum class TopicType {
    SHOP_NOW,
    SHOP_ORDER
}

enum class ErrorRest(var code: Int, var message: String) {
    SIGNIN_FAIL (10000, "로그인에 실패하였습니다."),
    SERVER_ERR (50000, "관리자에게 문의하세요."),
    SMSREQ_ERR (40001, "인증번호는 1분간 재발송 할 수 없습니다."),
    SMSCONF_ERR (40002, "인증번호가 틀립니다"),
    SMSOUT_ERR (40003, "인증 시간 초과, 다시 받아주세요."),
    CHGPW_ERR (40004, "현재 비밀번호가 틀립니다."),

    EMAIL_CHK_FAIL (40004, "이미 가입된 이메일 입니다."),

    ORDER_CHK_DELI_FAIL (50001, "배달가능 지역(거리)이 아닙니다."),
    ORDER_CHK_SELL_FAIL (50002, "주문할 수 없는 메뉴 입니다."),
    ORDER_CHK_SALE_FAIL (50003, "영업중인 매장이 아닙니다."),
    ORDER_CHK_MINORDER_FAIL (50004, "최소주문금액 보다 작은 주문금액입니다."),
    ORDER_CHK_SHOP_TP_FAIL (50005, "주문할 수 없는 매장입니다."),
    ORDER_CHK_PRICE_FAIL (50006, "가격에 변동이 있습니다."),
    ORDER_CHK_DIS_PER_FAIL (50007, "메뉴할인 기간이 종료 되었습니다."),
    ORDER_CHK_EMPTY_FAIL (50008, "주문 내역이없습니다."),

    EMPTY_OK (20000, ""),
    SAVE_OK (20001, "저장 되었습니다."),
    UPDATE_OK (20002, "수정 되었습니다."),
    DELETE_OK (20003, "삭제 되었습니다."),
    EMAIL_CHK_OK (20005, "사용 가능한 이메일 입니다."),
    PW_CHK_OK (20006, "비밀번호가 변경되었습니다."),
    CONF_CERT_OK (21001, "인증 되었습니다."),

    NETWORK_UNKNOWN (99999, "통신 상태가 원활하지 않습니다.")
}