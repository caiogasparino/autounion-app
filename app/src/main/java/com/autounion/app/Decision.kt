package com.autounion.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import android.net.Uri
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Decision : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decision)
        supportActionBar?.hide()

        val path = intent.extras?.getString("path")
        val id = intent.extras?.getString("id")
        val btnBack = findViewById<AppCompatButton>(R.id.decision_back)
        val imgView = findViewById<AppCompatImageView>(R.id.decision_image)
        val btnApprove = findViewById<AppCompatButton>(R.id.decision_approve)
        val btnRecuse = findViewById<AppCompatButton>(R.id.decision_recuse)

        imgView.setImageURI(Uri.parse("http://18.221.41.112/img/${path?.replace("\"", "")}"))

        btnBack.setOnClickListener{
            val intent = Intent(this@Decision, Approve::class.java)
            startActivity(intent)
        }

        btnApprove.setOnClickListener{
            val retrofitClient = NetworkUtils.getAPI("http://18.221.41.112/api/")
            val endpoint = retrofitClient.create(AutoUnion::class.java)
            val intent = Intent(this@Decision, Approve::class.java)

            endpoint.approve(id.toString()).enqueue(object: Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    startActivity(intent)
                    Toast.makeText(this@Decision, "Usuário aprovado.", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@Decision, "Erro ao aprovar usuário.", Toast.LENGTH_LONG).show()
                }
            })
        }

        btnRecuse.setOnClickListener{
            val retrofitClient = NetworkUtils.getAPI("http://18.221.41.112/api/")
            val endpoint = retrofitClient.create(AutoUnion::class.java)
            val intent = Intent(this@Decision, Approve::class.java)

            endpoint.reject(id.toString()).enqueue(object: Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    startActivity(intent)
                    Toast.makeText(this@Decision, "Usuário excluído.", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@Decision, "Erro ao reprovar usuário.", Toast.LENGTH_LONG).show()
                }
            })
        }

    }
}