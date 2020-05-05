package com.example.ecoappproject.ui.challenge

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.CHALLENGE_FRAGMENT_TAG
import com.example.ecoappproject.OnSwipeTouchListener
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.objects.ChallengeObject
import com.example.ecoappproject.ui.home.HomeFragment
import com.example.ecoappproject.ui.marking.MarkingFragment

class ChallengeFragment : Fragment(), OnChallengeItemClickListener {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_challenge, container, false)

        ChallengeObject.clearChallengeItemsList()
        ChallengeObject.getChallenges(
            requireActivity().applicationContext,
            root.findViewById(R.id.challenge_recycler_view),
            this
        )

        root.findViewById<ConstraintLayout>(R.id.constraint_layout_challenge_fragment)
            .setOnTouchListener(object :
                OnSwipeTouchListener(requireActivity().applicationContext) {
                override fun onSwipeRight() {
                    Log.w(CHALLENGE_FRAGMENT_TAG, "Swipe right")
                    swipeRightListener()
                }

                override fun onSwipeLeft() {
                    Log.w(CHALLENGE_FRAGMENT_TAG, "Swipe left")
                    swipeLeftListener()
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
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, ChallengeDescriptionFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun swipeLeftListener() {
        Log.w(CHALLENGE_FRAGMENT_TAG, "Start articles fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, HomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun swipeRightListener() {
        Log.w(CHALLENGE_FRAGMENT_TAG, "Start eco marking fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, MarkingFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}