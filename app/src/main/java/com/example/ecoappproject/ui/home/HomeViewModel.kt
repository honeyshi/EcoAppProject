package com.example.ecoappproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val articleName = MutableLiveData<String>()
    private val articleReadingTime = MutableLiveData<String>()
    private val articleDescription = MutableLiveData<String>()
    private val articleIsFavourite = MutableLiveData<Boolean>()
    private val articleImageUri = MutableLiveData<String>()

    fun setArticleName(articleName: String?){
        this.articleName.value = articleName
    }

    fun getArticleName() : LiveData<String> = articleName

    fun setArticleReadingTime(articleReadingTime: String?){
        this.articleReadingTime.value = articleReadingTime
    }

    fun getArticleReadingTime() : LiveData<String> = articleReadingTime

    fun setArticleDescription(articleDescription: String?){
        this.articleDescription.value = articleDescription
    }

    fun getArticleDescription() : LiveData<String> = articleDescription

    fun setArticleIsFavourite(articleIsFavourite: Boolean?){
        this.articleIsFavourite.value = articleIsFavourite
    }

    fun getArticleIsFavourite() : LiveData<Boolean> = articleIsFavourite

    fun setArticleImageUri(articleImageUri: String?){
        this.articleImageUri.value = articleImageUri
    }

    fun getArticleImageUri() : LiveData<String> = articleImageUri
}