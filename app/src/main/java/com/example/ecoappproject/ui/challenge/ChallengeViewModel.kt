package com.example.ecoappproject.ui.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChallengeViewModel : ViewModel() {

    private val challengeName = MutableLiveData<String>()
    private val challengeDescription = MutableLiveData<String>()
    private val challengeImageUri = MutableLiveData<String>()
    private val challengeIsStarted = MutableLiveData<String>()

    fun setChallengeName(challengeName: String?) {
        this.challengeName.value = challengeName
    }

    fun getChallengeName() : LiveData<String> = challengeName

    fun setChallengeDescription(challengeDescription: String?) {
        this.challengeDescription.value = challengeDescription
    }

    fun getChallengeDescription() : LiveData<String> = challengeDescription

    fun setChallengeImageUri(challengeImageUri: String?) {
        this.challengeImageUri.value = challengeImageUri
    }

    fun getChallengeImageUri() : LiveData<String> = challengeImageUri

    fun setChallengeIsStarted(challengeIsStarted: String?){
        this.challengeIsStarted.value = challengeIsStarted
    }

    fun getChallengeIsStarted() : LiveData<String> = challengeIsStarted
}