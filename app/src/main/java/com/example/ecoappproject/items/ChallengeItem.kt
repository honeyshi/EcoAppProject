package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChallengeItem (val name: String? = "",
                          val description: String? = "",
                          val started: String? = "",
                          val imageUri: String? = "")