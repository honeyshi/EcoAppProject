package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChallengeItem(
    val currentDay: Int? = 0,
    val description: String? = "",
    val id: String? = "",
    val imageUri: String? = "",
    val name: String? = "",
    val started: String? = "",
    val startedDescription: String? = ""
)