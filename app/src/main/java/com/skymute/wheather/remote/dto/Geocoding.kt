package com.skymute.wheather.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class Geocoding(
    var results: List<Result> = emptyList(),
    val generationtime_ms: Double ? = null,
)


@Serializable
data class Result(
    val id: Int ? = null,
    val name: String ? = null,
    val latitude: Double ? = null,
    val longitude: Double ? = null,
    val elevation: Double ? = null,
    val feature_code: String ? = null,
    val country_code: String ? = null,
    val admin1_id: Double ? = null,
    val admin2_id: Double ? = null,
    val timezone: String ? = null,
    val population: Double ? = null,
    val country_id: Double ? = null,
    val country: String ? = null,
    val admin1: String ? = null,
    val admin2: String ? = null,

)