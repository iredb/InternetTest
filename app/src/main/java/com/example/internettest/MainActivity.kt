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
import okhttp3.Request
import java.io.IOException

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

        val logButton: Button = findViewById(R.id.btnHTTP)

        logButton.setOnClickListener {
            fetchCats()
        }
    }

    private fun fetchCats() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // URL для запроса
                val apiUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                        "&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"

                // Выполняем запрос
                val response = URL(apiUrl).readText()

                // Логируем ответ уровня DEBUG с тегом "Flickr cats"
                Log.d("Flickr cats", response)
            } catch (e: SocketException) {
                Log.e("Flickr cats", "SocketException: Проверьте разрешение на доступ к интернету в AndroidManifest.xml")
            } catch (e: NetworkOnMainThreadException) {
                Log.e("Flickr cats", "NetworkOnMainThreadException: Убедитесь, что сетевой запрос выполняется не в основном потоке")
            } catch (e: Exception) {
                Log.e("Flickr cats", "Exception: ${e.localizedMessage}")
            }
        }
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
}