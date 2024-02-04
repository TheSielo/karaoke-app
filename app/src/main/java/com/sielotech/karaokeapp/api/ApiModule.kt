package com.sielotech.karaokeapp.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/** Provides Hilt with the dependencies it needs for database related injection. */
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    /** The base url used by Retrofit. */
    private const val BASE_URL = "https://api.sielotech.com/"

    /** Provides an [HttpLoggingInterceptor] for Retrofit.
     * @return A singleton instance of [HttpLoggingInterceptor].
     */
    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


    /** Provides an [OkHttpClient] for Retrofit.
     * @param httpLoggingInterceptor The interceptor to be added to this client.
     * @return A singleton instance of [HttpLoggingInterceptor].
     */
    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    /** Provides a [Retrofit] client.
     * @param okHttpClient The [OkHttpClient] to be added to this client.
     * @return A singleton instance of [Retrofit].
     */
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    /** Provides a [FuriganaApiService] client.
     * @param okHttpClient The [OkHttpClient] to be added to this client.
     * @return A singleton instance of [Retrofit].
     */
    @Singleton
    @Provides
    fun provideFuriganaApiService(retrofit: Retrofit): FuriganaApiService =
        retrofit.create(FuriganaApiService::class.java)
}
