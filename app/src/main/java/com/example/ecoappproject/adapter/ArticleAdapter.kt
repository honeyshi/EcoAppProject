package com.example.ecoappproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnArticleItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ArticleAdapter (private val articleItems : ArrayList<ArticleItem?>,
                      private val articleItemClickListener: OnArticleItemClickListener
) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val articleHead = itemView.findViewById<TextView>(R.id.text_view_article_name)
        private val favouriteButton = itemView.findViewById<ImageButton>(R.id.image_button_star)
        private val articleImage= itemView.findViewById<ImageView>(R.id.image_view_article)
        private val firebaseStorage = FirebaseStorage.getInstance()

        fun bind(articleItem: ArticleItem?, articleItemClickListener: OnArticleItemClickListener) {
            articleHead.text = articleItem?.header

            val gsReference = firebaseStorage
                .getReferenceFromUrl(articleItem?.imageUri.toString())
            // load image to imageView and set rounded corners
            Glide.with(itemView)
                .load(gsReference)
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(20,0)))
                .into(articleImage)

            if (articleItem?.favourite!!.toBoolean())
                favouriteButton.setImageResource(R.drawable.ic_star_pressed)
                else favouriteButton.setImageResource(R.drawable.ic_star_normal)

            favouriteButton.setOnClickListener{
                if (articleItem.favourite!!.toBoolean()) {
                    Log.w("Articles Adapter", "Delete from favourites")
                    favouriteButton.setImageResource(R.drawable.ic_star_normal)
                    ArticleObject.setArticleIsFavourite(itemView.context,
                        articleItem.header,
                        "false")
                    Toast.makeText(itemView.context, R.string.toast_text_delete_from_favourites, Toast.LENGTH_LONG).show()
                }
                else{
                    Log.w("Articles Adapter", "Add to favourites")
                    favouriteButton.setImageResource(R.drawable.ic_star_pressed)
                    ArticleObject.setArticleIsFavourite(itemView.context,
                        articleItem.header,
                        "true")
                    Toast.makeText(itemView.context, R.string.toast_text_add_to_favourites, Toast.LENGTH_LONG).show()
                }
            }

            itemView.setOnClickListener {
                Log.w("Articles Adapter", "Set on item click listener")
                articleItemClickListener.onItemClicked(articleItem)
            }
        }
    }

    override fun getItemCount(): Int = articleItems.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articleItems[position], articleItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_view, parent, false)
        return ArticleViewHolder(view)
    }
}