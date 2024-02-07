package com.example.placesapi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.placesapi.localdb.NearbyPlace
import com.example.placesapi.localdb.NearbyPlaceDatabase
import com.example.placesapi.localdb.NearbyPlaceViewModel
import com.example.placesapi.ui.theme.PlacesAPITheme
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest

// API K AIzaSyDYibUluvU0XY-k-KT55qicJt0v40OBkQc

const val TAG = "MainActivity"
const val LOCATION_PERMISSION_REQUEST_CODE = 42 // Choose any unique integer value
const val apiKey =  BuildConfig.PLACES_API_KEY //"AIzaSyDYibUluvU0XY-k-KT55qicJt0v40OBkQc"


class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            NearbyPlaceDatabase::class.java,
            "contacts.db"
        ).build()
    }

    private val viewModel by viewModels<NearbyPlaceViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NearbyPlaceViewModel(db.dao) as T
                }
            }
        }
    )




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")
            finish()
            return
        }

        setContent {
            PlacesAPITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(onClick = { getNearbyPlaces() }) {
                        Text(text = "Get Places")
                    }

                }
            }
        }
    }

    fun getNearbyPlaces() {
        Places.initialize(applicationContext, apiKey)

        val placesClient = Places.createClient(this)
        val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ID)

        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    val nearbyPlacesList = mutableListOf<NearbyPlace>()
                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                        Log.i(
                            TAG, "Place '${placeLikelihood.place.name} at coordinates ${placeLikelihood.place.latLng}, ID: ${placeLikelihood.place.id} has likelihood: ${placeLikelihood.likelihood}"
                        )
                    }

                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                        val name = placeLikelihood.place.name
                        val latLng = placeLikelihood.place.latLng?.toString() ?: ""
                        val id = placeLikelihood.place.id

                        if (name != null && id != null) {
                            val nearbyPlace = NearbyPlace(
                                name = name,
                                latLng = latLng,
                                id = id
                            )
                            nearbyPlacesList.add(nearbyPlace)
                        }
                    }

                    viewModel.saveNearbyPlaces(nearbyPlacesList)

                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.statusCode}")
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

    }



}

