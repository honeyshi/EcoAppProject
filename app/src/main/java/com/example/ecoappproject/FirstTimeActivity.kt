package com.example.ecoappproject

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class FirstTimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time)
    }

    fun onButtonStartButtonClick(view: View){
        Log.w(ContentValues.TAG, "Start Main Activity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}