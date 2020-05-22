package com.example.ecoappproject.ui.user

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.ecoappproject.EDIT_USER_PROFILE_FRAGMENT_TAG
import com.example.ecoappproject.R
import com.example.ecoappproject.objects.UserInformationObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class EditUserProfileFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var filePath: Uri
    private lateinit var userImageView: ImageView
    private val PICK_IMAGE_REQUEST = 71
    private val firebaseStorageReference = FirebaseStorage.getInstance().reference
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_user_profile, container, false)
        val userNameEditText = root.findViewById<EditText>(R.id.edit_text_user_name)
        val userDescriptionEditText = root.findViewById<EditText>(R.id.edit_text_user_information)
        val buttonSaveUserProfile = root.findViewById<Button>(R.id.button_save_user_profile)
        val userNameTextView = root.findViewById<TextView>(R.id.text_view_user_name_edit_profile)
        userImageView = root.findViewById(R.id.image_view_upload_photo_button)

        // Set user's image on edit page
        UserInformationObject.updateUserImageOnUI(
            requireActivity().applicationContext,
            userImageView,
            currentUserId.toString()
        )

        // Set user's email on edit page
        root.findViewById<TextView>(R.id.text_view_user_email_edit_profile).text =
            FirebaseAuth.getInstance().currentUser?.email

        // Set user's name on edit page
        userViewModel.getUserName().observe(viewLifecycleOwner, Observer {
            userNameTextView.text = it
        })

        // Save information which user has entered when click on save button
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

        userImageView.setOnClickListener {
            // Check if permission is already granted
            if (ContextCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                chooseImageFromGallery()
            }
            // In another way ask for permission
            else {
                Log.w(EDIT_USER_PROFILE_FRAGMENT_TAG, "There are no permissions so ask")
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        }

        return root
    }

    // Get image from user's gallery
    private fun chooseImageFromGallery() {
        val chooseImageIntent = Intent()
        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(chooseImageIntent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null
        ) {
            filePath = data.data!!
            uploadImageToStorage()
            setImageUrlToFirebaseDatabase()
        }
    }

    private fun uploadImageToStorage() {
        Log.w(EDIT_USER_PROFILE_FRAGMENT_TAG, "Upload selected image to storage")
        val ref =
            firebaseStorageReference.child("users/" + currentUserId.toString())
        ref.putFile(filePath)
            .addOnSuccessListener {
                Toast.makeText(
                    requireActivity().applicationContext,
                    R.string.toast_text_image_upload_success,
                    Toast.LENGTH_LONG
                )
                    .show()
                // Set new image on edit page
                Glide.with(requireActivity().applicationContext)
                    .load(filePath)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(userImageView)
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireActivity().applicationContext,
                    R.string.toast_text_image_upload_failure,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.w(EDIT_USER_PROFILE_FRAGMENT_TAG, "Handle result of request read storage permission")
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.w(EDIT_USER_PROFILE_FRAGMENT_TAG, "User is able to choose image")
                    // Let user choose image from storage
                    chooseImageFromGallery()
                }
            }
        }
    }

    private fun setImageUrlToFirebaseDatabase() {
        firebaseStorageReference.child("users/${currentUserId.toString()}").downloadUrl.addOnSuccessListener {
            // Got the download URL and set to firebase
            Log.w(EDIT_USER_PROFILE_FRAGMENT_TAG, "Set url $it in database")
            UserInformationObject.updateUserTextInformationInDatabase(
                "imageUrl",
                it.toString(),
                currentUserId.toString()
            )
        }
    }
}