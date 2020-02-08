package com.example.ecoappproject.ui.marking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.EcoMarkingObject

class MarkingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_marking, container, false)
        EcoMarkingObject.clearEcoMarkingList()
        EcoMarkingObject.getEcoMarkings(activity!!.applicationContext,
            root.findViewById(R.id.recycler_view_eco_marking))
        return root
    }
}