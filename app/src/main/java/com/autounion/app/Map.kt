package com.autounion.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Map : AppCompatActivity() {
    private lateinit var googleMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private var latitude: Double = -25.51364
    private var longitude: Double = -49.32152
    private var destiny: LatLng = LatLng(-25.51364, -49.32152)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        supportActionBar?.hide()

        val id = intent.extras?.getString("id")
        val admin = intent.extras?.getString("admin")
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        val btnBack = findViewById<AppCompatButton>(R.id.map_back)
        val btnApprove = findViewById<AppCompatButton>(R.id.map_approve)

        btnBack.setOnClickListener {
            val intent = Intent(this@Map, Login::class.java)
            startActivity(intent)
        }

        btnApprove.setOnClickListener {
            val targetActivity = if (admin == "0") {
                DeleteUser::class.java
            } else {
                Approve::class.java
            }
            val intent = Intent(this@Map, targetActivity)
            intent.putExtra("id", id)
            intent.putExtra("admin", admin)
            startActivity(intent)
        }

        checkLocationPermissions()

        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            setupLocationListeners()
            getCurrentLocation()
            setupMap()
            requestRoute()
        })
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                showPermissionDialog()
            } else {
                requestLocationPermissions()
            }
        } else {
            requestLocationPermissions()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("É necessário a permissão para continuar.")
            setTitle("Permissão")
            setPositiveButton("Continuar") { _, _ ->
                requestLocationPermissions()
            }
            setNegativeButton("Fechar o app") { dialog, _ -> dialog.dismiss() }
        }.show()
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            111
        )
    }

    private fun setupLocationListeners() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val gpsLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByGps = location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        val networkLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork = location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsLocationListener
            )
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkLocationListener
            )
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationByGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        locationByNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        currentLocation = locationByGps ?: locationByNetwork
        currentLocation?.let {
            latitude = it.latitude
            longitude = it.longitude
        }
    }

    private fun setupMap() {
        val currentLocation = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(currentLocation).title("My Location"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
    }

    private fun requestRoute() {
        val retrofitMatrix = NetworkUtils.getAPI("https://maps.googleapis.com")
        val matrixEndPoint = retrofitMatrix.create(Matrix::class.java)
        val origin = "$latitude,$longitude"
        val destination =
            "-25.420965,-49.248337|-25.442848,-49.304818|-23.289992,-51.220447|-24.953649,-53.420552|-23.570944,-46.673559|-23.549976,-46.566610|-23.593146,-46.677343|-26.316414,-48.863088|-26.893736,-49.065158|-27.574799,-48.598118|-26.958664,-48.639407"

        val ai: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName,
            PackageManager.GET_META_DATA
        )
        val key = ai.metaData["com.google.android.geo.API_KEY"].toString()

        matrixEndPoint.getMatrix(origin, destination, key).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                handleMatrixResponse(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Map, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleMatrixResponse(response: Response<ResponseBody>) {
        val body = response.body()
        val json: JSONObject? = if (body == null) {
            try {
                JSONObject(response.errorBody()?.string())
            } catch (ex: JSONException) {
                Toast.makeText(this, "Erro ao acessar resposta. (JSONObject)", Toast.LENGTH_LONG)
                    .show()
                return
            }
        } else {
            try {
                JSONObject(body.string())
            } catch (ex: JSONException) {
                Toast.makeText(this, "Erro ao acessar resposta. (JSONObject)", Toast.LENGTH_LONG)
                    .show()
                return
            }
        }

        json?.let {
            try {
                val rowsObject = it.optJSONArray("rows")
                val elements = rowsObject?.getJSONObject(0)?.optJSONArray("elements")
                var startValue =
                    elements?.getJSONObject(0)?.getJSONObject("distance")?.getInt("value") ?: return
                var index = 0

                for (i in 1 until elements.length()) {
                    val value =
                        elements?.getJSONObject(i)?.getJSONObject("distance")?.getInt("value")
                            ?: continue
                    if (value < startValue) {
                        startValue = value
                        index = i
                    }
                }

                val arrayLatLng = Address().getArray()
                destiny = arrayLatLng[index]

                googleMap.addMarker(MarkerOptions().position(destiny).title("Audi"))
                requestRouteToDestination()
            } catch (ex: JSONException) {
                Toast.makeText(this, "Erro ao acessar resposta. (JSONObject)", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun requestRouteToDestination() {
        val retrofitClient = NetworkUtils.getAPI("https://maps.googleapis.com/")
        val endpoint = retrofitClient.create(Maps::class.java)
        val origin = "$latitude,$longitude"
        val destination = "${destiny.latitude},${destiny.longitude}"

        val ai: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName,
            PackageManager.GET_META_DATA
        )
        val key = ai.metaData["com.google.android.geo.API_KEY"].toString()

        endpoint.getRoute(origin, destination, "false", "driving", key)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.let {
                        getRoute(it.string())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })
    }

    private fun getRoute(data: String) {
        val result = ArrayList<List<LatLng>>()
        val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
        val path = ArrayList<LatLng>()
        for (step in respObj.routes[0].legs[0].steps) {
            val startLatLng =
                LatLng(step.start_location.lat.toDouble(), step.start_location.lng.toDouble())
            path.add(startLatLng)
        }
        result.add(path)

        val lineOption = PolylineOptions()
        for (route in result) {
            lineOption.addAll(route)
            lineOption.width(10f)
            lineOption.color(Color.RED)
            lineOption.geodesic(true)
        }
        googleMap.addPolyline(lineOption)
    }
}
