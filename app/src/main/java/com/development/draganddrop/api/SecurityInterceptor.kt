package com.development.draganddrop.api

import okhttp3.Interceptor
import okhttp3.Response

const val PUBLIC_API_KEY = "38758d87e27475ce8f1b287692f947a5"
const val HASH_VALUE = "D36102160EB10EC2E5668876628AF105"
class SecurityInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalHttpUrl = chain.request().url
        val secureUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("apikey", PUBLIC_API_KEY)
            .addQueryParameter("ts", "1625703838785")
            .addQueryParameter("hash", HASH_VALUE.toLowerCase())
            .build()
        return chain.proceed(with(chain.request().newBuilder()){
            url(secureUrl)
        }.build())
    }
}