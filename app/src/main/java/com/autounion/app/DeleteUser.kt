package com.autounion.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)
        supportActionBar?.hide()

        val btnBack = findViewById<AppCompatButton>(R.id.back)
        val btnDelete = findViewById<AppCompatButton>(R.id.delete)
        val id = intent.extras?.getString("id")
        val admin = intent.extras?.getString("admin")

        btnBack.setOnClickListener {
            val intent = Intent(this@DeleteUser, Map::class.java)
            intent.putExtra("id", id)
            intent.putExtra("admin", admin)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            val retrofitClient = NetworkUtils.getAPI("http://18.221.41.112/api/")
            val endpoint = retrofitClient.create(AutoUnion::class.java)
            val intent = Intent(this@DeleteUser, Login::class.java)

            endpoint.deleteUser(id.toString()).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DeleteUser, "Usuário deletado com sucesso", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@DeleteUser, "Erro ao deletar usuário", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@DeleteUser, "Falha na comunicação: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
