package com.example.iotproject

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileWriter

@RequiresApi(Build.VERSION_CODES.O)
class MQTTViewModel(
    private val mqttClient: MQTTClient,
    private val temperatureFile: File,
    private val airPressureFile: File
) : ViewModel() {
    private val _sensorTemperatureData = MutableStateFlow("")
    val sensorTemperatureData: StateFlow<String> = _sensorTemperatureData

    private val _sensorAirPressureData = MutableStateFlow("")
    val sensorAirPressureData: StateFlow<String> = _sensorAirPressureData


    init {
        mqttClient.connect {
            mqttClient.subscribe(
                { message ->
                    val data = extractTimestampAndValue(message)

                    val value = data?.second ?: ""
                    val timestamp = data?.first ?: ""

                    saveToCsvFile(
                        temperatureFile,
                        timestamp,
                        value,
                        getLatency(timestamp.toLong()).toString()
                    )

                    _sensorTemperatureData.value = data?.second ?: ""

                    Log.d("MQTT", "Temperature Message received: $message")
                },
                { message ->
                    val data = extractTimestampAndValue(message)

                    val value = data?.second ?: ""
                    val timestamp = data?.first ?: ""

                    saveToCsvFile(
                        airPressureFile,
                        timestamp,
                        value,
                        getLatency(timestamp.toLong()).toString()
                    )


                    _sensorAirPressureData.value = data?.second ?: ""
                    Log.d("MQTT", "Air Pressure Message received: $message")
                })
            Log.d("MQTT", "Connected!")
        }
    }

    private fun saveToCsvFile(file: File, timestamp: String, value: String, latency: String) {
        try {
            val fileWriter = FileWriter(file, true)
            fileWriter.append("$timestamp,$value,$latency\n")
            fileWriter.close()
            Log.d("CSV", "Data saved to ${file.name}: $timestamp, $value")
        } catch (e: Exception) {
            Log.e("CSV", "Error saving to ${file.name}", e)
        }
    }


    private fun getLatency(
        newMessageTime: Long,
    ): Long {
        val currentTimeMillis = System.currentTimeMillis() / 1000
        return currentTimeMillis - newMessageTime
    }


    private fun extractTimestampAndValue(message: String): Pair<String, String>? {
        // Split the message into timestamp and value using " - " as the delimiter
        val parts = message.split(" - ")

        // Ensure the message is in the expected format
        if (parts.size < 2) {
            println("Invalid message format: $message")
            return null // Return null if the message is invalid
        }

        val timestamp = parts[0] // Extract the timestamp
        val value = parts[1] // Extract the value

        return Pair(timestamp, value) // Return as a Pair
    }

    override fun onCleared() {
        mqttClient.disconnect()
        super.onCleared()
    }

}

class MQTTViewModelFactory(
    private val mqttClient: MQTTClient,
    private val temperatureFile: File,
    private val airPressureFile: File
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MQTTViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MQTTViewModel(mqttClient, temperatureFile, airPressureFile) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

