package com.example.ecoappproject.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val userName = MutableLiveData<String>()

    fun setUserName(userName: String?) {
        this.userName.value = userName
    }

    fun getUserName(): LiveData<String> = userName
}