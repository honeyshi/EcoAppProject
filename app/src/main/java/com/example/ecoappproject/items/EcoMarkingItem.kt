package com.example.ecoappproject.items
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class EcoMarkingItem (val name: String? = "",
                           val description: String? = "",
                           val imageUri: String? = "")