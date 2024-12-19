package com.example.iotproject

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iotproject.ui.theme.IotProjectTheme
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.line.renderer.yaxis.SimpleYAxisDrawer
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IotProjectTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController)
                    },
                    topBar = {
                        TopBar()
                    }
                ) { innerPadding ->
                    innerPadding
                    AppNavigation(navController)
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = {
            navController.navigate(Screen.RealTimeData.route)
        }) {
            Text("Real Time Data")
        }
        Button(onClick = {
            navController.navigate(Screen.HistoricalData.route)
        }) {
            Text("Historical Data")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(title = {
        Text("IOT Project")
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.RealTimeData.route) {
        composable(Screen.RealTimeData.route) {

            val temperatureFile = File(LocalContext.current.filesDir, "temperature_data.csv")
            val airPressureFile = File(LocalContext.current.filesDir, "air_pressure_data.csv")

            val mqttViewModel: MQTTViewModel =
                viewModel(
                    factory = MQTTViewModelFactory(
                        MQTTClient(LocalContext.current),
                        temperatureFile,
                        airPressureFile
                    )
                )
            SensorScreen(
                sensorViewModel = mqttViewModel
            )
        }
        composable(Screen.HistoricalData.route) {
            HistoricalDataScreen(
                historicalDataViewModel = HistoricalDataViewModel()
            )
        }
    }
}

@Composable
fun SensorScreen(sensorViewModel: MQTTViewModel) {
    val sensorTemperatureData by sensorViewModel.sensorTemperatureData.collectAsState()
    val sensorTemperatureDataFloat = sensorTemperatureData.toFloatOrNull() ?: 0.0f

    val sensorAirPressureData by sensorViewModel.sensorAirPressureData.collectAsState()
    val sensorAirPressureDataFloat = sensorAirPressureData.toFloatOrNull() ?: 0.0f


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Gauge(
            value = sensorTemperatureDataFloat,
            maxValue = 50f,
            unit = "°C",
            label = "Temperature",
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(80.dp)
                .aspectRatio(1f)
        )

        Gauge(
            value = sensorAirPressureDataFloat,
            maxValue = 200000f,
            unit = "Pa",
            label = "Air Pressure",
            color = Color.Blue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 80.dp, end = 80.dp)
                .aspectRatio(1f)
        )

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoricalDataScreen(historicalDataViewModel: HistoricalDataViewModel) {
    val sensorData by historicalDataViewModel.sensorData.collectAsState()


    val data = LineChartData(
        points = sensorData.map { point ->
            LineChartData.Point(point.value, point.time)
        },
        lineDrawer = SolidLineDrawer()
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = 80.dp, start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        LineChart(
            linesChartData = listOf(data),
            horizontalOffset = 5f,
            xAxisDrawer = SimpleXAxisDrawer(),
            yAxisDrawer = SimpleYAxisDrawer()
        )
    }

}

@Composable
fun Gauge(
    value: Float,
    maxValue: Float,
    unit: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20f
            val radius = size.minDimension / 2 - strokeWidth

            // Draw background arc
            drawArc(
                color = Color.LightGray,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Draw progress arc
            val sweepAngle = (value / maxValue) * 270f
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Draw tick marks
            for (i in 0..10) {
                val angle = (135 + i * 27f) * (PI / 180f).toFloat()
                val start = radius - 20
                val startX = center.x + cos(angle) * start
                val startY = center.y + sin(angle) * start
                val endX = center.x + cos(angle) * radius
                val endY = center.y + sin(angle) * radius

                drawLine(
                    color = Color.DarkGray,
                    start = androidx.compose.ui.geometry.Offset(startX, startY),
                    end = androidx.compose.ui.geometry.Offset(endX, endY),
                    strokeWidth = 4f
                )
            }
        }

        // Draw text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$value $unit",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object RealTimeData : Screen("real_time_data")
    data object HistoricalData : Screen("historical_data")
}



