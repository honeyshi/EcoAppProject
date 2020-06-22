package com.example.ecoappproject.classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ecoappproject.R
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.ui.challenge.ChallengeViewModel
import com.example.ecoappproject.ui.home.HomeViewModel

class Helper(private val fragmentManager: FragmentManager) {
    fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =
            fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun saveArticleInfoToViewModel(homeViewModel: HomeViewModel, articleItem: ArticleItem?) {
        homeViewModel.setArticleName(articleItem?.header)
        homeViewModel.setArticleReadingTime(articleItem?.readingTime)
        homeViewModel.setArticleDescription(articleItem?.longDescription)
        homeViewModel.setArticleIsFavourite(articleItem?.favourite?.toBoolean())
        homeViewModel.setArticleImageUri(articleItem?.imageUri)
    }

    fun saveChallengeInfoToViewModel(
        challengeViewModel: ChallengeViewModel,
        challengeItem: ChallengeItem?
    ) {
        challengeViewModel.setChallengeName(challengeItem?.name)
        challengeViewModel.setChallengeDescription(challengeItem?.description)
        challengeViewModel.setChallengeImageUri(challengeItem?.imageUri)
        challengeViewModel.setChallengeIsStarted(challengeItem?.started)
        challengeViewModel.setChallengeId(challengeItem?.id)
        challengeViewModel.setChallengeStartedDescription(challengeItem?.startedDescription)
    }
}