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

class AnalyzeFragment : Fragment() {

    private lateinit var analyzeViewModel: AnalyzeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        analyzeViewModel =
            ViewModelProviders.of(this).get(AnalyzeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_analyze, container, false)
        val textView: TextView = root.findViewById(R.id.text_analyze)
        analyzeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}