package com.example.iotproject

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class HistoricalDataViewModel : ViewModel() {
    private val _sensorData = MutableStateFlow<List<SensorData>>(listOf())
    val sensorData: StateFlow<List<SensorData>> = _sensorData

    init {

        viewModelScope.launch {
            Log.d("HISTORICAL", "before client instant")
            val influxDBClient = InfluxDBClient()
            Log.d("HISTORICAL", "after client instant")
            withContext(Dispatchers.IO){
                Log.d("HISTORICAL", "before api call")
                try {
                    influxDBClient.getHistoricalData().collect { data ->
                        _sensorData.value = data
                    }
                } catch (e: Exception){
                    println(e.printStackTrace())
                }

            }
            Log.d("HISTORICAL", "after data set")
        }
    }


}