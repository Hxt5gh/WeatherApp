package com.skymute.wheather.domain

import com.skymute.wheather.remote.WeatherServiceImp
import com.skymute.wheather.remote.dto.CurrentWeatherData
import com.skymute.wheather.remote.dto.Geocoding
import com.skymute.wheather.remote.dto.HourlyWeatherData
import com.skymute.wheather.remote.dto.WeeklyWeatherData
import kotlinx.coroutines.flow.Flow

class WeatherDataRepoImp(val weatherService: WeatherServiceImp) : WeatherDataRepoService {
    override suspend fun getCurrentWeatherData(lat: Double, lon: Double): Flow<CurrentWeatherData> {
      return weatherService.getCurrentWeather(lat , lon)
    }

    override suspend fun getHourlyWeatherData(lat: Double, lon: Double): Flow<HourlyWeatherData> {
        return weatherService.getHourlyWeather(lat ,  lon)
    }

    override suspend fun getWeeklyWeatherData(lat: Double, lon: Double): Flow<WeeklyWeatherData> {
        return weatherService.getWeeklyWeather(lat ,  lon)
    }

    override suspend fun getGeoCodingData(query: String): Flow<Geocoding> {
        return  weatherService.getGeoCodes(query)
    }
}