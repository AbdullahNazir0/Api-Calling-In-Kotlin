package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.example.assignment3.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
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

        // client for api calling
        val client: OkHttpClient = OkHttpClient()

        login.setOnClickListener {
            // Login logic

        }

        googleLogin.setOnClickListener {
            loading.visibility = ProgressBar.VISIBLE

            val request: Request = Request.Builder().url("https://google.com").build()
            try {
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                println(body)
                Toast.makeText(this, "Signed in with google successfully", Toast.LENGTH_LONG).show()
            } catch (error: Throwable) {
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            } finally {
                loading.visibility = ProgressBar.GONE
            }
        }

        callbackManager = CallbackManager.Factory.create()
        facebookLogin.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Toast.makeText(this@LoginActivity, "Signed in with facebook successfully", Toast.LENGTH_LONG).show()
                loading.visibility = ProgressBar.GONE
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
}