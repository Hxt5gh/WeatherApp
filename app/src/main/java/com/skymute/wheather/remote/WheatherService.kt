package com.skymute.wheather.remote

import com.skymute.wheather.remote.dto.CurrentWeatherData
import com.skymute.wheather.remote.dto.Geocoding
import com.skymute.wheather.remote.dto.HourlyWeatherData
import com.skymute.wheather.remote.dto.WeeklyWeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherService {

    suspend fun getCurrentWeather(lat : Double , lon : Double) : Flow<CurrentWeatherData>
    suspend fun getHourlyWeather(lat : Double , lon : Double) : Flow<HourlyWeatherData>
    suspend fun getWeeklyWeather(lat : Double , lon : Double) : Flow<WeeklyWeatherData>
    suspend fun getGeoCodes(query : String) : Flow<Geocoding>

}