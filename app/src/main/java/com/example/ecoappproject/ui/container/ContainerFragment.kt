package com.example.ecoappproject.ui.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.ecoappproject.R
import com.example.ecoappproject.ui.home.HomeFragment
import com.example.ecoappproject.ui.map.MapFragment
import com.example.ecoappproject.ui.marking.MarkingFragment
import com.example.ecoappproject.ui.user.UserFragment
import java.util.ArrayList

class ContainerFragment : Fragment() {
    private lateinit var containerViewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_container, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerViewPager = view.findViewById(R.id.view_pager_container)

        val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)

        /* Add fragments to adapter */
        pagerAdapter.addFragment(MapFragment())
        pagerAdapter.addFragment(HomeFragment())
        pagerAdapter.addFragment(MarkingFragment())

        /* Initialize adapter for viewpager */
        containerViewPager.adapter = pagerAdapter

        /* Set Home Fragment as current */
        containerViewPager.currentItem = 1
    }

    /* Class adapter for viewpager behavior */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val fragmentList = ArrayList<Fragment>()

        override fun getCount(): Int = fragmentList.size

        override fun getItem(position: Int): Fragment = fragmentList[position]

        fun addFragment(fragment: Fragment){
            fragmentList.add(fragment)
        }
    }
}