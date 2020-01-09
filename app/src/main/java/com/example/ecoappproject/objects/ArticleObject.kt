package com.example.ecoappproject.objects

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoappproject.*
import com.example.ecoappproject.adapter.ArticleAdapter
import com.example.ecoappproject.interfaces.OnItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ArticleObject {
    private val articleItemList : ArrayList<ArticleItem> = ArrayList()
    private val articleItemDescription = mutableMapOf<String?, String?>()
    private val articleItemReadingTime = mutableMapOf<String?, String?>()
    private val articleItemIsFavourite = mutableMapOf<String?, Boolean?>()
    private lateinit var articlesRecyclerView : RecyclerView
    private lateinit var articleAdapter: ArticleAdapter

    private fun createArticleItemList(header: String?, isFavourite: Boolean){
        articleItemList.add(ArticleItem(header, isFavourite))
        //TODO("not implemented : add image for article")
    }

    private fun createDictionaryArticleDescription(articleName: String?,
                                                   longDescription: String?){
        articleItemDescription[articleName] = longDescription
    }

    private fun createDictionaryArticleReadingTime(articleName: String?,
                                                   readingTime: String?){
        articleItemReadingTime[articleName] = readingTime
    }

    private fun createDictionaryArticleIsFavourite(articleName: String?,
                                                   isFavourite: Boolean?){
        articleItemIsFavourite[articleName] = isFavourite
    }

    fun getArticleDescriptionFromDictionary(articleName: String?) : String? =
        articleItemDescription[articleName]

    fun getArticleReadingTimeFromDictionary(articleName: String?) : String? =
        articleItemReadingTime[articleName]

    fun getArticleIsFavouriteFromDictionary(articleName: String?) : Boolean? =
        articleItemIsFavourite[articleName]

    fun setArticleIsFavourite(context: Context,
                              articleName: String?,
                              isFavourite: String){
        Log.w(ContentValues.TAG, "Start changing value in database")
        FirebaseApp.initializeApp(context)
        val articlesDatabase = FirebaseDatabase.getInstance()
        val articlesReference = articlesDatabase.reference
        val editArticleItem = articlesReference.child(ARTICLES_DATABASE)
        editArticleItem.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (editArticle in dataSnapshot.children){
                    if (editArticle.child(ARTICLES_DATABASE_HEADER).value.toString() == articleName){
                        Log.w(ContentValues.TAG, "Change value!")
                        editArticle.ref.child(ARTICLES_DATABASE_IS_FAVOURITE).setValue(isFavourite)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

        articleItemIsFavourite[articleName] = isFavourite.toBoolean()
    }

    fun clearArticleItemList() {
        articleItemList.clear()
    }

    private fun initRecyclerView(context: Context, recyclerView: RecyclerView, itemClickListener: OnItemClickListener){
        Log.w(ContentValues.TAG, "Initialize recycler view")
        articlesRecyclerView = recyclerView
        // назначаем менеджер, который отвечает за форму отображения элементов
        articlesRecyclerView.layoutManager = LinearLayoutManager(context)
        // назначаем адаптер
        articleAdapter = ArticleAdapter(articleItemList, itemClickListener)
        articlesRecyclerView.adapter = articleAdapter
    }

    fun getArticles(context: Context, recyclerView: RecyclerView, itemClickListener: OnItemClickListener){
        Log.w(ContentValues.TAG, "Come to get articles")
        FirebaseApp.initializeApp(context)
        val articlesDatabase = FirebaseDatabase.getInstance()
        val articlesReference = articlesDatabase.reference
        articlesReference.child(ARTICLES_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (article in dataSnapshot.children) {
                    Log.w(ContentValues.TAG, "Read from database")
                    // get all articles from database (header and short description
                    createArticleItemList(
                        article.child(ARTICLES_DATABASE_HEADER).value.toString(),
                        article.child(ARTICLES_DATABASE_IS_FAVOURITE).value.toString().toBoolean())
                    // save description from database in order to show later in new window
                    createDictionaryArticleDescription(
                        article.child(ARTICLES_DATABASE_HEADER).value.toString(),
                        article.child(ARTICLES_DATABASE_DESCRIPTION).value.toString())
                    createDictionaryArticleReadingTime(
                        article.child(ARTICLES_DATABASE_HEADER).value.toString(),
                        article.child(ARTICLES_DATABASE_READING_TIME).value.toString())
                    createDictionaryArticleIsFavourite(
                        article.child(ARTICLES_DATABASE_HEADER).value.toString(),
                        article.child(ARTICLES_DATABASE_IS_FAVOURITE).value.toString().toBoolean())
                }
                // initialize Recycler view with articles
                initRecyclerView(context, recyclerView, itemClickListener)
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }
}