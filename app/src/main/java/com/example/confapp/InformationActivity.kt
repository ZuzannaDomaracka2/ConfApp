package com.example.confapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_information.*


class InformationActivity : AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        supportActionBar?.hide()

        mAuth=FirebaseAuth.getInstance()
        val currentUser=mAuth.currentUser

        name_text.text="Witaj " +currentUser?.displayName

        Glide.with(this).load(currentUser?.photoUrl).into(profile_image);

        sign_out_btn.setOnClickListener{
            currentUser?.delete()?.addOnCompleteListener{


                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
               // finish()

            }
            }

        }




    }
