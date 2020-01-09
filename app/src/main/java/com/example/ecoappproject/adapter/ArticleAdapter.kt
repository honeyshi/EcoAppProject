package com.example.ecoappproject.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject

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

            favouriteButton.setOnClickListener{
                if (articleItem.isFavourite) {
                    Log.w(ContentValues.TAG, "Delete from favourites")
                    favouriteButton.setImageResource(R.drawable.ic_star_normal)
                    ArticleObject.setArticleIsFavourite(itemView.context,
                        articleItem.header,
                        "false")
                    Toast.makeText(itemView.context, R.string.toast_text_delete_from_favourites, Toast.LENGTH_LONG).show()
                }
                else{
                    Log.w(ContentValues.TAG, "Add to favourites")
                    favouriteButton.setImageResource(R.drawable.ic_star_pressed)
                    ArticleObject.setArticleIsFavourite(itemView.context,
                        articleItem.header,
                        "true")
                    Toast.makeText(itemView.context, R.string.toast_text_add_to_favourites, Toast.LENGTH_LONG).show()
                }
            }

            itemView.setOnClickListener {
                Log.w(ContentValues.TAG, "Set on item click listener")
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