package com.ram.delivery.model.api

import com.ram.delivery.model.api.res.JusoAddressResponse
import com.ram.delivery.model.api.res.ResKakaoAddressSearch
import com.ram.delivery.model.api.res.ResKakaoCoordAddress
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface KakaoApiService {

    @Headers("Authorization: KakaoAK 4cc5d638d079dc9c7b89859c2615544c")
    @GET("/v2/local/search/address")
    fun reqAddress(
        @Query("query") query: String
    ): Single<ResKakaoAddressSearch>

    @Headers("Authorization: KakaoAK 4cc5d638d079dc9c7b89859c2615544c")
    @GET("/v2/local/geo/coord2address")
    fun reqCoord2Address(
        @Query("x") x: String,
        @Query("y") y: String
    ): Single<ResKakaoCoordAddress>
}