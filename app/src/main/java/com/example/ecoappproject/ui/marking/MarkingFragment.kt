package com.example.ecoappproject.ui.marking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.ecoappproject.ECO_MARKING_FRAGMENT_TAG
import com.example.ecoappproject.classes.OnSwipeTouchListener
import com.example.ecoappproject.R
import com.example.ecoappproject.classes.Helper
import com.example.ecoappproject.interfaces.OnMarkingItemClickListener
import com.example.ecoappproject.items.EcoMarkingItem
import com.example.ecoappproject.objects.EcoMarkingObject
import com.example.ecoappproject.ui.challenge.ChallengeFragment
import com.example.ecoappproject.ui.home.HomeFragment

class MarkingFragment : Fragment(), OnMarkingItemClickListener {
    private lateinit var helper: Helper
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        helper = Helper(requireActivity())
        root.findViewById<TextView>(R.id.text_view_header_home_fragment).text =
            getString(R.string.text_view_top_header_marking_fragment)

        root.findViewById<View>(R.id.switcher_round_home_fragment)
            .setBackgroundResource(R.drawable.ic_switch_round_marking)

        EcoMarkingObject.clearEcoMarkingList()
        EcoMarkingObject.getEcoMarkings(
            requireActivity().applicationContext,
            root.findViewById(R.id.home_recycler_view),
            this
        )

        root.findViewById<ConstraintLayout>(R.id.constraint_layout_home_fragment)
            .setOnTouchListener(object :
                OnSwipeTouchListener(requireActivity().applicationContext) {
                override fun onSwipeRight() {
                    Log.w(ECO_MARKING_FRAGMENT_TAG, "Swipe right - Start eco articles fragment")
                    helper.replaceFragment(HomeFragment())
                }

                override fun onSwipeLeft() {
                    Log.w(ECO_MARKING_FRAGMENT_TAG, "Swipe left - Start challenges fragment")
                    helper.replaceFragment(ChallengeFragment())
                }

                override fun onSwipeBottom() {}

                override fun onSwipeTop() {}
            })

        return root
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