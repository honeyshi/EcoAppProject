package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AwardItem(
    val awardName: String? = "",
    val awardDescription: String? = "",
    val challengeId: String? = "",
    val completed: Boolean? = false
)