package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserInformationItem(
    val imageUrl: String? = "",
    val userDescription: String? = "",
    val userName: String? = ""
)