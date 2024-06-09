package com.autounion.app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//"http://18.221.41.112/api/"
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