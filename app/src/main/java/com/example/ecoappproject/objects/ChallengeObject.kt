package com.example.ecoappproject.objects

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.CHALLENGE_DATABASE
import com.example.ecoappproject.CHALLENGE_DATABASE_IS_STARTED
import com.example.ecoappproject.CHALLENGE_DATABASE_NAME
import com.example.ecoappproject.USERS_DATABASE
import com.example.ecoappproject.adapter.ChallengeAdapter
import com.example.ecoappproject.interfaces.OnChallengeItemClickListener
import com.example.ecoappproject.items.ChallengeItem
import com.google.firebase.FirebaseApp
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
        challengeItemClickListener: OnChallengeItemClickListener
    ) {
        Log.w("Challenge Object", "Initialize recycler view")
        challengeRecyclerView = recyclerView
        challengeRecyclerView.layoutManager = LinearLayoutManager(context)
        challengeAdapter = ChallengeAdapter(challengeItemList, challengeItemClickListener)
        challengeRecyclerView.adapter = challengeAdapter
    }

    fun clearChallengeItemsList() {
        challengeItemList.clear()
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
        FirebaseApp.initializeApp(context)
        //val challengeReference = FirebaseDatabase.getInstance().reference
        //val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
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
}