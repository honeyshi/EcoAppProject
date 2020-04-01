package com.example.ecoappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001     // request code for Google sign in
    private val RC_CONVERT_ACC = 9002 // request code for convert Anonymous account to permanent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // If user is not signed in yet or not anonymously ->
        // allow user to sign in anonymously or with Google
        if (firebaseAuth.currentUser == null || firebaseAuth.currentUser?.isAnonymous == false) {
            button_login_activity_skip.setOnClickListener {
                skipSignIn()
            }
            button_login_activity_google.setOnClickListener {
                signInWithGoogle()
            }
        }
        // If user signed in anonymously ->
        // make button for anonymous sign in invisible
        // and convert an anonymous account to a permanent account
        else {
            button_login_activity_skip.visibility = View.INVISIBLE
            button_login_activity_google.setOnClickListener {
                convertAnonymousAccountToPermanent()
            }
        }
    }

    private fun signInWithGoogle() {
        Log.w("Login Activity", "Google Sign In")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun skipSignIn() {
        Log.w("Login Activity", "Skip Sign In")
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success - start main activity
                    Log.w("Login Activity", "signInAnonymously:success")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login Activity", "signInAnonymously:failure", task.exception)
                }
            }
    }

    private fun convertAnonymousAccountToPermanent() {
        Log.w("Login Activity", "Convert Anonymous account to permanent")
        val convertIntent = googleSignInClient.signInIntent
        startActivityForResult(convertIntent, RC_CONVERT_ACC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // Request for Google sign in
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Login Activity", "Google sign in failed", e)
            }
        }

        // Request for convert Anonymous account to permanent
        if (requestCode == RC_CONVERT_ACC) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseConvertAnonymousAccountToPermanentGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Login Activity", "Google sign in failed", e)

            }
        }
    }

    private fun firebaseAuthWithGoogle(googleSignInAccount: GoogleSignInAccount) {
        Log.d("Login Activity", "firebaseAuthWithGoogle:" + googleSignInAccount.id!!)

        val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login Activity", "signInWithCredential:success")
                    // Start Main Activity
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login Activity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun firebaseConvertAnonymousAccountToPermanentGoogle(googleSignInAccount: GoogleSignInAccount) {
        Log.d("Login Activity", "firebaseConvertToGoogle:" + googleSignInAccount.id!!)

        val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        firebaseAuth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Login Activity", "linkWithCredential:success")
                    // Start Main Activity
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Log.w("Login Activity", "linkWithCredential:failure", task.exception)
                }
            }
    }
}
