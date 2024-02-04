package com.example.catfactsapi

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.ResponseBody

class CatFactService {

    private val retrofit = RetrofitClient.getClient()
    private val catFactAPI = retrofit.create(CatFactAPI::class.java)

    fun successfulFactResponse() {
        val factsResponse = catFactAPI
            .getFacts()
            .execute()

        val successful = factsResponse.isSuccessful
        val httpStatusCode = factsResponse.code()
        val httpStatusMessage = factsResponse.message()
    }

    fun errorFactResponse() {
        val factsResponse = catFactAPI
            .getFacts()
            .execute()

        val errorBody: ResponseBody? = factsResponse.errorBody()

        val mapper = ObjectMapper()
        val mappedBody: ErrorResponse? = errorBody?.let { notNullErrorBody ->
            mapper.readValue(notNullErrorBody.toString(), ErrorResponse::class.java)
        }
    }

    fun headersFactResponse() {
        val factsResponse = catFactAPI
            .getFacts()
            .execute()

        val headers = factsResponse.headers()
        val customHeaderValue = headers["custom-headers"]
    }

}