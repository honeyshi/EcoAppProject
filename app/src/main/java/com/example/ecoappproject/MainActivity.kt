package com.example.ecoappproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.work.*
import com.example.ecoappproject.background.ChangeChallengeCurrentDayWorker
import com.example.ecoappproject.background.SendNotificationWorker
import com.example.ecoappproject.interfaces.OnGetStartedChallengePresenceListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.items.AwardItem
import com.example.ecoappproject.items.ChallengeItem
import com.example.ecoappproject.objects.ChallengeObject
import com.example.ecoappproject.ui.challenge.ChallengeStartedFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // region Start service for changing challenge current day
        // Create calendar for setting execution time
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Set Execution around 05:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 5)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        dueDate.set(Calendar.MILLISECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        // Enqueue worker job
        val dailyWorkRequest = OneTimeWorkRequestBuilder<ChangeChallengeCurrentDayWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()
        //WorkManager.getInstance(application).enqueue(dailyWorkRequest)
        // endregion

        // region Start service for sending notification to user
        Log.w("Main Activity", "Create notification channel")
        createNotificationChannel()

        val currentDateNotification = Calendar.getInstance()
        val dueDateNotification = Calendar.getInstance()

        // Set Execution around 12:00:00 AM
        dueDateNotification.set(Calendar.HOUR_OF_DAY, 12)
        dueDateNotification.set(Calendar.MINUTE, 0)
        dueDateNotification.set(Calendar.SECOND, 0)
        dueDateNotification.set(Calendar.MILLISECOND, 0)

        if (dueDateNotification.before(currentDate)) {
            dueDateNotification.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiffNotification =
            dueDateNotification.timeInMillis - currentDateNotification.timeInMillis

        val dailyNotificationRequest = OneTimeWorkRequestBuilder<SendNotificationWorker>()
            .setInitialDelay(timeDiffNotification, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(application)
            .beginWith(listOf(dailyWorkRequest, dailyNotificationRequest)).enqueue()
        // endregion

        // region Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseReference = FirebaseDatabase.getInstance().reference

        initializeDatabaseForCurrentUser()

        // endregion

        // region Set bottom bar navigation
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

        // endregion

        // region Settings for first time usage
        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val firstOpened: Boolean = sharedPref.getBoolean("first_opened", true)

        if (firstOpened) {
            val intent = Intent(this, FirstTimeActivity::class.java)
            startActivity(intent)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("first_opened", false)
            editor.apply()
        }

        // endregion
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
                    updateAwardDataForUserInDatabase(currentUserId)
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
                        .child("Article$countArticleItem")
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
                        .child("Challenge$countChallengeItem")
                    newRefChallenges.setValue(challengeItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
            }
        })

        var countAwardItem = 0
        // Add values from award database
        Log.w("Main Activity", "Set awards for new user")
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
                Log.w("Main Activity", "Failed to read value.", error.toException())
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
                                        "Main Activity",
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
                            Log.w("Main Activity", "Failed to read value.", error.toException())
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
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
                                        "Main Activity",
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
                            Log.w("Main Activity", "Failed to read value.", error.toException())
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
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
                                        "Main Activity",
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
                            Log.w("Main Activity", "Failed to read value.", error.toException())
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Main Activity", "Failed to read value.", error.toException())
            }
        })
    }
    // endregion
}
