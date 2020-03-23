package com.example.ecoappproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnMarkingItemClickListener
import com.example.ecoappproject.items.EcoMarkingItem
import com.google.firebase.storage.FirebaseStorage

class EcoMarkingAdapter (private val ecoMarkingList : ArrayList<EcoMarkingItem?>,
                         private val markingItemClickListener: OnMarkingItemClickListener) :
    RecyclerView.Adapter<EcoMarkingAdapter.EcoMarkingViewHolder>() {

    class EcoMarkingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var ecoMarkingName = itemView.findViewById<TextView>(R.id.text_view_eco_marking_name)
        private var ecoMarkingImageView = itemView.findViewById<ImageView>(R.id.image_view_eco_marking)
        private val firebaseStorage = FirebaseStorage.getInstance()

        fun bind(ecoMarkingItem: EcoMarkingItem?, markingItemClickListener: OnMarkingItemClickListener) {
            ecoMarkingName.text = ecoMarkingItem!!.name

            val gsReference = firebaseStorage
                .getReferenceFromUrl(ecoMarkingItem.imageUri.toString())

            Glide.with(itemView)
                .load(gsReference)
                .into(ecoMarkingImageView)

            itemView.setOnClickListener{
                Log.w("Marking Adapter", "Set on click listener")
                markingItemClickListener.onMarkingItemClick(ecoMarkingItem)
            }
        }
    }

    override fun getItemCount(): Int = ecoMarkingList.size

    override fun onBindViewHolder(holder: EcoMarkingViewHolder, position: Int) {
        holder.bind(ecoMarkingList[position], markingItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcoMarkingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.marking_view, parent, false)
        return EcoMarkingViewHolder(view)
    }
}
