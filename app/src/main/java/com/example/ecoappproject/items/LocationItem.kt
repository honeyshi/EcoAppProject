package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationItem(
    val geo: String? = "",
    val monday: String? = ""
)