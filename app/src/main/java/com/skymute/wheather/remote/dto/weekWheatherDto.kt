package com.skymute.wheather.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class WeeklyWeatherData(
    val latitude: Double ? = null,
    val longitude: Double ? = null,
    val generationtime_ms: Double ? = null,
    val utc_offset_seconds: Int ? = null,
    val timezone: String ? = null,
    val timezone_abbreviation: String ? = null,
    val elevation: Double ? = null,
    val daily_units: DailyUnits ? = null,
    val daily: Daily ? = null,
)

@Serializable
data class DailyUnits(
    val time: String,
    val weather_code: String,
    val apparent_temperature_max: String,
    val apparent_temperature_min: String,
    val sunrise: String,
    val sunset: String,
    val wind_gusts_10m_max: String,
    val wind_speed_10m_max: String
)

@Serializable
data class Daily(
    val time: List<Long>,//convert to Long for millis
    val weather_code: List<Int>,
    val apparent_temperature_max: List<Double>,
    val apparent_temperature_min: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    val wind_gusts_10m_max: List<Double>,
    val wind_speed_10m_max: List<Double>
)


