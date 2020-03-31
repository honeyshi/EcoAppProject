package com.example.ecoappproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.items.ChallengeItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        /* Initialize Firebase */
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseReference = FirebaseDatabase.getInstance().reference

        initializeDatabaseForCurrentUser()

        /* Set bottom bar navigation */
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_analyze,
                R.id.navigation_map,
                R.id.navigation_user
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)

        /* Do not show action bar */
        NavigationUI.setupWithNavController(navView, navController)


        /* Settings for first time usage */
        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val firstOpened: Boolean = sharedPref.getBoolean("first_opened", true)

        if (firstOpened) {
            val intent = Intent(this, FirstTimeActivity::class.java)
            startActivity(intent)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("first_opened", false)
            editor.apply()
        }
    }

    // region Working with Database for user
    private fun initializeDatabaseForCurrentUser() {
        val currentUserId = firebaseAuth.currentUser?.uid
        Log.w("Main Activity", "Current user id: $currentUserId")

        firebaseReference.child(USERS_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(currentUserId.toString()).exists()) {
                    Log.w("Main Activity", "Current user with id $currentUserId exists in DB")
                    // If user exists in Firebase database update his/her database to current
                    updateArticlesDataForUserInDatabase(currentUserId)
                    updateChallengesDataForUserInDatabase(currentUserId)
                } else {
                    Log.w("Main Activity", "Current user with id $currentUserId not exists in DB")
                    // If user not exists add to Firebase database
                    addNewUserInDatabase(currentUserId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
            }
        })
    }

    private fun addNewUserInDatabase(userId: String?) {
        // Add values from articles database
        Log.w("Main Activity", "Set articles for new user")
        var countArticleItem = 0
        firebaseReference.child(ARTICLES_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (article in dataSnapshot.children) {
                    val articleItem =
                        article.getValue(ArticleItem::class.java)
                    countArticleItem++
                    val newRefArticles = firebaseReference
                        .child(USERS_DATABASE)
                        .child(userId.toString())
                        .child(ARTICLES_DATABASE)
                        .child("Article${countArticleItem}")
                    newRefArticles.setValue(articleItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
            }
        })

        var countChallengeItem = 0
        // Add values from challenge database
        Log.w("Main Activity", "Set challenges for new user")
        firebaseReference.child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (challenge in dataSnapshot.children) {
                    val challengeItem =
                        challenge.getValue(ChallengeItem::class.java)
                    countChallengeItem++
                    val newRefChallenges = firebaseReference
                        .child(USERS_DATABASE)
                        .child(userId.toString())
                        .child(CHALLENGE_DATABASE)
                        .child("Challenge${countChallengeItem}")
                    newRefChallenges.setValue(challengeItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
            }
        })
    }

    private fun updateArticlesDataForUserInDatabase(userId: String?) {
        var countArticleItem = 0
        firebaseReference.child(ARTICLES_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (article in dataSnapshot.children) {
                    val articleItem =
                        article.getValue(ArticleItem::class.java)
                    countArticleItem++
                    firebaseReference
                        .child(USERS_DATABASE)
                        .child(userId.toString())
                        .child(ARTICLES_DATABASE)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // If value from whole DB not exists in user's DB
                                if (!dataSnapshot.child(article.key.toString()).exists()) {
                                    // Add this value un user's DB
                                    Log.w("Main Activity", "I should add ${articleItem?.header}}")
                                    val newRefArticles = firebaseReference
                                        .child(USERS_DATABASE)
                                        .child(userId.toString())
                                        .child(ARTICLES_DATABASE)
                                        .child("Article${countArticleItem}")
                                    newRefArticles.setValue(articleItem)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                                Log.w("Main Activity", "Failed to read value.", error.toException())
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
            }
        })
    }

    private fun updateChallengesDataForUserInDatabase(userId: String?) {
        var countChallengeItem = 0
        firebaseReference.child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (challenge in dataSnapshot.children) {
                    val challengeItem =
                        challenge.getValue(ChallengeItem::class.java)
                    countChallengeItem++
                    firebaseReference
                        .child(USERS_DATABASE)
                        .child(userId.toString())
                        .child(CHALLENGE_DATABASE)
                        .addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // If value from whole DB not exists in user's DB
                                if (!dataSnapshot.child(challenge.key.toString()).exists()) {
                                    // Add this value un user's DB
                                    Log.w("Main Activity", "I should add ${challengeItem?.name}}")
                                    val newRefChallenges = firebaseReference
                                        .child(USERS_DATABASE)
                                        .child(userId.toString())
                                        .child(CHALLENGE_DATABASE)
                                        .child("Challenge${countChallengeItem}")
                                    newRefChallenges.setValue(challengeItem)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                                Log.w("Main Activity", "Failed to read value.", error.toException())
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
            }
        })
    }
    // endregion
}
