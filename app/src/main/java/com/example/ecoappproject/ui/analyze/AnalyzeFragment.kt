package com.example.ecoappproject.ui.analyze

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.*
import com.example.ecoappproject.R
import com.example.ecoappproject.items.IngredientItem
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_analyze.*
import java.util.*
import kotlin.collections.ArrayList

class AnalyzeFragment : Fragment() {

    private lateinit var analyzeViewModel: AnalyzeViewModel
    private var ingredientItemList = ArrayList<IngredientItem?>()
    private var ingredientItem : IngredientItem? = IngredientItem(name_en = "")

    @kotlin.ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        analyzeViewModel =
            ViewModelProviders.of(requireActivity()).get(AnalyzeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_analyze, container, false)
        root.findViewById<Button>(R.id.button_analyze_one).setOnClickListener{
            /* Find ingredient in database */
            val ingredientText = edit_text_analyze_one.text.toString().toLowerCase(Locale.getDefault())
            getIngredientInfo(ingredientText)
        }
        return root
    }

    private fun startOneIngredientFragment(){
        /* Send values to another fragment */
        Log.w(ContentValues.TAG, "Set values")
        analyzeViewModel.setIngredientNameEN(ingredientItem?.name_en.toString())
        analyzeViewModel.setIngredientNameRU(ingredientItem?.name_ru.toString())
        analyzeViewModel.setIngredientRating(ingredientItem?.rating)
        analyzeViewModel.setIngredientDescription(ingredientItem?.description.toString())

        /* Start another fragment */
        Log.w(ContentValues.TAG, "Start one ingredient fragment")
        val analyzeIngredientResultFragment = AnalyzeIngredientResultFragment()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, analyzeIngredientResultFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun startNotFoundIngredientFragment(ingredientName: String){
        analyzeViewModel.setIngredientNameEN(ingredientName)

        /* Start another fragment */
        Log.w(ContentValues.TAG, "Start ingredient not found fragment")
        val analyzeIngredientNotFoundFragment = AnalyzeIngredientNotFoundFragment()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, analyzeIngredientNotFoundFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getIngredientInfo(ingredientName: String) {
        Log.w(ContentValues.TAG, ingredientName)

        /* Start getting data from DataBase */
        FirebaseApp.initializeApp(activity!!.applicationContext)
        val ingredientsDatabase = FirebaseDatabase.getInstance()
        val ingredientsReference = ingredientsDatabase.reference
        ingredientsReference.child(INGREDIENTS_DATABASE).addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // if ingredient exists - get information
                    if (dataSnapshot.hasChild(ingredientName)){
                        Log.w(ContentValues.TAG, "Ingredient in database")
                        ingredientItem =
                            dataSnapshot.child(ingredientName).getValue(IngredientItem::class.java)
                        startOneIngredientFragment()
                    }
                    // Create empty ingredient
                    else{
                        ingredientItem = IngredientItem(name_en = ingredientName)
                        startNotFoundIngredientFragment(ingredientName)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }
}