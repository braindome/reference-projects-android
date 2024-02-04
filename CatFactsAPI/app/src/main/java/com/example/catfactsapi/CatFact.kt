package com.example.catfactsapi

import com.fasterxml.jackson.annotation.JsonProperty

data class CatFact(
    @JsonProperty("fact") val fact: String,
    @JsonProperty("length") val length: Int
)
