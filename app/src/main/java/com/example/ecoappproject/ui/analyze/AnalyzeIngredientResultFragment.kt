package com.example.ecoappproject.ui.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.R

class AnalyzeIngredientResultFragment : Fragment() {
    private lateinit var analyzeViewModel: AnalyzeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        analyzeViewModel =
            ViewModelProviders.of(requireActivity()).get(AnalyzeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_analyze_one_ingredient, container, false)

        val textViewIngredientNameEN =
            root.findViewById<TextView>(R.id.text_view_one_ingredient_result_header_en)
        val textViewIngredientNameRU =
            root.findViewById<TextView>(R.id.text_view_one_ingredient_result_header_ru)
        val ratingBarIngredientRating =
            root.findViewById<RatingBar>(R.id.rating_bar_one_ingredient_result)
        val textViewIngredientDescription =
            root.findViewById<TextView>(R.id.text_view_one_ingredient_result_description)

        analyzeViewModel.getIngredientNameEN().observe(this, Observer {
            textViewIngredientNameEN.text = it
        })

        analyzeViewModel.getIngredientNameRU().observe(this, Observer {
            textViewIngredientNameRU.text = it
        })

        analyzeViewModel.getIngredientRating().observe(this, Observer {
            ratingBarIngredientRating.rating = it.toFloat()
        })

        analyzeViewModel.getIngredientDescription().observe(this, Observer {
            textViewIngredientDescription.text = it
        })

        return root
    }
}