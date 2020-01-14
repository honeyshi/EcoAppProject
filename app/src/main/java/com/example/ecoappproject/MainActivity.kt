package com.example.ecoappproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        /* Set bottom bar navigation */
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_analyze, R.id.navigation_map, R.id.navigation_user
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)

        /* Do not show action bar */
        NavigationUI.setupWithNavController(navView, navController)


        /* Settings for first time usage */
        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val firstOpened : Boolean = sharedPref.getBoolean("first_opened", true)

        if (firstOpened){
            val intent = Intent(this, FirstTimeActivity::class.java)
            startActivity(intent)
            val editor : SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("first_opened", false)
            editor.apply()
        }
    }
}
