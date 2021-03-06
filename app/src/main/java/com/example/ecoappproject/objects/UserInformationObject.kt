package com.example.ecoappproject.objects

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.ecoappproject.R
import com.example.ecoappproject.USERS_DATABASE
import com.example.ecoappproject.USER_INFORMATION_DATABASE
import com.example.ecoappproject.USER_INFORMATION_OBJECT_TAG
import com.example.ecoappproject.items.UserInformationItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

object UserInformationObject {
    private val userInformationDatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseStorage = FirebaseStorage.getInstance()

    fun updateUserInformationOnUI(
        userDescriptionTextView: TextView,
        userNameTextView: TextView,
        userId: String
    ) {
        userInformationDatabaseReference.child(USERS_DATABASE)
            .child(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userInformationItem = dataSnapshot.child(USER_INFORMATION_DATABASE)
                        .getValue(UserInformationItem::class.java)
                    Log.w(
                        USER_INFORMATION_OBJECT_TAG,
                        "User description - ${userInformationItem?.userDescription}. User name - ${userInformationItem?.userName}"
                    )
                    // Set information about user
                    userDescriptionTextView.text = userInformationItem?.userDescription
                    userNameTextView.text = userInformationItem?.userName
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(USER_INFORMATION_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun updateUserImageOnUI(
        context: Context,
        userImageView: ImageView,
        userId: String
    ) {
        userInformationDatabaseReference.child(USERS_DATABASE)
            .child(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userInformationItem = dataSnapshot.child(USER_INFORMATION_DATABASE)
                        .getValue(UserInformationItem::class.java)
                    if (userInformationItem?.imageUrl?.isNotEmpty() == true) {
                        Log.w(USER_INFORMATION_OBJECT_TAG, "User has image - upload it")
                        userImageView.setImageResource(0)
                        userImageView.setBackgroundResource(0)
                        // Load image to imageView and make it circle
                        Glide.with(context)
                            .load(userInformationItem.imageUrl)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .error(R.drawable.shape_oval_accent)
                            .into(userImageView)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(USER_INFORMATION_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun updateUserTextInformationInDatabase(fieldName: String, newValue: String, userId: String) {
        userInformationDatabaseReference.child(USERS_DATABASE)
            .child(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.child(USER_INFORMATION_DATABASE).child(fieldName).ref.setValue(
                        newValue
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(USER_INFORMATION_OBJECT_TAG, "Failed to read value.", error.toException())
                }
            })
    }
}