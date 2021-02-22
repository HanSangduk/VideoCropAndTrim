package com.ram.delivery.model.api.req

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ReqAppReg(var appReg: String)

data class ReqAppKey(
    var appReg: String,
    var appVer: String,
    var deviId: String,
    var deviOsVer: String,
    var deviTpCd: String
)

data class TermsItem(var casNo: String, var agreeYn: String)

data class ReqSaveSchAddr(
    var addr: String,
    var deliAreaNo: String,
    var oldAddr: String,
    var xpos: String,
    var ypos: String
)

data class ReqMembSrhWordRequest(var srhWord: String)

data class ReqSaveDeliv(
    var addr1: String,
    var addr2: String?,
    var zipNo: String?,
    var jibunAddr: String,
    var xpos: String,
    var ypos: String,
    var areaInfo: String?,
    val town: String
)

@Parcelize
data class ReqShopInfo(
    var deliAreaNo: String,
    var shopNo: String,
    var xPos: String,
    var yPos: String
) : Parcelable

@Parcelize
data class ReqMenuInfo(
    var shopNo: String,
    var menuNo: String,
    var deliAreaNo: String,
    var xPos: String,
    var yPos: String
) : Parcelable

data class ReqBaskSave(
    var detMenuNo: Int,
    var menuNo: String,
    var orderCnt: Int,
    var shopNo: String,
    val listOptData: List<ListOptData>?
)

data class ListOptData(var optCateNo: String, var optNo: String)

data class ReqReviewList(var shopNo: String, var photoYn: String, var sortCd: String)

data class ReqShopNo(var shopNo: String)

data class ReqBaskDel(var cartNo: String, var delRange: String, var shopNo: String)

data class ReqSaveOrder(var membNo: String, var orderData: ReqOrderItem)
data class ReqOrderItem(
    var cartNo: String,
    var membNo: String,
    var detMenuNo: String,
    var orderCnt: Int,
    var orderAmt: Int,
    var deliAmt: Int,
    var totalAmt: Int,
    var deliAddr1: String,
    var deliAddr2: String,
    var phoneNo: String,
    var safeTelNo: String,
    var deliReq: String,
    var dcYn: String,
    var castListCnr: Int,
    var optMenuCnt: Int,
    var listTermsData: ArrayList<ReqTermsItem>,
    var listOptData: ArrayList<ReqSaveOptItem>
)

data class ReqTermsItem(var membNo: String, var xPos: String, var yPos: String)
data class ReqSaveOptItem(var optMenuNo: String)

@Parcelize
data class ReqOrderSave(
    var approvalDt: String = "",
    var approvalNo: String = "",
    var cardInfo: String = "",
    var deliAddr1: String = "",
    var deliAddr2: String = "",
    var deliReq: String = "",
    var installMonth: String = "",
    var noInterestYn: String = "",
    var orderCateNo: String = "",
    var orderDt: String = "",
    var orderNo: String = "",
    var orderSafeYn: String = "",
    var orderTelNo: String = "",
    var payNo: Int = 0,
    var vanCd: String = "",
    var deliReqChk: String = "",
    var casList: List<ReqCateList>? = null,
    var cardNm: String = "",
    var cancelNo: String = "",
    var xpos: String = "",
    var ypos: String = "",
    var deliAddrLandLot1: String = "",
    var deliAddrLandLot2: String = ""
) : Parcelable

@Parcelize
data class ReqCateList(var casAgreeYn: String, var casNo: String) : Parcelable

data class ReqPayCancel(var cancelNo: String, var orderDt: String, var orderNo: String)

@Parcelize
data class ReqOrderPreSave(
    var deliAreaNo: String,
    var orderAmt: Int,
    var shopNo: String,
    var orderType: String,
    var xpos: String,
    var ypos: String,
    var menuList: ArrayList<ReqMenuItem>
) : Parcelable

@Parcelize
data class ReqMenuItem(
    var addAmt: Int,
    var dcNo: String,
    var menuAmt: Int,
    var menuNm: String,
    var menuNo: String,
    var menuSeq: Int,
    var orderCnt: Int,
    var selMenuNm: String,
    val optList: ArrayList<ReqOptItem>
) : Parcelable

@Parcelize
data class ReqOptItem(
    val optAmt: Int,
    val optCateNo: String,
    val optNm: String,
    val optNo: String
) : Parcelable

data class ReqReviewSave(
    var orderNo: String,
    var rviewDesc: String,
    var rviewImgfNo: String,
    var rviewPt: Float,
    var shopNo: String
)

data class ReqSaveOneQuest(
    var askCd: String,
    var askDesc: String,
    var askTitl: String,
    var imgFileNo: String
)

data class ReqSignIn(var email: String, var password: String)

data class ReqEmailCheck(var email: String?, val jobType: String)

data class ReqSignUp(var email: String?, var pw: String?, var phoneNo: String?)
