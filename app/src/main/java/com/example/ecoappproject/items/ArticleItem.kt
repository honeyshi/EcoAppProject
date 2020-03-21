package com.example.ecoappproject.items

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ArticleItem (val header : String? = "",
                        val longDescription : String? = "",
                        val readingTime : String? = "",
                        var favourite : String? = "",
                        val imageUri: String? = "")