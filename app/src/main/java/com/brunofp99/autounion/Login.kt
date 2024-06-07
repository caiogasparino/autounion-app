package com.brunofp99.autounion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.widget.AppCompatButton as AppButton

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val btnCreate = findViewById<AppButton>(R.id.login_create)
        val btnEnter = findViewById<AppButton>(R.id.login_enter)
        val email = findViewById<EditText>(R.id.login_email)
        val password = findViewById<EditText>(R.id.login_password)

        if(
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                AlertDialog.Builder(this).apply {
                    setMessage("É necessario a permissão, para continuar.")
                    setTitle("Permissão")
                    setPositiveButton("Continuar") { d, i ->
                        ActivityCompat.requestPermissions(
                            this@Login,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            112)
                    }
                    setNegativeButton("Fechar o app") { d, i -> d.dismiss() }
                }.show()
            } else {
                ActivityCompat.requestPermissions(
                    this@Login,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    112)
            }

        }else{
            ActivityCompat.requestPermissions(
                this@Login,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                111)
        }

        btnCreate.setOnClickListener {
            val intent = Intent(this@Login, Create::class.java)
            startActivity(intent)
        }

        btnEnter.setOnClickListener{
            val retrofitClient = NetworkUtils.getAPI("http://18.221.41.112/api/")
            val endpoint = retrofitClient.create(AutoUnion::class.java)
            val intent = Intent(this@Login, Map::class.java)
            val loginInfo = LoginInfo(email.text.toString(), password.text.toString())

            endpoint.signIn(loginInfo).enqueue(object: Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val body = response.body()
                    if(body == null){
                        var json: JSONObject? = null

                        try {
                            json = JSONObject(response.errorBody()?.string())
                        } catch (ex: JSONException) {
                            Toast.makeText(
                                this@Login,
                                "Erro ao entrar. (JSONObject)",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        Toast.makeText(this@Login, json?.getString("message"), Toast.LENGTH_LONG).show()
                    }else{
                        var json: JSONObject? = null

                        try {
                            json = JSONObject(body?.string())
                        } catch (ex: JSONException) {
                            Toast.makeText(
                                this@Login,
                                "Erro ao acessar resposta. (JSONObject)",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        val userObject = JSONObject(json?.getString("user"))
                        intent.putExtra("id", userObject.getString("id"))
                        intent.putExtra("admin", userObject.getString("admin"))
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@Login, "Erro ao fazer o login.", Toast.LENGTH_LONG).show()
                }
            })
        }

    }

    data class LoginInfo (
        @SerializedName("email") val email: String?,
        @SerializedName("password") val password: String?
    )

}