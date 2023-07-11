package com.semaphr.utils

import android.content.Context
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.provider.Settings
import com.semaphr.model.AppDetails
import java.util.*

fun getDeviceName(): String =
    if (MODEL.startsWith(MANUFACTURER, ignoreCase = true)) {
        MODEL
    } else {
        "$MANUFACTURER $MODEL"
    }.capitalize(Locale.ROOT)

class AppDetailsHelper constructor(private val context: Context) {

    var versionName: String = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    var versionCode: Int = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    var applicationId = context.packageName
    var deviceID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    var device = getDeviceName()

    fun toAppDetails(): AppDetails {
        return AppDetails(versionName, versionCode.toString(), device, deviceID, applicationId)
    }
}