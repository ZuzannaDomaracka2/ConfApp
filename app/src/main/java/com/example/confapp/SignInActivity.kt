package com.example.confapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.acivity_sign_in.*
import java.util.*



class SignInActivity : AppCompatActivity() {
    val provider = OAuthProvider.newBuilder("github.com")
    private lateinit var mAuth:FirebaseAuth
    var googleSignInClient:GoogleSignInClient?=null
    var callbackManager=CallbackManager.Factory.create()
    private val RC_SIGN_IN=1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_sign_in)
        supportActionBar?.hide()



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        mAuth = FirebaseAuth.getInstance()


        sign_in_btn.setOnClickListener {
            signInGoogle()
        }

        fb_button.setOnClickListener {
            signInFacebook()
        }
        gh_button.setOnClickListener {
         // SignInGithub()
            firebaseAuthWithGithub()
        }


    }




    private fun infDisplay(){
        var currentUser=FirebaseAuth.getInstance().currentUser
        if(currentUser!=null) {
            val intent = Intent(this, InformationActivity::class.java)
            startActivity(intent)
            finish()
        }

    }



    private fun signInGoogle() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }



    private fun signInFacebook()
    {

        LoginManager.getInstance().loginBehavior=LoginBehavior.WEB_VIEW_ONLY
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email","public_profile"))

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                firebaseAuthWithFacebook(result)

            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }

        })


}








    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode,resultCode,data)


        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {

                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {

                    Log.w("SignInActivity", "Google sign in failed", e)
                }
            } else {
                Log.w("SignInActivity", exception.toString())
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d("SignInActivity", "signInWithCredential:success")
                    infDisplay()
                }
                else {
                    Log.d("SignInActivity", "signInWithCredential:failure")
                }
            }
    }
    fun firebaseAuthWithGithub()

    {
        val pending = FirebaseAuth.getInstance().pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                //Log.d(TAG, "checkPending:onSuccess:$authResult")
                // Get the user profile with authResult.getUser() and
                // authResult.getAdditionalUserInfo(), and the ID
                // token from Apple with authResult.getCredential().
            }.addOnFailureListener { e ->
              //  Log.w(TAG, "checkPending:onFailure", e)
            }
        } else {
            FirebaseAuth.getInstance().startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener { authResult ->
                    // Sign-in successful!
                   // Log.d(TAG, "activitySignIn:onSuccess:${authResult.user}")
                    val user = authResult.user
                    // ...
                }
                .addOnFailureListener { e ->
                   // Log.w(TAG, "activitySignIn:onFailure", e)
                }
            //Log.d(TAG, "pending: null")
        }

    }

    fun firebaseAuthWithFacebook(result:LoginResult?){
        val credential = FacebookAuthProvider.getCredential(result?.accessToken?.token!!)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    infDisplay()

                } else {

                    print("Authentication failed")

                }

            }

    }





}