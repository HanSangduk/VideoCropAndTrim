package com.ram.delivery.model.api

import com.ram.delivery.DeliveryApplication
import com.ram.delivery.model.api.req.ReqAppKey
import com.ram.delivery.model.api.req.ReqSaveDeliv
import com.ram.delivery.model.api.req.TermsItem
import com.ram.delivery.model.api.res.*
import com.ram.delivery.other.CateTpCd2
import com.ram.delivery.other.Command
import com.ram.delivery.other.JobTypeCd
import com.ram.delivery.other.RunTypeCd
import io.reactivex.Single
import retrofit2.http.*

interface BaseApiService {


    /* ==========================================
     * cust-controller Cust Controller
    =============================================*/

    // 앱키발급
    @POST("/api/order/main/app-key")
    fun reqAppKey(
        @Body appKeyRequest: ReqAppKey
    ): Single<ResAppKey>

    //    // 앱레지스트 변경
//    @POST("api/order/cust/app-reg")
//    fun reqAppReg(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqAppReg
//    ): Single<ResBase>
//
//    // 1:1문의분류가져오기
//    @GET("api/order/cust/ask-div")
//    fun reqAskDiv(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<List<ResCode>>
//
//    // 아이디 찾기
//    @GET("/api/order/cust/find-id")
//    fun reqFindId(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("certYn") certYn: String,
//        @Query("phoneNo") phoneNo: String
//    ): Single<ResFindId>
//
//    // 비밀번호 찿기 - 비밀번호 변경
//    @GET("/api/order/cust/chk-email-phone")
//    fun reqChkEmailPhone(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("email") email: String,
//        @Query("phoneNo") phoneNo: String
//    ): Single<ResBase>
//
//    // 비밀번호 찿기 - 비밀번호 변경
//    @GET("/api/order/cust/chg-find-pw")
//    fun reqChgFindPw(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("chgPw") chgPw: String,
//        @Query("email") email: String
//    ): Single<ResFindId>
//
//    // 회원로그인후 - 비밀번호 변경
//    @GET("/api/order/cust/chg-pw")
//    fun reqChgPw(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("chgPw") chgPw: String,
//        @Query("pw") pw: String
//    ): Single<ResFindId>

