package com.example.ecoappproject.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.classes.OnSwipeTouchListener
import com.example.ecoappproject.R
import com.example.ecoappproject.interfaces.OnArticleItemClickListener
import com.example.ecoappproject.items.ArticleItem
import com.example.ecoappproject.objects.ArticleObject
import com.example.ecoappproject.classes.Helper
import com.example.ecoappproject.ui.articleDescription.ArticleDescriptionFragment
import com.example.ecoappproject.ui.challenge.ChallengeFragment
import com.example.ecoappproject.ui.marking.MarkingFragment


class HomeFragment : Fragment(), OnArticleItemClickListener {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var helper: Helper
    private val TAG = HomeFragment::class.simpleName

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        helper = Helper(parentFragmentManager)
        root.findViewById<TextView>(R.id.text_view_header_home_fragment).text =
            getString(R.string.text_view_top_header_home_fragment)

        root.findViewById<View>(R.id.switcher_round_home_fragment)
            .setBackgroundResource(R.drawable.ic_switch_round_home)

        ArticleObject.clearArticleItemList()
        ArticleObject.getArticles(
            requireActivity().applicationContext,
            root.findViewById(R.id.home_recycler_view),
            this
        )

        root.findViewById<ConstraintLayout>(R.id.constraint_layout_home_fragment)
            .setOnTouchListener(object :
                OnSwipeTouchListener(requireActivity().applicationContext) {
                override fun onSwipeRight() {
                    Log.w(TAG, "Swipe right - Start eco challenges fragment")
                    helper.replaceFragment(ChallengeFragment())
                }

                override fun onSwipeLeft() {
                    Log.w(TAG, "Swipe left - Start eco marking fragment")
                    helper.replaceFragment(MarkingFragment())
                }

                override fun onSwipeBottom() {}

                override fun onSwipeTop() {}
            })

        return root
    }

    override fun onItemClicked(articleItem: ArticleItem?) {
        Log.w(TAG, "Save data to view model")
        helper.saveArticleInfoToViewModel(homeViewModel, articleItem)

        Log.w(TAG, "Start description fragment")
        helper.replaceFragment(ArticleDescriptionFragment())
    }
}