package com.semaphr.api

import com.semaphr.model.AppDetails
import com.semaphr.model.CheckKeysRequest
import com.semaphr.model.CheckKeysResponse
import com.semaphr.model.SemaphrResponse
import retrofit2.Response
import retrofit2.http.*

interface SemaphrApi {

    @POST("status")
    suspend fun getCurrentStatus(@Header("PROJECT-KEY") apiKey: String, @Body request: AppDetails): Response<SemaphrResponse>

    @POST("validate")
    suspend fun checkKeys(@Header("PROJECT-KEY") apiKey: String, @Body request: CheckKeysRequest): Response<CheckKeysResponse>
}