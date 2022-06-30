package com.development.draganddrop.domain

import com.squareup.moshi.Json

data class Country(
    var index:Int = -1,
    @Json(name = "name") val name:String,
    @Json(name = "capital") val capital: String? = null,
    @Json(name = "flag") val flag: String,
    @Json(name = "independent") val independent: Boolean
 )

