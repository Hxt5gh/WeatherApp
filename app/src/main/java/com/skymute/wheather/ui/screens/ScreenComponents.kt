package com.skymute.wheather.ui.screens

import android.service.controls.templates.RangeTemplate
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.trimmedLength
import com.plcoding.weatherapp.domain.weather.WeatherType
import com.skymute.wheather.R
import com.skymute.wheather.remote.dto.Geocoding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComposeView(
    modifier: Modifier = Modifier,
    data: Geocoding,
    onSearch: (String) -> Unit,
    onPlaceTap: (lat: Double, lon: Double) -> Unit,
    onMyLocation: () -> Unit,
    isSearching :(Boolean)-> Unit
) {
    var query by remember { mutableStateOf("") }
    var enable by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = {
            query = it
            onSearch(it)
            if (query.length >= 3) {
                scope.launch {
                    delay(500)
                    onSearch(query)
                }

            }
        },
        onSearch = {
            isSearching(enable)
        },
        active = enable,
        placeholder = { Text(text = "Search") },
        onActiveChange = {
            enable = it
            isSearching(enable)
        },
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "null") },
        trailingIcon = {
            if (enable) {
                Icon(
                    modifier = Modifier.clickable
                    {
                        enable = false
                        isSearching(enable)
                    },
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "null"
                )
            } else {
                Icon(
                    modifier = Modifier.clickable
                    {
                        enable = false
                        isSearching(enable)
                        onMyLocation()
                    },
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "null"
                )
            }

        }
    ) {
        if (!data.results.isEmpty()) {
            data.results.forEach {
                SearchViewItem(name = if (it.name != null) it.name + ", ${it.country}" else "") {
                    query = ""
                    onPlaceTap(it.latitude!!, it.longitude!!)
                    enable = false
                }
            }
        }
    }
}

@Composable
fun SearchViewItem(name: String, onTap: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .height(50.dp)
        .clickable {
            onTap()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier, text = name, fontSize = 18.sp, maxLines = 1)
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

    }

}

@Composable
fun WeatherCard(
    modifier: Modifier = Modifier ,
    temperature : Double = 31.0 ,
    templateUnit : String = "°C" ,
    dayCode : WeatherType  ?= null,
    location : String ? = "...........",
    minTemperature : Double = 30.0 ,
    maxTemperature : Double = 30.0 ,
    humidity : Double = 22.0,
    humidityType : String = "%" ,
    windType : String = "km/h" ,
    windSpeed : Double = 2.0,
    rain : Double = 0.0 ,
    rainType : String = "mm",
    day : String = ""
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .heightIn()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.fillMaxWidth() , contentAlignment = Alignment.CenterEnd ){
                Text(text = day , fontSize = 18.sp , color = Color.Black)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = dayCode!!.iconRes),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = dayCode.weatherDesc , fontSize = 24.sp , color = Color.Black)
            Text(text = location.toString() , fontSize = 24.sp , color = Color.Gray)

            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "$temperature$templateUnit" , fontSize = 60.sp , color = Color.Black , fontFamily = FontFamily.SansSerif)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround

            ) {
                WeatherInfo(type = "Humidity" , value = humidity , valueType = humidityType)
                WeatherInfo(type = "Rain" , value = rain , valueType = rainType)
                WeatherInfo(type = "Wind Speed" , value = windSpeed , valueType = windType)

            }

        }
    }
}

@Composable
fun TemperatureComp(modifier: Modifier , imageVector: ImageVector = Icons.Default.ArrowUpward , temperature: Double = 30.0 , rotate : Boolean = false) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (rotate){
            Image(imageVector = imageVector, contentDescription = "Increase" , modifier = Modifier.rotate(180f))
        }else {
            Image (imageVector = imageVector, contentDescription = "Increase")
        }
        Text(text = "${temperature}°C" , fontSize = 22.sp)
    }
}

@Composable
fun WeatherInfo(
    type: String = "humidity",
    value: Double = 30.0,
    valueType: String = "°C"
) {
    Column(
        modifier = Modifier
            .width(100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${type}", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${value}${valueType}", fontSize = 12.sp , maxLines = 1)

    }
}

@Preview
@Composable
private fun WeatherInfoPrev() {
    //WeatherInfo()
}

@Preview
@Composable
fun WeatherCardPrev(modifier: Modifier = Modifier) {
//    WeatherCard(modifier = Modifier
//        .fillMaxWidth()
//        .height(600.dp) )
}



@Preview
@Composable
fun SearchComposeViewPrev(modifier: Modifier = Modifier) {
    //SearchComposeView(onSearch = {})
    // SearchViewItem( name = "Chandigarh")
}

@Composable
fun SmallCard(
    modifier: Modifier = Modifier ,
    time : String = "" ,
    dayCode : WeatherType  ?= null,
    temperature : Double = 0.0
) {
    ElevatedCard(modifier = modifier.padding(4.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = time)
            Spacer(modifier = Modifier.height(8.dp))
            Image(painter = painterResource(id = dayCode!!.iconRes), contentDescription = "" , modifier = Modifier.size(34.dp))
            //Image(painter = painterResource(id = R.drawable.ic_rainy), contentDescription = "" , modifier = Modifier.size(34.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${temperature}${"°C"}")
        }
    }
}
@Preview
@Composable
fun SmallCardPrev(modifier: Modifier = Modifier) {
  SmallCard(modifier = Modifier
      .height(100.dp)
      .width(70.dp),
      temperature = 30.0,
      time = "18:00"
  )
}

@Composable
fun DetailScreenWeather(
    modifier: Modifier = Modifier,
    day: String = "Today",
    weatherType : WeatherType ? = null,
    minTemp : String = "30",
    maxTemp : String = "35"
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
             horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            val str = if(weatherType!!.weatherDesc.length >5){
                weatherType.weatherDesc.let {
                    it.removeRange(8, it.length)
                } + "..."
            } else {
                weatherType.weatherDesc
            }
            Log.d("debug", "DetailScreenWeather: ${str} ")
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = day, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(20.dp))
                Image(
                    painter = painterResource(id = weatherType!!.iconRes),
                    contentDescription = "",
                    modifier = Modifier.size(46.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = str, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Clip)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "${minTemp}/${maxTemp}°C", fontSize = 14.sp)
            }
            Divider()
//            Row(
//                modifier = Modifier.fillMaxSize().weight(1f),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                Text(text = "Today", fontSize = 18.sp)
//                Spacer(modifier = Modifier.width(20.dp))
//                Image(
//                    painter = painterResource(id = R.drawable.ic_rainythunder),
//                    contentDescription = "",
//                    modifier = Modifier.size(46.dp)
//                )
//                Spacer(modifier = Modifier.width(10.dp))
//                Text(text = "Thunder Strom".let {
//                    it.removeRange(10, it.length)
//                } + "...", fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Clip)
//                Spacer(modifier = Modifier.width(20.dp))
//                Text(text = "30.3/49.3 °C", fontSize = 18.sp)
//            }
        }
    }
}

@Preview
@Composable
private fun DetailScreenWeatherPrev() {
    DetailScreenWeather(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp))
}