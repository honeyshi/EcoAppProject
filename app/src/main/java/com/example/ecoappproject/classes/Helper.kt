package com.example.ecoappproject.classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ecoappproject.R

class Helper(private val fragmentManager: FragmentManager) {
    fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =
            fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}