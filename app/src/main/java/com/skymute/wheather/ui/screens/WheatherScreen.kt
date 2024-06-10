package com.skymute.wheather.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.plcoding.weatherapp.domain.weather.WeatherType.Companion.fromWMO
import com.skymute.wheather.R
import com.skymute.wheather.presentation.UIEvents
import com.skymute.wheather.presentation.UserEvents
import com.skymute.wheather.presentation.WeatherDataViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(onNavigate : (Double , Double , String) -> Unit) {
    val viewmodel: WeatherDataViewModel = koinViewModel<WeatherDataViewModel>()
    val currentWeatherState by viewmodel.weatherCurrentData.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val hourlyWeatherState by viewmodel.weatherHourlyData.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val geoCodingDataState by viewmodel.geoCodingData.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val context = androidx.compose.ui.platform.LocalContext.current

    var latitude  = 0.0
    var longitude =  0.0

    val scope = rememberCoroutineScope()

    var isSearching by remember { mutableStateOf(false) }

    val fusedLocationProviderClient : FusedLocationProviderClient = FusedLocationProviderClient(context)

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            viewmodel.permissionsToRequest.forEach { permission ->
                viewmodel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )

    val dialogQueue = viewmodel.visiblePermissionDialogQueue

    val lifecycleOwner = LocalLifecycleOwner.current
    var getDataLoading by remember {
        mutableStateOf(true)
    }
    var getDataLoading1 by remember {
        mutableStateOf(true)
    }
    var getDataLoading2 by remember {
        mutableStateOf(true)
    }
    var getDataLoading3 by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(lifecycleOwner.lifecycle){
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            viewmodel.uiEvents.collect{event->
                when(event){
                    is UIEvents.getWeatherDataLoading -> {
                        getDataLoading = event.isLoading
                    }

                    is UIEvents.getHourlyDataLoading -> {
                        getDataLoading1 = event.isLoading
                    }

                    is UIEvents.getWeeklyDataLoading -> {
                        getDataLoading2 = event.isLoading
                    }

                    is UIEvents.getGeoDataLoading -> {
                        getDataLoading3 = event.isLoading
                    }
                }

            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            val key1 =    viewmodel.readKey("latitude")
            val key2 =  viewmodel.readKey("longitude")
           if (key1 == null && key2 == null){
               latitude = 30.76
               longitude = 76.34
               viewmodel.onEvent(UserEvents.GetWeatherData(30.76,76.34))
               viewmodel.getGeoCodingData()
           }else
           {
               latitude = key1!!.toDouble()
               longitude = key2!!.toDouble()
               Log.d("checking", "WeatherScreen:  ${key1} ${key2}")
               viewmodel.onEvent(UserEvents.GetWeatherData(key1!!.toDouble(),key2!!.toDouble()))
           }
        }
    }

    Scaffold(

        topBar = {
                    SearchComposeView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 4.dp),
                        data = geoCodingDataState,
                        onSearch = {
                            viewmodel.getGeoCodingData(it)
                        },
                        onPlaceTap = { lat, lon ->
                            viewmodel.onEvent(UserEvents.GetWeatherData(lat, lon))
                        },
                        onMyLocation = {
                            if (viewmodel.permissionsToRequest.all {
                                    ActivityCompat.checkSelfPermission(
                                        context,
                                        it
                                    ) == PackageManager.PERMISSION_GRANTED
                                }) {
                                //get location here
                                val location = fusedLocationProviderClient.lastLocation
                                location.addOnSuccessListener {
                                    if (it != null) {
                                        scope.launch {
                                            viewmodel.saveKey("latitude" , "${it.latitude}")
                                            viewmodel.saveKey("longitude" , "${it.longitude}")
                                        }
                                        geoCodingDataState.results = emptyList()
                                        viewmodel.onEvent(UserEvents.GetWeatherData(it.latitude, it.longitude))
                                        Log.d("Chandigarh", String.format(Locale.US, "%s -- %s", it.latitude, it.longitude))

                                    }
                                }

                            } else {
                                multiplePermissionResultLauncher.launch(viewmodel.permissionsToRequest)
                            }
                        },
                        isSearching ={
                            isSearching = it
                        }

                    )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)

        ) {

            dialogQueue
                .reversed()
                .forEach { permission ->
                    PermissionDialog(
                        permissionTextProvider = when (permission) {
                            Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                LocationTextProviderOne()
                            }
                            Manifest.permission.ACCESS_FINE_LOCATION -> {
                                LocationTextProviderOne()
                            }
                            else -> return@forEach
                        },
                        isPermanentlyDeclined = !ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            permission
                        ),
                        onDismiss = viewmodel::dismissDialog,
                        onOkClick = {
                            viewmodel.dismissDialog()
                            multiplePermissionResultLauncher.launch(
                                arrayOf(permission)
                            )
                        },
                        onGoToAppSettingsClick = {
                            openSettings(context)
                            viewmodel.dismissDialog()
                        }
                    )
                }




            if (getDataLoading && getDataLoading1  && getDataLoading3){
                Box(
                    modifier = Modifier
                        .fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                   // Text(text = "Loading...")
                    AnimatedPreloader(modifier = Modifier.size(100.dp) , R.raw.animation)
                }
            }else{
                    Log.d("issearching", "WeatherScreen: ${isSearching} ")
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(22.dp),
                    ) {

                        item {
                           val location = if(geoCodingDataState.results.isNotEmpty() ) geoCodingDataState.results[0].name +" , "+geoCodingDataState.results[0].country else ""


                                WeatherCard(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    temperature = currentWeatherState.current!!.apparent_temperature,
                                    templateUnit = currentWeatherState.current_units!!.apparent_temperature,
                                    dayCode = fromWMO(currentWeatherState.current!!.weather_code),
                                    location = location,
                                    humidity = currentWeatherState.current!!.relative_humidity_2m,
                                    humidityType = currentWeatherState.current_units!!.relative_humidity_2m,
                                    rain = currentWeatherState.current!!.rain,
                                    rainType = currentWeatherState.current_units!!.rain,
                                    windSpeed = currentWeatherState.current!!.wind_speed_10m,
                                    windType = currentWeatherState.current_units!!.wind_speed_10m,
                                    day = viewmodel.CurrentData(currentWeatherState.current!!.time)

                                )

                        }
                        item {
                            Spacer(modifier = Modifier.height(18.dp))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)){
                                LazyRow(modifier = Modifier.fillMaxSize()) {
                                    itemsIndexed(hourlyWeatherState.hourly!!.time){ index, item ->

                                            val time = viewmodel.CurrentTime(hourlyWeatherState.hourly!!.time[index])

                                                SmallCard(
                                                    modifier = Modifier
                                                        .height(120.dp)
                                                        .width(85.dp),
                                                    temperature = hourlyWeatherState.hourly!!.apparent_temperature[index],
                                                    time = time,
                                                    dayCode = fromWMO(hourlyWeatherState.hourly!!.weather_code[index])
                                                )

                                    }
                                }
                            }
                        }
                        item {
                                Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = {
                                    val location = if (viewmodel.geoCodingData.value.results.isNotEmpty()) viewmodel.geoCodingData.value.results[0].name else ""
                                    if (currentWeatherState.latitude != null){
                                        latitude = currentWeatherState.latitude!!
                                        longitude = currentWeatherState.longitude!!
                                        onNavigate(latitude, longitude , location!!)
                                        Log.d("debug", "WeatherScreen: L ${location}")
                                    }else
                                    {
                                        onNavigate(latitude, longitude , location!!)
                                        Log.d("debug", "WeatherScreen: L ${location}")
                                    }

                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Text(text = "7 Day Forecast")
                            }
                        }

                    }
            }
        }
    }

}

@Preview
@Composable
private fun Prev() {
   // WeatherScreen(onNavigate = {})
}


fun openSettings(context: Activity) {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).also { intent ->
        context.startActivity(intent)
    }
}

