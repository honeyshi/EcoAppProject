package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationItem(
    val geo: String? = "",
    val address: String? = "",
    val canRecycle: String? = "",
    val monday: String? = "",
    val tuesday: String? = "",
    val wednesday: String? = "",
    val thursday: String? = "",
    val friday: String? = "",
    val saturday: String? = "",
    val sunday: String? = ""
)