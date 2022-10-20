package com.example.litmuscloudwifi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.litmuscloudwifi.databinding.ActivityMainBinding
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var wifiId = ""
    var wifiPassword = ""
    var gatewayNumber = ""

    var order_wifi = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0\", \"value\": {}}], \"id\": \"jn00m6o1\"}"
    var order_wifi2 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/ssid\", \"value\": \"testapp3\"}], \"id\": \"61u0y3fr\"}"
    var order_wifi3 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/password\", \"value\": \"testapp3\"}], \"id\": \"mocsxk38\"}"
    var order_wifi4 = "{\"jsonrpc\": \"2.0\", \"method\": \"get_env\", \"params\": {\"path\": \"/network/wifi_sta/0\"}, \"id\": \"ym0yo3bk\"}"

    var order_commit = "{\"jsonrpc\": \"2.0\", \"method\": \"commit_env\", \"id\": \"lh7brbsi\"}"
    var order_reset = "{\"jsonrpc\": \"2.0\", \"method\": \"reset\", \"id\": \"iyc1etnx\"}"
    var order_checkWifi = "{\"jsonrpc\": \"2.0\", \"method\": \"get_env\", \"params\": {\"path\": \"/network\"}, \"id\": \"j6gm1ch4\"}"

    val client = MqttClient("tcp://wnt.litmuscloud.com:1883",MqttClient.generateClientId(), null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //val client2 = MqttAndroidClient(this.applicationContext, "tcp://wnt.litmuscloud.com:1883", MqttClient.generateClientId())
        val option = MqttConnectOptions()
        option.password = "V1ZJDQHQ4QFUjUZ3yqY0HrhhFWDMv".toCharArray()
        option.userName = "mqttmasteruser"

        Log.d("asdf client.isConnected","${client.isConnected}")

        binding.connectButton.setOnClickListener {
            if(binding.gateWayEditText.text?.length == 0) {
                Toast.makeText(this, "게이트웨이 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    gatewayNumber = binding.gateWayEditText.text.toString()
                    Log.d("asdf gatewayNumber","${gatewayNumber}")
                    client.connect(option)
                    Log.d("asdf client.isConnected","${client.isConnected}")
                    if (client.isConnected) {
                        Toast.makeText(this, "${gatewayNumber} 게이트웨이에 연결되었습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("asdf id isNullOrEmpty","${binding.wifiIdEditText.text.isNullOrEmpty()}")
                        Log.d("asdf password isNullOrEmpty","${binding.wifiIdPasswordText.text.isNullOrEmpty()}")
                        binding.connectButton.isClickable = false
                        client.subscribe("${TOPIC}/${gatewayNumber}")

                    }

                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.disConnectButton.setOnClickListener {
            if (client.isConnected) {
                client.disconnect()
                gatewayNumber = binding.gateWayEditText.text.toString()
                Toast.makeText(this, "${gatewayNumber} 게이트웨이와 연결을 해제했습니다.", Toast.LENGTH_SHORT).show()
                binding.gateWayEditText.text = null
                binding.wifiIdEditText.text = null
                binding.wifiIdPasswordText.text = null
                binding.connectButton.isClickable = true
                Log.d("asdf client.isConnected disConnectButton","${client.isConnected}")
            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sendButton.setOnClickListener {
            Log.d("asdf button click","click")

            if (client.isConnected) {
                try {
                    gatewayNumber = binding.gateWayEditText.text.toString()
                    wifiId = binding.wifiIdEditText.text.toString()
                    wifiPassword = binding.wifiIdPasswordText.text.toString()

                    order_wifi2 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/ssid\", \"value\": \"${wifiId}\"}], \"id\": \"61u0y3fr\"}"
                    order_wifi3 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/password\", \"value\": \"${wifiPassword}\"}], \"id\": \"mocsxk38\"}"

                    //client.subscribe("${TOPIC}/${gatewayNumber}")

                    client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${order_wifi}".toByteArray()))
                    client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${order_wifi2}".toByteArray()))
                    client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${order_wifi3}".toByteArray()))
                    client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${order_wifi4}".toByteArray()))

                } catch (e : Exception) {
                    e.printStackTrace()
                    Log.d("asdf error","error")
                }
            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resetButton.setOnClickListener {
            if(client.isConnected) {
                gatewayNumber = binding.gateWayEditText.text.toString()

                //client.subscribe("${TOPIC}/${gatewayNumber}")
                client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${order_reset}".toByteArray()))

            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.testButton.setOnClickListener {
            try {
                gatewayNumber = binding.gateWayEditText.text.toString()
                val message = binding.wifiIdEditText.text.toString()

                //client.subscribe("${TOPIC}/${gatewayNumber}")
                client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${message}".toByteArray()))


            } catch (e : Exception) {
                e.printStackTrace()
                Log.d("asdf error","error")
            }
        }


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

        testButton2()
        commitButton()
        checkWifiButton()
    }

    private fun checkWifiButton() {
        binding.checkWifiButton.setOnClickListener {
            Log.d("asdf checkWifiButton click","click")

            gatewayNumber = binding.gateWayEditText.text.toString()

            //client.subscribe("${TOPIC}/${gatewayNumber}")
            client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${order_checkWifi}".toByteArray()))

            val wifiInfo = client.getTopic("${TOPIC}/${gatewayNumber}")
            Log.d("asdf wifiInfo","${wifiInfo}")

        }
    }

    private fun commitButton() {
        binding.commitButton.setOnClickListener {
            Log.d("asdf commitButton click","click")

            gatewayNumber = binding.gateWayEditText.text.toString()

            //client.subscribe("${TOPIC}/${gatewayNumber}")
            client.publish("${TOPIC}/${gatewayNumber}",MqttMessage("${order_commit}".toByteArray()))


        }
    }

    private fun testButton2()  {
        binding.testButton2.setOnClickListener {
            Log.d("asdf testButton2 click","click")
        }
    }

    companion object {
        val TOPIC = "lcg100-request"
    }
}














































