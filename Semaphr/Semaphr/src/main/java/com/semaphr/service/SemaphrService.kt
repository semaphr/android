package com.semaphr.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.semaphr.BuildConfig
import com.semaphr.api.SemaphrApi
import com.semaphr.model.AppDetails
import com.semaphr.model.CheckKeysRequest
import com.semaphr.model.ErrorResponse
import com.semaphr.model.SemaphrStatus
import com.semaphr.utils.KSJsonDateTypeAdapterFactory
import com.semaphr.utils.KSResult
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


val nullOnEmptyConverterFactory = object : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object :
        Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
        override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
    }
}

class SemaphrService {
    private val semaphrApi: SemaphrApi

    init {
        semaphrApi = getRetrofit().create(SemaphrApi::class.java)
    }

    suspend fun getCurrentStatus(apiKey: String, appDetails: AppDetails): KSResult<SemaphrStatus> {
        try {
            val response = semaphrApi.getCurrentStatus(apiKey, appDetails)
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return if (it.rule?.platform == "android") {
                        KSResult.Success(it.rule?.toStatus() ?: SemaphrStatus.None())
                    } else {
                        KSResult.Success(SemaphrStatus.None())
                    }
                } ?: run {
                    return KSResult.Error(IOException("Failed to get current status. Please check that your API key is correct and that application id matches the one configured in the Semaphr dashboard."))
                }
            } else {
                response.errorBody()?.let {
                    val gson = Gson()
                    val message: ErrorResponse = gson.fromJson(it.charStream(), ErrorResponse::class.java)

                    return KSResult.Error(IOException(message.error))
                } ?: run {
                    return KSResult.Error(IOException("Failed to get current status. Please check that your API key is correct and that application id matches the one configured in the Semaphr dashboard."))
                }
            }
        } catch (e: Exception) {
            return KSResult.Error(e)
        }
    }

    suspend fun checkKeys(apiKey: String, identifier: String): KSResult<Boolean> {
        try {
            val request = CheckKeysRequest(identifier)
            val response = semaphrApi.checkKeys(apiKey, request)
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return KSResult.Success(it.valid)
                }
            }

            return KSResult.Error(IOException("The provided API key does not match any valid Semaphr project."))
        } catch (e: Exception) {
            return KSResult.Error(e)
        }
    }

    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder().setLenient().registerTypeAdapterFactory(
            KSJsonDateTypeAdapterFactory()
        ).create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkhttpClient())
            .build()
    }

    private fun getOkhttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(40, TimeUnit.SECONDS)
        builder.readTimeout(40, TimeUnit.SECONDS)
        builder.writeTimeout(40, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            /** add logging interceptor at last Interceptor*/
            builder.addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return builder.build()
    }

}