package com.example.ecoappproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.items.AwardItem
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.objects.ChallengeObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_loader.*


class LoaderActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        Glide.with(this).asGif().load(R.drawable.ic_loader_activity)
            .into(image_view_loader_activity_icon)

        // region Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseReference = FirebaseDatabase.getInstance().reference

        // endregion

        // region Settings for first time usage
        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val firstOpened: Boolean = sharedPref.getBoolean("first_opened", true)

        // If we open app in first time - run first time activity
        if (firstOpened) {
            val intent = Intent(this, FirstTimeActivity::class.java)
            startActivity(intent)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("first_opened", false)
            editor.apply()
        }
        // If not - increase challenge, initialize Firebase and start Main Activity after delay
        else {
            initializeDatabaseForCurrentUser()

            Log.w(LOADER_ACTIVITY_TAG, "Start increasing current day for challenges")
            ChallengeObject.increaseCurrentDayForStartedChallenges()

            Handler().postDelayed(startMainActivity, 2000)
        }

        // endregion
    }

    private val startMainActivity = Runnable {
        // do what you need to do here after the delay
        Log.w(LOADER_ACTIVITY_TAG, "Start main activity after delay")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // region Working with Database for user
    private fun initializeDatabaseForCurrentUser() {
        val currentUserId = firebaseAuth.currentUser?.uid
        Log.w(LOADER_ACTIVITY_TAG, "Current user id: $currentUserId")

        firebaseReference.child(USERS_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(currentUserId.toString()).exists()) {
                    Log.w(LOADER_ACTIVITY_TAG, "Current user with id $currentUserId exists in DB")
                    // If user exists in Firebase database update his/her database to current
                    updateArticlesDataForUserInDatabase(currentUserId)
                    updateChallengesDataForUserInDatabase(currentUserId)
                    updateAwardDataForUserInDatabase(currentUserId)
                } else {
                    Log.w(
                        LOADER_ACTIVITY_TAG,
                        "Current user with id $currentUserId not exists in DB"
                    )
                    // If user not exists add to Firebase database
                    addNewUserInDatabase(currentUserId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun addNewUserInDatabase(userId: String?) {
        // Add values from articles database
        Log.w(LOADER_ACTIVITY_TAG, "Set articles for new user")
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
                        .child("Article$countArticleItem")
                    newRefArticles.setValue(articleItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
            }
        })

        var countChallengeItem = 0
        // Add values from challenge database
        Log.w(LOADER_ACTIVITY_TAG, "Set challenges for new user")
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
                        .child("Challenge$countChallengeItem")
                    newRefChallenges.setValue(challengeItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
            }
        })

        var countAwardItem = 0
        // Add values from award database
        Log.w(LOADER_ACTIVITY_TAG, "Set awards for new user")
        firebaseReference.child(AWARD_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (award in dataSnapshot.children) {
                    val awardItem =
                        award.getValue(AwardItem::class.java)
                    countAwardItem++
                    val newRefAwards = firebaseReference
                        .child(USERS_DATABASE)
                        .child(userId.toString())
                        .child(AWARD_DATABASE)
                        .child("Award$countAwardItem")
                    newRefAwards.setValue(awardItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun updateArticlesDataForUserInDatabase(userId: String?) {
        val articleItemList = ArrayList<ArticleItem?>()
        firebaseReference.child(ARTICLES_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get all articles from database
                for (article in dataSnapshot.children) {
                    val articleItem =
                        article.getValue(ArticleItem::class.java)
                    articleItemList.add(articleItem)
                }
                firebaseReference
                    .child(USERS_DATABASE)
                    .child(userId.toString())
                    .child(ARTICLES_DATABASE)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // If value from whole DB not exists in user's DB
                            for (i in 0 until articleItemList.size) {
                                if (!dataSnapshot.child("Article${i + 1}").exists()) {
                                    // Add this value un user's DB
                                    Log.w(
                                        LOADER_ACTIVITY_TAG,
                                        "I should add article ${articleItemList[i]?.header}"
                                    )
                                    val newRefArticles = firebaseReference
                                        .child(USERS_DATABASE)
                                        .child(userId.toString())
                                        .child(ARTICLES_DATABASE)
                                        .child("Article${i + 1}")
                                    newRefArticles.setValue(articleItemList[i])
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun updateChallengesDataForUserInDatabase(userId: String?) {
        val challengeItemList = ArrayList<ChallengeItem?>()
        firebaseReference.child(CHALLENGE_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get all challenges from DataBase
                for (challenge in dataSnapshot.children) {
                    val challengeItem =
                        challenge.getValue(ChallengeItem::class.java)
                    challengeItemList.add(challengeItem)
                }
                // Check challenges in user's DataBase
                firebaseReference
                    .child(USERS_DATABASE)
                    .child(userId.toString())
                    .child(CHALLENGE_DATABASE)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // If value from whole DB not exists in user's DB
                            for (i in 0 until challengeItemList.size) {
                                if (!dataSnapshot.child("Challenge${i + 1}").exists()) {
                                    // Add this value un user's DB
                                    Log.w(
                                        LOADER_ACTIVITY_TAG,
                                        "I should add challenge ${challengeItemList[i]?.name}"
                                    )
                                    val newRefChallenges = firebaseReference
                                        .child(USERS_DATABASE)
                                        .child(userId.toString())
                                        .child(CHALLENGE_DATABASE)
                                        .child("Challenge${i + 1}")
                                    newRefChallenges.setValue(challengeItemList[i])
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun updateAwardDataForUserInDatabase(userId: String?) {
        val awardItemList = ArrayList<AwardItem?>()
        firebaseReference.child(AWARD_DATABASE).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get all awards from DataBase
                for (award in dataSnapshot.children) {
                    val awardItem =
                        award.getValue(AwardItem::class.java)
                    awardItemList.add(awardItem)
                }
                // Check challenges in user's DataBase
                firebaseReference
                    .child(USERS_DATABASE)
                    .child(userId.toString())
                    .child(AWARD_DATABASE)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // If value from whole DB not exists in user's DB
                            for (i in 0 until awardItemList.size) {
                                if (!dataSnapshot.child("Award${i + 1}").exists()) {
                                    // Add this value un user's DB
                                    Log.w(
                                        LOADER_ACTIVITY_TAG,
                                        "I should add award ${awardItemList[i]?.awardName}"
                                    )
                                    val newRefAwards = firebaseReference
                                        .child(USERS_DATABASE)
                                        .child(userId.toString())
                                        .child(AWARD_DATABASE)
                                        .child("Award${i + 1}")
                                    newRefAwards.setValue(awardItemList[i])
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(LOADER_ACTIVITY_TAG, "Failed to read value.", error.toException())
            }
        })
    }
    // endregion
}
