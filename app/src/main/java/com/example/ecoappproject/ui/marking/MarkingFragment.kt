package com.example.ecoappproject.ui.marking

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.EcoMarkingObject
import com.example.ecoappproject.ui.home.HomeFragment

class MarkingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_marking, container, false)
        EcoMarkingObject.clearEcoMarkingList()
        EcoMarkingObject.getEcoMarkings(activity!!.applicationContext,
            root.findViewById(R.id.recycler_view_eco_marking))

        root.findViewById<ImageButton>(R.id.image_button_marking_fragment_left).setOnClickListener{
            leftArrowClickListener()
        }
        return root
    }

    private fun leftArrowClickListener(){
        Log.w(ContentValues.TAG, "Start eco articles fragment")
        val homeFragment = HomeFragment()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, homeFragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }
}