package com.sielotech.karaokeapp.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FuriganaApiService {
    @POST("furigana")
    suspend fun getFurigana(@Body body: Map<String,String>): Response<List<List<String>>>
}