package com.example.ecoappproject.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.activity.LoginActivity
import com.example.ecoappproject.R
import com.example.ecoappproject.USER_FRAGMENT_TAG
import com.example.ecoappproject.interfaces.OnArticleItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.objects.UserInformationObject
import com.example.ecoappproject.ui.articleDescription.ArticleDescriptionFragment
import com.example.ecoappproject.ui.award.AwardFragment
import com.example.ecoappproject.ui.challenge.ChallengeStartedFragment
import com.example.ecoappproject.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class UserFragment : Fragment(), OnArticleItemClickListener {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        val userNameTextView = root.findViewById<TextView>(R.id.text_view_user_name)
        val userDescriptionTextView = root.findViewById<TextView>(R.id.text_view_user_description)
        val userImageView = root.findViewById<ImageView>(R.id.image_view_user_icon)
        val editProfileImageButton =
            root.findViewById<ImageButton>(R.id.image_button_edit_user_profile)

        ArticleObject.clearFavouriteArticleItemList()
        ArticleObject.getFavouriteArticles(
            requireActivity().applicationContext,
            root.findViewById(R.id.favourite_articles_recycler_view),
            this,
            root.findViewById(R.id.text_view_no_favourites)
        )

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        // Set listener for edit button
        editProfileImageButton.setOnClickListener {
            onEditButtonClick(userNameTextView.text.toString())
        }
        // If user is sign in with Google set name and image from database
        if (currentUser?.isAnonymous == false) {
            Log.w(USER_FRAGMENT_TAG, "User from google - update information from database")
            UserInformationObject.updateUserInformationOnUI(
                userDescriptionTextView,
                userNameTextView,
                currentUser.uid
            )
            UserInformationObject.updateUserImageOnUI(
                requireActivity().applicationContext,
                userImageView,
                currentUser.uid
            )
        }

        // Set listeners for section buttons
        root.findViewById<Button>(R.id.button_user_fragment_challenge).setOnClickListener {
            onChallengeButtonClick()
        }

        root.findViewById<Button>(R.id.button_user_fragment_award).setOnClickListener {
            onAwardButtonClick()
        }
        return root
    }

    private fun onChallengeButtonClick() {
        Log.w(USER_FRAGMENT_TAG, "Click on challenge button")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, ChallengeStartedFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onAwardButtonClick() {
        Log.w(USER_FRAGMENT_TAG, "Click on award button")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, AwardFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onEditButtonClick(userName: String) {
        Log.w(USER_FRAGMENT_TAG, "Click on edit button")
        // If user is not sign in or anonymous - edit button should allow signing in
        if (firebaseAuth.currentUser == null || firebaseAuth.currentUser!!.isAnonymous) {
            Log.w(
                USER_FRAGMENT_TAG,
                "User is not sign in (Anonymous ${firebaseAuth.currentUser!!.isAnonymous}). Start signing in"
            )
            startLoginActivity()
        }
        // If user is sign - in start activity for editing profile
        else {
            Log.w(USER_FRAGMENT_TAG, "Start edit user profile fragment")
            userViewModel.setUserName(userName)
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, EditUserProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClicked(articleItem: ArticleItem?) {
        val articleName = articleItem?.header
        val articleDescription = articleItem?.longDescription
        val articleReadingTime = articleItem?.readingTime
        val articleIsFavourite = articleItem?.favourite?.toBoolean()
        val articleImageUri = articleItem?.imageUri
        val articleDescriptionFragment = ArticleDescriptionFragment()

        Log.w(USER_FRAGMENT_TAG, "Save data to view model")
        homeViewModel.setArticleName(articleName)
        homeViewModel.setArticleReadingTime(articleReadingTime)
        homeViewModel.setArticleDescription(articleDescription)
        homeViewModel.setArticleIsFavourite(articleIsFavourite)
        homeViewModel.setArticleImageUri(articleImageUri)

        Log.w(USER_FRAGMENT_TAG, "Start description fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, articleDescriptionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}