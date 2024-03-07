package com.example.weather1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weather1.adapter.ForecastAdapterNewActivity
import com.example.weather1.mvvm.WeatherVm
import com.example.weather1.service.LocationHelper

class ForecastActivity : AppCompatActivity() {
    private lateinit var adapterForeCastNewAdapter: ForecastAdapterNewActivity
    lateinit var viM : WeatherVm
    lateinit var rvForeCast: RecyclerView

    var longi : String = ""
    var lati: String = ""

    private lateinit var locationHelper: LocationHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        viM = ViewModelProvider(this).get(WeatherVm::class.java)
        locationHelper = LocationHelper(this)
        adapterForeCastNewAdapter = ForecastAdapterNewActivity()
        rvForeCast = findViewById<RecyclerView>(R.id.rvForeCast)

        //lati = intent.getStringExtra("latitude").toString() // Default value as needed
        //longi = intent.getStringExtra("longitude").toString() // Default value as needed

        val myprefsloc = SharedPrefs(this)
        val lati = myprefsloc.getValue("latitude")
        val longi = myprefsloc.getValue("latitude")


        val sharedPrefs = SharedPrefs.getInstance(this)
        val city = sharedPrefs.getValueOrNull("city")
        //val city="Ambāla"
        Log.d("Prefs city", city.toString())
        if (city!=null){
            viM.getForecastUpcoming(city)

        } else {
            viM.getForecastUpcoming(null, lati, longi)
        }
        viM.forecastWeatherLiveData.observe(this, Observer {
            val setNewlist = it as List<WeatherList>
            Log.d("Forecast LiveData", setNewlist.toString())
            adapterForeCastNewAdapter.setList(setNewlist)
            rvForeCast.adapter = adapterForeCastNewAdapter
        })
    }

}