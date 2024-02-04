package com.example.apiplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.apiplayground.ui.theme.APIPlaygroundTheme
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APIPlaygroundTheme {
                var catFactString = remember { mutableStateOf<String>("")}
                
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Text(text = catFactString.value)
                        Button(onClick = { lifecycleScope.launch { getCatFact(catFactString) } }) {

                        }
                    }
                    
                }
            }
        }
    }
    
    fun getCatFact(catFactString: MutableState<String>) {
        val apiUrl = "https://catfact.ninja/fact" //API endpoint

        try {
            val url : URL = URI.create(apiUrl).toURL()
            val connection : HttpURLConnection = url.openConnection() as HttpURLConnection

            //Request method: GET
            connection.requestMethod = "GET"

            // Response code
            val responseCode: Int = connection.responseCode
            println("Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read and print the response data
                val reader : BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                val response = StringBuilder()

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()

                catFactString.value = response.toString()
            } else {
                println("Error: Unable to fetch data from the API")
            }

            // Close the connection
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

