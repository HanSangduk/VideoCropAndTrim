package com.ram.delivery.model.api

import com.ram.delivery.model.api.res.JusoAddressResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface JusoApiService {

    @GET("/addrlink/addrLinkApi.do")
    fun getAddress(@QueryMap queryMap: HashMap<String, Any?>): Single<JusoAddressResponse>
}