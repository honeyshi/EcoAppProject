package com.example.ecoappproject.ui.articleDescription

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.ui.home.HomeViewModel


class ArticleDescriptionFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_article_description, container, false)

        val textViewArticleHeader =
            root.findViewById<TextView>(R.id.text_view_article_whole_name)
        val textViewArticleReadingTime =
            root.findViewById<TextView>(R.id.text_view_article_whole_reading_time)
        val textViewArticleDescription =
            root.findViewById<TextView>(R.id.text_view_article_whole_description)
        val imageButtonArticleIsFavourite =
            root.findViewById<ImageButton>(R.id.image_button_article_whole_star)
        lateinit var articleName : String

        homeViewModel.getArticleName().observe(this, Observer {
            textViewArticleHeader.text = it
            articleName = it
        })

        homeViewModel.getArticleReadingTime().observe(this, Observer {
            textViewArticleReadingTime.text = it
        })

        homeViewModel.getArticleDescription().observe(this, Observer {
            textViewArticleDescription.text = it
        })

        homeViewModel.getArticleIsFavourite().observe(this, Observer {
            if (it) imageButtonArticleIsFavourite.setImageResource(R.drawable.ic_star_pressed)
            else imageButtonArticleIsFavourite.setImageResource(R.drawable.ic_star_normal)
        })

        imageButtonArticleIsFavourite.setOnClickListener {
            homeViewModel.getArticleIsFavourite().observe(this, Observer {
                // article is favourite - set not favourite
                if (it) {
                    Log.w(ContentValues.TAG, "Delete from favourites")
                    imageButtonArticleIsFavourite.setImageResource(R.drawable.ic_star_normal)
                    ArticleObject.setArticleIsFavourite(activity!!.baseContext,
                        articleName,
                        "false")
                    Toast.makeText(activity!!.baseContext, R.string.toast_text_delete_from_favourites, Toast.LENGTH_LONG)
                        .show()
                }
                // article is not favourite - set favourite
                else {
                    Log.w(ContentValues.TAG, "Add to favourites")
                    imageButtonArticleIsFavourite.setImageResource(R.drawable.ic_star_pressed)
                    ArticleObject.setArticleIsFavourite(activity!!.baseContext,
                        articleName,
                        "true")
                    Toast.makeText(activity!!.baseContext, R.string.toast_text_add_to_favourites, Toast.LENGTH_LONG)
                        .show()
                }
            })
        }

        return root
    }
}