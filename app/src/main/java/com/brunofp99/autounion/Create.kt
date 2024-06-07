package com.brunofp99.autounion

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Create : AppCompatActivity() {
    private lateinit var pickImage: AppCompatButton
    private lateinit var selectedImage: AppCompatImageView
    private lateinit var photoName: String
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        supportActionBar?.hide()

        val btnBack = findViewById<AppCompatButton>(R.id.create_back)
        val name = findViewById<EditText>(R.id.create_name)
        val email = findViewById<EditText>(R.id.create_email)
        val password = findViewById<EditText>(R.id.create_password)
        val btnFindPhoto = findViewById<AppCompatButton>(R.id.create_find_photo)
        val btnCreate = findViewById<AppCompatButton>(R.id.create)

        pickImage = findViewById(R.id.create_find_photo)
        selectedImage = findViewById(R.id.selected_image)

        btnBack.setOnClickListener {
            val intent = Intent(this@Create, Login::class.java)
            startActivity(intent)
        }

        pickImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }

        btnCreate.setOnClickListener {
            val retrofitClient = NetworkUtils.getAPI("http://18.221.41.112/api/")
            val endpoint = retrofitClient.create(AutoUnion::class.java)
            val intent = Intent(this@Create, Login::class.java)
            val body = UserInfo(name.text.toString(), email.text.toString(), password.text.toString())
            val file = File(imageUri.path ?: "")
            val request = RequestBody.create(MediaType.parse("image/jpeg"), file)
            val imageBody = MultipartBody.Part.createFormData("image", photoName, request)

            endpoint.create(body).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val body = response.body()

                    if (body == null) {
                        var json: JSONObject? = null

                        try {
                            json = JSONObject(response.errorBody()?.string())
                        } catch (ex: JSONException) {
                            Toast.makeText(
                                this@Create,
                                "Erro ao salvar usuário. (JSONObject)",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        Toast.makeText(this@Create, json?.getString("message"), Toast.LENGTH_LONG).show()
                    } else {
                        val id = body.string()
                        startActivity(intent)
                        Toast.makeText(this@Create, "Usuário salvo.", Toast.LENGTH_LONG).show()

                        endpoint.saveImage(id, imageBody).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.body() == null) {
                                    Toast.makeText(this@Create, "Erro ao salvar a imagem, usuário salvo.", Toast.LENGTH_LONG).show()
                                } else {
                                    startActivity(intent)
                                    Toast.makeText(this@Create, "Usuário salvo.", Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(this@Create, "Erro ao salvar a imagem, usuário salvo.", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@Create, "Erro ao criar usuário.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    data class UserInfo(
        @SerializedName("name") val name: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("password") val password: String?
    )

    private val changeImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data
                imageUri = data?.data!!
                photoName = it.data?.data.toString().substring(it.data?.data.toString().lastIndexOf('/') + 1)
                selectedImage.setImageURI(data?.data)
            }
        }
}
