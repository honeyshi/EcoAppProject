package com.example.ecoappproject.ui.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecoappproject.R
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ChallengeDescriptionNotStartedFragment : Fragment() {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_not_started_challenge_description,
            container, false)

        val textViewChallengeName =
            root.findViewById<TextView>(R.id.text_view_challenge_not_started_name)
        val textViewChallengeDescription =
            root.findViewById<TextView>(R.id.text_view_challenge_not_started_description)
        val imageViewChallenge =
            root.findViewById<ImageView>(R.id.image_view_challenge_not_started)

        challengeViewModel.getChallengeName().observe(viewLifecycleOwner, Observer {
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
                            RoundedCornersTransformation(100,0,
                                RoundedCornersTransformation.CornerType.BOTTOM)
                        ))
                .into(imageViewChallenge)
        })

        return root
    }
}