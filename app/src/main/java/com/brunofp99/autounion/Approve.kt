package com.brunofp99.autounion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback

class Approve : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approve)
        supportActionBar?.hide()

        getProfiles()

        val btnBack = findViewById<AppCompatButton>(R.id.create_back)

        btnBack.setOnClickListener {
            val intent = Intent(this@Approve, Map::class.java)
            startActivity(intent)
        }
    }

    data class Profile(val id: String, val name: String, val path: String, val email: String)

    fun getProfiles(){
        val retrofitClient = NetworkUtils.getAPI("http://18.221.41.112/api/")
        val endpoint = retrofitClient.create(AutoUnion::class.java)

        endpoint.getUnderAnalysis().enqueue(object : Callback<List<JsonObject>> {
            override fun onResponse(call: Call<List<JsonObject>>, response: retrofit2.Response<List<JsonObject>>) {
                val data = mutableListOf<Profile>()
                var names = arrayOf<String>()
                val listView = findViewById<ListView>(R.id.list_approve)

                response.body()?.iterator()?.forEach {
                    val profile = Profile(
                        it.get("id").toString(),
                        it.get("name").toString(),
                        it.get("path").toString(),
                        it.get("email").toString()
                    );
                    data.add(profile)
                }

                data.iterator()?.forEach { names += it.name }

                val arrayAdapter = ArrayAdapter(this@Approve, android.R.layout.simple_list_item_1, names)

                listView.adapter = arrayAdapter

                listView.setOnItemClickListener{ adapterView, view, i, l ->
                    val intent = Intent(this@Approve, Decision::class.java)
                    intent.putExtra("path", data[i].path)
                    intent.putExtra("id", data[i].id)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<List<JsonObject>>, t: Throwable) {
                Toast.makeText(this@Approve, "Algo deu errado, tente novamente mais tarde.", Toast.LENGTH_LONG).show()
            }
        })
    }
}