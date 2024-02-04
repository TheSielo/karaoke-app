package com.sielotech.karaokeapp.api

import javax.inject.Inject

class FuriganaRepository @Inject constructor(
    private val furiganaApiService: FuriganaApiService
) {
    suspend fun getFurigana(text: String): List<List<String>> {
        return furiganaApiService.getFurigana(mapOf("text" to text)).body()?: emptyList()
    }
}