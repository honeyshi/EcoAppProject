package com.example.ecoappproject.objects

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.*
import com.example.ecoappproject.adapter.ChallengeAdapter
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.interfaces.OnGetChallengeCurrentDayListener
import com.example.ecoappproject.interfaces.OnGetChallengeTrackerListener
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.items.ChallengeTrackerItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object ChallengeObject {
    private val challengeItemList = ArrayList<ChallengeItem?>()
    private lateinit var challengeAdapter: ChallengeAdapter
    private lateinit var challengeRecyclerView: RecyclerView
    private val challengeReference = FirebaseDatabase.getInstance().reference
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private fun initRecyclerView(
        context: Context,
        recyclerView: RecyclerView,
        challengeItemClickListener: OnChallengeItemClickListener,
        textView: TextView? = null,
        isStarted: Boolean = false
    ) {
        Log.w(CHALLENGE_OBJECT_TAG, "Initialize recycler view")
        challengeRecyclerView = recyclerView
        challengeRecyclerView.layoutManager = LinearLayoutManager(context)
        challengeAdapter = ChallengeAdapter(challengeItemList, challengeItemClickListener)
        challengeRecyclerView.adapter = challengeAdapter
        if (isStarted) textView?.visibility = View.INVISIBLE
    }

    fun clearChallengeItemsList() {
        challengeItemList.clear()
    }

    fun getCurrentDayForChallenge(
        challengeId: String,
        onGetChallengeCurrentDayListener: OnGetChallengeCurrentDayListener
    ) {
        challengeReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challenge in dataSnapshot.children) {
                        if (challenge.child("id").value.toString() == challengeId) {
                            val challengeCurrentDay =
                                challenge.child(CHALLENGE_DATABASE_CURRENT_DAY).value.toString()
                                    .toInt()
                            Log.w(
                                CHALLENGE_OBJECT_TAG,
                                "Current day for challenge $challengeId is $challengeCurrentDay"
                            )
                            onGetChallengeCurrentDayListener.onGetChallengeCurrentDay(
                                challengeCurrentDay
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun removeCurrentDayForChallenge(challengeId: String) {
        challengeReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challenge in dataSnapshot.children) {
                        if (challenge.child("id").value.toString() == challengeId) {
                            Log.w(
                                CHALLENGE_OBJECT_TAG,
                                "Set current day 0 for challenge $challengeId"
                            )
                            challenge.ref.child(CHALLENGE_DATABASE_CURRENT_DAY).setValue(0)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun increaseCurrentDayForStartedChallenges() {
        challengeReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challenge in dataSnapshot.children) {
                        val challengeItem =
                            challenge.getValue(ChallengeItem::class.java)
                        // If challenge started increase current day for it
                        // by calculating difference between started date and current
                        if (challengeItem?.started?.toBoolean() == true) {
                            // Get date object for both dates
                            val currentDate = Calendar.getInstance().time
                            val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
                            val startedDate = dateFormat.parse(challengeItem.startedDate.toString())
                            // Get calendar object for both dates
                            val currentDateCalendar = Calendar.getInstance()
                            currentDateCalendar.time = currentDate
                            val startedDateCalendar = Calendar.getInstance()
                            startedDateCalendar.time = startedDate!!
                            // Calculate difference between dates in milliseconds
                            val difference =
                                currentDateCalendar.timeInMillis - startedDateCalendar.timeInMillis
                            // Convert difference from milliseconds to days
                            val daysDifference = (difference / (24 * 60 * 60 * 1000)).toInt()
                            Log.w(CHALLENGE_OBJECT_TAG, "Difference for dates is $daysDifference")
                            // Set current day for challenge as difference in dates + 1
                            challenge.ref.child(CHALLENGE_DATABASE_CURRENT_DAY)
                                .setValue(daysDifference + 1)
                            // If current day became 31 or more we should automatically finish this challenge
                            if (daysDifference + 1 > 30) {
                                deleteChallengeTracker(challengeItem.id.toString())
                                setChallengeIsStarted(challengeItem.name.toString(), "false")
                                removeCurrentDayForChallenge(challengeItem.id.toString())
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun getDayStatusInChallengeTracker(
        challengeId: String,
        onGetChallengeTrackerListener: OnGetChallengeTrackerListener
    ) {
        Log.w(CHALLENGE_OBJECT_TAG, "Get tracker for Challenge with id $challengeId")
        val challengeTrackerList = HashMap<String, String>()
        challengeReference
            .child(CHALLENGE_TRACKER_DATABASE)
            .child(currentUserId.toString())
            .child(challengeId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (day in dataSnapshot.children) {
                        challengeTrackerList[day.key.toString()] = day.value.toString()
                    }
                    onGetChallengeTrackerListener.onGetChallengeTracker(challengeTrackerList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun setDayStatusInChallengeTracker(challengeId: String, currentDay: Int) {
        Log.w(
            CHALLENGE_OBJECT_TAG,
            "Set true on day $currentDay in challenge tracker for Challenge with id $challengeId"
        )
        challengeReference
            .child(CHALLENGE_TRACKER_DATABASE)
            .child(currentUserId.toString())
            .child(challengeId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    challengeReference
                        .child(CHALLENGE_TRACKER_DATABASE)
                        .child(currentUserId.toString())
                        .child(challengeId)
                        .child("day$currentDay")
                        .setValue("true")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun createChallengeTracker(challengeId: String) {
        Log.w(CHALLENGE_OBJECT_TAG, "Create challenge tracker for Challenge with id $challengeId")
        challengeReference
            .child(CHALLENGE_TRACKER_DATABASE)
            .child(currentUserId.toString())
            .child(challengeId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    challengeReference
                        .child(CHALLENGE_TRACKER_DATABASE)
                        .child(currentUserId.toString())
                        .child(challengeId)
                        .setValue(ChallengeTrackerItem())
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun deleteChallengeTracker(challengeId: String) {
        Log.w(CHALLENGE_OBJECT_TAG, "Delete challenge tracker for Challenge with id $challengeId")
        challengeReference
            .child(CHALLENGE_TRACKER_DATABASE)
            .child(currentUserId.toString())
            .child(challengeId)
            .removeValue()
    }

    fun setChallengeIsStarted(
        challengeName: String,
        isStarted: String
    ) {
        challengeReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challenge in dataSnapshot.children) {
                        if (challenge.child(CHALLENGE_DATABASE_NAME).value.toString() == challengeName) {
                            Log.w(
                                CHALLENGE_OBJECT_TAG,
                                "Set isStarted $isStarted for challenge $challengeName"
                            )
                            // If we start challenge set current day 1
                            if (isStarted.toBoolean()) {
                                challenge.ref.child(CHALLENGE_DATABASE_CURRENT_DAY).setValue(1)
                                // remember started date for challenge
                                val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
                                val formattedDate = dateFormat.format(Calendar.getInstance().time)
                                challenge.ref.child(CHALLENGE_DATABASE_STARTED_DATE).setValue(
                                    formattedDate
                                )
                            }
                            // If we finish challenge remove started date for it
                            else {
                                challenge.ref.child(CHALLENGE_DATABASE_STARTED_DATE).removeValue()
                            }
                            challenge.ref.child(CHALLENGE_DATABASE_IS_STARTED).setValue(isStarted)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun getChallenges(
        context: Context,
        recyclerView: RecyclerView,
        challengeItemClickListener: OnChallengeItemClickListener
    ) {
        Log.w(CHALLENGE_OBJECT_TAG, "Start getting challenges from DataBase")
        challengeReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challenge in dataSnapshot.children) {
                        val challengeItem =
                            challenge.getValue(ChallengeItem::class.java)
                        Log.w(CHALLENGE_OBJECT_TAG, "Current challenge: ${challengeItem?.name}")
                        challengeItemList.add(challengeItem)
                    }
                    initRecyclerView(context, recyclerView, challengeItemClickListener)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun getStartedChallenges(
        context: Context,
        recyclerView: RecyclerView,
        challengeItemClickListener: OnChallengeItemClickListener,
        textView: TextView?
    ) {
        Log.w(CHALLENGE_OBJECT_TAG, "Start getting started challenges from DataBase")
        var isStarted = false
        challengeReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challenge in dataSnapshot.children) {
                        val challengeItem =
                            challenge.getValue(ChallengeItem::class.java)
                        Log.w(CHALLENGE_OBJECT_TAG, "Current challenge: ${challengeItem?.name}")
                        if (challengeItem?.started?.toBoolean() == true) {
                            isStarted = true
                            challengeItemList.add(challengeItem)
                        }
                    }
                    initRecyclerView(
                        context,
                        recyclerView,
                        challengeItemClickListener,
                        textView,
                        isStarted
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(CHALLENGE_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }
}