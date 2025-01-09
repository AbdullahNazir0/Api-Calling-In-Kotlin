package com.example.assignment3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment3.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            Toast.makeText(this@MainActivity, "Button clicked", Toast.LENGTH_LONG).show()
            binding.btn.isEnabled = false
            binding.resultTv.text = "Loading..."
            val prompt = binding.promptEt.text.toString()
            if(prompt.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter a prompt", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val responseText = fetchResponseFromOpenAI(prompt)
                    withContext(Dispatchers.Main) {
                        binding.resultTv.text = responseText
                        binding.promptEt.text.clear()
                        binding.btn.isEnabled = true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.resultTv.text = "Error: ${e.message}"
                        binding.btn.isEnabled = true
                    }
                }
            }
        }
    }

    private fun fetchResponseFromOpenAI(prompt: String): String {
        val client = OkHttpClient()

        val requestBodyString = createGeminiRequestBody(prompt)
        val requestBody = requestBodyString.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${resources.getString(R.string.gemini_api)}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code}")
        }
        val responseBody = response.body?.string()
        val jsonResponse = JSONObject(responseBody ?: "")
        val candidates = jsonResponse.getJSONArray("candidates")
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        val firstPart = parts.getJSONObject(0)
        val text = firstPart.getString("text")
        return text
    }

    private fun createGeminiRequestBody(prompt: String): String {
        val requestBody = JSONObject()

        val contents = JSONArray()
        val content = JSONObject()
        content.put("role", "user")

        val parts = JSONArray()
        val part = JSONObject()
        part.put("text", prompt)
        parts.put(part)

        content.put("parts", parts)
        contents.put(content)

        requestBody.put("contents", contents)

        return requestBody.toString()
    }
}