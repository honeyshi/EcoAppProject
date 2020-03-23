package com.example.ecoappproject.ui.marking

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnMarkingItemClickListener
import com.example.ecoappproject.items.EcoMarkingItem
import com.example.ecoappproject.objects.EcoMarkingObject
import com.example.ecoappproject.ui.challenge.ChallengeFragment
import com.example.ecoappproject.ui.home.HomeFragment

class MarkingFragment : Fragment(), OnMarkingItemClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_marking, container, false)
        EcoMarkingObject.clearEcoMarkingList()
        EcoMarkingObject.getEcoMarkings(activity!!.applicationContext,
            root.findViewById(R.id.recycler_view_eco_marking),
            this)

        root.findViewById<ImageButton>(R.id.image_button_marking_fragment_left).setOnClickListener{
            leftArrowClickListener()
        }

        root.findViewById<ImageButton>(R.id.image_button_marking_fragment_right).setOnClickListener {
            rightArrowClickListener()
        }

        return root
    }

    private fun leftArrowClickListener(){
        Log.w("Eco Marking fragment", "Start eco articles fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, HomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun rightArrowClickListener(){
        Log.w("Eco Marking fragment", "Start challenges fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, ChallengeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onMarkingItemClick(markingItem: EcoMarkingItem) {
        Log.w("Eco Marking fragment", "Click on item")
        val builder = AlertDialog.Builder(ContextThemeWrapper(activity!!, R.style.DialogTheme))
        builder.setTitle(markingItem.name)
        builder.setMessage(markingItem.description)
        builder.setPositiveButton(android.R.string.ok, null)

        builder.show()
    }
}