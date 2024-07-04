package com.example.weather1.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.weather1.R
import com.example.weather1.WeatherList
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.math.roundToInt

// WeatherToday is a subclass of RecyclerView.Adapter that manages TodayHolder instances.
class WeatherToday : RecyclerView.Adapter<TodayHolder>() {
    // This list holds weather data (WeatherList objects).
    private var listOfTodayWeather = listOf<WeatherList>()

    // Inflates the layout (R.layout.todayforecastlayout) for each item in the RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todayforecastlayout, parent, false)
        return TodayHolder(view)
    }

    // Returns the number of items in listOfTodayWeather.
    override fun getItemCount(): Int {
        return listOfTodayWeather.size
    }

    // Binds data to TodayHolder views. It extracts time, converts temperature from Kelvin to
    // Celsius, and sets the weather icon based on icon field in WeatherList
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TodayHolder, position: Int) {
        val todayForeCast = listOfTodayWeather[position]
        Log.d("dttxt",todayForeCast.dtTxt.toString())
        holder.timeDisplay.text = todayForeCast.dtTxt!!.substring(11, 16).toString()
        val temperatureFahrenheit = todayForeCast.main?.temp
        val temperatureCelsius = (temperatureFahrenheit?.minus(273.15))
        val temperatureCelsiusRounded = temperatureCelsius?.roundToInt()
        val temperatureFormatted = temperatureCelsiusRounded.toString()
        holder.tempDisplay.text = "$temperatureFormatted °"
        val calendar = Calendar.getInstance()

// Define the desired format
        val dateFormat = SimpleDateFormat("HH::mm")
        val formattedTime = dateFormat.format(calendar.time)

        val timeofapi = todayForeCast.dtTxt!!.split(" ")
        val partafterspace = timeofapi[1]

        Log.e("time" , " formatted time:${formattedTime}, timeofapi: ${partafterspace}")

        for ( i in todayForeCast.weather){
            if (i.icon == "01d") {
                holder.imageDisplay.setImageResource(R.drawable.oned)
            }
            if (i.icon == "01n") {
                holder.imageDisplay.setImageResource(R.drawable.onen)
            }
            if (i.icon == "02d") {
                holder.imageDisplay.setImageResource(R.drawable.twod)
            }
            if (i.icon == "02n") {
                holder.imageDisplay.setImageResource(R.drawable.twon)
            }
            if (i.icon == "03d" || i.icon == "03n") {
                holder.imageDisplay.setImageResource(R.drawable.threedn)
            }
            if (i.icon == "10d") {
                holder.imageDisplay.setImageResource(R.drawable.tend)
            }
            if (i.icon == "10n") {
                holder.imageDisplay.setImageResource(R.drawable.tenn)
            }
            if (i.icon == "04d" || i.icon == "04n") {
                holder.imageDisplay.setImageResource(R.drawable.fourdn)
            }
            if (i.icon == "09d" || i.icon == "09n") {
                holder.imageDisplay.setImageResource(R.drawable.ninedn)
            }
            if (i.icon == "11d" || i.icon == "11n") {
                holder.imageDisplay.setImageResource(R.drawable.elevend)
            }
            if (i.icon == "13d" || i.icon == "13n") {

                holder.imageDisplay.setImageResource(R.drawable.thirteend)
            }
            if (i.icon == "50d" || i.icon == "50n") {
                holder.imageDisplay.setImageResource(R.drawable.fiftydn)
            }
        }
    }

    // Updates listOfTodayWeather with new data. This method is typically called from outside the
    // adapter to update the data set.
    fun setList(listOfToday: List<WeatherList>) {
        this.listOfTodayWeather = listOfToday
    }
}

// TodayHolder is a RecyclerView ViewHolder that holds references to views (ImageView for weather
// icon, TextView for temperature and time) inside each item's layout (R.layout.todayforecastlayout).
class TodayHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val imageDisplay : ImageView = itemView.findViewById(R.id.imageDisplay)
    val tempDisplay : TextView = itemView.findViewById(R.id.tempDisplay)
    val timeDisplay : TextView = itemView.findViewById(R.id.timeDisplay)
}



