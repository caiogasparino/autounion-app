package com.brunofp99.autounion

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//"http://127.0.0.1:8000/api/"
//"https://maps.googleapis.com/maps/api/distancematrix/"
class NetworkUtils {
    companion object {
        fun getAPI(path : String) : Retrofit {
            return Retrofit.Builder()
                .baseUrl(path)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}