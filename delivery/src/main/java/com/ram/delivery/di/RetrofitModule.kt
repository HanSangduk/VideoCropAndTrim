package com.ram.delivery.di

import android.content.Context
import com.ram.delivery.BuildConfig
import com.ram.delivery.R
import com.ram.delivery.model.api.BaseApiService
import com.ram.delivery.model.api.JusoApiService
import com.ram.delivery.model.api.KakaoApiService
import com.ram.delivery.model.api.SafeTalkApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class JusoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoMapRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class JusoUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoUrl


@Module
@InstallIn( SingletonComponent::class )
object RetrofitModule {

    /**
     * @baseUrl
     * @Inject lateinit var baseurl: String 과 같이 사용
     */
    @BaseUrl
    @Provides
    fun provideBaseUrl(@ApplicationContext context: Context) = context.getString(R.string.rest_server_url)

    @JusoUrl
    @Provides
    fun provideJusoUrl(@ApplicationContext context: Context) = context.getString(R.string.juso_develop_server_url)

    @KakaoUrl
    @Provides
    fun provideKakaoUrl(@ApplicationContext context: Context) = context.getString(R.string.kakao_develop_server_url)


    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }else{
        OkHttpClient
            .Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @BaseRetrofit
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @BaseUrl baseUrl:String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()


    @JusoRetrofit
    @Provides
    fun provideJusoRetrofit(
        okHttpClient: OkHttpClient,
        @JusoUrl baseUrl:String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()


    @KakaoMapRetrofit
    @Provides
    fun provideKakaoRetrofit(
        okHttpClient: OkHttpClient,
        @KakaoUrl baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()


    @Singleton
    @Provides
    fun provideApiService(@BaseRetrofit retrofit: Retrofit) = retrofit.create(BaseApiService::class.java)

    @Singleton
    @Provides
    fun provideSafeTalkApiService(@BaseRetrofit retrofit: Retrofit) = retrofit.create(SafeTalkApiService::class.java)

    @Singleton
    @Provides
    fun provideJusoApiService(@JusoRetrofit retrofit: Retrofit) = retrofit.create(JusoApiService::class.java)

    @Singleton
    @Provides
    fun provideKakaoApiService(@KakaoMapRetrofit retrofit: Retrofit) = retrofit.create(KakaoApiService::class.java)

}