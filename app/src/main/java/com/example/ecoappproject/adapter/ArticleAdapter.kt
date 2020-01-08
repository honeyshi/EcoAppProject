package com.example.ecoappproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnItemClickListener
import com.example.ecoappproject.items.ArticleItem

class ArticleAdapter (private val articleItems : ArrayList<ArticleItem>,
                      private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var articleHead = itemView.findViewById<TextView>(R.id.text_view_article_name)
        private var favouriteButton = itemView.findViewById<ImageButton>(R.id.image_button_star)
        //TODO: use later private var articleImage= itemView.findViewById<ImageView>(R.id.image_view_article)

        fun bind(articleItem: ArticleItem, itemClickListener: OnItemClickListener) {
            articleHead.text = articleItem.header

            if (articleItem.isFavourite)
                favouriteButton.setImageResource(R.drawable.ic_star_pressed)
                else favouriteButton.setImageResource(R.drawable.ic_star_normal)

            itemView.setOnClickListener {
                itemClickListener.onItemClicked(articleItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return articleItems.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articleItems[position], itemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_view, parent, false)
        return ArticleViewHolder(view)
    }
}