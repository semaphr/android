package com.semaphr.model

import android.os.Parcelable
import android.service.carrier.CarrierIdentifier
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckKeysRequest (
    var identifier: String
) : Parcelable {
}