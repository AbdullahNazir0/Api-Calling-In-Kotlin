package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.example.assignment3.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
    }
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(application)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val googleLogin = binding.googleLogin
        val facebookLogin = binding.facebookLogin

        login.setOnClickListener {
            // Login logic

        }

        val geo = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, geo)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {

        }
        googleLogin.setOnClickListener {
            loading.visibility = ProgressBar.VISIBLE
            signIn()
            loading.visibility = ProgressBar.GONE
        }

        callbackManager = CallbackManager.Factory.create()
        facebookLogin.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Toast.makeText(this@LoginActivity, "Signed in with facebook successfully", Toast.LENGTH_LONG).show()
                loading.visibility = ProgressBar.GONE
                val intent = Intent()
                intent.setClass(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@LoginActivity, "Error: $error", Toast.LENGTH_LONG).show()
                loading.visibility = ProgressBar.GONE
            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "Cancelled", Toast.LENGTH_LONG).show()
                loading.visibility = ProgressBar.GONE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            Toast.makeText(this@LoginActivity, "Signed in successfully", Toast.LENGTH_LONG).show()
            val intent = Intent()
            intent.setClass(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)

        } catch (e: ApiException) {
            Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}