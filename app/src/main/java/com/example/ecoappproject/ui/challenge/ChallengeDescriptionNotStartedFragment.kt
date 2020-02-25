package com.example.ecoappproject.ui.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.R

class ChallengeDescriptionNotStartedFragment : Fragment() {
    private lateinit var challengeViewModel: ChallengeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        challengeViewModel =
            ViewModelProviders.of(requireActivity()).get(ChallengeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_not_started_challenge_description,
            container, false)

        val textViewChallengeName =
            root.findViewById<TextView>(R.id.text_view_challenge_not_started_name)
        val textViewChallengeDescription =
            root.findViewById<TextView>(R.id.text_view_challenge_not_started_description)

        challengeViewModel.getChallengeName().observe(this, Observer {
            textViewChallengeName.text = it
        })

        challengeViewModel.getChallengeDescription().observe(this, Observer {
            textViewChallengeDescription.text = it
        })

        return root
    }
}