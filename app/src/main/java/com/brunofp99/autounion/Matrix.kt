package com.brunofp99.autounion

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Matrix {

    @GET("/maps/api/distancematrix/json")
    fun getMatrix(
        @Query(value = "origins") origin : String,
        @Query(value = "destinations") destinations : String,
        @Query(value = "key") key : String
    ): Call<ResponseBody>
}