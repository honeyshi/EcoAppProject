package com.example.ecoappproject.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.ecoappproject.EDIT_USER_PROFILE_FRAGMENT_TAG
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.UserInformationObject
import com.google.firebase.auth.FirebaseAuth

class EditUserProfileFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_user_profile, container, false)
        val userNameEditText = root.findViewById<EditText>(R.id.edit_text_user_name)
        val userDescriptionEditText = root.findViewById<EditText>(R.id.edit_text_user_information)
        val buttonSaveUserProfile = root.findViewById<Button>(R.id.button_save_user_profile)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val userNameTextView = root.findViewById<TextView>(R.id.text_view_user_name_edit_profile)

        root.findViewById<TextView>(R.id.text_view_user_email_edit_profile).text =
            FirebaseAuth.getInstance().currentUser?.email

        userViewModel.getUserName().observe(viewLifecycleOwner, Observer {
            userNameTextView.text = it
        })

        buttonSaveUserProfile.setOnClickListener {
            if (userNameEditText.text.isNotEmpty()) {
                Log.w(EDIT_USER_PROFILE_FRAGMENT_TAG, "User name is not empty - send to database")
                UserInformationObject.updateUserTextInformationInDatabase(
                    "userName",
                    userNameEditText.text.toString(),
                    currentUserId.toString()
                )
                userNameTextView.text = userNameEditText.text
            }
            if (userDescriptionEditText.text.isNotEmpty()) {
                Log.w(
                    EDIT_USER_PROFILE_FRAGMENT_TAG,
                    "User description is not empty - send to database"
                )
                UserInformationObject.updateUserTextInformationInDatabase(
                    "userDescription",
                    userDescriptionEditText.text.toString(),
                    currentUserId.toString()
                )
            }
            Toast.makeText(
                requireActivity().applicationContext,
                R.string.toast_text_save_user_profile,
                Toast.LENGTH_LONG
            ).show()
        }

        return root
    }
}