package com.example.catfactsapi

import retrofit2.Call
import retrofit2.http.GET

interface CatFactAPI {
    @GET("catfacts")
    fun getFacts(): Call<List<CatFact>>
}