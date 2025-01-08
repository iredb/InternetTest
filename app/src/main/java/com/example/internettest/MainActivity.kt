package com.example.internettest

import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.SocketException
import java.net.URL
import okhttp3.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val httpButton: Button = findViewById(R.id.btnHTTP)

        httpButton.setOnClickListener {
            fetchCats()
        }

        val okHttpButton: Button = findViewById(R.id.btnOkHTTP)

        okHttpButton.setOnClickListener {
            fetchViaOkHTTP()
        }
    }

    private fun fetchCats() {
        CoroutineScope(Dispatchers.IO).launch {
            val flickrUrl =
                "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"

            try {
                val url = URL(flickrUrl)
                val urlConnection = url.openConnection() as HttpURLConnection

                try {
                    val inputStream = BufferedInputStream(urlConnection.inputStream)
                    val response = readStream(inputStream)
                    Log.d("Flickr cats", "Response: $response")
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: SocketException) {
                Log.e("Flickr cats", "SocketException: Проверьте разрешение на доступ к интернету в AndroidManifest.xml")
            } catch (e: NetworkOnMainThreadException) {
                Log.e("Flickr cats", "NetworkOnMainThreadException: Убедитесь, что сетевое взаимодействие производится в другом потоке")
            } catch (e: Exception) {
                Log.e("Flickr cats", "Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun readStream(inputStream: InputStream): String {
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun fetchCatsViaOkHTTP() {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.i("Flickr OkCats", responseData ?: "Empty Response")
                } else {
                    Log.e("Flickr OkCats", "Request failed with code: ${response.code}")
                }
            } catch (e: SocketException) {
                Log.e("Flickr OkCats", "SocketException: Проверьте разрешение на доступ к интернету в AndroidManifest.xml")
            } catch (e: IOException) {
                Log.e("Flickr OkCats", "IOException: ${e.localizedMessage}")
            } catch (e: Exception) {
                Log.e("Flickr OkCats", "Exception: ${e.localizedMessage}")
            }
        }
    }

    private val client = OkHttpClient()

    private fun fetchViaOkHTTP() {
        val url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Flickr OkCats", "Request failed: ${e.message}", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Log.i("Flickr OkCats", "Response: $responseBody")
                    } ?: Log.e("Flickr OkCats", "Response body is null")
                } else {
                    Log.e("Flickr OkCats", "Request failed with code: ${response.code}")
                }
            }
        })
    }
}