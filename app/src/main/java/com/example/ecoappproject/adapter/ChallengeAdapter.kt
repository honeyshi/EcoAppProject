package com.example.ecoappproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.items.ChallengeItem
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ChallengeAdapter (private val challengeItems : ArrayList<ChallengeItem?>,
                        private val challengeItemClickListener: OnChallengeItemClickListener
) :
    RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    class ChallengeViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        private var challengeName = itemView.findViewById<TextView>(R.id.text_view_challenge_name)
        private var challengeImage = itemView.findViewById<ImageView>(R.id.image_view_challenge)
        private val firebaseStorage = FirebaseStorage.getInstance()

        fun bind(challengeItem: ChallengeItem?, challengeItemClickListener: OnChallengeItemClickListener){
            challengeName.text = challengeItem?.name
            // get image from storage by its Uri in it
            val gsReference = firebaseStorage
                .getReferenceFromUrl(challengeItem?.imageUri.toString())
            // load image to imageView and set rounded corners
            Glide.with(itemView)
                .load(gsReference)
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(20,0)))
                .into(challengeImage)

            itemView.setOnClickListener {
                Log.w("Challenge Adapter", "Set on item click listener")
                challengeItemClickListener.onChallengeItemClicked(challengeItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return challengeItems.size
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bind(challengeItems[position], challengeItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.challenge_view, parent, false)
        return ChallengeViewHolder(view)
    }
}