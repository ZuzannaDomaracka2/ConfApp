package com.example.confapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_information.*
import java.util.*
import kotlin.collections.HashMap


class InformationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    var googleSignInClient: GoogleSignInClient? = null


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
        saveUserData()
        readUsers()


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

            currentUser.delete().addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    println("Konto zostało usunięte")

                    val intent = Intent(this, SignInActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()

                } else {
                    println("Konto nie zostało usunięte")
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
        setStatus("online")
        val adapter=GroupAdapter<ViewHolder>()



    }

    override fun onPause() {
        super.onPause()
        setStatus("offline")
        val adapter=GroupAdapter<ViewHolder>()


    }



    private fun readUsers(){

        val mRef = FirebaseDatabase.getInstance().getReference("/Users/")
        val firebaseuserID=FirebaseAuth.getInstance().currentUser!!.uid
        mRef.addListenerForSingleValueEvent(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter=GroupAdapter<ViewHolder>()

                snapshot.children.forEach {
                    val user = it.getValue(Users::class.java)


                    if (!(user!!.uid).equals(firebaseuserID)) {

                        adapter.add(UserDisplay(user))


                    }
                    adapter.notifyDataSetChanged()


                }
                myRecyclerview.adapter=adapter
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {


            }
        })
    }


    }






