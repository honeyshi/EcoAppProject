package com.example.ecoappproject.ui.challenge

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.ChallengeObject
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ChallengeDescriptionFragment : Fragment() {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()
    private lateinit var challengeName: String
    private lateinit var buttonStartChallenge: Button
    private lateinit var buttonEndChallenge: Button
    private lateinit var textViewStartedChallenge: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(
            R.layout.fragment_challenge_description,
            container, false
        )

        val textViewChallengeName =
            root.findViewById<TextView>(R.id.text_view_challenge_name)
        val textViewChallengeDescription =
            root.findViewById<TextView>(R.id.text_view_challenge_description)
        val imageViewChallenge =
            root.findViewById<ImageView>(R.id.image_view_challenge)
        buttonStartChallenge = root.findViewById(R.id.button_challenge_not_started)
        buttonEndChallenge = root.findViewById(R.id.button_challenge_started)
        textViewStartedChallenge = root.findViewById(R.id.text_view_challenge_message_started)

        // Get data from previous fragment
        challengeViewModel.getChallengeName().observe(viewLifecycleOwner, Observer {
            challengeName = it
            textViewChallengeName.text = it
        })

        challengeViewModel.getChallengeDescription().observe(viewLifecycleOwner, Observer {
            textViewChallengeDescription.text = it
        })

        challengeViewModel.getChallengeImageUri().observe(viewLifecycleOwner, Observer {
            val gsReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(it)
            Glide.with(root)
                .load(gsReference)
                .apply(
                    RequestOptions
                        .bitmapTransform(
                            RoundedCornersTransformation(
                                100, 0,
                                RoundedCornersTransformation.CornerType.BOTTOM
                            )
                        )
                )
                .into(imageViewChallenge)
        })

        // Change layout view in accordance of challenge status
        challengeViewModel.getChallengeIsStarted().observe(viewLifecycleOwner, Observer {
            if (it.toBoolean()) {
                buttonStartChallenge.visibility = View.INVISIBLE
                buttonEndChallenge.visibility = View.VISIBLE
                textViewStartedChallenge.visibility = View.VISIBLE
            } else {
                buttonStartChallenge.visibility = View.VISIBLE
                buttonEndChallenge.visibility = View.INVISIBLE
                textViewStartedChallenge.visibility = View.INVISIBLE
            }
        })

        // Set onClick listeners
        buttonStartChallenge.setOnClickListener {
            startChallenge()
        }

        buttonEndChallenge.setOnClickListener {
            endChallenge()
        }

        return root
    }

    private fun startChallenge() {
        // Set status started for challenge in Database
        ChallengeObject.setChallengeIsStarted(challengeName, "true")

        // Show dialog window that user starts challenge
        val builder = AlertDialog.Builder(ContextThemeWrapper(activity!!, R.style.DialogTheme))
        builder.setTitle(resources.getString(R.string.dialog_start_challenge_header))
        builder.setMessage(resources.getString(R.string.dialog_start_challenge_message))
        // When user submit dialog show fragment with started challenge
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            buttonStartChallenge.visibility = View.INVISIBLE
            buttonEndChallenge.visibility = View.VISIBLE
            textViewStartedChallenge.visibility = View.VISIBLE
        }
        // Close dialog on Cancel
        builder.setNegativeButton(android.R.string.cancel, null)

        builder.show()
    }

    private fun endChallenge() {
        // Set status started for challenge in Database
        ChallengeObject.setChallengeIsStarted(challengeName, "false")

        // Show dialog window that user starts challenge
        val builder = AlertDialog.Builder(ContextThemeWrapper(activity!!, R.style.DialogTheme))
        builder.setTitle(resources.getString(R.string.dialog_end_challenge_header))
        builder.setMessage(resources.getString(R.string.dialog_end_challenge_message))
        // When user submit dialog show fragment with not started challenge
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            buttonStartChallenge.visibility = View.VISIBLE
            buttonEndChallenge.visibility = View.INVISIBLE
            textViewStartedChallenge.visibility = View.INVISIBLE
        }
        // Close dialog on Cancel
        builder.setNegativeButton(android.R.string.cancel, null)

        builder.show()
    }
}