package com.example.ecoappproject.objects

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.ECO_MARKING_DATABASE
import com.example.ecoappproject.adapter.EcoMarkingAdapter
import com.example.ecoappproject.items.EcoMarkingItem
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object EcoMarkingObject {
    private val ecoMarkingList : ArrayList<EcoMarkingItem?> = ArrayList()
    private lateinit var ecoMarkingAdapter: EcoMarkingAdapter
    private lateinit var ecoMarkingRecyclerView: RecyclerView

    private fun sortEcoMarkingList(){
        ecoMarkingList.sortBy { it?.name }
    }

    fun clearEcoMarkingList(){
        ecoMarkingList.clear()
    }

    private fun initRecyclerView(context: Context, recyclerView: RecyclerView){
        Log.w(ContentValues.TAG, "Initialize recycler view")
        ecoMarkingRecyclerView = recyclerView
        // назначаем менеджер, который отвечает за форму отображения элементов
        ecoMarkingRecyclerView.layoutManager = LinearLayoutManager(context)
        // назначаем адаптер
        ecoMarkingAdapter = EcoMarkingAdapter(ecoMarkingList)
        ecoMarkingRecyclerView.adapter = ecoMarkingAdapter
    }

    fun getEcoMarkings(context: Context, recyclerView: RecyclerView){
        Log.w(ContentValues.TAG, "Start eco markings")
        FirebaseApp.initializeApp(context)
        val ecoMarkingDatabase = FirebaseDatabase.getInstance()
        val ecoMarkingReference = ecoMarkingDatabase.reference

        ecoMarkingReference.child(ECO_MARKING_DATABASE).addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ecoMarking in dataSnapshot.children) {
                        val ecoMarkingItem =
                            ecoMarking.getValue(EcoMarkingItem::class.java)
                        Log.w(ContentValues.TAG, "Current eco marking: ${ecoMarkingItem?.name}")
                        ecoMarkingList.add(ecoMarkingItem)
                    }
                    sortEcoMarkingList()
                    initRecyclerView(context, recyclerView)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }
}