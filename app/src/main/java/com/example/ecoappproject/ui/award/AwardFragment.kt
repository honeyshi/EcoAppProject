package com.example.ecoappproject.ui.award

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.AwardObject
import com.example.ecoappproject.ui.challenge.ChallengeStartedFragment
import com.example.ecoappproject.ui.user.UserFragment

class AwardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_award, container, false)

        AwardObject.clearAwardItemList()
        AwardObject.getAwards(
            requireActivity().applicationContext,
            root.findViewById(R.id.award_recycler_view)
        )

        return root
    }
}