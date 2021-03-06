package com.example.ecoappproject.ui.challenge

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnGetChallengeCurrentDayListener
import com.example.ecoappproject.interfaces.OnGetChallengeTrackerListener
import com.example.ecoappproject.objects.AwardObject
import com.example.ecoappproject.objects.ChallengeObject

class ChallengeStartedDescriptionFragment : Fragment() {

    private val challengeViewModel: ChallengeViewModel by activityViewModels()
    private val leafArraySize = 30
    private lateinit var challengeName: String
    private lateinit var challengeId: String
    private lateinit var buttonEndChallenge: Button
    private lateinit var textViewStartedChallenge: TextView
    private val TAG = ChallengeStartedDescriptionFragment::class.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =
            inflater.inflate(R.layout.fragment_challenge_started_description, container, false)

        val textViewChallengeStartedName =
            root.findViewById<TextView>(R.id.text_view_started_challenge_header)
        val textViewChallengeStartedDescription =
            root.findViewById<TextView>(R.id.text_view_started_challenge_description)
        buttonEndChallenge = root.findViewById(R.id.button_challenge_started)
        textViewStartedChallenge = root.findViewById(R.id.text_view_challenge_message_started)

        // Create array with leaf buttons
        Log.w(TAG, "Create leaf array")
        val leafImageButtonList = ArrayList<ImageButton>()
        val leafImageButtonStatusMap = HashMap<String, Boolean>()
        for (i in 1..leafArraySize) {
            val button = root.findViewWithTag<ImageButton>("image_button_leaf_$i")
            leafImageButtonList.add(button)
            leafImageButtonStatusMap["image_button_leaf_$i"] = false
        }


        // Get data from ViewModel
        challengeViewModel.getChallengeId().observe(viewLifecycleOwner, Observer {
            challengeId = it
            // Get array for button statuses
            Log.w(TAG, "Get button status")
            ChallengeObject.getDayStatusInChallengeTracker(
                it,
                object : OnGetChallengeTrackerListener {
                    override fun onGetChallengeTracker(challengeTrackerStatusList: HashMap<String, String>) {
                        Log.w(TAG, "Loaded day trackers ${challengeTrackerStatusList.size}")
                        // Set buttons color accordingly to tracker
                        for (i in 1..leafArraySize) {
                            if (challengeTrackerStatusList["day$i"]?.toBoolean() == true) {
                                leafImageButtonList[i - 1].setBackgroundResource(0)
                                leafImageButtonList[i - 1].setImageResource(
                                    R.drawable.ic_leaf_pressed
                                )
                                leafImageButtonStatusMap["image_button_leaf_$i"] = true
                            } else {
                                leafImageButtonList[i - 1].setBackgroundResource(0)
                                leafImageButtonList[i - 1].setImageResource(
                                    R.drawable.ic_leaf
                                )
                            }
                        }
                    }
                })
            // Get current day for challenge
            Log.w(TAG, "Get current day")
            ChallengeObject.getCurrentDayForChallenge(
                it,
                object : OnGetChallengeCurrentDayListener {
                    override fun onGetChallengeCurrentDay(currentDay: Int) {
                        Log.w(TAG, "Set listener for current day button")
                        // Set click listener for current leaf button
                        if (leafImageButtonStatusMap["image_button_leaf_$currentDay"] == false) {
                            leafImageButtonList[currentDay - 1].setBackgroundResource(0)
                            leafImageButtonList[currentDay - 1].setImageResource(R.drawable.ic_leaf_current)
                        }
                        leafImageButtonList[currentDay - 1].setOnClickListener {
                            Log.w(TAG, "Click on button")
                            leafImageButtonList[currentDay - 1].setBackgroundResource(0)
                            leafImageButtonList[currentDay - 1].setImageResource(R.drawable.ic_leaf_pressed)
                            ChallengeObject.setDayStatusInChallengeTracker(challengeId, currentDay)
                            val countMarkedButtons =
                                leafImageButtonStatusMap.filterValues { leafImage -> leafImage }
                                    .count()
                            Log.w(
                                TAG, "Marked buttons $countMarkedButtons"
                            )

                            // Stop challenge if user marks last day
                            // and validate whether he marked all days (case with 30 days)
                            if (currentDay == leafArraySize && countMarkedButtons == leafArraySize - 1) {
                                Log.w(TAG, "Challenge completed")
                                // Show dialog that user got award
                                val builder =
                                    AlertDialog.Builder(
                                        ContextThemeWrapper(
                                            activity!!,
                                            R.style.DialogTheme
                                        )
                                    )
                                builder.setTitle(resources.getString(R.string.dialog_end_challenge_header))
                                builder.setMessage(resources.getString(R.string.dialog_get_award_message))
                                builder.setPositiveButton(android.R.string.ok, null)
                                // 1) Hide button and message
                                // 2) Set status not started for challenge in Database
                                // 3) Delete tracker for challenge in Database
                                buttonEndChallenge.visibility = View.INVISIBLE
                                textViewStartedChallenge.visibility = View.INVISIBLE

                                ChallengeObject.setChallengeIsStarted(challengeName, "false")
                                ChallengeObject.deleteChallengeTracker(challengeId)
                                ChallengeObject.removeCurrentDayForChallenge(challengeId)
                                // Set completed status for award
                                AwardObject.setCompletedAward(challengeId)

                                builder.show()
                            }

                            // Case when not every day is marked
                            if (currentDay == leafArraySize && countMarkedButtons != leafArraySize - 1) {
                                Log.w(TAG, "Challenge is not completed")
                                // Show dialog that user not got award
                                val builder =
                                    AlertDialog.Builder(
                                        ContextThemeWrapper(
                                            activity!!,
                                            R.style.DialogTheme
                                        )
                                    )
                                builder.setTitle(resources.getString(R.string.dialog_end_challenge_header))
                                builder.setMessage(resources.getString(R.string.dialog_not_get_award_message))
                                builder.setPositiveButton(android.R.string.ok, null)
                                // 1) Hide button and message
                                // 2) Set status not started for challenge in Database
                                // 3) Delete tracker for challenge in Database
                                buttonEndChallenge.visibility = View.INVISIBLE
                                textViewStartedChallenge.visibility = View.INVISIBLE

                                ChallengeObject.setChallengeIsStarted(challengeName, "false")
                                ChallengeObject.deleteChallengeTracker(challengeId)
                                ChallengeObject.removeCurrentDayForChallenge(challengeId)

                                builder.show()
                            }
                        }
                    }
                })
        })

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
        // Show dialog window that user ends challenge
        val builder =
            AlertDialog.Builder(ContextThemeWrapper(requireActivity(), R.style.DialogTheme))
        builder.setTitle(resources.getString(R.string.dialog_end_challenge_header))
        builder.setMessage(resources.getString(R.string.dialog_end_challenge_message))
        // When user submit dialog
        // 1) Hide button and message
        // 2) Set status not started for challenge in Database
        // 3) Delete tracker for challenge in Database
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            buttonEndChallenge.visibility = View.INVISIBLE
            textViewStartedChallenge.visibility = View.INVISIBLE

            ChallengeObject.setChallengeIsStarted(challengeName, "false")
            ChallengeObject.deleteChallengeTracker(challengeId)
            ChallengeObject.removeCurrentDayForChallenge(challengeId)
        }
        // Close dialog on Cancel
        builder.setNegativeButton(android.R.string.cancel, null)

        builder.show()
    }
}