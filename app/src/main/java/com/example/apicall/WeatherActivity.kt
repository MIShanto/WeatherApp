package com.example.apicall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.location.Geocoder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.*
import org.json.JSONObject

class WeatherActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        //get data from intent
        val intent = intent
        val cityName = intent.getStringExtra("CityName").toString()

        Log.d("", "onCreate: "+cityName)

        if (cityName != null) {
            getWeather(cityName)
        }
    }


    private fun getWeather(cityName : String)
    {
        var url = "https://api.openweathermap.org/data/2.5/weather?q=${cityName}&units=metric&appid=5bf963ea904e482e1e5f466a65347b06"

        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response)
            {
                var jsonString = response.body?.string()

                ParseJson(jsonString)

            }
        })
    }

    private fun ParseJson(jsonString: String?) {

        // get JSONObject from JSON file
        val obj = JSONObject(jsonString)

        Log.d("TAG", obj.toString())
        //Log.d("TAG", "ParseJson: "+obj.getString("name"))

        // fetch JSONObject named weather
        val jsonArray: JSONArray = obj.getJSONArray("weather")
        val weatherJsonObject : JSONObject = jsonArray.getJSONObject(0)
        val weatherDescription : String = weatherJsonObject.getString("description")

        var weatherText : TextView = findViewById(R.id.weatherDescription)

        //id to get icon from https://openweathermap.org/img/wn/<iconID>@2x.png
        // full icon list: https://openweathermap.org/weather-conditions#Icon-list
        val weatherIcon : String = weatherJsonObject.getString("icon")

        val mainJSONObject : JSONObject = obj.getJSONObject("main")
        val temp : String = (mainJSONObject.getString("temp"))
        val feelsLike : String = (mainJSONObject.getString("feels_like"))
        val humidity : String = mainJSONObject.getString("humidity") // %

        val windJSONObject : JSONObject = obj.getJSONObject(    "wind")
        val windSpeed : String = windJSONObject.getString("speed") // meter/sec


        runOnUiThread{
            val weatherImage : ImageView = findViewById<ImageView>(R.id.weather_icon)
            Picasso.get().load("https://openweathermap.org/img/wn/${weatherIcon}@2x.png").into(weatherImage)
            weatherImage.visibility = View.VISIBLE

            weatherText.text = weatherDescription
            findViewById<TextView>(R.id.temp).text = temp
            findViewById<TextView>(R.id.textView3).text = "Feels like: ${feelsLike}\n\nHumidity: ${humidity}%" +
                    "\n\nWind speed: ${windSpeed} meter/sec"

        }

        findViewById<Button>(R.id.back_to_main).setOnClickListener{
            //intent to start activity
            val intent = Intent(this@WeatherActivity, MainActivity::class.java)
            startActivity(intent)
        }


    }
}