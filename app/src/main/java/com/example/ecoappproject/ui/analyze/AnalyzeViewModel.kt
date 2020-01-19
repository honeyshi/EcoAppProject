package com.example.ecoappproject.ui.analyze

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnalyzeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is analyze Fragment"
    }
    val text: LiveData<String> = _text

    private val ingredientNameRU = MutableLiveData<String>()
    private val ingredientNameEN = MutableLiveData<String>()
    private val ingredientRating = MutableLiveData<Int>()
    private val ingredientDescription = MutableLiveData<String>()

    fun setIngredientNameRU(ingredientNameRU: String?){
        this.ingredientNameRU.value = ingredientNameRU
    }

    fun getIngredientNameRU() : LiveData<String> = ingredientNameRU

    fun setIngredientNameEN(ingredientNameEN: String?){
        this.ingredientNameEN.value = ingredientNameEN
    }

    fun getIngredientNameEN() : LiveData<String> = ingredientNameEN

    fun setIngredientRating(ingredientRating: Int?){
        this.ingredientRating.value = ingredientRating
    }

    fun getIngredientRating() : LiveData<Int> = ingredientRating

    fun setIngredientDescription(ingredientDescription: String?){
        this.ingredientDescription.value = ingredientDescription
    }

    fun getIngredientDescription() : LiveData<String> = ingredientDescription
}