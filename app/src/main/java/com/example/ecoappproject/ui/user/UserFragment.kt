package com.example.ecoappproject.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.ecoappproject.LoginActivity
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnArticleItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.ui.articleDescription.ArticleDescriptionFragment
import com.example.ecoappproject.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class UserFragment : Fragment(), OnArticleItemClickListener {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        val userNameTextView = root.findViewById<TextView>(R.id.text_view_user_name)
        val userIconImageButton = root.findViewById<ImageButton>(R.id.image_button_user_icon)

        ArticleObject.clearArticleItemList()
        ArticleObject.getFavouriteArticles(
            activity!!.applicationContext,
            root.findViewById(R.id.favourite_articles_recycler_view),
            this,
            root.findViewById(R.id.text_view_no_favourites)
        )

        Log.w(
            "User Fragment",
            "Favourite articles ${ArticleObject.getArticleItemListSize()}"
        )

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        // If user is not sign in or anonymous image button should allow signing in
        if (currentUser == null || currentUser.isAnonymous) {
            Log.w(
                "User Fragment",
                "User is not sign in (Anonymous ${currentUser?.isAnonymous})"
            )
            userIconImageButton.setOnClickListener {
                startLoginActivity()
            }
        }
        // If user is sign in with Google set name and image from account
        else {
            Log.w("User Fragment", "User is sign in ${currentUser.displayName}")
            if (currentUser.displayName?.isEmpty() == true) {
                userNameTextView.text = currentUser.email
            } else {
                userNameTextView.text = currentUser.displayName
            }
            Log.w("User Fragment", "User photo url ${currentUser.photoUrl}")
            Glide.with(root).load(currentUser.photoUrl).into(userIconImageButton)
        }
        return root
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

        Log.w("User fragment:", "Save data to view model")
        homeViewModel.setArticleName(articleName)
        homeViewModel.setArticleReadingTime(articleReadingTime)
        homeViewModel.setArticleDescription(articleDescription)
        homeViewModel.setArticleIsFavourite(articleIsFavourite)
        homeViewModel.setArticleImageUri(articleImageUri)

        Log.w("User fragment:", "Start description fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, articleDescriptionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}