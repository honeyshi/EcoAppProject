package com.example.ecoappproject.ui.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.R


class AnalyzeIngredientNotFoundFragment : Fragment() {
    private lateinit var analyzeViewModel: AnalyzeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        analyzeViewModel =
            ViewModelProviders.of(requireActivity()).get(AnalyzeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_analyze_one_ingredient_not_found, container, false)

        val textViewIngredientName  =
            root.findViewById<TextView>(R.id.text_view_one_ingredient_result_header_not_found)

        analyzeViewModel.getIngredientNameEN().observe(this, Observer {
            textViewIngredientName.text = it
        })

        return root
    }
}