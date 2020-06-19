package com.example.ecoappproject.ui.challenge

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.CHALLENGE_FRAGMENT_TAG
import com.example.ecoappproject.classes.OnSwipeTouchListener
import com.example.ecoappproject.R
import com.example.ecoappproject.classes.Helper
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.objects.ChallengeObject
import com.example.ecoappproject.ui.home.HomeFragment
import com.example.ecoappproject.ui.marking.MarkingFragment

class ChallengeFragment : Fragment(), OnChallengeItemClickListener {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()
    private lateinit var helper: Helper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        helper = Helper(requireActivity())

        root.findViewById<TextView>(R.id.text_view_header_home_fragment).text =
            getString(R.string.text_view_top_header_challenge_fragment)

        root.findViewById<View>(R.id.switcher_round_home_fragment)
            .setBackgroundResource(R.drawable.ic_switch_round_challenge)

        ChallengeObject.clearChallengeItemsList()
        ChallengeObject.getChallenges(
            requireActivity().applicationContext,
            root.findViewById(R.id.home_recycler_view),
            this
        )

        root.findViewById<ConstraintLayout>(R.id.constraint_layout_home_fragment)
            .setOnTouchListener(object :
                OnSwipeTouchListener(requireActivity().applicationContext) {
                override fun onSwipeRight() {
                    Log.w(CHALLENGE_FRAGMENT_TAG, "Swipe right - Start eco marking fragment")
                    helper.replaceFragment(MarkingFragment())
                }

                override fun onSwipeLeft() {
                    Log.w(CHALLENGE_FRAGMENT_TAG, "Swipe left - Start articles fragment")
                    helper.replaceFragment(HomeFragment())
                }

                override fun onSwipeBottom() {}

                override fun onSwipeTop() {}
            })

        return root
    }

    override fun onChallengeItemClicked(challengeItem: ChallengeItem?) {
        val challengeName = challengeItem?.name
        val challengeDescription = challengeItem?.description
        val challengeIsStarted = challengeItem?.started
        val challengeImageUri = challengeItem?.imageUri
        val challengeId = challengeItem?.id

        Log.w(CHALLENGE_FRAGMENT_TAG, "Save data to view model")
        challengeViewModel.setChallengeName(challengeName)
        challengeViewModel.setChallengeDescription(challengeDescription)
        challengeViewModel.setChallengeImageUri(challengeImageUri)
        challengeViewModel.setChallengeIsStarted(challengeIsStarted)
        challengeViewModel.setChallengeId(challengeId)

        Log.w(CHALLENGE_FRAGMENT_TAG, "Start challenge description fragment")
        helper.replaceFragment(ChallengeDescriptionFragment())
    }
}