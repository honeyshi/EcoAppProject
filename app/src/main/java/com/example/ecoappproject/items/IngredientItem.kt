package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class IngredientItem(val description: String? = "",
                          val name_en: String? = "",
                          val name_ru: String? = "",
                          val rating: Int? = 0)