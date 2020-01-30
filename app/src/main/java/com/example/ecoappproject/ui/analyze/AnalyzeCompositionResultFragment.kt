package com.example.ecoappproject.ui.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.R
import com.example.ecoappproject.adapter.CompositionAnalysisAdapter

class AnalyzeCompositionResultFragment : Fragment() {
    private lateinit var analyzeViewModel: AnalyzeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        analyzeViewModel =
            ViewModelProviders.of(requireActivity()).get(AnalyzeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_analyze_whole, container, false)
        val textViewApprovalMessage =
            root.findViewById<TextView>(R.id.text_view_whole_analysis_result_approval_message)
        val textViewNotFoundMessage =
            root.findViewById<TextView>(R.id.text_view_whole_analysis_result_not_found_message)
        val textViewNotFoundList =
            root.findViewById<TextView>(R.id.text_view_whole_analysis_result_not_found_list)
        val recyclerViewAnalyzeWhole =
            root.findViewById<RecyclerView>(R.id.recycler_view_analyze_whole)
        recyclerViewAnalyzeWhole.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        analyzeViewModel.getIsApproved().observe(this, Observer {
            // approved
            if (it) textViewApprovalMessage.text = resources.getString(R.string.text_view_whole_analysis_result_approved_message)
            // not approved
            else textViewApprovalMessage.text = resources.getString(R.string.text_view_whole_analysis_result_not_approved_message)
        })

        analyzeViewModel.getIsNotFound().observe(this, Observer {
            // not found
            if (it) {
                textViewNotFoundMessage.visibility = View.VISIBLE
                analyzeViewModel.getNotFoundIngredients().observe(this, Observer { notFoundList ->
                    textViewNotFoundList.text = notFoundList
                    textViewNotFoundList.visibility = View.VISIBLE
                })
            }
        })

        analyzeViewModel.getIngredientItemList().observe(this, Observer{
            val compositionAnalysisAdapter = CompositionAnalysisAdapter(it)
            recyclerViewAnalyzeWhole.adapter = compositionAnalysisAdapter
        })

        return root
    }
}