package com.skymute.wheather.domain

import com.skymute.wheather.remote.dto.CurrentWeatherData
import com.skymute.wheather.remote.dto.Geocoding
import com.skymute.wheather.remote.dto.HourlyWeatherData
import com.skymute.wheather.remote.dto.WeeklyWeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherDataRepoService {
   suspend fun  getCurrentWeatherData(lat : Double , lon : Double): Flow<CurrentWeatherData>
   suspend fun  getHourlyWeatherData(lat : Double , lon : Double): Flow<HourlyWeatherData>
   suspend fun  getWeeklyWeatherData(lat : Double , lon : Double): Flow<WeeklyWeatherData>
   suspend fun  getGeoCodingData(query : String): Flow<Geocoding>
}
