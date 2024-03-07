package com.example.weather1

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weather1.databinding.ActivitySplashScreenBinding
import com.example.weather1.mvvm.WeatherVm
import com.example.weather1.service.LocationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SplashScreen : AppCompatActivity() {

    private lateinit var locationHelper: LocationHelper

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        locationHelper = LocationHelper(this)
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermissionAndEnabled()
    }

    private fun checkLocationPermissionAndEnabled() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission is granted, check if location services are enabled
            checkLocationEnabled()
        }
    }

    private fun checkLocationEnabled() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isLocationEnabled) {
            showLocationSettingsRequest()
        } else {
            // Location is enabled and permission granted, proceed with your app's functionality
            requestLocationUpdates()
        }
    }

    private fun showLocationSettingsRequest() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Location Required")
            .setMessage("This app requires location services to be enabled. Please enable location services.")
            .setPositiveButton("Settings") { _, _ ->
                // Direct the user to the location settings
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Optionally handle the user's denial
            }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, check if location services are enabled
                checkLocationEnabled()
            } else {
                // Permission denied, handle the failure
            }
        }
    }

    private fun requestLocationUpdates()  {
        Log.d("Request Location Updates","")
        locationHelper.requestLocationUpdates { location ->
            // Log latitude and longitude here
            val latitude = location.latitude
            val longitude = location.longitude
            2
           //proceedToMainActivity(latitude.toString(), longitude.toString())

            val myprefs = SharedPrefs(this)
            myprefs.setValue("longitude",longitude.toString())
            myprefs.setValue("latitude",latitude.toString())
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    /*private fun proceedToMainActivity(latitude: String?, longitude: String?) {
        val intent = Intent(this, MainActivity::class.java)
      //  intent.putExtra("latitude", latitude)
      //  intent.putExtra("longitude", longitude)
        startActivity(intent)
        finish()
    }*/

}