package com.example.ecoappproject.ui.articleDescription

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.ui.home.HomeViewModel
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class ArticleDescriptionFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_article_description, container, false)

        val textViewArticleHeader =
            root.findViewById<TextView>(R.id.text_view_article_whole_name)
        val textViewArticleReadingTime =
            root.findViewById<TextView>(R.id.text_view_article_whole_reading_time)
        val textViewArticleDescription =
            root.findViewById<TextView>(R.id.text_view_article_whole_description)
        val imageButtonArticleIsFavourite =
            root.findViewById<ImageButton>(R.id.image_button_article_whole_star)
        val imageViewArticle =
            root.findViewById<ImageView>(R.id.image_view_article_whole)

        lateinit var articleName : String

        homeViewModel.getArticleName().observe(viewLifecycleOwner, Observer {
            textViewArticleHeader.text = it
            articleName = it
        })

        homeViewModel.getArticleReadingTime().observe(viewLifecycleOwner, Observer {
            textViewArticleReadingTime.text = it
        })

        homeViewModel.getArticleDescription().observe(viewLifecycleOwner, Observer {
            textViewArticleDescription.text = it
        })

        homeViewModel.getArticleIsFavourite().observe(viewLifecycleOwner, Observer {
            if (it) imageButtonArticleIsFavourite.setImageResource(R.drawable.ic_star_pressed)
            else imageButtonArticleIsFavourite.setImageResource(R.drawable.ic_star_normal)
        })

        homeViewModel.getArticleImageUri().observe(viewLifecycleOwner, Observer {
            val gsReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(it)
            Glide.with(root)
                .load(gsReference)
                .apply(
                    RequestOptions
                        .bitmapTransform(
                            RoundedCornersTransformation(100,0,
                                RoundedCornersTransformation.CornerType.BOTTOM)
                        ))
                .into(imageViewArticle)
        })

        imageButtonArticleIsFavourite.setOnClickListener {
            homeViewModel.getArticleIsFavourite().observe(viewLifecycleOwner, Observer {
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