    @GET("/api/order/cust/info-lastaddr")
    fun reqInfoLastAddr(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<LastAddressResponse>

    // 회원배송지 전체 삭제
    @GET("/api/order/cust/del-All-srhAddr")
    fun reqDelAllSrhAddr(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ResBase>
//
//    // 회원배송지 개별 삭제
//    @GET("/api/order/cust/del-deliv")
//    fun reqDelDeliv(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliSeq") deliSeq: String
//    ): Single<ResBase>
//
    // 회원배송지 조회
    @POST("/api/order/cust/deliv")
    fun reqDeliv(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<List<ResDeliv>>
//
    // 최근주소지 조회
    @GET("/api/order/cust/list-srhAddr")
    fun reqSrhAddr(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ArrayList<ResAddress>>
//
//    // 최근 주소지 검색 정보 저장
//    @POST("/api/order/cust/save-srhAddr")
//    fun reqSaveSrhAddr(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqSaveSchAddr
//    ): Single<ResBase>
//
    // 최근주소지 개별 삭제
    @GET("/api/order/cust/del-deliv")
    fun reqDelSrhAddr(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("deliSeq") deliSeq: Int
    ): Single<ResBase>
//
//    // 최근 주소지 검색 정보 저장
//    @POST("/api/order/cust/save-word")
//    fun reqSaveWord(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqMembSrhWordRequest
//    ): Single<ResBase>
//
//    // 내 정보 가져오기
//    @GET("/api/order/cust/info-my")
//    fun reqInfoMy(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<ResInfoMy>
//
//    // 내 주문 종합정보 가져오기 (기본정보, 공지,이벤트,1:1
//    @GET("/api/order/cust/info-myTot")
//    fun reqInfoMyTot(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deviTpCd") deviTpCd: String
//    ): Single<ResInfoMyTot>
//
//    // 1:1 문의 내역 가져오기
//    @GET("/api/order/cust/list-oneQuest")
//    fun reqListOneQuest(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResListOneQuest>
//
//    // 1:1 문의 내역 가져오기
//    @GET("/api/order/cust/oneQuest")
//    fun reqOneQuest(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("askNo") askNo: String
//    ): Single<ListOneQuestItem>
//
    // 회원배송지 정보 저장하기
    @POST("/api/order/cust/save-deliv")
    fun reqSaveDeliv(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body param: ReqSaveDeliv
    ): Single<ResBase>

    //
//    // 내 정보 저장하기 ( 닉네임, 이메일수신여부, sms수신동의, 휴대폰번호 변경, 성인인증, 패스워드변경)
//    @GET("/api/order/cust/save-myinfo")
//    fun reqSaveMyinfo(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("chgData") chgData: String,
//        @Query("saveType") saveType: String
//    ): Single<ResBase>
//
//    // 1:1 문의 내역 저장하기
//    @POST("/api/order/cust/save-oneask")
//    fun reqSaveOneQuest(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqSaveOneQuest
//    ): Single<ResBase>
//
//    // 회원정보 저장
//    @POST("/api/order/cust/sign-up")
//    fun reqSignUp(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqSignUp
//    ): Single<ResSignUp>
//
//    @GET("/api/order/cust/withdrawal")
//    fun reqWithdrawal(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<ResWithDrawal>
//
//    @Multipart
//    @POST("/api/order/cust/save-myphoto")
//    fun reqSaveMyPhoto(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Part files: List<MultipartBody.Part>
//    ): Single<ResSaveMyPhoto>
//
//    /* ==========================================
//     * main-controller Main Controller
//    =============================================*/
//
    // 앱버젼
    @GET("/api/order/main/app-ver")
    fun reqAppVer(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ResAppVer>

        // 배너 리스트
    @GET("/api/order/main/banner")
    fun reqBanner(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ArrayList<ResBanner>>
//
//    // 카테고리 리스트
//    @GET("/api/order/main/category")
//    fun reqCategory(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("cateTpCd") cateTpCd: String
//    ): Single<ResCategoryData>
//
    // 개선한 카테고리 리스트
    @GET("/api/order/main/category")
    fun reqCategory(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("cateTpCd") cateTpCd: CateTpCd2
    ): Single<ResCategoryData>
//
    // 앱로딩 이미지
    @GET("/api/order/main/load-img")
    fun reqLoadImg(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ResLoadImg>

    //
//    // 방문로그 저장하기 : 로그타입(10 앱로딩 20 홈 30 단골 40 장바구니 50 주문내역 60 마이페이지 70 메뉴공유 80 매장공유)
//    @POST("/api/order/main/save-log")
//    fun reqSaveLog(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("logTpCd") cateTpCd: String
//    ): Single<ResBase>
//
    // 튜토리얼 이미지
    @GET("/api/order/main/totual-img")
    fun reqTutorial(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ArrayList<ResTutorial>>

    //
//    /* ==========================================
//     * order-controller Order Controller
//    =============================================*/
//
    // 장바구니 카운트
    @GET("/api/order/order/bask-cnt")
    fun reqBaskCnt(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ResBaskCnt>
//
//    // 장바구니 삭제
//    @POST("/api/order/order/bask-del")
//    fun reqBaskDel(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqBaskDel
//    ): Single<ResBase>
//
//    // 장바구니 리스트 가져오기
//    @GET("/api/order/order/bask-list")
//    fun reqBaskList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String,
//        @Query("nearYn") nearYn: String,
//        @Query("xPos") xPos: String,
//        @Query("yPos") yPos: String
//    ): Single<List<ResBaskList>>
//
//    // 장바구니 저장
//    @POST("/api/order/order/bask-save")
//    fun reqBaskSave(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqBaskSave
//    ): Single<ResBase>
//
//    // 주문정보
//    @GET("/api/order/order/order-info")
//    fun reqOrderInfo(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("orderNo") orderNo: String
//    ): Single<ResOrderInfo>
//
//    // 결재주문내역 가져오기
//    @GET("/api/order/order/order-list")
//    fun reqOrderList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String,
//        @Query("nearYn") nearYn: String,
//        @Query("xPos") xPos: String,
//        @Query("yPos") yPos: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResOrderHistoryData>
//
//    // 주문내역(하단)
//    @GET("/api/order/order/order-comp-list")
//    fun reqOrderCompList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResOrderCompData>
//
//    // 고객주문내역 가져오기
//    @GET("/api/order/order/cust-order-list")
//    fun reqOrderCustList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResOrderCustData>
//
//    // 주문결제정보 저장
//    @POST("/api/order/order/order-pay-save")
//    fun reqOrderPaySave(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqOrderSave
//    ): Single<ResOrderPaySave>
//
//    // 0원 주문결제정보 저장
//    @POST("/api/order/order/order-zero-pay-save")
//    fun reqOrderZeroPaySave(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqOrderSave
//    ): Single<ResOrderZeroPaySave>
//
//    // 주문결제정보 저장 실패
//    @POST("/api/order/order/pay-cancel")
//    fun reqPayCancel(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqPayCancel
//    ): Single<ResBase>
//
//    // 주문정보 저장
//    @POST("/api/order/order/order-pre-save")
//    fun reqOrderPreSave(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqOrderPreSave
//    ): Single<ResOrderPreSave>
//
//    // 전화주문내역 가져오기
//    @GET("/api/order/order/order-tel")
//    fun reqOrderTel(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String,
//        @Query("nearYn") nearYn: String,
//        @Query("xPos") xPos: String,
//        @Query("yPos") yPos: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResOrderTelData>
//
//    // 전화주문 저장
//    @POST("/api/order/order/order-tel-save")
//    fun reqOrderTelSave(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqShopNo
//    ): Single<ResBase>
//
//    // 결제전 체크
//    @POST("/api/order/order/pay-check")
//    fun reqPayCheck(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String,
//        @Query("orderNo") orderNo: String
//    ): Single<ResBase>
//
//    // 결제수단정보
//    @GET("/api/order/order/pay-method")
//    fun reqPayMethod(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("shopNo") shopNo: String
//    ): Single<List<ResPayMethod>>
//
//    // 주문 리뷰저장
//    @POST("/api/order/order/review-save")
//    fun reqReviewSave(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqReviewSave
//    ): Single<ResBase>
//
//    // 주문 리뷰 리스트
//    @GET("/api/order/order/review-list")
//    fun reqOrderReviewList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int,
//        @Query("rviewYn") rviewYn: String = "Y"
//    ): Single<ResOrderReviewList>
//
//    /* ==========================================
//     * pub-controller Pub Controller
//    =============================================*/
//
    // 회사정보 가져오기
    @GET("/api/order/pub/company")
    fun reqCompany(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ResCompany>

//    //	인증번호 요청
//    @GET("/api/order/pub/trans-cert")
//    fun reqTransCert(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("userPhone") userPhone: String,
//        @Query("regMemYn") regMemYn: String = "N"
//    ): Single<ResTransCert>
//
//    //	sms 인증 확인하기
//    @GET("/api/order/pub/conf-cert")
//    fun reqConfCert(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("certDt") certDt: String,
//        @Query("certNo") certNo: String,
//        @Query("certNum") certNum: String,
//        @Query("deviceId") deviceId: String,
//        @Query("userPhone") userPhone: String
//    ): Single<ResBase>
//
//    // 이미지 업로드
//    @Multipart
//    @POST("/api/order/pub/files-upload")
//    fun reqFileUpload(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Part file: MultipartBody.Part
//    ): Single<ResFilesUpload>
//
//    @Multipart
//    @POST("/api/order/pub/files-upload")
//    fun reqFilesUpload(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Part files: List<MultipartBody.Part>
//    ): Single<ResFilesUpload>
//
//    @Multipart
//    @POST("/api/order/pub/files-upload")
//    suspend fun reqFilesUploadAsync(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Part files: List<MultipartBody.Part>
//    ): FileResponse
//
//    // 이벤트 리스트 가져오기
//    @GET("/api/order/pub/list-event")
//    fun reqListEvent(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResEventData>
//
//    //	이벤트상세 가져오기
//    @GET("/api/order/pub/info-event")
//    fun reqInfoEvent(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("evenNo") evenNo: String
//    ): Single<ResListEvent>
//
//    // 공지사항 리스트 가져오기
//    @GET("/api/order/pub/list-noti")
//    fun reqListNoti(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResNotiData>
//
//    // 공지사항상세 가져오기
//    @GET("/api/order/pub/info-noti")
//    fun reqInfoNoti(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("nticeNo") nticeNo: String
//    ): Single<ResListNoti>
//
//    //	자주묻는 질문 가져오기/
//    @GET("/api/order/pub/list-ask")
//    fun reqListAsk(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<ResListFaq>
//
//    // 최신공지,이벤트 가져오기 (메인)
//    @GET("/api/order/pub/main-noti")
//    fun reqMainNoti(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<ResMainNoti>
//
    // 팝업가져오기
    @GET("/api/order/pub/popup")
    fun reqPopup(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
    ): Single<ArrayList<ResPopup>>

    //
//    //	환경설정 가져오기
//    @GET("/api/order/pub/info-setting")
//    fun reqInfoSetting(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deviTpCd") deviTpCd: String
//    ): Single<ResSetting>
//
//    // 환결설정 정보 저장하기
//    @GET("/api/order/pub/save-setting")
//    fun reqSaveSetting(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("setType") setType: String,
//        @Query("setYn") setYn: String
//    ): Single<ResBase>
//
//    // 환결설정 정보 저장하기
//    @GET("/api/order/pub/save-setting")
//    suspend fun reqSaveSettingAsync(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("setType") setType: String,
//        @Query("setYn") setYn: String
//    ): Response
//
    // 약관 리스트가져오기
    @GET("/api/order/pub/terms")
    fun reqTerms(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("casTyCd") casTyCd: String,
        @Query("casNo") casNo: String = ""
    ): Single<List<ResTerms>>

    //
    // 약관동의결과 저장하기
    @POST("/api/order/pub/save-terms")
    fun reqSaveTerms(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Body casAgreeRequestList: List<TermsItem>,
        @Query("casTyCd") casTyCd: String
    ): Single<ResBase>

    // 노출 및 클릭 카운트 처리
    // jobType : 작업 10 자주묻는질문, 20 배너 (최초 배너 리스트 전달시 자동 첫번째거 노출 카운트처리함), 30 팝업(최초 배너 리스트 전달시 자동 첫번째거 노출 카운트처리함)
    // runType : 실행 : 10 클릭, 20 노출
    @GET("/api/order/pub/view-click")
    fun reqViewClick(
        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
        @Query("jobType") jobType: JobTypeCd,
        @Query("relNo") relNo: String,
        @Query("runType") runType: RunTypeCd
    ): Single<ResBase>
//
//    /* ==========================================
//     * search-controller Search Controller
//    =============================================*/
//
//    // 검색어 전체 삭제
//    @POST("/api/order/search/del-all-word")
//    fun reqDelAllWord(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<ResBase>
//
//    // 검색어 개별 삭제
//    @POST("/api/order/search/del-word")
//    fun reqDelWord(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqMembSrhWordRequest
//    ): Single<ResBase>
//
//    // 검색어 조회
//    @GET("/api/order/search/word")
//    fun reqSearchWord(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<List<ResSearchWord>>
//
//    /* ==========================================
//     * shop-controller Shop Controller
//    =============================================*/
//
//    // 메뉴 상세정보
//    @GET("/api/order/shop/menu-info")
//    fun reqMenuInfo(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String,
//        @Query("menuNo") menuNo: String,
//        @Query("shopNo") shopNo: String,
//        @Query("xPos") xPos: Double,
//        @Query("yPos") yPos: Double
//    ): Single<ResMenuInfo>
//
//    // 매장 메뉴리스트 가져오기
//    @GET("/api/order/shop/menu-list")
//    fun reqMenuList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("shopNo") shopNo: String
//    ): Single<ResMenuList>
//
//    // 단골매장 삭제
//    @POST("/api/order/shop/regular-del")
//    fun reqRegularDel(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqShopNo
//    ): Single<ResBase>
//
//    // 매장 메뉴리스트 가져오기
//    @GET("/api/order/shop/regular-list")
//    fun reqRegularList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String,
//        @Query("nearYn") nearYn: String,
//        @Query("xPos") xPos: String,
//        @Query("yPos") yPos: String
//    ): Single<ResShopData>
//
//    // 매장 메뉴리스트 가져오기(map)
//    @GET("/api/order/shop/regular-list")
//    fun reqRegularList2(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @QueryMap params: HashMap<String, String?>
//    ): Single<RegularListResponse>
//
//    // 단골매장 저장
//    @POST("/api/order/shop/regular-save")
//    fun reqRegularSave(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqShopNo
//    ): Single<ResBase>
//
//    // 리뷰리스트 가져오기
//    @GET("/api/order/shop/review-list")
//    fun reqReviewList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("shopNo") shopNo: String,
//        @Query("photoYn") photoYn: String,
//        @Query("sortCd") sortCd: String,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResReviewList>
//
//
////    // 개선한, 매장리스트 가져오기
////    @GET("/api/order/shop/shop-list")
////    fun reqShopList2(
////        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
////        @QueryMap params: HashMap<String, String?>
////    ): Single<ShopListResponse>
//
//    // 검색매장리스트
//    @GET("/api/order/shop/search-list")
//    fun reqSearchList2(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("cateNo") cateNo: String?,
//        @Query("deliAreaNo") deliAreaNo: String?,
//        @Query("serchText") serchText: String?,
//        @Query("xPos") xPos: String?,
//        @Query("yPos") yPos: String?,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResShopSearch>
//
//    // 검색매장리스트
//    @GET("/api/order/shop/search-list")
//    fun reqSearchList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("cateNo") cateNo: String?,
//        @Query("deliAreaNo") deliAreaNo: String?,
//        @Query("serchText") serchText: String?,
//        @Query("xPos") xPos: String?,
//        @Query("yPos") yPos: String?,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResShopSearch>
//
//    // 매장상세정보 가져오기
//    @GET("/api/order/shop/shop-info")
//    fun reqShopInfo(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String?,
//        @Query("shopNo") shopNo: String?,
//        @Query("xPos") xPos: String?,
//        @Query("yPos") yPos: String?
//    ): Single<ResShopInfo>
//
//    @GET("/api/order/shop/shop-info")
//    suspend fun reqShopInfoAsync(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("deliAreaNo") deliAreaNo: String?,
//        @Query("shopNo") shopNo: String?,
//        @Query("xPos") xPos: String?,
//        @Query("yPos") yPos: String?
//    ): ResShopInfo
//
//    // 매장리스트 가져오기
//    @GET("/api/order/shop/shop-list")
//    fun reqShopList(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("cateNo") cateNo: String?,
//        @Query("deliAreaNo") deliAreaNo: String?,
//        @Query("srhArrayCd") srhArrayCd: String?,
//        @Query("xPos") xPos: String?,
//        @Query("yPos") yPos: String?,
//        @Query("lastNo") lastNo: String,
//        @Query("limit") limit: Int
//    ): Single<ResShopList>
//
//    // 개선한, 매장리스트 가져오기
//    @GET("/api/order/shop/shop-list")
//    fun reqShopList2(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @QueryMap params: HashMap<String, String?>
//    ): Single<ShopListResponse>
//
//    @GET("/api/order/shop/shop-list")
//    fun reqShopList2Test(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @QueryMap params: HashMap<String, String>
//    ): Single<ShopListResponse>
//
//    // 매장 영업 정보
//    @GET("/api/order/shop/shop-sale")
//    fun reqShopSale(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("shopNo") shopNo: String?
//    ): Single<ResShopSale>
//
//    // 공유매장
//    @POST("/api/order/shop/shop-share")
//    fun reqShopShare(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqShopNo
//    ): Single<ResBase>
//
//    /* ==========================================
//     * signin-controller Signin Controller
//    =============================================*/
//
//    // 로그아웃
//    @POST("/api/order/member/log-out")
//    fun reqLogout(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<ResAppKey>
//
//    // 이메일 중복 체크
//    @POST("/api/order/member/mail-check")
//    fun reqEmailCheck(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqEmailCheck
//    ): Single<ResBase>
//
//    // 공개키
//    @GET("/api/order/member/public-key")
//    fun reqPublicKey(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String
//    ): Single<ResPublicKey>
//
//    // 공유매장
//    @POST("/api/order/member/sign-in")
//    fun reqSignIn(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body ReqData: ReqSignIn
//    ): Single<ResSignIn>
//
//    // 결제 성공
//    @GET("/api/order/redis/send-order")
//    fun reqOrderComplete(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("orderNo") orderNo: String
//    ): Single<ResBase>
//
//    /* ==========================================
//     * redis-controller Redis Controller
//    =============================================*/
//
//    // now count
//    @GET("/api/order/redis/now-cnt")
//    fun reqNowCnt(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("shopNo") shopNo: String?,
//        @Query("type") type: String = "M"
//    ): Single<ResBase>
//
//    // 매장 cctv 정보
//    @GET("/api/order/cctv/cctv-info")
//    suspend fun getCctvInfoAsync(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("shopNo") shopNo: String
//    ): CctvInfoStandaloneResponse
//
//    // cctv 신고하기
//    @POST("/api/order/cctv/report-shop")
//    suspend fun reportShop(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Body reportShop: CctvReportShopDto
//    ): Response
//
//    // 매장검색(주로 안심톡에서 사용)
//    @GET("/api/order/shop/search-shop")
//    suspend fun searchShop(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("lastNo") lastNo: Int,
//        @Query("limit") limit: Int,
//        @Query("serchText") serchText: String,
//    ): ShopSearchByAllResponse
//
//    // 시도 조회
//    @GET("/api/order/addr/city")
//    suspend fun searchAddrCity(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//    ): AddrCityResponse
//
//    //시/구/군 조회
//    @GET("/api/order/addr/town")
//    suspend fun searchAddrTown(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("legalCity") legalCity: String
//    ): AddrTownResponse
//
//    //읍/면/동 및 법정동 코드 조회
//    @GET("/api/order/addr/dong")
//    suspend fun searchAddrDong(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("legalCity") legalCity: String,
//        @Query("legalTown") legalTown: String
//    ): AddrDongResponse
//
//    // 카테고리 리스트
//    @GET("/api/order/main/category")
//    suspend fun reqCategoryAsync(
//        @Header(Command.PARAMETER_X_AUTH_TOKEN) appKey: String,
//        @Query("cateTpCd") cateTpCd: CateTpCd2
//    ): CategoriesResponse

}