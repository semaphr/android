package com.semaphr.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

sealed class SemaphrStatus {
    data class Block(val id: String, val title: String, val message: String, val imageLink: String?) : SemaphrStatus()
    data class Update(val id: String, val title: String, val message: String, val dismissable: Boolean, val imageLink: String?) : SemaphrStatus()
    data class Notify(val id: String, val title: String, val message: String, val dismissable: Boolean, val imageLink: String?) : SemaphrStatus()
    class None() : SemaphrStatus()
}

@Parcelize
data class SemaphrResponse (
    var rule: SemaphrRule?,
) : Parcelable {
}

@Parcelize
data class SemaphrRule (
    var id: Int,
    var title: String,
    var message: String,
    var dismissible: Boolean,
    @SerializedName("rule_type")
    var ruleType: String,
    var platform: String
) : Parcelable {

    fun toStatus(): SemaphrStatus {
        return when (ruleType) {
            "notify" -> SemaphrStatus.Notify(id.toString(), title, message, dismissible, null)
            "block" -> SemaphrStatus.Block(id.toString(), title, message, null)
            "update" -> SemaphrStatus.Update(id.toString(), title, message, dismissible, null)
            else -> SemaphrStatus.None()
        }
    }

}

@Parcelize
data class ErrorResponse (
    var error: String?,
) : Parcelable {
}