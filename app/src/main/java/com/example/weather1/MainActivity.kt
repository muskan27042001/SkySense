package com.example.weather1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weather1.adapter.ForeCastAdapter
import com.example.weather1.adapter.WeatherToday
import com.example.weather1.databinding.ActivityMainBinding
import com.example.weather1.mvvm.WeatherVm
import com.example.weather1.service.LocationHelper
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    lateinit var viM: WeatherVm
    private lateinit var adapterForeCastAdapter: ForeCastAdapter
    lateinit var adapterWeatherToday: WeatherToday
    lateinit var mainforecastadapter: RecyclerView
    lateinit var forecastRecyclerView: RecyclerView

    private lateinit var binding: ActivityMainBinding

    var longi: String = ""
    var lati: String = ""

    private lateinit var locationHelper: LocationHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Initialize ViewModel
        viM = ViewModelProvider(this).get(WeatherVm::class.java)
        // Initialize adapters and RecyclerViews
        adapterForeCastAdapter = ForeCastAdapter()
        adapterWeatherToday = WeatherToday()
        mainforecastadapter = findViewById<RecyclerView>(R.id.mainforecastadapter)
        forecastRecyclerView = findViewById<RecyclerView>(R.id.forecastRecyclerView)
        // Initialize binding for data binding
        binding.lifecycleOwner = this
        binding.vm = viM
        // Show loading indicator initially
        //binding.progressBar.visibility = View.VISIBLE
        //showLoadingIndicator(true)
        // Handler to delay hiding
        /*Handler(Looper.getMainLooper()).postDelayed({
            // This block will be executed after 3 seconds
            // Fetch data here if needed, then update UI accordingly
            showLoadingIndicator(false)
        }, 3000) // 3000 milliseconds delay for 3 seconds*/

        //lati = intent.getStringExtra("latitude").toString() // Default value as needed
        //longi = intent.getStringExtra("longitude").toString() // Default value as needed

        val myprefsloc = SharedPrefs(this)
        lati = myprefsloc.getValue("latitude").toString()
        longi = myprefsloc.getValue("longitude").toString()

        Log.d("Mainactivity lati",lati.toString())
        Log.d("Mainactivity longi",longi.toString())


        viM.getWeather(null, lati, longi)
        viM.getForecastUpcoming(null,lati,longi)

        val sharedPrefs = SharedPrefs.getInstance(this@MainActivity)
        // so that if we are new city
        //sharedPrefs.clearCityValue()
        // observing live data of today weather and setting adapter
        viM.todayWeatherLiveData.observe(this, Observer {
            val setNewlist = it as List<WeatherList>
            Log.e("TODayweather list", it.toString())
            adapterWeatherToday.setList(setNewlist)
            forecastRecyclerView.adapter = adapterWeatherToday
        })

        binding.next5Days.setOnClickListener {
            startActivity(Intent(this, ForecastActivity::class.java))
        }

        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.BLACK)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val sharedPrefs = SharedPrefs.getInstance(this@MainActivity)
                sharedPrefs.setValueOrNull("city", query!!)
                if (!query.isNullOrEmpty()) {
                    viM.getWeather(query)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                    binding.searchView.isIconified = true
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        viM.closetorexactlysameweatherdata.observe(this, Observer { weatherData ->
            weatherData?.let {
                val temperatureFahrenheit = it.main?.temp
                val temperatureCelsius = (temperatureFahrenheit?.minus(273.15))
                val temperatureCelsiusRouded = temperatureCelsius?.roundToInt()
                val temperatureFormatted = temperatureCelsiusRouded.toString()

                it.weather?.firstOrNull()?.let { weather ->
                    binding.descMain.text = weather.description
                    Log.d("CLOSE", weather.main!!)
                    Log.d("icon",weather.icon!!)
                    when (weather.main) {
                        "Rain", "Drizzle", "Thunderstorm", "Clear" -> {
                            //notificationhelper.startNotification()
                            Log.e("MAIN", weather.main!!)
                        }

                        else -> {}
                    }
                }

                binding.tempMain.text = "$temperatureFormatted°C"

                val feeltemperatureFahrenheit = it.main?.feelsLike
                val feeltemperatureCelsius = (feeltemperatureFahrenheit?.minus(273.15))
                val feeltemperatureFormatted = String.format("%.2f", feeltemperatureCelsius ?: 0.0)

                val maxtemperatureFahrenheit = it.main?.tempMax
                val maxtemperatureCelsius = (maxtemperatureFahrenheit?.minus(273.15))
                val maxtemperatureCelsiusRounded = maxtemperatureCelsius?.roundToInt()
                val maxtemperatureFormatted = maxtemperatureCelsiusRounded.toString()

                val mintemperatureFahrenheit = it.main?.tempMin
                val mintemperatureCelsius = (mintemperatureFahrenheit?.minus(273.15))
                val mintemperatureCelsiusRounded = mintemperatureCelsius?.roundToInt()
                val mintemperatureFormatted = mintemperatureCelsiusRounded.toString()

                binding.maxTemp.text = "$maxtemperatureFormatted°C"
                binding.minTemp.text = "$mintemperatureFormatted°C"
                binding.feelsliketext.text = "$feeltemperatureFormatted°"
                binding.windtext.text = it.wind?.speed.toString()
                binding.humiditytext.text = it.main?.humidity.toString()
                binding.aptxt.text = it.main?.pressure.toString()
                binding.visibilitytext.text = "${it.visibility.toString()}%"

                val systemTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"))
                Log.d("systemtime",systemTime)
                for (i in it.weather) {
                    if (i.icon == "01d") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.clearday)
                        //  binding.imageMain.setImageResource(R.drawable.oned)
                    }
                    if (i.icon == "01n" && (systemTime > "15" && systemTime <="18")) {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.eveningsky)
                        // binding.backgroundlayout.setBackgroundResource(R.drawable.night2)
                    }
                    if (i.icon == "01n" && systemTime > "18") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.clearnight)
                        // binding.backgroundlayout.setBackgroundResource(R.drawable.night2)
                    }
                    if (i.icon == "01n" && (systemTime >= "09" && systemTime <= "15")) {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.clearday)
                        // binding.backgroundlayout.setBackgroundResource(R.drawable.night2)
                    }
                    if (i.icon == "02d" || i.icon == "04d") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.cloudyday)
                        //binding.imageMain.setImageResource(R.drawable.twod)
                    }
                    if (i.icon == "02n" || i.icon == "04n") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.cloudynight)
                        //binding.imageMain.setImageResource(R.drawable.twon)
                    }
                    if (i.icon == "03d" || i.icon == "03n") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.graycloudyday)
                        //binding.imageMain.setImageResource(R.drawable.threedn)
                    }
                    if (i.icon == "10d" || i.icon == "09d") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.rainyday)
                    }
                    if (i.icon == "10n" || i.icon == "09n") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.rainynight)
                    }
                    if (i.icon == "11d" || i.icon == "11n") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.thundernight)
                        // binding.imageMain.setImageResource(R.drawable.elevend)
                    }
                    if (i.icon == "13d" || i.icon == "13n") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.snowyday)
                    }
                    if (i.icon == "50d" || i.icon == "50n") {
                        binding.scrollViewBg.setBackgroundResource(R.drawable.windyday)
                    }
                }

                it.dtTxt?.let { dtTxt ->
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val date = inputFormat.parse(dtTxt)
                    val outputFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
                    date?.let {
                        val dateanddayname = outputFormat.format(it)
                        binding.dateDayMain.text = dateanddayname
                    }
                }

                // Process weather icons similarly, checking for nulls safely
            } ?: run {
                // Handle the case where weatherData is null
                Toast.makeText(this, "Weather data is not available.", Toast.LENGTH_SHORT).show()
            }
        })

       // val city="Ambāla"
        val sharedPrefs1 = SharedPrefs.getInstance(this)
        val city = sharedPrefs1.getValueOrNull("city")
        Log.d("Prefs", city.toString())
        if (city!=null){
            viM.getForecastUpcoming(city)

        } else {
            viM.getWeather(null, lati, longi)

        }
        viM.forecastWeatherLiveData.observe(this, Observer {
            val setNewlist = it as List<WeatherList>
            Log.d("Forecast LiveData", setNewlist.toString())
            adapterForeCastAdapter.setList(setNewlist)
            mainforecastadapter.adapter = adapterForeCastAdapter
        })
    }

    private fun showLoadingIndicator(show: Boolean) {
       // binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.mainforecastadapter.visibility = if (show) View.GONE else View.VISIBLE
        binding.forecastRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
        binding.searchviewlayout.visibility = if (show) View.GONE else View.VISIBLE
        binding.citydaydatetemplayout.visibility = if (show) View.GONE else View.VISIBLE
        binding.scrollViewBg.visibility = if (show) View.GONE else View.VISIBLE
        // Hide or show other UI elements as needed
    }
}