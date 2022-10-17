package com.example.litmuscloudwifi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.litmuscloudwifi.databinding.ActivityMainBinding
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val order = "{\"jsonrpc\": \"2.0\", \"method\": \"hello\", \"id\": \"g8g1wv6j\"}"
    val order2 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0\", \"value\": {}}], \"id\": \"jn00m6o1\"}"
    val order3 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/ssid\", \"value\": \"testapp2\"}], \"id\": \"61u0y3fr\"}"
    val order4 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/password\", \"value\": \"testapp2\"}], \"id\": \"mocsxk38\"}"
    val order5 = "{\"jsonrpc\": \"2.0\", \"method\": \"get_env\", \"params\": {\"path\": \"/network/wifi_sta/0\"}, \"id\": \"ym0yo3bk\"}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val client = MqttClient("tcp://wnt.litmuscloud.com:1883",MqttClient.generateClientId(), null)
        //val client2 = MqttAndroidClient(this.applicationContext, "tcp://wnt.litmuscloud.com:1883", MqttClient.generateClientId())
        val option = MqttConnectOptions()
        option.password = "V1ZJDQHQ4QFUjUZ3yqY0HrhhFWDMv".toCharArray()
        option.userName = "mqttmasteruser"

        client.connect(option)

        binding.sendButton.setOnClickListener {
            Log.d("asdf button click","click")
            try {
                client.publish("lcg100-request/908",MqttMessage("${order2}".toByteArray()))
                client.publish("lcg100-request/908",MqttMessage("${order3}".toByteArray()))
                client.publish("lcg100-request/908",MqttMessage("${order4}".toByteArray()))
                client.publish("lcg100-request/908",MqttMessage("${order5}".toByteArray()))

            } catch (e : Exception) {
                e.printStackTrace()
                Log.d("asdf error","error")
            }
        }

        client.subscribe("lcg100-request/908")

        client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d("asdf connectionLost","connectionLost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("asdf messageArrived","messageArrived")
                Log.d("asdf messageArrived topic","${topic}")
                Log.d("asdf messageArrived message","${message}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("asdf deliveryComplete","deliveryComplete")
            }

        })

    }
}














































