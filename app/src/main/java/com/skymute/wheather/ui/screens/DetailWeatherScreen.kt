package com.skymute.wheather.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.plcoding.weatherapp.domain.weather.WeatherType
import com.skymute.wheather.R
import com.skymute.wheather.presentation.UIEvents
import com.skymute.wheather.presentation.UserEvents
import com.skymute.wheather.presentation.WeatherDataViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailWeatherScreen(lat : String , lon : String , location : String , onNavigate :() -> Unit) {
    val viewmodel: WeatherDataViewModel = koinViewModel<WeatherDataViewModel>()
    val weeklyWeatherState by viewmodel.weatherWeeklyData.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    var getDataLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = Unit) {
        viewmodel.onEvent(UserEvents.GetWeatherData(lat.toDouble(), lon.toDouble()))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle){
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            viewmodel.uiEvents.collect{event->
                when(event){
                    is UIEvents.getWeatherDataLoading -> {
                    }

                    is UIEvents.getHourlyDataLoading -> {

                    }

                    is UIEvents.getWeeklyDataLoading -> {
                        getDataLoading = event.isLoading
                    }

                    is UIEvents.getGeoDataLoading -> {

                    }
                }

            }
        }
    }

    val apend = if(location.equals("x")) "" else "-${location}"

    Scaffold(modifier = Modifier.fillMaxSize() ,
        topBar = {
            TopAppBar(

                title = { Text(text = "Next 14 days forecast${apend}") },
//                navigationIcon = {
//                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "" , modifier = Modifier.padding(start = 8.dp).clickable {
//                        onNavigate()
//                    })
//                }
            )
        }
    ) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            if (getDataLoading || weeklyWeatherState == null){
                Box(
                    modifier = Modifier
                        .fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    // Text(text = "Loading...")
                    AnimatedPreloader(modifier = Modifier.size(100.dp) , R.raw.animation)
                }

            }else
            {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    weeklyWeatherState.let {
                        itemsIndexed(it.daily!!.weather_code){ index, item ->
                            DetailScreenWeather(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(bottom = 6.dp),
                                day =  viewmodel.CurrentData(it.daily.time[index].toLong()),
                                weatherType = WeatherType.fromWMO(it.daily.weather_code[index]),
                                minTemp = it.daily.apparent_temperature_min[index].toString(),
                                maxTemp = it.daily.apparent_temperature_max[index].toString(),
                            )
                        }
                    }
                }
            }
        }
    }


}