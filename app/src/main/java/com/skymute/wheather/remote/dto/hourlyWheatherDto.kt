package com.skymute.wheather.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class HourlyWeatherData(
    val latitude: Double ? = null,
    val longitude: Double ? = null,
    val generationtime_ms: Double ? = null,
    val utc_offset_seconds: Int ? = null,
    val timezone: String ? = null,
    val timezone_abbreviation: String ? = null,
    val elevation: Double ? = null,
    val hourly_units: HourlyUnits ? = null,
    val hourly: HourlyX ? = null,
)

@Serializable
data class HourlyUnits(
    val time: String,
    val apparent_temperature: String,
    val rain: String,
    val weather_code: String
)

@Serializable
data class HourlyX(
    val time: List<Long>, //change to long for millis
    val apparent_temperature: List<Double>,
    val rain: List<Double>,
    val weather_code: List<Int>
)