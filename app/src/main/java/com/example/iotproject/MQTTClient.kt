package com.example.iotproject

import android.content.Context
import android.util.Log
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MQTTClient(
    context: Context
) {
    private val mqttClient: MqttAndroidClient

    init {
        val serverUri = "ssl://c339f5ffde214f699d87407cac7db049.s1.eu.hivemq.cloud:8883"
        mqttClient =
            MqttAndroidClient(context, serverUri, MqttClient.generateClientId(), Ack.AUTO_ACK)
    }

    fun connect(
        username: String = "OuluIot2024",
        password: String = "OuluIot2024",
        onConnected: () -> Unit
    ) {
        val options = MqttConnectOptions()
        options.userName = username
        options.password = password.toCharArray()
        mqttClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                onConnected()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("MQTT", "Error connecting to MQTT!")
                exception?.printStackTrace()
            }
        })
    }


    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }

    fun subscribe(
        onTemperatureMessageReceived: (String) -> Unit,
        onAirPressureMessageReceived: (String) -> Unit
    ) {
        mqttClient.setCallback(
            object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {}

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic == "picow/temperature") {
                        onTemperatureMessageReceived(message.toString())
                    }
                    if (topic == "picow/pressure") {
                        onAirPressureMessageReceived(message.toString())
                    }
                }
                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            }
        )
        mqttClient.subscribe("picow/temperature", 0)
        mqttClient.subscribe("picow/pressure", 0)
    }


    fun publish(
        topic: String,
        msg: String,
        qos: Int = 0,
        retained: Boolean = false
    ) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {}

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {}

            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}