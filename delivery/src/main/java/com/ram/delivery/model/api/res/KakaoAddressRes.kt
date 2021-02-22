package com.ram.delivery.model.api.res

// 주소 검색
data class ResKakaoAddressSearch(
    val meta: KakaoAddressMeta,
    val documents: List<KakaoAddressDocument>
)

data class ResKakaoListAddressSearch(
    val meta: KakaoAddressMeta,
    val documents: List<KakaoAddressDocument>
)

data class KakaoAddressMeta(val total_count: Int, val pageable_count: Int, val is_end: Boolean)
data class KakaoAddressDocument(
    val address_type: String,
    val address_name: String,
    val y: String,
    val x: String,
    val road_address: KakaoRoadAddress?,
    val address: KakaoAddress
)

data class KakaoRoadAddress(
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val road_name: String,
    val underground_yn: String,
    val main_building_no: String,
    val sub_building_no: String,
    val building_name: String,
    val zone_no: String,
    val y: String,
    val x: String
)

data class KakaoAddress(
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val region_3depth_h_name: String,
    val h_code: String,
    val b_code: String,
    val mountain_yn: String,
    val main_address_no: String,
    val sub_address_no: String,
    val y: String,
    val x: String
)

// 좌표로 주소 변환하기
data class ResKakaoCoordAddress(val meta: KakaoCoordMeta, val documents: List<KakaoCoordDocument>)
data class KakaoCoordMeta(val total_count: Int)
data class KakaoCoordDocument(
    val address: KakaoCoordAddress,
    val road_address: KakaoCoordRoadAddress?
)

data class KakaoCoordAddress(
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val mountain_yn: String,
    val main_address_no: String,
    val sub_address_no: String,
    val zip_code: String
)

data class KakaoCoordRoadAddress(
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val road_name: String,
    val underground_yn: String,
    val main_building_no: String,
    val sub_building_no: String,
    val building_name: String,
    val zip_code: String
)


fun KakaoAddress.toTown(): String {
    return region_2depth_name.plus(" ").plus(region_3depth_name)
}