package com.example.catfactsapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.catfactsapi.ui.theme.CatFactsAPITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatFactsAPITheme {
                CatFactApp()
            }
        }
    }
}

@Composable
fun CatFactApp() {
    val fact = "test"
    Text(text = fact )
}


