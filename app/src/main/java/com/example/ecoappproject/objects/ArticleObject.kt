package com.example.ecoappproject.objects

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.*
import com.example.ecoappproject.adapter.ArticleAdapter
import com.example.ecoappproject.interfaces.OnArticleItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ArticleObject {
    private val articleItemList = ArrayList<ArticleItem?>()
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter

    fun setArticleIsFavourite(
        context: Context,
        articleName: String?,
        isFavourite: String
    ) {
        Log.w("Article Object", "Start changing value in database")
        FirebaseApp.initializeApp(context)
        val articlesReference = FirebaseDatabase.getInstance().reference
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val editArticleItem = articlesReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(ARTICLES_DATABASE)
        editArticleItem.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (editArticle in dataSnapshot.children) {
                    if (editArticle.child(ARTICLES_DATABASE_HEADER).value.toString() == articleName) {
                        Log.w(
                            "Article Object",
                            "Set isFavourite $isFavourite for article $articleName"
                        )
                        editArticle.ref.child(ARTICLES_DATABASE_IS_FAVOURITE).setValue(isFavourite)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Article Object", "Failed to read value.", error.toException())
            }
        })

        articleItemList.map { articleItem ->
            if (articleItem?.header == articleName)
                articleItem?.favourite = isFavourite
        }
    }

    fun clearArticleItemList() {
        articleItemList.clear()
    }

    private fun initRecyclerView(
        context: Context,
        recyclerView: RecyclerView,
        articleItemClickListener: OnArticleItemClickListener
    ) {
        Log.w("Article Object", "Initialize recycler view")
        articlesRecyclerView = recyclerView
        // назначаем менеджер, который отвечает за форму отображения элементов
        articlesRecyclerView.layoutManager = LinearLayoutManager(context)
        // назначаем адаптер
        articleAdapter = ArticleAdapter(articleItemList, articleItemClickListener)
        articlesRecyclerView.adapter = articleAdapter
    }

    fun getArticles(
        context: Context,
        recyclerView: RecyclerView,
        articleItemClickListener: OnArticleItemClickListener
    ) {
        Log.w("Article Object", "Come to get articles")
        FirebaseApp.initializeApp(context)
        val articlesReference = FirebaseDatabase.getInstance().reference
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        articlesReference
            .child(USERS_DATABASE)
            .child(currentUserId.toString())
            .child(ARTICLES_DATABASE)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (article in dataSnapshot.children) {
                        val articleItem =
                            article.getValue(ArticleItem::class.java)
                        Log.w("Article object", "Current article name is ${articleItem?.header}")
                        Log.w("Article object", "Favourite status is ${articleItem?.favourite}")
                        articleItemList.add(articleItem)
                    }
                    initRecyclerView(context, recyclerView, articleItemClickListener)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Article Object", "Failed to read value.", error.toException())
                }
            })
    }
}