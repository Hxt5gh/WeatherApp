package com.skymute.wheather.remote

import android.util.Log
import com.skymute.wheather.remote.dto.CurrentWeatherData
import com.skymute.wheather.remote.dto.Geocoding
import com.skymute.wheather.remote.dto.HourlyWeatherData
import com.skymute.wheather.remote.dto.WeeklyWeatherData
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherServiceImp(val httpClient: HttpClient) : WeatherService {
    var response: HttpResponse? = null
    override suspend fun getCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeatherData> =
        flow {
            try {
                val url = URLBuilder().apply {
                    takeFrom("https://api.open-meteo.com/v1/forecast")
                    parameters.append("latitude", "${lat}")
                    parameters.append("longitude", "${lon}")
                    parameters.append(
                        "current",
                        //"relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m,wind_direction_10m"
                         "relative_humidity_2m,apparent_temperature,is_day,rain,weather_code,wind_speed_10m,wind_direction_10m"
                    )
                    parameters.append("timeformat", "unixtime")
                }.build()

                val data = httpClient.get<CurrentWeatherData>(url)
                Log.d("debug", "getCurrentWeather: ${data}")
                emit(data)
            } catch (e: Exception) {
                emit(CurrentWeatherData())
            }
        }

    //https://api.open-meteo.com/v1/forecast?latitude=30.7363&longitude=76.7884&hourly=apparent_temperature,rain,weather_code&timeformat=unixtime&forecast_days=1
    override suspend fun getHourlyWeather(lat: Double, lon: Double): Flow<HourlyWeatherData> =
        flow {
            val url = URLBuilder().apply {
                takeFrom("https://api.open-meteo.com/v1/forecast")
                parameters.append("latitude", "${lat}")
                parameters.append("longitude", "${lon}")
                parameters.append("hourly", "apparent_temperature,rain,weather_code")
                parameters.append("timeformat", "unixtime")//for time in millis
                parameters.append("forecast_days", "1")//for time in millis

            }.build()

            try {
                val data = httpClient.get<HourlyWeatherData>(url)
                Log.d("debug", "getHourlyWeatherXX:  ${data}")
                emit(data)
            } catch (e: Exception) {
                emit(HourlyWeatherData())
            }

        }

    //https://api.open-meteo.com/v1/forecast?latitude=30.7363&longitude=76.7884&daily=weather_code,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,wind_speed_10m_max,wind_gusts_10m_max&timeformat=unixtime
    override suspend fun getWeeklyWeather(lat: Double, lon: Double): Flow<WeeklyWeatherData> =
        flow {
            val url = URLBuilder().apply {
                takeFrom("https://api.open-meteo.com/v1/forecast")
                parameters.append("latitude", "${lat}")
                parameters.append("longitude", "${lon}")
                parameters.append(
                    "daily",
                    "weather_code,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,wind_speed_10m_max,wind_gusts_10m_max"
                )
                parameters.append("timeformat", "unixtime")//for time in millis
                parameters.append("forecast_days", "14")

            }.build()

            try {
                val data = httpClient.get<WeeklyWeatherData>(url)
                emit(data)
            } catch (e: Exception) {
                emit(WeeklyWeatherData())
            }
        }

    //https://geocoding-api.open-meteo.com/v1/search?name=ha&count=20&language=en&format=json
    override suspend fun getGeoCodes(query: String): Flow<Geocoding> = flow {
        val url = URLBuilder().apply {
            takeFrom("https://geocoding-api.open-meteo.com/v1/search")
            parameters.append("name", query)
            parameters.append("count", "20")
            parameters.append("language", "en")
            parameters.append("format" , "json")

        }.build()

        try {
            val data = httpClient.get<Geocoding>(url)
            Log.d("debug", "getGeoCodes: ${data}")
            emit(data)
        }catch (e : Exception)
        {
            emit(Geocoding())
        }

    }

}