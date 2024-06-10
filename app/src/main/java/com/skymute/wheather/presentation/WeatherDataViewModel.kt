package com.skymute.wheather.presentation

import android.Manifest
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymute.wheather.MainActivity
import com.skymute.wheather.domain.WeatherDataRepoImp
import com.skymute.wheather.remote.dto.CurrentWeatherData
import com.skymute.wheather.remote.dto.Daily
import com.skymute.wheather.remote.dto.DailyUnits
import com.skymute.wheather.remote.dto.Geocoding
import com.skymute.wheather.remote.dto.HourlyWeatherData
import com.skymute.wheather.remote.dto.WeeklyWeatherData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherDataViewModel(private  val weatherDataRepo: WeatherDataRepoImp)  : ViewModel(){


    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val visiblePermissionDialogQueue = mutableStateListOf<String>()
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }
    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    private  var _weatherCurrentData = MutableStateFlow<CurrentWeatherData>(CurrentWeatherData())
    val weatherCurrentData  : StateFlow <CurrentWeatherData> = _weatherCurrentData.asStateFlow()

    private  var _weatherHourlyData = MutableStateFlow<HourlyWeatherData>(HourlyWeatherData())
    val weatherHourlyData   : StateFlow <HourlyWeatherData> = _weatherHourlyData.asStateFlow()

    private  var _weatherWeeklyData = MutableStateFlow<WeeklyWeatherData>(WeeklyWeatherData())
    val weatherWeeklyData   : StateFlow <WeeklyWeatherData> = _weatherWeeklyData.asStateFlow()

    private  var _geoCodingData = MutableStateFlow<Geocoding>(Geocoding())
    val geoCodingData   : StateFlow <Geocoding> = _geoCodingData.asStateFlow()

    private var _uiEvents = Channel<UIEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onEvent(event : UserEvents){
        when(event){
            is UserEvents.GetWeatherData -> {
                viewModelScope.launch {
                    val job1 = async { getWeatherData(event.lat, event.lon) }
                    val job2 = async { getHourlyWeatherData(event.lat, event.lon) }
                    val job3 = async { getWeeklyWeatherData(event.lat, event.lon) }
                    awaitAll(job1, job2, job3)
                }
            }
           is UserEvents.GetGeoCodingData -> {
               viewModelScope.launch {
                   getGeoCodingData(event.string)
               }
            }

        }
    }

    fun sendEvents(events: UIEvents) {
        viewModelScope.launch {
            _uiEvents.send(element = events)
        }
    }


    fun getWeatherData(lat: Double = 30.7363, lon: Double = 76.7884) {

        viewModelScope.launch {
            sendEvents(UIEvents.getWeatherDataLoading(true))
            weatherDataRepo.getCurrentWeatherData(lat, lon).collect { data ->
                Log.d("debug", "getWeatherData before:  ${data.latitude}  ${data.latitude}")
                _weatherCurrentData.update {
                    it.copy(
                        latitude = data.latitude,
                        longitude = data.longitude,
                        generationtime_ms = data.generationtime_ms,
                        utc_offset_seconds = data.utc_offset_seconds,
                        timezone = data.timezone,
                        timezone_abbreviation = data.timezone_abbreviation,
                        elevation = data.elevation,
                        current_units = data.current_units,
                        current = data.current
                    )
                }
                sendEvents(UIEvents.getWeatherDataLoading(false))

            }
        }
    }

    fun getHourlyWeatherData(lat: Double = 30.7363, lon: Double = 76.7884) {
        viewModelScope.launch {
            sendEvents(UIEvents.getHourlyDataLoading(true))
            weatherDataRepo.getHourlyWeatherData(lat, lon).collect { data ->
                _weatherHourlyData.update {
                    it.copy(
                        latitude = data.latitude,
                        longitude = data.longitude,
                        generationtime_ms = data.generationtime_ms,
                        utc_offset_seconds = data.utc_offset_seconds,
                        timezone = data.timezone,
                        timezone_abbreviation = data.timezone_abbreviation,
                        elevation = data.elevation,
                        hourly_units = data.hourly_units,
                        hourly = data.hourly
                    )
                }
                sendEvents(UIEvents.getHourlyDataLoading(false))
            }
        }
    }

    fun getWeeklyWeatherData(lat: Double = 30.7363, lon: Double = 76.7884){
        viewModelScope.launch {
            sendEvents(UIEvents.getWeeklyDataLoading(true))
            weatherDataRepo.getWeeklyWeatherData(lat, lon).collect{data ->
                _weatherWeeklyData.update {
                    it.copy(
                        latitude = data.latitude,
                        longitude = data.longitude,
                        generationtime_ms = data.generationtime_ms,
                        utc_offset_seconds = data.utc_offset_seconds,
                        timezone = data.timezone,
                        timezone_abbreviation = data.timezone_abbreviation,
                        elevation = data.elevation,
                        daily_units = data.daily_units,
                        daily = data.daily,
                    )
                }
                sendEvents(UIEvents.getWeeklyDataLoading(false))
            }
        }
    }

    fun getGeoCodingData(query: String = "INDIA"){
        viewModelScope.launch {
            sendEvents(UIEvents.getGeoDataLoading(true))
            weatherDataRepo.getGeoCodingData(query).collect{data ->
                _geoCodingData.update {
                    it.copy(
                        results = data.results,
                        generationtime_ms = data.generationtime_ms
                    )

                }
                sendEvents(UIEvents.getGeoDataLoading(false))
            }
        }
    }

    suspend fun saveKey(key : String , value : String)
    {
        val dataStoreKey = preferencesKey<String>(key)
        MainActivity.dataStore.edit {location->
            location[dataStoreKey] = value
        }
    }
    suspend fun readKey(key : String) : String?
    {
        val dataStoreKey = preferencesKey<String>(key)
        val pref = MainActivity.dataStore.data.first()
        return pref[dataStoreKey]
    }

    fun CurrentData(time: Long): String {
        val timestamp = time * 1000 // Multiply by 1000 to convert seconds to milliseconds
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd MMMM", Locale.ENGLISH)
        return formatter.format(date)
    }
    fun CurrentTime(time: Long): String {
        val timestamp = time * 1000 // Multiply by 1000 to convert seconds to milliseconds
        val date = Date(timestamp)
        // Format the time as "HH:mm"
        val timeFormatter = SimpleDateFormat("HH:00", Locale.ENGLISH)
        return timeFormatter.format(date)
    }
}



sealed class UserEvents(){
    data class GetWeatherData(val lat : Double , val lon : Double) : UserEvents()
    data class GetGeoCodingData(val string : String) : UserEvents()

}

sealed class UIEvents {
    data class getWeatherDataLoading(val isLoading: Boolean) : UIEvents()
    data class getHourlyDataLoading(val isLoading: Boolean) : UIEvents()
    data class getWeeklyDataLoading(val isLoading: Boolean) : UIEvents()
    data class getGeoDataLoading(val isLoading: Boolean) : UIEvents()

}