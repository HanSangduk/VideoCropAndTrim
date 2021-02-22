package com.ram.delivery.model.sub

import com.ram.delivery.model.api.req.ReqShopInfo
import com.ram.delivery.model.api.res.JusoResponse
import com.ram.delivery.model.api.res.ResAddress
import com.ram.delivery.model.api.res.ResPopup
import io.reactivex.rxkotlin.toObservable

//fun List<ResPopup>.toMainPopupModels(): List<MainPopupModel> {
//    return map {
//        MainPopupModel(
//            popLandingCd = it.popLandingCd,
//            popuDesc = it.popuDesc,
//            popLandingDtl = it.popLandingDtl,
//            popuImgfNoUrl = it.popuImgfNoUrl,
//            popuNo = it.popuNo,
//            popuTitl = it.popuTitl,
//            viewNum = it.viewNum
//        )
//    }.toList()
//}
//
//
//fun ShopModel.toReqShopInfo(): ReqShopInfo {
//    return ReqShopInfo(
//        deliAreaNo = deliAreaNo,
//        shopNo = shopNo!!,
//        xPos = xPos!!,
//        yPos = yPos!!
//    )
//}

fun List<JusoResponse>.toResAddressList(): List<ResAddress> {
    return toObservable().map {
        ResAddress(
            addr = it.roadAddrPart1,
            oldAddr = it.jibunAddr,
            xpos = "",
            ypos = "",
            addrDet = "",
            addrSeq = 0,
            udrtYn = it.udrtYn,
            buldMnnm = it.buldMnnm,
            buldSlno = it.buldSlno,
            deliAreaNo = "",  // 행정구역코드
            rnMgtSn = it.rnMgtSn,
            zipNo = it.zipNo
        )
    }.toList().blockingGet()
}

fun List<JusoResponse>.toResAddressList(xPos:String, yPos:String): List<ResAddress> {
    return toObservable().map {
        ResAddress(
            addr = it.roadAddrPart1,
            oldAddr = it.jibunAddr,
            xpos = xPos,
            ypos = yPos,
            addrDet = "",
            addrSeq = 0,
            udrtYn = it.udrtYn,
            buldMnnm = it.buldMnnm,
            buldSlno = it.buldSlno,
            deliAreaNo = it.admCd,  // 행정구역코드(?)
            rnMgtSn = it.rnMgtSn,
            zipNo = it.zipNo
        )
    }.toList().blockingGet()
}

//fun ResMainNoti.toMainNoticeModels(): List<MainNoticeModel> {
//    val items = mutableListOf<MainNoticeModel>()
//    noticeInfo?.noticeNo?.let {
//        items.add(
//            MainNoticeModel(
//                no = noticeInfo.noticeNo,
//                title = noticeInfo.noticeTitle,
//                isNotice = true
//            )
//        )
//    }
//    eventInfo?.eventNo?.let {
//        items.add(
//            MainNoticeModel(
//                no = eventInfo.eventNo,
//                title = eventInfo.eventTitle,
//                isNotice = false
//            )
//        )
//    }
//    return items
//}
//
//fun ResOrderPaySave.toPayResultSuccessArgs(): PayResultSuccessArgs {
//    return PayResultSuccessArgs(
//        orderNo = orderNo,
//        orderAmt = orderAmt,
//        addDt = addDt,
//        menuDesc = menuDesc,
//        resultCode = resultCode
//    )
//}
//
//fun RegularListResponse.toRegularModels(): List<RegularItemModel> {
//    val items = mutableListOf<RegularItemModel>()
//    val titlesInfo = mutableListOf(
//        Pair(R.string.sub_title_safety_shop, R.string.tooltip_safety_shop),
//        Pair(R.string.sub_title_right_shop, R.string.tooltip_right_shop),
//        Pair(R.string.sub_title_call_shop, R.string.tooltip_call_shop)
//    )
//    val safeItems = listSData.map {
//        it.toRegularItemModel()
//    }
//    val rightItems = listPData.map {
//        it.toRegularItemModels()
//    }
//    val callItems = listTData.map {
//        it.toRegularItemModel()
//    }
//    val itemsGroup = listOf(safeItems, rightItems, callItems)
//
//    itemsGroup.forEachIndexed { index, list ->
//        if (list.isNotEmpty()) {
//            val titleId = titlesInfo[index].first
//            val tooltipId = titlesInfo[index].second
//            items.add(RegularHeaderItemModel(titleId, tooltipId))
//            items.addAll(list)
//        }
//    }
//
//    return items
//}
//
//
//fun RegularSafeResponse.toRegularItemModel(): RegularItemModel {
//    return RegularSafeItemModel(
//        basicDeliAmt = basicDeliAmt,
//        distanceInfo = distanceInfo,
//        minOrderAmt = minOrderAmt,
//        nowCnt = nowCnt,
//        orderCnt = orderCnt,
//        rviewCnt = rviewCnt,
//        rviewPt = rviewPt,
//        shopImgUrl = shopImgUrl,
//        shopNm = shopNm,
//        shopNo = shopNo,
//        viewNum = viewNum,
//        saleStatCd = saleStatCd,
//        cdnUrl = cdnUrl ?: "http://3.35.42.105:80/rc00002/_definst_/rc00002_400p_256k.stream/playlist.m3u8?t_starttime=1606867200&t_endtime=1606914059&t_hash=RID1pyvGiSrFLBAecGtZQ0nadr06wlm_iwReOZGhyzg="
//    )
//}
//
//fun RegularRightResponse.toRegularItemModels(): RegularItemModel {
//    return RegularRightItemModel(
//        basicDeliAmt = basicDeliAmt,
//        distanceInfo = distanceInfo,
//        minOrderAmt = minOrderAmt,
//        nowCnt = nowCnt,
//        orderCnt = orderCnt,
//        rviewCnt = rviewCnt,
//        rviewPt = rviewPt,
//        shopImgUrl = shopImgUrl,
//        shopNm = shopNm,
//        shopNo = shopNo,
//        viewNum = viewNum,
//        saleStatCd = saleStatCd
//    )
//}
//
//fun RegularCallResponse.toRegularItemModel(): RegularItemModel {
//    return RegularCallItemModel(
//        distanceInfo = distanceInfo,
//        shopNm = shopNm,
//        shopNo = shopNo,
//        viewNum = viewNum
//    )
//}