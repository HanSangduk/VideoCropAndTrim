package com.ram.delivery.other

object Constants  {
    const val REST_ADDRESS_SEARCH_API_KEY = "U01TX0FVVEgyMDIwMDgwNTE3NDc1NTExMDAyODQ="
    const val REST_ADDRESS_LOCATION_API_KEY = "devU01TX0FVVEgyMDIwMDUwNjExNTU0MzEwOTcyNzg="

    const val NOTIFICATION_CHANNEL_NAME = "MelchiChannel"
    const val DOWNLOAD_LOADING_FILE_NAME = "melchi_loadingFile.png"

    const val PAYMENT_SERVER_URL = "/pay/kcp-order"
    const val PAYMENT_GALAXIA_SERVER_URI = "/pay/galaxia-pay"
    const val PAYMENT_ZERO_PAY_URI = "/payment/zeropay/reserve"

    const val AUTH_SERVER_URI = "/identity/identity"

    const val TOPIC_URL_SHOP_NOW = "/sub/shop/now-cnt/"
    const val TOPIC_URL_LIST_NOW = "/sub/shop/nows/list"
    const val TOPIC_URL_SHOP_ORDER = "/sub/shop/order-cnt/"
    const val TOPIC_URL_LIST_ORDER = "/sub/shop/orders/list"

    const val SHOP_LIST_LIMIT_COUNT = 50

    const val REST_DEVICE_OS_TYPE_CODE = "SA"

    const val RECORD_COUNT_PER_PAGE = 20

    const val PERMISSION_REQUEST_CODE = 3000

    const val RESPONSE_CODE_VIEW_CLICK = 21000
    const val RESPONSE_CODE_SAVE_LOG = 21001

    const val DEFAULT_LOCATION_LATITUDE = "37.56647" // 위도, ypos
    const val DEFAULT_LOCATION_LONGITUDE = "126.977963" // 경도, xpos
    const val DEFAULT_LOCATION_JIBUN_ADDRESS = "서울특별시 중구 태평로1가 31"
    const val DEFAULT_LOCATION_STREET_ADDRESS = "서울특별시 중구 세종대로 110"
    const val DEFAULT_LOCATION_DETAIL_ADDRESS = "31"
    const val DEFAULT_LOCATION_ADDRESS = "서울특별시 중구 세종대로 110"
    const val DEFAULT_LOCATION_SEARCH_ADDRESS = "세종대로 110 "
    const val DEFAULT_LOCATION_ZIPCODE = "04533"
    const val DEFAULT_LOCATION_DELIAREANO = "1114010311140550"
    const val DEFAULT_LOCATION_TOWN = "중구 태평로1가"
    const val BASE_KAKAO_COORD_TYPE = "WGS84"
    const val BASE_KAKAO_INPUT_COORD_TYPE = "WTM"

    const val REST_CODE_SUCCESS : String = "S"
    const val REST_CODE_UNKNOWN : String = "NN"

    const val ACTIVITY_CALL_ADDRESS_SEARCH = 100
    const val ACTIVITY_CALL_ADDRESS_SETUP = 200
    const val ACTIVITY_CALL_MENU_INFO = 300
    const val ACTIVITY_CALL_PAYMENT_METHOD = 400
    const val ACTIVITY_CALL_SHOP_REVIEW_WRITE = 500
    const val ACTIVITY_CALL_INQUIRY_REGIST = 700
    const val ACTIVITY_CALL_SIGN_IN_MY_ORDER = 800
    const val ACTIVITY_CALL_MAIN_POP = 900


    const val DIALOG_LOCATION = 1000
    const val DIALOG_CALL = 2000
    const val DIALOG_CALL_CENTER = 3000
}

const val SAFE_TALK_ARGS = "args"
const val SAFE_TALK_RESULT = "result"