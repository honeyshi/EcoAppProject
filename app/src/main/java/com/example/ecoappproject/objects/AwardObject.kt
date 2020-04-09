package com.example.ecoappproject.objects

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.AWARD_DATABASE
import com.example.ecoappproject.adapter.AwardAdapter
import com.example.ecoappproject.items.AwardItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object AwardObject {
    private val awardItemList = ArrayList<AwardItem?>()
    private lateinit var awardRecyclerView: RecyclerView
    private lateinit var awardAdapter: AwardAdapter
    private val awardReference = FirebaseDatabase.getInstance().reference

    fun clearAwardItemList() {
        awardItemList.clear()
    }

    fun initRecyclerView(
        context: Context,
        recyclerView: RecyclerView
    ) {
        Log.w("Award Object", "Initialize Recycler view")
        awardRecyclerView = recyclerView
        awardRecyclerView.layoutManager = LinearLayoutManager(context)
        awardAdapter = AwardAdapter(awardItemList)
        awardRecyclerView.adapter = awardAdapter
    }

    fun getAwards(
        context: Context,
        recyclerView: RecyclerView
    ) {
        Log.w("Award Object", "Get awards from Database")
        awardReference.child(AWARD_DATABASE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (award in dataSnapshot.children){
                        val awardItem = award.getValue(AwardItem::class.java)
                        Log.w("Award Object", "Current award is ${awardItem?.awardName}")
                        awardItemList.add(awardItem)
                    }
                    initRecyclerView(context, recyclerView)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Award Object", "Failed to read value.", error.toException())
                }
            })
    }
}