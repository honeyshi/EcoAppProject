package com.example.ecoappproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoappproject.FIRST_TIME_ACTIVITY_TAG
import com.example.ecoappproject.R


class FirstTimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time)
    }

    fun onButtonStartButtonClick(view: View){
        Log.w(FIRST_TIME_ACTIVITY_TAG, "Start Login Activity")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}