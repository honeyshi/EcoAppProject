package com.example.ecoappproject.ui.marking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ecoappproject.ECO_MARKING_FRAGMENT_TAG
import com.example.ecoappproject.OnSwipeTouchListener
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnMarkingItemClickListener
import com.example.ecoappproject.items.EcoMarkingItem
import com.example.ecoappproject.objects.EcoMarkingObject
import com.example.ecoappproject.ui.challenge.ChallengeFragment
import com.example.ecoappproject.ui.home.HomeFragment

class MarkingFragment : Fragment(), OnMarkingItemClickListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_marking, container, false)
        EcoMarkingObject.clearEcoMarkingList()
        EcoMarkingObject.getEcoMarkings(
            requireActivity().applicationContext,
            root.findViewById(R.id.recycler_view_eco_marking),
            this
        )

        root.findViewById<ConstraintLayout>(R.id.constraint_layout_marking_fragment)
            .setOnTouchListener(object :
                OnSwipeTouchListener(requireActivity().applicationContext) {
                override fun onSwipeRight() {
                    Log.w(ECO_MARKING_FRAGMENT_TAG, "Swipe right")
                    swipeRightListener()
                }

                override fun onSwipeLeft() {
                    Log.w(ECO_MARKING_FRAGMENT_TAG, "Swipe left")
                    swipeLeftListener()
                }

                override fun onSwipeBottom() {}

                override fun onSwipeTop() {}
            })

        return root
    }

    private fun swipeRightListener() {
        Log.w(ECO_MARKING_FRAGMENT_TAG, "Start eco articles fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, HomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun swipeLeftListener() {
        Log.w(ECO_MARKING_FRAGMENT_TAG, "Start challenges fragment")
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, ChallengeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onMarkingItemClick(markingItem: EcoMarkingItem) {
        Log.w(ECO_MARKING_FRAGMENT_TAG, "Click on item")
        val builder =
            AlertDialog.Builder(ContextThemeWrapper(requireActivity(), R.style.DialogTheme))
        builder.setTitle(markingItem.name)
        builder.setMessage(markingItem.description)
        builder.setPositiveButton(android.R.string.ok, null)

        builder.show()
    }
}