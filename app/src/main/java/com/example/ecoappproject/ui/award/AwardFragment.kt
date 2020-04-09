package com.example.ecoappproject.ui.award

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.AwardObject

class AwardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_award, container, false)

        AwardObject.clearAwardItemList()
        AwardObject.getAwards(
            activity!!.applicationContext,
            root.findViewById(R.id.award_recycler_view)
        )

        return root
    }
}