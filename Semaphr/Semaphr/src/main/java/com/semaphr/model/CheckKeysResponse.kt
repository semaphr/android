package com.semaphr.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckKeysResponse (
    var valid: Boolean
) : Parcelable {
}