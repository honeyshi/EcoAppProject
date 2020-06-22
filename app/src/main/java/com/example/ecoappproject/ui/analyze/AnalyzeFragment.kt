package com.example.ecoappproject.ui.analyze

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.ecoappproject.*
import com.example.ecoappproject.R
import com.example.ecoappproject.classes.Helper
import com.example.ecoappproject.items.IngredientItem
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_analyze.*
import java.util.*
import kotlin.collections.ArrayList

class AnalyzeFragment : Fragment() {

    private val analyzeViewModel: AnalyzeViewModel by activityViewModels()
    private var ingredientItem: IngredientItem? = IngredientItem(name_en = "")
    private val ingredientItemList = ArrayList<IngredientItem?>()
    private val notFoundIngredientList = ArrayList<String>()
    private var ratingCount = 0
    private var isApproved = true
    private val TAG = AnalyzeFragment::class.simpleName

    private val ingredientsDatabase = FirebaseDatabase.getInstance()
    private val ingredientsReference = ingredientsDatabase.reference

    private lateinit var helper: Helper

    @kotlin.ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_analyze, container, false)
        helper = Helper(parentFragmentManager)
        root.findViewById<Button>(R.id.button_analyze_one).setOnClickListener {
            /* Find ingredient in database */
            val ingredientText =
                edit_text_analyze_one.text.toString().toLowerCase(Locale.getDefault())

            /* Check if user input something */
            if (ingredientText.isEmpty()) {
                Toast.makeText(
                    activity?.applicationContext,
                    R.string.toast_text_input_ingredient,
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                getIngredientInfo(ingredientText)
            }
        }
        root.findViewById<Button>(R.id.button_analyze_whole).setOnClickListener {
            /* Split input */
            val inputIngredientList =
                edit_text_analyze_whole.text.toString()
                    .replace("\\s", "")
                    .split(",").toTypedArray()

            /* Check if user input something */
            if (inputIngredientList[0].isEmpty()) {
                Toast.makeText(
                    activity?.applicationContext,
                    R.string.toast_text_input_composition,
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                getCompositionInfo(inputIngredientList)
            }
        }
        return root
    }

    /* Starting fragments */
    private fun startOneIngredientFragment() {
        /* Send values to another fragment */
        Log.w(TAG, "Set values")
        analyzeViewModel.setIngredientNameEN(ingredientItem?.name_en.toString())
        analyzeViewModel.setIngredientNameRU(ingredientItem?.name_ru.toString())
        analyzeViewModel.setIngredientRating(ingredientItem?.rating)
        analyzeViewModel.setIngredientDescription(ingredientItem?.description.toString())

        /* Start another fragment */
        Log.w(TAG, "Start one ingredient fragment")
        helper.replaceFragment(AnalyzeIngredientResultFragment())
    }

    private fun startWholeAnalysisFragment() {
        /* Send ingredients list for recycler view */
        analyzeViewModel.setIngredientItemList(ingredientItemList)

        /* Set approval details */
        analyzeViewModel.setIsApproved(isApproved)
        if (notFoundIngredientList.isEmpty()) analyzeViewModel.setIsNotFound(false) else analyzeViewModel.setIsNotFound(
            true
        )
        analyzeViewModel.setNotFoundIngredients(notFoundIngredientList.joinToString(separator = " "))

        /* Start another fragment */
        Log.w(TAG, "Start whole composition fragment")
        helper.replaceFragment(AnalyzeCompositionResultFragment())
    }

    private fun startNotFoundIngredientFragment(ingredientName: String) {
        analyzeViewModel.setIngredientNameEN(ingredientName)

        /* Start another fragment */
        Log.w(TAG, "Start ingredient not found fragment")
        helper.replaceFragment(AnalyzeIngredientNotFoundFragment())
    }

    /* Get information from database*/
    private fun getIngredientInfo(ingredientName: String) {
        Log.w(TAG, ingredientName)

        /* Start getting data from DataBase */
        FirebaseApp.initializeApp(requireActivity().applicationContext)
        ingredientsReference.child(INGREDIENTS_DATABASE).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // if ingredient exists - get information
                    if (dataSnapshot.hasChild(ingredientName)) {
                        Log.w(TAG, "Ingredient $ingredientName in database")
                        ingredientItem =
                            dataSnapshot.child(ingredientName).getValue(IngredientItem::class.java)
                        startOneIngredientFragment()
                    }
                    // Create empty ingredient
                    else {
                        ingredientItem = IngredientItem(name_en = ingredientName)
                        startNotFoundIngredientFragment(ingredientName)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun getCompositionInfo(ingredientList: Array<String>) {
        Log.w(TAG, "Get composition data from Database")

        FirebaseApp.initializeApp(requireActivity().applicationContext)
        ingredientsReference.child(INGREDIENTS_DATABASE).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ingredientName in ingredientList) {
                        /* If ingredient in database add it to list */
                        if (dataSnapshot.hasChild(ingredientName)) {
                            Log.w(TAG, "Ingredient $ingredientName in database")
                            val ingredientItem =
                                dataSnapshot.child(ingredientName)
                                    .getValue(IngredientItem::class.java)
                            ingredientItemList.add(ingredientItem)
                            /* Check rating of ingredient */
                            if (ingredientItem!!.rating!! < 3) {
                                ratingCount++
                            }
                        }
                        /* If ingredient in database add it to not found list */
                        else {
                            notFoundIngredientList.add(ingredientName)
                        }
                    }
                    Log.w(TAG, "Rating is: $ratingCount")
                    /* If there are items with low rating
                    *  then composition is not approved */
                    if (ratingCount != 0) {
                        isApproved = false
                    }
                    /* If there are items which were not found
                    *  then composition is not approved */
                    if (notFoundIngredientList.isNotEmpty()) {
                        isApproved = false
                    }
                    Log.w(TAG, "Approval status: $isApproved")
                    startWholeAnalysisFragment()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }
}