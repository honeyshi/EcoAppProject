package com.example.ecoappproject.ui.challenge

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.ChallengeObject

class ChallengeStartedDescriptionFragment : Fragment() {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()
    private lateinit var challengeName: String
    private lateinit var buttonEndChallenge: Button
    private lateinit var textViewStartedChallenge: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_challenge_started_description, container, false)

        val textViewChallengeStartedName =
            root.findViewById<TextView>(R.id.text_view_started_challenge_header)
        val textViewChallengeStartedDescription =
            root.findViewById<TextView>(R.id.text_view_started_challenge_description)
        buttonEndChallenge = root.findViewById(R.id.button_challenge_started)
        textViewStartedChallenge = root.findViewById(R.id.text_view_challenge_message_started)

        // Get data from ViewModel
        challengeViewModel.getChallengeName().observe(viewLifecycleOwner, Observer {
            challengeName = it
            textViewChallengeStartedName.text = it
        })

        challengeViewModel.getChallengeStartedDescription().observe(viewLifecycleOwner, Observer {
            textViewChallengeStartedDescription.text = it
        })

        // Set button click listener
        root.findViewById<Button>(R.id.button_challenge_started).setOnClickListener {
            endChallenge()
        }

        return root
    }

    private fun endChallenge() {
        // Set status started for challenge in Database
        ChallengeObject.setChallengeIsStarted(challengeName, "false")

        // Show dialog window that user starts challenge
        val builder = AlertDialog.Builder(ContextThemeWrapper(activity!!, R.style.DialogTheme))
        builder.setTitle(resources.getString(R.string.dialog_end_challenge_header))
        builder.setMessage(resources.getString(R.string.dialog_end_challenge_message))
        // When user submit dialog hide button and message
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            buttonEndChallenge.visibility = View.INVISIBLE
            textViewStartedChallenge.visibility = View.INVISIBLE
        }
        // Close dialog on Cancel
        builder.setNegativeButton(android.R.string.cancel, null)

        builder.show()
    }
}