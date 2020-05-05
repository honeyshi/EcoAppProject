package com.example.ecoappproject.ui.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.R
import com.example.ecoappproject.adapter.CompositionAnalysisAdapter

class AnalyzeCompositionResultFragment : Fragment() {
    private val analyzeViewModel: AnalyzeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_analyze_whole, container, false)
        val textViewApprovalMessage =
            root.findViewById<TextView>(R.id.text_view_whole_analysis_result_approval_message)
        val textViewNotFoundMessage =
            root.findViewById<TextView>(R.id.text_view_whole_analysis_result_not_found_message)
        val textViewNotFoundList =
            root.findViewById<TextView>(R.id.text_view_whole_analysis_result_not_found_list)
        val recyclerViewAnalyzeWhole =
            root.findViewById<RecyclerView>(R.id.recycler_view_analyze_whole)
        recyclerViewAnalyzeWhole.layoutManager =
            LinearLayoutManager(requireActivity().applicationContext)

        analyzeViewModel.getIsApproved().observe(viewLifecycleOwner, Observer {
            // approved
            if (it) textViewApprovalMessage.text =
                resources.getString(R.string.text_view_whole_analysis_result_approved_message)
            // not approved
            else textViewApprovalMessage.text =
                resources.getString(R.string.text_view_whole_analysis_result_not_approved_message)
        })

        analyzeViewModel.getIsNotFound().observe(viewLifecycleOwner, Observer {
            // not found
            if (it) {
                textViewNotFoundMessage.visibility = View.VISIBLE
                analyzeViewModel.getNotFoundIngredients()
                    .observe(viewLifecycleOwner, Observer { notFoundList ->
                        textViewNotFoundList.text = notFoundList
                        textViewNotFoundList.visibility = View.VISIBLE
                    })
            }
        })

        analyzeViewModel.getIngredientItemList().observe(viewLifecycleOwner, Observer {
            val compositionAnalysisAdapter = CompositionAnalysisAdapter(it)
            recyclerViewAnalyzeWhole.adapter = compositionAnalysisAdapter
        })

        return root
    }
}