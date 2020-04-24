package com.example.ecoappproject.background

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.ecoappproject.CHANNEL_ID
import com.example.ecoappproject.MainActivity
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnGetStartedChallengePresenceListener
import com.example.ecoappproject.objects.ChallengeObject

class SendNotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.w("SendNotificationWorker", "Here I send reminder to mark challenge")

        ChallengeObject.isStartedChallengeExists(object : OnGetStartedChallengePresenceListener {
            override fun OnGetStartedChallengePresence(isPresented: Boolean) {
                if (isPresented) {
                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

                    val notificationId = 1

                    val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_leaf)
                        .setContentTitle(applicationContext.resources.getString(R.string.notification_challenge_title))
                        .setContentText(applicationContext.resources.getString(R.string.notification_challenge_description))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true) // remove notification when user click on it

                    with(NotificationManagerCompat.from(applicationContext)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(notificationId, builder.build())
                    }
                }
            }
        })

        return Result.success()
    }
}