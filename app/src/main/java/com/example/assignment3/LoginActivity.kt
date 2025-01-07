package com.example.assignment3

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.example.assignment3.databinding.ActivityLoginBinding
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        facebookLogin.setOnClickListener {
            loading.visibility = ProgressBar.VISIBLE
        }

    }
}