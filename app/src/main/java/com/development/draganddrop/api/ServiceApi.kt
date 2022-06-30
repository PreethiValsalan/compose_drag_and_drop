package com.development.draganddrop.api

import com.development.draganddrop.domain.Country
import retrofit2.http.GET

interface ServiceApi {

    @GET("v2/all?fields=name,capital,flag")
    suspend fun getCountries() : List<Country>

}