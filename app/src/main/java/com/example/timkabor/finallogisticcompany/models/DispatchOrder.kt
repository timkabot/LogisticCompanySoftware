package com.example.timkabor.finallogisticcompany.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class DispatchOrder( // Actually, in API
        var id: String                      = "unknown",
        var status: String?                  = "unknown",
        var order: String                   = "unknown",
        var vehicle: String?                = "unknown",
        var creation_time: Date             = Date(0L),
        var source_longitude: Double        = 0.0,
        var source_latitude: Double         = 0.0,
        var destination_longitude: Double   = 0.0,
        var destination_latitude: Double    = 0.0,
        var delivery_window_start: Date     = Date(0L),
        var delivery_window_end: Date       = Date(0L),
        var source: String                  = "Unknown",
        var destination: String             = "Unknown",
        var deliver_to: String              = "Unknown",

        // Not in api yet
        var phone_number: String?           = "8-800-555-35-35",
        var phone_to_service: String        = "7-917-655-23-27"




):Parcelable
