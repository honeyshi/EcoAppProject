package com.example.ecoappproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnGetChallengeTrackerListener
import com.example.ecoappproject.items.AwardItem
import com.example.ecoappproject.objects.ChallengeObject

class AwardAdapter(private val awardItems: ArrayList<AwardItem?>) :
    RecyclerView.Adapter<AwardAdapter.AwardViewHolder>() {
    class AwardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewAwardName = itemView.findViewById<TextView>(R.id.text_view_award_name)
        private val textViewAwardDescription =
            itemView.findViewById<TextView>(R.id.text_view_award_description)
        private val progressBarAward = itemView.findViewById<ProgressBar>(R.id.progress_bar_award)
        private val textViewProgressAward =
            itemView.findViewById<TextView>(R.id.text_view_award_progress)

        fun bind(awardItem: AwardItem?) {
            textViewAwardName.text = awardItem?.awardName
            textViewAwardDescription.text = awardItem?.awardDescription

            ChallengeObject.getDayStatusInChallengeTracker(
                awardItem?.challengeId.toString(),
                object : OnGetChallengeTrackerListener {
                    override fun onGetChallengeTracker(challengeTrackerStatusList: HashMap<String, String>) {
                        Log.w(
                            "Award Adapter",
                            "Load day tracker for challenge ${awardItem?.challengeId}"
                        )
                        // Get amount of days with "true" value
                        var progress = 0
                        for (day in challengeTrackerStatusList) {
                            if (day.value.toBoolean()) progress++
                        }
                        progressBarAward.progress = progress
                        textViewProgressAward.text = "$progress/30"
                    }
                })
        }
    }

    override fun getItemCount(): Int = awardItems.size

    override fun onBindViewHolder(holder: AwardViewHolder, position: Int) {
        holder.bind(awardItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AwardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.award_view, parent, false)
        return AwardViewHolder(view)
    }
}