package com.example.ecoappproject.ui.challenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.R
import com.example.ecoappproject.classes.Helper
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.objects.ChallengeObject
import com.example.ecoappproject.ui.award.AwardFragment
import com.example.ecoappproject.ui.user.UserFragment

class ChallengeStartedFragment : Fragment(), OnChallengeItemClickListener {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()
    private val TAG = ChallengeStartedFragment::class.simpleName
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_challenge_started, container, false)
        helper = Helper(parentFragmentManager)

        ChallengeObject.clearChallengeItemsList()
        ChallengeObject.getStartedChallenges(
            requireActivity().applicationContext,
            root.findViewById(R.id.started_challenge_recycler_view),
            this,
            root.findViewById(R.id.text_view_no_started_challenges)
        )

        return root
    }

    override fun onChallengeItemClicked(challengeItem: ChallengeItem?) {
        Log.w(TAG, "Click on challenge item")
        val challengeName = challengeItem?.name
        val challengeStartedDescription = challengeItem?.startedDescription
        val challengeId = challengeItem?.id

        Log.w(TAG, "Save data to view model")
        challengeViewModel.setChallengeName(challengeName)
        challengeViewModel.setChallengeStartedDescription(challengeStartedDescription)
        challengeViewModel.setChallengeId(challengeId)

        Log.w(TAG, "Start challenge description fragment")
        helper.replaceFragment(ChallengeStartedDescriptionFragment())
    }
}