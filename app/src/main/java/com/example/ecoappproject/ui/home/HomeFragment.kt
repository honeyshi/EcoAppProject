package com.example.ecoappproject.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.HOME_FRAGMENT_TAG
import com.example.ecoappproject.OnSwipeTouchListener
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnArticleItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.ui.articleDescription.ArticleDescriptionFragment
import com.example.ecoappproject.ui.challenge.ChallengeFragment
import com.example.ecoappproject.ui.marking.MarkingFragment


class HomeFragment : Fragment(), OnArticleItemClickListener {

    private val homeViewModel: HomeViewModel by activityViewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.findViewById<TextView>(R.id.text_view_header_home_fragment).text =
            getString(R.string.text_view_top_header_home_fragment)

        root.findViewById<View>(R.id.switcher_round_home_fragment)
            .setBackgroundResource(R.drawable.ic_switch_round_home)

        ArticleObject.clearArticleItemList()
        ArticleObject.getArticles(
            requireActivity().applicationContext,
            root.findViewById(R.id.home_recycler_view),
            this
        )

        root.findViewById<ConstraintLayout>(R.id.constraint_layout_home_fragment)
            .setOnTouchListener(object :
                OnSwipeTouchListener(requireActivity().applicationContext) {
                override fun onSwipeRight() {
                    Log.w(HOME_FRAGMENT_TAG, "Swipe right")
                    swipeRightListener()
                }

                override fun onSwipeLeft() {
                    Log.w(HOME_FRAGMENT_TAG, "Swipe left")
                    swipeLeftListener()
                }

                override fun onSwipeBottom() {}

                override fun onSwipeTop() {}
            })

        return root
    }

    override fun onItemClicked(articleItem: ArticleItem?) {
        val articleName = articleItem?.header
        val articleDescription = articleItem?.longDescription
        val articleReadingTime = articleItem?.readingTime
        val articleIsFavourite = articleItem?.favourite?.toBoolean()
        val articleImageUri = articleItem?.imageUri
        val articleDescriptionFragment = ArticleDescriptionFragment()

        Log.w(HOME_FRAGMENT_TAG, "Save data to view model")
        homeViewModel.setArticleName(articleName)
        homeViewModel.setArticleReadingTime(articleReadingTime)
        homeViewModel.setArticleDescription(articleDescription)
        homeViewModel.setArticleIsFavourite(articleIsFavourite)
        homeViewModel.setArticleImageUri(articleImageUri)

        Log.w(HOME_FRAGMENT_TAG, "Start description fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, articleDescriptionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun swipeLeftListener() {
        Log.w(HOME_FRAGMENT_TAG, "Start eco marking fragment")
        val markingFragment = MarkingFragment()
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, markingFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun swipeRightListener() {
        Log.w(HOME_FRAGMENT_TAG, "Start eco challenges fragment")
        val challengeFragment = ChallengeFragment()
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, challengeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}