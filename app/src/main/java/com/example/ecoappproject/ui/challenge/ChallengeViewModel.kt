package com.example.ecoappproject.ui.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChallengeViewModel : ViewModel() {

    private val challengeName = MutableLiveData<String>()
    private val challengeDescription = MutableLiveData<String>()

    fun setChallengeName(challengeName: String?) {
        this.challengeName.value = challengeName
    }

    fun getChallengeName() : LiveData<String> = challengeName

    fun setChallengeDescription(challengeDescription: String?) {
        this.challengeDescription.value = challengeDescription
    }

    fun getChallengeDescription() : LiveData<String> = challengeDescription
}