package com.brunofp99.autounion

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
//import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.os.Bundle
//import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.BitmapDescriptor
//import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
//import com.google.android.gms.tasks.CancellationToken
//import com.google.android.gms.tasks.CancellationTokenSource
//import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.gson.Gson
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import okhttp3.Callback
//import okhttp3.Dispatcher
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody
//import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
//import retrofit2.create
//import kotlin.collections.Map

class Map : AppCompatActivity() {
    private lateinit var googleMap: GoogleMap
    //    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    //    private lateinit var mapFragment : SupportMapFragment
//    private var mGoogleMap:GoogleMap? = null
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
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        val btnBack = findViewById<AppCompatButton>(R.id.map_back)
        val btnApprove = findViewById<AppCompatButton>(R.id.map_approve)

        btnBack.setOnClickListener {
            val intent = Intent(this@Map, Login::class.java)
            startActivity(intent)
        }

        btnApprove.setOnClickListener {
            if(admin == "0"){
                val intent = Intent(this@Map, DeleteUser::class.java)
                intent.putExtra("id", id)
                intent.putExtra("admin", admin)
                startActivity(intent)
            }else {
                val intent = Intent(this@Map, Approve::class.java)
                startActivity(intent)
            }
        }

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
                            this@Map,
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
                    this@Map,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    112)
            }

        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                111)
        }

        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            val gpsLocationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationByGps= location
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            val networkLocationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationByNetwork= location
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }

            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }

            val lastKnownLocationByNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }

            if (locationByGps != null) {
                currentLocation = locationByGps
                latitude = currentLocation?.latitude!!
                longitude = currentLocation?.longitude!!
                // use latitude and longitude as per your need
            } else {
                currentLocation = locationByNetwork
                latitude = currentLocation?.latitude!!
                longitude = currentLocation?.longitude!!
                // use latitude and longitude as per your need
            }
//            val marker = BitmapDescriptorFactory.defaultMarker()

            val currentLocation = LatLng(latitude, longitude)
//                .icon(BitmapDescriptor())
            googleMap.addMarker(MarkerOptions().position(currentLocation).title("My Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))

            val retrofitMatrix = NetworkUtils.getAPI("https://maps.googleapis.com")
            val matrixEndPoint = retrofitMatrix.create(Matrix::class.java)
            val origin = "${currentLocation.latitude},${currentLocation.longitude}"
            val destination = "-25.420965,-49.248337|-25.442848,-49.304818|-23.289992,-51.220447|-24.953649,-53.420552|-23.570944,-46.673559|-23.549976,-46.566610|-23.593146,-46.677343|-26.316414,-48.863088|-26.893736,-49.065158|-27.574799,-48.598118|-26.958664,-48.639407"
            val ai: ApplicationInfo = applicationContext.packageManager
                .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
            val value = ai.metaData["com.google.android.geo.API_KEY"]
            val key = value.toString()

            matrixEndPoint.getMatrix(origin,destination,key).enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                    val body = response.body()
                    var json: JSONObject? = null
                    if(body == null){
                        try {
                            json = JSONObject(response.errorBody()?.string())
                        } catch (ex: JSONException) {
                            Toast.makeText(
                                this@Map,
                                "Erro ao acessar resposta. (JSONObject) ",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Toast.makeText(this@Map, json?.getString("message"), Toast.LENGTH_LONG).show()
                    }else{
                        try {
                            json = JSONObject(body?.string())
                            val rowsObject = json.optJSONArray("rows")
                            val valorRow = rowsObject.getJSONObject(0)
                            val elements = valorRow.optJSONArray("elements")
                            val startElement = elements.getJSONObject(0)
                            val startDistance = startElement.getJSONObject("distance")
                            var startValue = startDistance.getInt("value")
                            var index = 0

                            for (i in 1 until elements.length()) {
                                val valorElement = elements?.getJSONObject(i)
                                val distance = valorElement?.getJSONObject("distance")
                                val value = distance?.getInt("value")
                                if (value != null) {
                                    if(value < startValue){
                                        startValue = value
                                        index = i
                                    }
                                }
                            }

                            val arrayLatLng = Address().getArray()
                            destiny = arrayLatLng[index]

                            googleMap.addMarker(MarkerOptions().position(destiny).title("Audi"))

                            val retrofitClient = NetworkUtils.getAPI("https://maps.googleapis.com/")
                            val endpoint = retrofitClient.create(Maps::class.java)

                            endpoint.getRoute(origin, "${destiny.latitude},${destiny.longitude}", "false", "driving", key).enqueue(object : retrofit2.Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                                    val res = response.body()!!.string()
                                    getRoute(res)
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                            })
                        } catch (ex: JSONException) {
                            Toast.makeText(
                                this@Map,
                                "Erro ao acessar resposta. (JSONObject)",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@Map, t.message, Toast.LENGTH_LONG).show()
                }
            })
        })
    }

    private fun getRoute(data: String) {
        val result =  ArrayList<List<LatLng>>()
        val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)
        val path =  ArrayList<LatLng>()
        for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
            val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
                ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
            path.add(startLatLng)
//            val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
//            path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
        }
        result.add(path)

        val lineoption = PolylineOptions()
        for (i in result.indices){
            lineoption.addAll(result[i])
            lineoption.width(10f)
            lineoption.color(Color.RED)
            lineoption.geodesic(true)
        }
        googleMap.addPolyline(lineoption)
    }

//    private fun decodePolyline(encoded: String): List<LatLng> {
//
//        val poly = ArrayList<LatLng>()
//        var index = 0
//        val len = encoded.length
//        var lat = 0
//        var lng = 0
//
//        while (index < len) {
//            var b: Int
//            var shift = 0
//            var result = 0
//            do {
//                b = encoded[index++].toInt() - 63
//                result = result or (b and 0x1f shl shift)
//                shift += 5
//            } while (b >= 0x20)
//            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            lat += dlat
//
//            shift = 0
//            result = 0
//            do {
//                b = encoded[index++].toInt() - 63
//                result = result or (b and 0x1f shl shift)
//                shift += 5
//            } while (b >= 0x20)
//            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            lng += dlng
//
//            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
//            poly.add(latLng)
//        }
//
//        return poly
//    }
}