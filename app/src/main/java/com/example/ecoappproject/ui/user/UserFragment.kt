package com.example.ecoappproject.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ecoappproject.LoginActivity
import com.example.ecoappproject.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {
    //private lateinit var userViewModel: UserViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //userViewModel =
        //    ViewModelProviders.of(this).get(UserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // If user is not sign in or anonymous image button should allow signing in
        if (firebaseAuth.currentUser == null || firebaseAuth.currentUser?.isAnonymous == true) {
            Log.w("User Fragment",
                "User is not sign in (Anonymous ${firebaseAuth.currentUser?.isAnonymous})")
            root.findViewById<ImageButton>(R.id.image_button_user_icon).setOnClickListener {
                startLoginActivity()
            }
        }
        else {
            Log.w("User Fragment", "User is sign in")
        }
        return root
    }

    private fun startLoginActivity(){
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }
}