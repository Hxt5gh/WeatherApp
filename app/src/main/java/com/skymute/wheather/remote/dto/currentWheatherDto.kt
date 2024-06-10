package com.skymute.wheather.remote.dto

import kotlinx.serialization.Serializable





@Serializable
data class CurrentWeatherData(
    val latitude: Double ? = null,
    val longitude: Double ? = null,
    val generationtime_ms: Double ? = null,
    val utc_offset_seconds: Int ? = null,
    val timezone: String ? = null,
    val timezone_abbreviation: String ? = null,
    val elevation: Double ? = null,
    val current_units: CurrentUnits? = null,
    val current: Current? = null,
)



@Serializable
data class CurrentUnits(
    val time : String,
    val interval : String,
    val relative_humidity_2m : String,
    val apparent_temperature : String,
    val is_day : String,
    val rain : String  = "",
    val weather_code : String,
    val wind_speed_10m : String,
    val wind_direction_10m : String,
)




@Serializable
data class Current(
    val time: Long,
    val interval: Double,
    val relative_humidity_2m: Double,
    val apparent_temperature: Double,
    val is_day: Int,
    val rain : Double = 0.0,
    val weather_code: Int,
    val wind_speed_10m: Double,
    val wind_direction_10m: Double
)
