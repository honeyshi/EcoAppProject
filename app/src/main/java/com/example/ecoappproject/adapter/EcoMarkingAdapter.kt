package com.example.ecoappproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.R
import com.example.ecoappproject.items.EcoMarkingItem

class EcoMarkingAdapter (private val ecoMarkingList : ArrayList<EcoMarkingItem?>) :
    RecyclerView.Adapter<EcoMarkingAdapter.EcoMarkingViewHolder>() {

    class EcoMarkingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var ecoMarkingName = itemView.findViewById<TextView>(R.id.text_view_eco_marking_name)
        //TODO: add image


        fun bind(ecoMarkingItem: EcoMarkingItem?) {
            ecoMarkingName.text = ecoMarkingItem!!.name
        }
    }

    override fun getItemCount(): Int = ecoMarkingList.size

    override fun onBindViewHolder(holder: EcoMarkingViewHolder, position: Int) {
        holder.bind(ecoMarkingList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcoMarkingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.marking_view, parent, false)
        return EcoMarkingViewHolder(view)
    }
}
