package com.example.ecoappproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.items.IngredientItem
import com.example.ecoappproject.R

class CompositionAnalysisAdapter(private val compositionItems: ArrayList<IngredientItem?>) :
    RecyclerView.Adapter<CompositionAnalysisAdapter.CompositionAnalysisViewHolder>(){

    class CompositionAnalysisViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private var textViewIngredientNameEN =
            itemView.findViewById<TextView>(R.id.text_view_analysis_whole_component_header_en)
        private var textViewIngredientNameRU =
            itemView.findViewById<TextView>(R.id.text_view_analysis_whole_component_header_ru)
        private var ratingBarIngredientRating =
            itemView.findViewById<RatingBar>(R.id.rating_bar_analysis_whole_component_rating)

        fun bind(ingredientItem: IngredientItem?){
            textViewIngredientNameEN.text = ingredientItem!!.name_en
            textViewIngredientNameRU.text = ingredientItem.name_ru
            ratingBarIngredientRating.rating = ingredientItem.rating!!.toFloat()
        }
    }

    override fun getItemCount(): Int {
        return compositionItems.size
    }

    override fun onBindViewHolder(holder: CompositionAnalysisViewHolder, position: Int) {
        holder.bind(compositionItems[position])
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompositionAnalysisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.analysis_component_view, parent, false)
        return CompositionAnalysisViewHolder(view)
    }
}
