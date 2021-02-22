package com.ram.delivery.model.api.res

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//
//import android.os.Parcelable
//import com.google.gson.annotations.SerializedName
//import kotlinx.android.parcel.Parcelize

data class ResBase(var code: Int, var message: String)

data class ResAppKey(var appKey: String)
//
data class ResLoadImg(var filePath: String)
//
data class ResAppVer(var appVer: String, var desVer: String, var appVerDt: String)
//
@Parcelize
data class ResTutorial(var filePath: String, var viewNum: Int) : Parcelable

data class LastAddressResponse(
    var addr1: String,
    var addr2: String,
    var areaCd: String,
    var jibunAddr: String,
    var xpos: String,
    var ypos: String,
    val town:String,
    val legalCd:String
)
//
data class ResTerms(var casNo: String, var casNm: String, var casDesc: String, var isAgree: Boolean)
//
//data class ResSearch(
//    var srhWord: String,
//    var oldAddr: String,
//    var areaCd: String,
//    var xPos: String,
//    var yPos: String,
//    var AddDt: String
//)
//
//data class ResSearchWord(val addDt: String, val srhWord: String, var pos: Int = -1)
//
//data class ResFilesUpload(var fileNo: String)
//data class ResSaveMyPhoto(var fileNo: String, var filePath: String)
//
//data class ResWithDrawal(var appKey: String, var outCust: String)
//
///*
//bannLandingCd / bannLandingDtl
//10 공지사항 상세
//20 이벤트 상세
//30 URL 입력
//40 (GNB) 홈 화면으로 이동
//50 (GNB) 단골매장 화면으로 이동
//60 (GNB) 장바구니 화면으로 이동
//70 (GNB) 주문내역 화면으로 이동
//80 (GNB) 마이오더 화면으로 이동
//90 (마이오더) 쿠폰 화면으로 이동
//100 (마이오더) 리뷰관리 화면으로 이동
//110 없음
//*/
@Parcelize
data class ResBanner(
    var bannNo: String,
    var bannNm: String,
    var bannLandingDtl: String,
    var dbannImgPath: String,
    var sbannImgPath: String,
    var viewNum: Int,
    var bannLandingCd: String
) : Parcelable
//
//data class ResTransCert(val certDt: String, val certNum: Int)
//
data class ResCategoryData(var cateBaseList: List<ResCategory>, var catePlanList: List<ResCategory>)
data class ResCategory(
    var cateNo: String,
    var cateNm: String,
    var cateImgPath: String,
    var cateUrl: String,
    var linkCd: String,
    var viewNum: Int,
    var isChoice: Boolean = false
)
//
//data class ResMainNoti(val noticeInfo: MainNoticeInfo?, val eventInfo: MainEventInfo?)
//data class MainNoticeInfo(val noticeNo: String, val noticeTitle: String)
//data class MainEventInfo(val eventNo: String, val eventTitle: String)
//
data class ResCompany(
    var companyNo: String,
    var companyNm: String,
    var repNm: String,
    var addr: String,
    var regNo: String,
    var mailOrderNo: String,
    var hoster: String,
    var oneAsk: String,
    var oneAskUrl: String,
    var custCntr: String,
    var callNo: String,
    var agreeInfo: String
)
//
///*
//popLandingCd / popLandingDtl
//10 공지사항 상세
//20 이벤트 상세
//30 URL 입력
//40 (GNB) 홈 화면으로 이동
//50 (GNB) 단골매장 화면으로 이동
//60 (GNB) 장바구니 화면으로 이동
//70 (GNB) 주문내역 화면으로 이동
//80 (GNB) 마이오더 화면으로 이동
//90 (마이오더) 쿠폰 화면으로 이동
//100 (마이오더) 리뷰관리 화면으로 이동
//110 없음
//*/
@Parcelize
data class ResPopup(
    var popuNo: String?,
    var popuTitl: String,
    var popuDesc: String?,
    var popuImgfNoUrl: String,
    var viewNum: String?,
    var popLandingCd: String?,
    var popLandingDtl: String?
) : Parcelable
//
//data class ResShopList(
//    var lastNo: String,
//    val listPData: List<ShopListData>,
//    val listSData: List<ShopListData>,
//    val listTData: List<ShopListData>
//)
//
//data class ShopListData(
//    var basicDeliAmt: Int,
//    var distanceInfo: Float,
//    var minOrderAmt: Int,
//    var nowCnt: Int,
//    var orderCnt: Int,
//    var rviewCnt: Int,
//    var rviewPt: Float,
//    var shopImgUrl: String,
//    var shopNm: String,
//    var shopNo: String,
//    var shopTpCd: String,
//    var saleStatCd: String,
//    var viewNum: Int,
//    val cdnUrl: String? = ""
//)
//
//data class ResShopSearch(
//    var lastNo: String,
//    val cateList: ArrayList<CateData>,
//    val listPData: List<ShopListData>,
//    val listSData: List<ShopListData>,
//    val listTData: List<ShopListData>
//)
//
//data class CateData(var cateNo: String, var cateNm: String)
//
//data class ResShopData(
//    val listPData: ArrayList<ShopData>,
//    val listSData: ArrayList<ShopData>,
//    val listTData: ArrayList<ShopTData>
//)
//
////todo 이건 좀 수정이 필요
//data class RegularListResponse(
//    val modifyImport: String
////    val listPData: ArrayList<RegularRightResponse>,
////    val listSData: ArrayList<RegularSafeResponse>,
////    val listTData: ArrayList<RegularCallResponse>
//)
//
//data class ShopData(
//    var basicDeliAmt: Int,
//    var distanceInfo: Float,
//    var minOrderAmt: Int,
//    var nowCnt: Int,
//    var orderCnt: Int,
//    var rviewCnt: Int,
//    var rviewPt: Float,
//    var shopImgUrl: String,
//    var shopNm: String,
//    var shopNo: String,
//    var viewNum: Int,
//    var saleStatCd: String
//)
//
//data class ShopTData(
//    var shopNo: String,
//    var shopNm: String,
//    var distanceInfo: String,
//    var viewNum: Int
//)
//
//@Parcelize
//data class ResShopInfo(
//    var basicDeliAmt: Int,
//    var freqCnt: Int,
//    var freqYn: String,
//    var gradeImgUrl: String,
//    var minOrderAmt: Int,
//    var nowCnt: Int,
//    var orderCnt: Int,
//    var rviewCnt: Int,
//    var rviewPoint: Int,
//    var rviewPt: Float,
//    var saleStatCd: String,
//    var saleStatNm: String,
//    var shopDesc: String,
//    var shopNm: String,
//    var shopNo: String,
//    var shopTyCd: String,
//    var telNo: String,
//    var transMsg: String,
//    val xpos: Double,
//    val ypos: Double,
//    var addr: String,
//    var gibunAddr: String,
//    var cartAddYn: String,
//    var cartCnt: Int,
//    val cctvInfo: List<CctvInfoResponse>,
//    val cctvReportContent: List<CctvReportContentResponse>
//) : Parcelable
//
//data class ResMenuList(
//    val allList: ArrayList<MenuAllList>,
//    val dcList: ArrayList<MenuItem>,
//    val recoList: ArrayList<MenuItem>
//)
//
//data class MenuAllList(
//    val adtSaleYn: String,
//    val menuGrpNm: String,
//    val menuGrpNo: String,
//    val menuList: ArrayList<MenuItem>,
//    @MenuListModel.Companion.RowType val type: Int = MenuListModel.PARENT,
//    var isExpanded: Boolean = true
//)
//
//data class MenuItem(
//    val menuNo: String,
//    val menuNm: String,
//    val dcAmt: Float,
//    val dcPriceAmt: Int,
//    val menuImgPath: String,
//    var priceAmt: Int,
//    var saleAmt: Int,
//    var saleCd: String,
//    var saleCdNm: String,
//    var menuDesc: String?,
//    @MenuListModel.Companion.RowType val type: Int = MenuListModel.CHILD
//)
//
//data class ResMenuInfo(
//    val adtSaleYn: String,
//    val dcNo: String,
//    val dcPercent: Int,
//    val dcPriceAmt: Int,
//    val deliAreaYn: String,
//    val menuDesc: String,
//    val menuImgPath: String,
//    val menuNm: String,
//    val menuNo: String,
//    val minOrderAmt: Int,
//    val priceAmt: Int,
//    val salePriceAmt: Int,
//    val saleStatYn: String,
//    val sellStatYn: String,
//    val menuPriceCd: String,
//    val selMenuList: ArrayList<SelMenuList>,
//    val optCateList: ArrayList<OptCateList>
//)
//
//data class SelMenuList(
//    val addPriceAmt: Int,
//    val selMenuNm: String,
//    val selMenuNo: String,
//    val selMenuSeq: Int,
//    var check: Boolean = false
//)
//
//data class OptCateList(
//    val optCateNm: String,
//    val optCateNo: String,
//    val selLimitCnt: Int,
//    val optList: ArrayList<OptList>
//)
//
//data class OptList(
//    val addPriceAmt: Int,
//    val optCateNo: String,
//    val optMenuNm: String,
//    val optMenuNo: String,
//    var check: Boolean = false
//)
//
//data class ResShopSale(
//    val shopAddr: String,
//    val shopOldAddr: String,
//    val shopDesc: String,
//    val shopTelNo: String,
//    val minOrderAmt: Int,
//    val basicDeliAmt: Int,
//    val deliCondiDesc: String,
//    val orgInfo: String,
//    val deliTargetCd: String,
//    val addDeliYn: String,
//    val xpos: Double,
//    val ypos: Double,
//    val deliForderAmt: Int,
//    val deliAreaList: ArrayList<ResDeliAreaList>,
//    val plusDeliList: ArrayList<ResPlusDeliList>,
//    val saleWeekList: List<ResSaleWeekList>
//)
//
//data class ResDeliAreaList(var areaNm: String)
//data class ResPlusDeliList(var areaNm: String, val plusDeliAmt: Int)
//data class ResSaleWeekList(
//    val week: Int,
//    val weekNm: String,
//    val saleStTime: String,
//    val saleEdTime: String,
//    val holiDayYn: String
//)
//
data class ResBaskCnt(var baskCnt: Int)
//
//data class ResListBask(var resultCnt: Int)
//
//data class ResSaveRegularShop(var resultCnt: Int)
//
//data class ResMenuItem(
//    var menuNo: String,
//    var menuImgfNoUrl: String,
//    var menuNm: String,
//    var menuDesc: String,
//    var dcPercent: Double,
//    var dcAmt: Int,
//    var priceAmt: Int,
//    var salePrice: Int,
//    var minOrderAmt: Int,
//    var deliAreaYn: String,
//    var saleStatCd: String,
//    var saleCd: String,
//    var mMenuSelPosiCnt: String,
//    val resMenuMData: List<ResMenuMData>,
//    val resMenuOData: List<ResMenuOData>,
//    val resMenuOMData: List<ResMenuOMData>
//)
//
//data class ResMenuMData(var deMenuNo: String, var detMenuNm: String, var addPriceAmt: String)
//data class ResMenuOData(var optCateNo: String, var optMenuNm: String, var selEsseCd: String)
//data class ResMenuOMData(
//    var optCateNo: String,
//    var optMenuNo: String,
//    var optMenuNm: String,
//    var addPriceAmt: String
//)
//
//data class ResReviewList(
//    val lastNo: String,
//    var limit: Int,
//    var totCount: Int,
//    val reviewList: ArrayList<ResReview>
//)
//
//data class ResReview(
//    val blindYn: String,
//    val commAddDt: String?,
//    val commDesc: String,
//    val membImgfNoUrl: String,
//    val nickNmEmail: String,
//    val orderMenuNm: String,
//    val rviewDesc: String,
//    val rviewDt: String,
//    val rviewNo: String,
//    val rviewPt: Float,
//    val shopNm: String,
//    val rviewPoint: Int,
//    val photoList: List<PhotoList>,
//    var isExpand: Boolean = true
//)
//
//@Parcelize
//data class PhotoList(var fileSeq: Int, var rviewImgfNoUrl: String) : Parcelable
//
//data class ResOrderReviewList(
//    val lastNo: String,
//    var limit: Int,
//    val orderList: ArrayList<ResOrderReview>
//)
//
//data class ResOrderReview(
//    val blindYn: String,
//    val commAddDt: String?,
//    val commDesc: String,
//    val orderAmt: Int,
//    val orderDt: String,
//    val orderMenuNm: String,
//    val orderNo: String,
//    val rviewDesc: String,
//    val rviewDt: String,
//    val rviewPt: Float,
//    val shopNo: String,
//    val shopNm: String,
//    val rviewYn: String,
//    val rviewPoint: Int,
//    val photoList: List<PhotoList>
//)
//
//data class ResDtlShop(
//    var shopAddr: String,
//    var shopOldAddr: String,
//    var shopDesc: String,
//    var shopTelNo: String,
//    var deliAreaNm: String,
//    var minOrderAmt: Int,
//    var basicDeliAmt: Int,
//    var deliCondiDesc: String,
//    var orgInfo: String,
//    var deliCnt: Int,
//    var saleCnt: Int,
//    var deliData: List<ResDeliData>,
//    val saleData: List<ResSaleData>
//)
//
//data class ResDeliData(var areaNoNm: String, var plusDeliAmt: Int)
//data class ResSaleData(var week: String, var saleStDay: String, var saleEdDay: String)
//
//data class ResBaskList(
//    var shopNo: String,
//    var shopNm: String,
//    var deliAreaYn: String,
//    var minOrderAmt: Int,
//    var orderAmt: Int,
//    var orderPosibYn: String,
//    var saleYn: String,
//    var sellYn: String,
//    var menuList: ArrayList<BaskMenuList>
//)
//
//data class BaskMenuList(
//    var menuNo: String,
//    var menuNm: String,
//    var cartNo: String,
//    var menuSeq: Int,
//    var menuPriceAmt: Int,
//    var orderCnt: Int,
//    var addOptAmt: Int,
//    var addOptDesc: String,
//    var sellStatYn: String,
//    var selMenuNm: String,
//    var cartOptList: List<CartOptList>,
//    var addAmt: Int,
//    var shopNo: String,
//    var dcNo: String
//)
//
//data class CartOptList(
//    var optAmt: Int,
//    var optCateNo: String,
//    var optMenuNo: String,
//    var optNm: String,
//    var optNo: String
//)
//
//data class ResSaveOrder(var orderNo: String)
//
//data class ResCheckOrder(var orderProdessCd: String, var orderRejectDesc: String)
//
//data class ResSaveInfoOrder(
//    var orderNo: String,
//    var menuNm: String,
//    var payAmt: String,
//    var payDt: String
//)
//
//data class ResInfoHistOrder(
//    var orderNo: String,
//    var orderDt: String,
//    var shopNo: String,
//    var shopNm: String,
//    var orderStatCd: String,
//    var orderStatNm: String,
//    var orderWatingTime: Int,
//    var orderMenuSimple: String,
//    var orderAmt: Int
//)
//
//data class ResListHistOrder(
//    var orderNo: String,
//    var orderDt: String,
//    var shopNo: String,
//    var shopNm: String,
//    var orderStatCd: String,
//    var orderStatNm: String,
//    var orderWatingTime: Int,
//    var orderMenuSimple: String,
//    var orderAmt: Int
//)
//
//data class ResListHistCall(
//    var telDt: String,
//    var shopCd: String,
//    var shopNm: String,
//    var shopTelNo: String,
//    var shopAddr: String,
//    var shopOldAddr: String
//)
//
//data class ResOrderInfo(
//    var orderNo: String,
//    var shopNo: String,
//    var shopNm: String,
//    var phoneNo: String,
//    var deliReqChk: String,
//    var cancelDesc: String,
//    var deliAddr: String,
//    var deliAmt: Int,
//    var deliExpTime: String,
//    var deliReq: String,
//    var orderAmt: Int,
//    var orderDt: String,
//    var orderDtTime: String,
//    var orderStatCd: String,
//    var orderWeek: String,
//    var payAmt: Int,
//    var payMethod: String,
//    var safeTelNo: String,
//    var orderAcceptDt: String,
//    var orderCompleteDt: String,
//    val addDt: String,
//    val menuList: List<OrderInfoItem>
//)
//
//data class OrderInfoItem(
//    var menuNo: String,
//    var menuNm: String,
//    var menuSeq: Int,
//    var menuAmt: Int,
//    var optAmt: Int,
//    var optDescNm: String,
//    var orderCnt: Int
//)
//
//data class ResOrderData(var lastNo: String, var orderList: List<ResOrderList>)
//
//@Parcelize
//data class ResOrderList(
//    var shopNo: String,
//    var shopNm: String,
//    var orderNo: String,
//    var orderAmt: Int,
//    var orderAcceptDt: String,
//    var orderDt: String,
//    var orderMenuNm: String,
//    var orderStatCd: String,
//    var week: String,
//    var isReview: String,
//    var deliExpTime: Int,
//    var pos: Int
//) : Parcelable
//
//data class ResOrderHistoryData(var lastNo: String, var orderList: List<OrderHistoryItem>)
//data class OrderHistoryItem(
//    var shopNo: String,
//    var shopNm: String,
//    var orderNo: String,
//    var orderAmt: Int,
//    var orderAcceptDt: String,
//    var orderDt: String,
//    var orderMenuNm: String,
//    var orderStatCd: String,
//    var week: String,
//    var isReview: String,
//    var deliExpTime: Int,
//    var pos: Int,
//    var isTimer: Boolean = true,
//    var timer: MelchiCountDownTimer? = null
//)
//
//data class ResOrderCompData(
//    var lastNo: String,
//    var sumOrderAmt: Int,
//    var orderList: List<ResOrderCompList>
//)
//
//data class ResOrderCompList(
//    var shopNo: String,
//    var shopNm: String,
//    var orderNo: String,
//    var orderAmt: Int,
//    var orderDt: String,
//    var orderMenuNm: String
//)
//
//data class ResOrderCustData(
//    var lastNo: String,
//    var totOrderAmt: Int,
//    var orderList: List<ResOrderCustList>
//)
//
//data class ResOrderCustList(
//    var deliExpTime: String,
//    var isReview: String,
//    var orderAcceptDt: String,
//    var orderAmt: Int,
//    var orderDt: String,
//    var orderMenuNm: String,
//    var orderNo: String,
//    var orderStatCd: String,
//    var shopAddr: String,
//    var shopNm: String,
//    var shopNo: String,
//    var shopOldAddr: String,
//    var shopTelNo: String,
//    var week: String
//)
//
//@Parcelize
//data class ResOrderPaySave(
//    var orderNo: String,
//    var orderAmt: Int,
//    @SerializedName("addDt")
//    val addDt: String,
//    var menuDesc: String,
//    var resultCode: String = "0000",
//    val msg: String? = null
//) : Parcelable
//
//data class ResOrderZeroPaySave(
//    var orderNo: String,
//    var orderAmt: Int,
//    var orderDt: String,
//    @SerializedName("addDt")
//    val addDt: String,
//    var menuDesc: String
//)
//
//@Parcelize
//data class ResOrderPreSave(
//    var orderNo: String,
//    var orderNm: String,
//    var payAmt: Int,
//    var orderDesc: String,
//    var shopNo: String? = ""
//) : Parcelable
//
//data class ResOrderTelData(var lastNo: String, var orderList: List<ResOrderTel>)
//data class ResOrderTel(
//    var shopNo: String,
//    var shopNm: String,
//    var orderDt: String,
//    var shopAddr: String,
//    var shopTelNo: String,
//    var deliExpTime: Int,
//    var isReview: String,
//    var orderAcceptDt: String,
//    var orderAmt: Int,
//    var orderMenuNm: String,
//    var orderNo: String,
//    var orderStatCd: String,
//    var shopOldAddr: String,
//    var week: String
//)
//
//@Parcelize
//data class ResPayMethod(var basicYn: String, var payNo: Int, var payNm: String, var useYn: String) :
//    Parcelable
//
//
//data class ResInfoMyTot(
//    var appVer: String,
//    var coupCnt: Int,
//    var evenYn: String,
//    var filePath: String,
//    var membImgfNo: String,
//    var nickNm: String,
//    var nticeYn: String,
//    val safetalkYn: String,
//    var oneaskYn: String,
//    var rvwCnt: Int,
//    var totOrderAmt: Int
//)
//
//data class ResNotiData(var lastNo: String, var nticeList: List<ResListNoti>)
//data class ResListNoti(
//    var nticeNo: String,
//    var nticeTitl: String,
//    var nticeDesc: String,
//    var addDt: String,
//    var viewCnt: String
//)
//
//data class ResEventData(var lastNo: String, var eventList: List<ResListEvent>)
//data class ResListEvent(
//    var evenNo: String,
//    var evenTitl: String,
//    var listFilePath: String,
//    var imgFilePath: String,
//    var evenStatCd: String,
//    var evenStatNm: String,
//    var evenStDay: String,
//    var evenEdDay: String,
//    var evenDesc: String
//)
//
//data class ResListFaq(var dcList: ArrayList<FaqDcItem>, var recoList: List<FaqRecoItem>)
//data class FaqDcItem(
//    var cateNo: String,
//    var cateNm: String,
//    var viewNum: Int,
//    var isChoice: Boolean = false
//)
//
//data class FaqRecoItem(
//    var ansDesc: String,
//    var cateNo: String,
//    var quesDesc: String,
//    var quesNo: String,
//    var viewNum: Int,
//    var isView: Boolean = false
//)
//
//data class ResListOneQuest(val lastNo: String, val oneAskList: List<ListOneQuestItem>)
//
//@Parcelize
//data class ListOneQuestItem(
//    var askCd: String,
//    var askDesc: String,
//    var askNo: String,
//    var askStatCd: String,
//    var askStatNm: String,
//    var askTitl: String,
//    var imgFileNo: String?,
//    var replayAddDt: String,
//    var replayAnsDt: String?,
//    var replyDesc: String?,
//    var imgList: List<ResImgList>?
//) : Parcelable
//
//data class ResInfoCustCenter(
//    var custCntrImgfNoUrl: String,
//    var cntrDesc: String,
//    var callNo: String
//)
//
//data class ResSetting(
//    var appVer: String,
//    var commYn: String,
//    var eventYn: String,
//    var noticeYn: String,
//    var orderStatusYn: String,
//    var reqAnsYn: String,
//    var reviewWYn: String,
//    val safetalkYn: String
//)
//
data class ResSignIn(
    var appKey: String,
    var email: String,
    var name: String,
    var adtAuthCd: String,
    var phoneNo: String,
    var baskCnt: Int
)
//
//@Parcelize
//data class ResSignUp(var email: String, var phoneNo: String, var membJoinDt: String) : Parcelable
//
//data class ResCode(var codeDtlCd: String, var codeDtlNm: String, var isChoice: Boolean = false)
//
//@Parcelize
//data class ResFindId(var email: String, var phoneNo: String, var membJoinDt: String = "") :
//    Parcelable
//
//data class ResInfoLastAddr(
//    var addr1: String,
//    var addr2: String,
//    var areaCd: String,
//    var jibunAddr: String,
//    var xpos: String,
//    var ypos: String,
//    val town: String
//)
//
//data class ResInfoMy(
//    var adtAuthCd: String,
//    var adtAuthNm: String,
//    var email: String,
//    var emailRcvYn: String,
//    var nickNm: String,
//    var phoneNo: String,
//    var smsRcvYn: String
//)
//
//data class ResNowOrder(var shopNo: String, var nWatchingCnt: String, var nOrderCnt: String)
//
//data class ResListCode(var codeNo: String, var codeNm: String)
//
data class ResError(
    val status: String,
    val code: Int,
    val message: String,
    val path: String,
    val errorType: String
)
//
//@Parcelize
//data class ResImgList(val imgPath: String, val viewNum: Int) : Parcelable
//
data class ResDeliv(
    var addr1: String,
    val addr2: String?,
    var deliSeq: Int,
    var deliAreaNo: String,
    var oldAddr: String,
    var xpos: String,
    var ypos: String,
    var zipNo: String,
    var pos: Int = -1,
    val town: String
)
//
//// RSA
//data class ResPublicKey(val publicKey: String)
//
//data class Address(
//    var detailAddress: String,
//    var locationLat: Double,
//    var locationLon: Double,
//    var lotsAddress: String,
//    var streetAddress: String,
//    var zipNo: String
//)
//
@Parcelize
data class ResAddress(
    var addr: String,
    var addrSeq: Int,
    var deliAreaNo: String?,
    var oldAddr: String,
    var xpos: String,
    var ypos: String,
    var zipNo: String?,
    var rnMgtSn: String?,
    var udrtYn: String?,
    var buldMnnm: String?,
    var buldSlno: String?,
    var addrDet: String?
) : Parcelable
//
//// Address Rest Api
//data class ResAddressSearch(val results: ResAddressResult)
//data class ResAddressResult(val common: ResAddressCommon, val juso: List<ResAddressJuso>)
//data class ResAddressCommon(
//    val errorMessage: String,
//    val countPerPage: String,
//    val totalCount: String,
//    val errorCode: String,
//    val currentPage: String
//)
//
//data class ResAddressJuso(
//    val detBdNmList: String,
//    val engAddr: String,
//    val rn: String,
//    val emdNm: String,
//    val zipNo: String,
//    val roadAddrPart2: String,
//    val emdNo: String,
//    val sggNm: String,
//    val jibunAddr: String,
//    val siNm: String,
//    val roadAddrPart1: String,
//    val bdNm: String,
//    val admCd: String,
//    val udrtYn: String,
//    val lnbrMnnm: String,
//    val roadAddr: String,
//    val lnbrSlno: String,
//    val buldMnnm: String,
//    val bdKdcd: String,
//    val liNm: String,
//    val rnMgtSn: String,
//    val mtYn: String,
//    val bdMgtSn: String,
//    val buldSlno: String
//)
//
data class ResLocationSearch(val results: ResLocationResult)
data class ResLocationResult(val common: ResLocationCommon, val juso: List<ResLocationJuso>)
data class ResLocationCommon(
    val errorMessage: String,
    val countPerPage: String,
    val totalCount: String,
    val errorCode: String,
    val currentPage: String
)
//
data class ResLocationJuso(
    val buldMnnm: String,
    val rnMgtSn: String,
    val bdNm: String,
    val entX: String,
    val entY: String,
    val admCd: String,
    val bdMgtSn: String,
    val rbuldSlnonMgtSn: String,
    val udrtYn: String
)
//
//// Kakao Rest Api
//// 키워드로 장소 검색
//data class ResKakaoKeywordSearch(val meta: KakaoMeta, val documents: List<KakaoDocument>)
//data class KakaoMeta(
//    val total_count: Int,
//    val pageable_count: Int,
//    val is_end: Boolean,
//    val same_name: KakaoSameName
//)
//
//data class KakaoSameName(
//    val region: ArrayList<String>,
//    val keyword: String,
//    val selected_region: String
//)
//
//data class KakaoDocument(
//    val id: String,
//    val place_name: String,
//    val category_name: String,
//    val category_group_code: String,
//    val category_group_name: String,
//    val phone: String,
//    val address_name: String,
//    val road_address_name: String,
//    val x: Double,
//    val y: Double,
//    val place_url: String,
//    val distance: String
//)
//
//
//// 좌표로 행정구역정보 받기
//data class ResKakaoCoordRegion(
//    val meta: KakaoCoordMeta,
//    val documents: List<KakaoCoordRegionDocument>
//)
//
//data class KakaoCoordRegionDocument(
//    val region_type: String,
//    val address_name: String,
//    val region_1depth_name: String,
//    val region_2depth_name: String,
//    val region_3depth_name: String,
//    val region_4depth_name: String,
//    val code: String,
//    val x: Double,
//    val y: Double
//)
//
//// 좌표계 변환
//data class ResKakaoTransCoord(
//    val meta: KakaoTransCoordMeta,
//    val documents: List<KakaoTransCoordDocument>
//)
//
//data class KakaoTransCoordMeta(val total_count: Int)
//data class KakaoTransCoordDocument(val x: Double, val y: Double)