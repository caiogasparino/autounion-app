package com.brunofp99.autounion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            val userId = id.toString() // Assuming id is a variable holding the user ID

            // Confirmation Dialog (Optional)
            val confirmationDialog = AlertDialog.Builder(this@DeleteUser)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes") { dialog, which ->
                    val retrofitClient = NetworkUtils.getAPI("http://127.0.0.1:8000/api/")
                    val endpoint = retrofitClient.create(AutoUnion::class.java)
                    endpoint.deleteUser(userId)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
            confirmationDialog.show()
        }

        fun deleteUser(userId: String) {



            val retrofitClient = NetworkUtils.getAPI("http://127.0.0.1:8000/api/")
            val endpoint = retrofitClient.create(AutoUnion::class.java)

            // Asynchronous execution example using enqueue (modify based on your library)
            endpoint.deleteUser(userId).enqueue(object : Callback {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        // User deleted successfully, start Login activity
                        val intent = Intent(this@DeleteUser, Login::class.java)
                        startActivity(intent)
                    } else {
                        // Handle delete failure (e.g., show error message)
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    // Handle network error
                }
            })
        }

//        btnDelete.setOnClickListener{
//            val retrofitClient = NetworkUtils.getAPI("http://127.0.0.1:8000/api/")
//            val endpoint = retrofitClient.create(AutoUnion::class.java)
//            val intent = Intent(this@DeleteUser, Login::class.java)
//
//            endpoint.deleteUser(id.toString())
//            startActivity(intent)
//        }


    }
}