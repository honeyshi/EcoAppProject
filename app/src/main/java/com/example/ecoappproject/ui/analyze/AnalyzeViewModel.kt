package com.example.ecoappproject.ui.analyze

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecoappproject.items.IngredientItem

class AnalyzeViewModel : ViewModel() {

    /* Ingredient analysis */
    private val ingredientNameRU = MutableLiveData<String>()
    private val ingredientNameEN = MutableLiveData<String>()
    private val ingredientRating = MutableLiveData<Int>()
    private val ingredientDescription = MutableLiveData<String>()

    /* Composition analysis */
    private val isApproved = MutableLiveData<Boolean>()
    private val isNotFound = MutableLiveData<Boolean>()
    private val notFoundIngredients = MutableLiveData<String>()
    private val ingredientItemList = MutableLiveData<ArrayList<IngredientItem?>>()

    /* Ingredient analysis */
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

    /* Composition analysis */
    fun setIsApproved(isApproved: Boolean){
        this.isApproved.value = isApproved
    }

    fun getIsApproved() : LiveData<Boolean> = isApproved

    fun setIsNotFound(isNotFound: Boolean){
        this.isNotFound.value = isNotFound
    }

    fun getIsNotFound() : LiveData<Boolean> = isNotFound

    fun setNotFoundIngredients(notFoundIngredients: String){
        this.notFoundIngredients.value = notFoundIngredients
    }

    fun getNotFoundIngredients() : LiveData<String> = notFoundIngredients

    fun setIngredientItemList(ingredientItemList : ArrayList<IngredientItem?>){
        this.ingredientItemList.value = ingredientItemList
    }

    fun getIngredientItemList() : LiveData<ArrayList<IngredientItem?>> = ingredientItemList
}