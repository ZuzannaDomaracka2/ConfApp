package com.example.confapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_information.*


class InformationActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    var googleSignInClient:GoogleSignInClient?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        name_text.text = "Witaj, " + currentUser?.displayName

        Glide.with(this).load(currentUser?.photoUrl).into(profile_image)

        sign_out_btn.setOnClickListener {
            removeUser()
        }
            }

    private fun removeUser() {
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val credential = EmailAuthProvider.getCredential("user@example.com", "password")
        currentUser?.reauthenticate(credential)?.addOnCompleteListener {
            googleSignInClient?.signOut()
            LoginManager.getInstance().logOut()
            //FirebaseAuth.getInstance().signOut()


            currentUser.delete().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                   println("Konto zostało usunięte")

                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    println("Konto nie zostało usunięte")
                }
            }
        }
    }
        }





