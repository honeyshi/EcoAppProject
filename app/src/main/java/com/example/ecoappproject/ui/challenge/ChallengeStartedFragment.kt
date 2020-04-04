package com.example.ecoappproject.ui.challenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.objects.ChallengeObject

class ChallengeStartedFragment : Fragment(), OnChallengeItemClickListener {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_challenge_started, container, false)

        ChallengeObject.clearChallengeItemsList()
        ChallengeObject.getStartedChallenges(
            activity!!.applicationContext,
            root.findViewById(R.id.started_challenge_recycler_view),
            this,
            root.findViewById(R.id.text_view_no_started_challenges)
        )

        return root
    }

    override fun onChallengeItemClicked(challengeItem: ChallengeItem?) {
        Log.w("Challenge Started", "Click on challenge item")
        val challengeName = challengeItem?.name
        val challengeStartedDescription = challengeItem?.startedDescription

        Log.w("Challenge Started", "Save data to view model")
        challengeViewModel.setChallengeName(challengeName)
        challengeViewModel.setChallengeStartedDescription(challengeStartedDescription)

        Log.w("Challenge Started", "Start challenge description fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, ChallengeStartedDescriptionFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}