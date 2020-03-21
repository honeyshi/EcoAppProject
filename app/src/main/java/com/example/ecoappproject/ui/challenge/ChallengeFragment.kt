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
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.objects.ChallengeObject
import com.example.ecoappproject.ui.home.HomeFragment
import com.example.ecoappproject.ui.marking.MarkingFragment

class ChallengeFragment : Fragment(), OnChallengeItemClickListener {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_challenge, container, false)

        ChallengeObject.clearChallengeItemsList()
        ChallengeObject.getChallenges(activity!!.applicationContext,
            root.findViewById(R.id.challenge_recycler_view),
            this)

        root.findViewById<ImageButton>(R.id.image_button_challenge_fragment_left).setOnClickListener {
            leftArrowClickListener()
        }

        root.findViewById<ImageButton>(R.id.image_button_challenge_fragment_right).setOnClickListener {
            rightArrowClickListener()
        }

        return root
    }

    override fun onChallengeItemClicked(challengeItem: ChallengeItem?) {
        val challengeName = challengeItem?.name
        val challengeDescription = challengeItem?.description
        val challengeIsStarted = challengeItem?.started
        val challengeImageUri = challengeItem?.imageUri

        Log.w("Challenge fragment:", "Save data to view model")
        challengeViewModel.setChallengeName(challengeName)
        challengeViewModel.setChallengeDescription(challengeDescription)
        challengeViewModel.setChallengeImageUri(challengeImageUri)

        if (challengeIsStarted!!.toBoolean()){
            Log.w("Challenge fragment:", "Open started challenge fragment " +
                    "'cause it has started value: $challengeIsStarted")
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, ChallengeDescriptionStartedFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
        else{
            Log.w("Challenge fragment:", "Open not started challenge fragment " +
                    "'cause it has started value: $challengeIsStarted")
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, ChallengeDescriptionNotStartedFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun rightArrowClickListener(){
        Log.w("Challenge fragment:", "Start articles fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, HomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun leftArrowClickListener(){
        Log.w("Challenge fragment:", "Start eco marking fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, MarkingFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}