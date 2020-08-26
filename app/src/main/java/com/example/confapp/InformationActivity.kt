package com.example.confapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.user_row.view.*



class InformationActivity : AppCompatActivity() {

    private var check=1
    private lateinit var mAuth: FirebaseAuth
    var googleSignInClient: GoogleSignInClient? = null



    @SuppressLint("SetTextI18n")
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



        name_text.text = "Hello, " + currentUser?.displayName
        Picasso.get().load(currentUser?.photoUrl).into(profile_image)

        saveUserData()
        readUsers()


        sign_out_button.setOnClickListener {

            val firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId).removeValue()
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

            currentUser.delete().addOnCompleteListener(this) { task ->


                if (task.isSuccessful) {
                    Toast.makeText(this,"Account deleted", Toast.LENGTH_SHORT).show();
                    check=0
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this,"Error, try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    private fun saveUserData(){

        val uid = FirebaseAuth.getInstance().uid?:""

        val mRef = FirebaseDatabase.getInstance().getReference("/Users/$uid")

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val userName = currentUser?.displayName.toString()

        val urlPhoto=currentUser?.photoUrl.toString()


        val user=Users(uid, userName, urlPhoto,status = String())

        mRef.setValue(user).addOnSuccessListener {
            Log.d("InformationActivity", "data saved")
        }
            .addOnFailureListener{
                Log.d("InformationActivity", "error")
            }
    }

    private fun setStatus(status:String){

        val uid = FirebaseAuth.getInstance().uid?:""
        val mRef = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        val hashMap = HashMap<String,Any>()
        hashMap["status"]=status
        mRef.updateChildren(hashMap)
    }

    override fun onResume() {

        super.onResume()
        if(check==1) {
            setStatus("online")
        }
    }

    override fun onPause() {
        super.onPause()
        if(check==1) {
            setStatus("offline")
        }
    }


    private fun readUsers(){

        val mRef = FirebaseDatabase.getInstance().getReference("/Users/")
        val firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
        mRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter=GroupAdapter<ViewHolder>()

                snapshot.children.forEach {
                    val user = it.getValue(Users::class.java)


                    if ((user!!.uid) != firebaseUserId) {

                        adapter.add(UserDisplay(user))
                    }

                }
                myRecyclerview.adapter=adapter
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {


            }
        })
    }

}