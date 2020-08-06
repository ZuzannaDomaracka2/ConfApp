package com.example.confapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_information.*


class InformationActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        name_text.text = "Witaj, " + currentUser?.displayName

        Glide.with(this).load(currentUser?.photoUrl).into(profile_image)



        sign_out_btn.setOnClickListener {


            val credential = EmailAuthProvider.getCredential("user@example.com", "password")
            currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                googleSignInClient?.signOut()

                currentUser?.delete()?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Toast.makeText( applicationContext," Konto zostało usunięte ",Toast.LENGTH_SHORT).show();

                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Log.d("InformationActivity", "signOutWithCredential:failure")
                }
            }


            }
        }

            }

        }





