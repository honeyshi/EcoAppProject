package com.example.ecoappproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ecoappproject.objects.ChallengeObject
import kotlinx.android.synthetic.main.activity_loader.*


class LoaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        Glide.with(this).asGif().load(R.drawable.ic_loader_activity)
            .into(image_view_loader_activity_icon)

        Log.w(LOADER_ACTIVITY_TAG, "Start increasing current day for challenges")
        ChallengeObject.increaseCurrentDayForStartedChallenges()

        Handler().postDelayed(startMainActivity, 1000)
    }

    private val startMainActivity = Runnable {
        // do what you need to do here after the delay
        Log.w(LOADER_ACTIVITY_TAG, "Start main activity after delay")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
