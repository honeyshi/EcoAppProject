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
import com.example.ecoappproject.interfaces.OnGetChallengeTrackerListener
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.items.ChallengeTrackerItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        Log.w("Challenge Object", "Initialize recycler view")
        challengeRecyclerView = recyclerView
        challengeRecyclerView.layoutManager = LinearLayoutManager(context)
        challengeAdapter = ChallengeAdapter(challengeItemList, challengeItemClickListener)
        challengeRecyclerView.adapter = challengeAdapter
        if (isStarted) textView?.visibility = View.INVISIBLE
    }

    fun clearChallengeItemsList() {
        challengeItemList.clear()
    }

    fun getDayStatusInChallengeTracker(
        challengeId: String,
        onGetChallengeTrackerListener: OnGetChallengeTrackerListener
    ) {
        Log.w("Challenge Object", "Get tracker for Challenge with id $challengeId")
        //val challengeTrackerList = ArrayList<String>()
        val challengeTrackerList = HashMap<String, String>()
        challengeReference
            .child(CHALLENGE_TRACKER_DATABASE)
            .child(currentUserId.toString())
            .child(challengeId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (day in dataSnapshot.children) {
                        //challengeTrackerList.add(day.value.toString())
                        challengeTrackerList[day.key.toString()] = day.value.toString()
                    }
                    onGetChallengeTrackerListener.onGetChallengeTracker(challengeTrackerList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Challenge Object", "Failed to read value.", error.toException())
                }
            })
    }

    fun setDayStatusInChallengeTracker(challengeId: String, currentDay: Int) {
        Log.w(
            "Challenge Object",
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
                    Log.w("Challenge Object", "Failed to read value.", error.toException())
                }
            })
    }

    fun createChallengeTracker(challengeId: String) {
        Log.w("Challenge Object", "Create challenge tracker for Challenge with id $challengeId")
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
                    Log.w("Challenge Object", "Failed to read value.", error.toException())
                }
            })
    }

    fun deleteChallengeTracker(challengeId: String) {
        Log.w("Challenge Object", "Delete challenge tracker for Challenge with id $challengeId")
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
                                "Challenge Object",
                                "Set isStarted $isStarted for challenge $challengeName"
                            )
                            challenge.ref.child(CHALLENGE_DATABASE_IS_STARTED).setValue(isStarted)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Challenge Object", "Failed to read value.", error.toException())
                }
            })
    }

    fun getChallenges(
        context: Context,
        recyclerView: RecyclerView,
        challengeItemClickListener: OnChallengeItemClickListener
    ) {
        Log.w("Challenge Object", "Start getting challenges from DataBase")
        challengeReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (challenge in dataSnapshot.children) {
                        val challengeItem =
                            challenge.getValue(ChallengeItem::class.java)
                        Log.w("Challenge Object", "Current challenge: ${challengeItem?.name}")
                        challengeItemList.add(challengeItem)
                    }
                    initRecyclerView(context, recyclerView, challengeItemClickListener)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Challenge Object", "Failed to read value.", error.toException())
                }
            })
    }

    fun getStartedChallenges(
        context: Context,
        recyclerView: RecyclerView,
        challengeItemClickListener: OnChallengeItemClickListener,
        textView: TextView?
    ) {
        Log.w("Challenge Object", "Start getting started challenges from DataBase")
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
                        Log.w("Challenge Object", "Current challenge: ${challengeItem?.name}")
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
                    Log.w("Challenge Object", "Failed to read value.", error.toException())
                }
            })
    }
}