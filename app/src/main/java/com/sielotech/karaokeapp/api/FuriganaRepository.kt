package com.sielotech.karaokeapp.api

import javax.inject.Inject

/** Entry point for all operations involving the furigana API.
 * @param furiganaApiService A [FuriganaApiService] singleton that's provided by Hilt through DI.
 */
class FuriganaRepository @Inject constructor(
    private val furiganaApiService: FuriganaApiService
) {
    suspend fun getFurigana(text: String): List<List<String>> {
        return furiganaApiService.getFurigana(mapOf("text" to text)).body()?: emptyList()
    }
}