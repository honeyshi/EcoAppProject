package com.example.ecoappproject.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.ui.articleDescription.ArticleDescriptionFragment


class HomeFragment : Fragment(), OnItemClickListener {

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
        ArticleObject.getArticles(activity!!.applicationContext, root.findViewById(R.id.home_recycler_view), this)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }

    override fun onItemClicked(articleItem: ArticleItem) {
        val articleName = articleItem.header
        val articleDescription = ArticleObject.getArticleDescriptionFromDictionary(articleName)
        val articleReadingTime = ArticleObject.getArticleReadingTimeFromDictionary(articleName)
        val articleIsFavourite = ArticleObject.getArticleIsFavouriteFromDictionary(articleName)
        val articleDescriptionFragment = ArticleDescriptionFragment()

        Log.w(ContentValues.TAG, "Save data to view model")
        homeViewModel.setArticleName(articleName)
        homeViewModel.setArticleReadingTime(articleReadingTime)
        homeViewModel.setArticleDescription(articleDescription)
        homeViewModel.setArticleIsFavourite(articleIsFavourite)

        Log.w(ContentValues.TAG, "Start fragment")
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, articleDescriptionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}