package com.skymute.wheather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skymute.wheather.ui.screens.DetailWeatherScreen
import com.skymute.wheather.ui.screens.SplashScreen
import com.skymute.wheather.ui.screens.WeatherScreen
import com.skymute.wheather.ui.theme.WheatherTheme


class MainActivity : ComponentActivity() {
    companion object
    {
     lateinit var dataStore : DataStore<Preferences>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = createDataStore(name = "Location")
        enableEdgeToEdge()
        setContent {
            WheatherTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Routes.Splash_Screen) {
                    composable(Routes.Splash_Screen) {
                        SplashScreen {
                            navController.navigate(Routes.FIRST_SCREEN) {
                                popUpTo(Routes.Splash_Screen)
                                {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(Routes.FIRST_SCREEN) {
                        WeatherScreen(onNavigate = { lat , lon , location ->
                            Log.d("navigation", "onCreate: ${lat} ${lon}")
                             val latitude = lat.toString()
                             val longitude = lon.toString()
                             val location = if(location.toString().equals("")) "x" else location
                            Log.d("debug", "onCreate: ${location}  ")
                            // navController.navigate()
                            navController.navigate("${Routes.SECOND_SCREEN}/${latitude}/${longitude}/${location}")
                        })


                    }
                   // composable(Routes.SECOND_SCREEN) {
                    composable(route = "${Routes.SECOND_SCREEN}/{${"latitude"}}/{${"longitude"}}/{${"location"}}" ,
                        arguments = listOf(
                            navArgument(name = "latitude"){type = NavType.StringType},
                            navArgument(name = "longitude"){type = NavType.StringType},
                            navArgument(name = "location"){type = NavType.StringType},
                        )
                    ) {
                        val lat = it.arguments?.getString("latitude").toString()
                        val long = it.arguments?.getString("longitude").toString()
                        val location = it.arguments?.getString("location").toString()
                        DetailWeatherScreen(lat , long , location.toString()){
                        }

                    }

                }
            }
        }
    }
}

object Routes{

    const val Splash_Screen ="Splash_Screen"
    const val FIRST_SCREEN = "FIRST_SCREEN"
    const val SECOND_SCREEN ="SECOND_SCREEN"

}