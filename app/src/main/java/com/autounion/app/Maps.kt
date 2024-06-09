package com.autounion.app

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Maps {
    @GET("/maps/api/directions/json")
    fun getRoute(
        @Query(value = "origin") origin : String,
        @Query(value = "destination") destinations : String,
        @Query(value = "sensor") sensor : String,
        @Query(value = "mode") mode : String,
        @Query(value = "key") key : String
    ) : Call<ResponseBody>
}