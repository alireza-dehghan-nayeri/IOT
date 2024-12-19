package com.example.iotproject

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.influxdb.client.InfluxDBClientFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InfluxDBClient {
    private val influxDBClient = InfluxDBClientFactory.create(
        "http://10.0.2.2:8086",
        "wBokyiAdFUOJ7Q5Pkxzo77Gz6inidGKxzyFJ5XnMvjovFC6MstNzmN7m_hVSH1BOvgVdjCn6sqUELeYwJzmYlw==".toCharArray(),
        "Group 20",
        "IOT Project"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHistoricalData(): Flow<List<SensorData>> = flow {
        val query = """
            from(bucket: "IOT Project")
            |> range(start: -1h)
            |> filter(fn: (r) => r._measurement == "temperature")
        """.trimIndent()

        val results = influxDBClient.queryApi.query(query)


        val data = results.first().records.map { record ->
            SensorData(
                time = "",
                value = record.value.toString().toFloatOrNull() ?: 0.0f
            )

        }
        Log.d("HISTORICAL", "before emit call")
        emit(data)
    }

}


data class SensorData(val time: String, val value: Float)
