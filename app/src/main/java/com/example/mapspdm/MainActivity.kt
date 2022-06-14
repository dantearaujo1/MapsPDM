package com.example.mapspdm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQ_CODE = 1000;
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var btGetLocation: Button
    private lateinit var btOpenMap: Button
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvProvider: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btGetLocation = findViewById(R.id.btGetLocation)
        btOpenMap = findViewById(R.id.btOpenMap)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvProvider = findViewById(R.id.tvProvider)

        btGetLocation.setOnClickListener{
            getCurrentLocation()
        }

        btOpenMap.setOnClickListener{
            openMap()
        }
    }

    private fun getCurrentLocation(){
        checkPermission()
        if(isLocationEnable()){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if(location != null){
                        latitude = location.latitude
                        longitude = location.longitude

                        tvLatitude.text = "Latitude: ${location.latitude}"
                        tvLongitude.text = "Longitude: ${location.longitude}"
                        tvProvider.text = "Provider: ${location.provider}"

                        btOpenMap.visibility = View.VISIBLE
                    }
                    else{
                        Toast.makeText(this,"Location can't be displayed", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Failed on getting current location", Toast.LENGTH_SHORT).show()
                }
        }
        else{
            Toast.makeText(this,"Location is not enabled", Toast.LENGTH_SHORT).show()
        }

    }


    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQ_CODE)
            return
        }
    }

    private fun isLocationEnable():Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun openMap(){
        val uri = Uri.parse("geo:$latitude},${longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_REQ_CODE ->{
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Permission Denied, You need to grant permission to access location", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }



}
