package com.example.confapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.acivity_sign_in.*
import java.io.FileInputStream
import java.util.*


class SignInActivity : AppCompatActivity()  {

    companion object {
        val TAG = "SignInActivity"
    }



    private lateinit var mAuth: FirebaseAuth
    var googleSignInClient: GoogleSignInClient? = null
    var callbackManager = CallbackManager.Factory.create()
    private val RC_SIGN_IN = 1
    val uid = FirebaseAuth.getInstance().uid?:""



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
            signInGithub()

        }
        lin_button.setOnClickListener {
            //signInLinkedIn()
        }


    }
    // Logowanie LinkedIn

    private fun signInLinkedIn() {

        val serviceAccount = FileInputStream("path/to/serviceAccountKey.json")

        val options =
            FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://confapp-88132.firebaseio.com")
                .build()

        FirebaseApp.initializeApp(options)
        
    }






    private fun signInGithub() {

        val provider = OAuthProvider.newBuilder("github.com")

        val pendingResultTask: Task<AuthResult>? = FirebaseAuth.getInstance().getPendingAuthResult()
        if (pendingResultTask != null) {

            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener<AuthResult?> {
                        Log.d(TAG, "success $it")
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                    })
                .addOnFailureListener(
                    OnFailureListener {
                        // Handle failure.
                        Log.d(TAG, "error", it)
                    })
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.\
            FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider( /* activity= */this, provider.build())
                .addOnSuccessListener(
                    OnSuccessListener<AuthResult?> {
                        Log.d(TAG, "success $it")
                        infDisplay()
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                    })
                .addOnFailureListener(
                    OnFailureListener {
                        // Handle failure.
                        Log.d(TAG, "error", it)
                    })
        }
    }





    private fun infDisplay(){
        val currentUser=FirebaseAuth.getInstance().currentUser
        if(currentUser!=null) {
            val intent = Intent(this, InformationActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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


