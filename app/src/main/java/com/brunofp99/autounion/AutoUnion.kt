package com.brunofp99.autounion

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.Headers
import kotlin.collections.Map

interface AutoUnion {
    @Headers("Content-Type: application/json")
    @POST("user/create")
    fun create( @Body data : Create.UserInfo) : Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun signIn( @Body data : Login.LoginInfo ) : Call<ResponseBody>

    @DELETE("user/deleteUserPermanently/{id}")
    fun deleteUser(@Path(value = "id", encoded = true) id : String) : Call<ResponseBody>

    @GET("underAnalysis")
    fun getUnderAnalysis() : Call<List<JsonObject>>

    @POST("underAnalysis/approve/{id}")
    fun approve(@Path(value = "id", encoded = true) id : String) : Call<ResponseBody>

    @DELETE("underAnalysis/reject/{id}")
    fun reject(@Path(value = "id", encoded = true) id : String) : Call<ResponseBody>

    @Headers("Content-Type: multipart/form-data")
    @POST("api/image/{id}")
    fun saveImage(@Path(value = "id", encoded = true) id : String, @Body data : MultipartBody.Part) : Call<ResponseBody>


}
