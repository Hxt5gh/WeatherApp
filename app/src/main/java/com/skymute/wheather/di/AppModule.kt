package com.skymute.wheatherapp.di

import android.util.Log
import com.skymute.wheather.domain.WeatherDataRepoImp
import com.skymute.wheather.presentation.WeatherDataViewModel
import com.skymute.wheather.remote.WeatherServiceImp
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single { httpClient() }
    single { WeatherServiceImp(httpClient()) }
    single { WeatherDataRepoImp(get()) }
    viewModel { WeatherDataViewModel(get()) }
}

fun httpClient() : HttpClient {
    val json = Json{
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
    }
    return HttpClient(CIO) {
        install(JsonFeature){
            serializer = KotlinxSerializer(json)
            acceptContentTypes = acceptContentTypes + ContentType.Any
        }

        install(Logging){
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("TAG", "LOGGER : ${message}")
                }
            }
        }

    }
}