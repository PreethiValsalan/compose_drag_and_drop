package com.development.draganddrop.di

import com.development.draganddrop.api.SecurityInterceptor
import com.development.draganddrop.api.ServiceApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://restcountries.com/"
@InstallIn(SingletonComponent::class)
@Module
class MainAppModule {

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }

    @Provides
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addNetworkInterceptor(logging)
        okHttpClient.addInterceptor(SecurityInterceptor())
        return okHttpClient.build()
    }

    @Provides
    fun provideJsonBuilder() : Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build();
    }

    @Provides
    fun provideRetrofit(jsonBuilder: Moshi, okHttpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(jsonBuilder))
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    fun getServiceApi(retrofit: Retrofit): ServiceApi {
        return retrofit.create(ServiceApi::class.java)
    }

}