package com.semaphr.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppDetails (
    var version: String,
    var build: String,
    @SerializedName("device_type")
    var deviceType: String,
    @SerializedName("device_identifier")
    var deviceIdentifier: String,
    var identifier: String,
    var platform: String = "android"
) : Parcelable {
}