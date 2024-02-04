package com.sielotech.karaokeapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Setups and allows to retrieve the Retrofit client. */
object FuriganaApiHelper {
    /** The logging interceptor used by retrofit. */
    private val httpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    /** The OkHttpClient used by Retrofit. */
    private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .build()

    /** The Retrofit client used for calls at api.sielotech.com. It uses GSON for serialization. */
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sielotech.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** The Retrofit service for api.sielotech.com. */
    var furiganaService: FuriganaApiService = retrofit.create(FuriganaApiService::class.java)
}