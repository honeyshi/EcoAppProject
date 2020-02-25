package com.example.ecoappproject.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnArticleItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.ui.articleDescription.ArticleDescriptionFragment
import com.example.ecoappproject.ui.challenge.ChallengeFragment
import com.example.ecoappproject.ui.marking.MarkingFragment


class HomeFragment : Fragment(), OnArticleItemClickListener {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        ArticleObject.clearArticleItemList()
        ArticleObject.getArticles(activity!!.applicationContext,
            root.findViewById(R.id.home_recycler_view),
            this)

        root.findViewById<ImageButton>(R.id.image_button_home_fragment_right).setOnClickListener{
            rightArrowClickListener()
        }

        root.findViewById<ImageButton>(R.id.image_button_home_fragment_left).setOnClickListener{
            leftArrowClickListener()
        }

        return root
    }

    override fun onItemClicked(articleItem: ArticleItem) {
        val articleName = articleItem.header
        val articleDescription = ArticleObject.getArticleDescriptionFromDictionary(articleName)
        val articleReadingTime = ArticleObject.getArticleReadingTimeFromDictionary(articleName)
        val articleIsFavourite = ArticleObject.getArticleIsFavouriteFromDictionary(articleName)
        val articleDescriptionFragment = ArticleDescriptionFragment()

        Log.w("Home fragment:", "Save data to view model")
        homeViewModel.setArticleName(articleName)
        homeViewModel.setArticleReadingTime(articleReadingTime)
        homeViewModel.setArticleDescription(articleDescription)
        homeViewModel.setArticleIsFavourite(articleIsFavourite)

        Log.w("Home fragment:", "Start description fragment")
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, articleDescriptionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun rightArrowClickListener(){
        Log.w("Home fragment:", "Start eco marking fragment")
        val markingFragment = MarkingFragment()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, markingFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun leftArrowClickListener(){
        Log.w("Home fragment:", "Start eco challenges fragment")
        val challengeFragment = ChallengeFragment()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, challengeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}