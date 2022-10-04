package com.example.dolarxreal.API

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET


interface Endpoint {
    @GET("/json/last/USD-BRL")
    fun getCurrencies() : Call<JsonObject>

}
