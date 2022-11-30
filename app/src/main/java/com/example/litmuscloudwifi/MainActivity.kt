package com.example.litmuscloudwifi

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.litmuscloudwifi.databinding.ActivityMainBinding
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var wifiId = ""
    var wifiPassword = ""
    var gatewayNumber = ""
    var random_id = ""
    var checkWifiButton = false

    var order_wifi = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0\", \"value\": {}}], \"id\": \"jn00m6o1\"}"
    var order_wifi2 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/ssid\", \"value\": \"testapp3\"}], \"id\": \"61u0y3fr\"}"
    var order_wifi3 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/password\", \"value\": \"testapp3\"}], \"id\": \"mocsxk38\"}"
    var order_wifi4 = "{\"jsonrpc\": \"2.0\", \"method\": \"get_env\", \"params\": {\"path\": \"/network/wifi_sta/0\"}, \"id\": \"ym0yo3bk\"}"

    var order_commit = "{\"jsonrpc\": \"2.0\", \"method\": \"commit_env\", \"id\": \"lh7brbsi\"}"
    var order_reset = "{\"jsonrpc\": \"2.0\", \"method\": \"reset\", \"id\": \"iyc1etnx\"}"
    var order_checkWifi = "{\"jsonrpc\": \"2.0\", \"method\": \"get_env\", \"params\": {\"path\": \"/network\"}, \"id\": \"j6gm1ch4\"}"
    var order_factory_reset = "{\"jsonrpc\": \"2.0\", \"method\": \"factory_reset\", \"id\": \"nofqvwx3\"}"

    var order_gateWayInfo = "{\"jsonrpc\": \"2.0\", \"method\": \"hello\", \"id\": \"7qmmla2f\"}"

    val client = MqttClient(MQTT_SERVER_URL,MqttClient.generateClientId(), null)
    var messageKeyList = mutableListOf<String>()
    var wifiMessageKeyList = mutableListOf<String>()
    var wifiBuffer = StringBuffer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.d("asdf client.isConnected","${client.isConnected}")

        client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d("asdf connectionLost","connectionLost")
                binding.connectButton.isClickable = true
                binding.gateWayEditText.text = null
                binding.wifiInfoTextView.text = null
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("asdf messageArrived","messageArrived")
                Log.d("asdf messageArrived topic","${topic}")
                Log.d("asdf messageArrived message","${message}")

                if (checkWifiButton) {
                    binding.wifiInfoTextView.text = "${message}"

                    val jsonMessage = JSONObject(message.toString())
                    jsonMessage.keys().forEach {
                        Log.d("asdf jsonMessage.keys()","${it}")
                        messageKeyList.add(it)
                    }

                    Log.d("asdf jsonMessage","${jsonMessage}")
                    Log.d("asdf messageKeyList","${messageKeyList}")
                    Log.d("asdf messageKeyList.get(1)","${messageKeyList.get(1)}")

                    if (messageKeyList.get(1) == "result") {
                        val wifiMessage = JSONObject(jsonMessage.toString()).get(messageKeyList.get(1))
                        Log.d("asdf wifiMessage","${wifiMessage}")
                        val wifiMessageKeys = JSONObject(wifiMessage.toString()).keys().forEach {
                            wifiMessageKeyList.add(it)
                        }
                        Log.d("asdf wifiMessageKeyList","${wifiMessageKeyList}")
                        Log.d("asdf wifiMessageKeyList.get(1)","${wifiMessageKeyList.get(1)}")
                        if (wifiMessageKeyList.get(1) == "wifi_sta") {
                            val wifiInfo = JSONObject(wifiMessage.toString()).get(wifiMessageKeyList.get(1))
                            Log.d("asdf wifiInfo","${wifiInfo}")

                            val wifiInfoArray = JSONArray(wifiInfo.toString())
                            val wifiInfoArraySize = wifiInfoArray.length()
                            Log.d("asdf wifiInfoArraySize","${wifiInfoArraySize}")

                            for(i in 0 until wifiInfoArraySize) {
                                Log.d("asdf wifiInfoArray ${i}","${wifiInfoArray.get(i)}")
                                wifiBuffer.append(wifiInfoArray.get(i).toString() +"\n")
                            }
                            Log.d("asdf wifiBuffer","${wifiBuffer}")
                            binding.wifiInfoTextView.text = wifiBuffer

                        }
                    }

                    wifiBuffer.delete(0, wifiBuffer.length)
                    messageKeyList.clear()
                    wifiMessageKeyList.clear()
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("asdf deliveryComplete","deliveryComplete")
            }

        })

        connectButton()
        addWifiButton()
        disConnectButton()
        commitButton()
        resetButton()
        infoButton()
        checkWifiButton()
        factoryResetButton()
        clearButton()
    }

    private fun connectButton() {
        binding.connectButton.setOnClickListener {
            checkWifiButton = false
            val option = MqttConnectOptions()
            option.password = MQTT_PASSWORD.toCharArray()
            option.userName = MQTT_USERNAME
            option.willMessage

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
                        client.subscribe("${TOPIC_RESPONSE}/${gatewayNumber}")
                        client.subscribe("${TOPIC_REQUEST}/${gatewayNumber}")

                    }

                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun addWifiButton() {
        binding.addWifiButton.setOnClickListener {
            checkWifiButton = false
            Log.d("asdf button click","click")
            randomNumber()
            if (client.isConnected) {

                if (binding.wifiIdEditText.text.isNullOrEmpty().not()) {
                    try {

                        gatewayNumber = binding.gateWayEditText.text.toString()
                        wifiId = binding.wifiIdEditText.text.toString()
                        wifiPassword = binding.wifiIdPasswordText.text.toString()

                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("WIFI 정보를 입력하시겠습니까?")
                            .setTitle("ID : ${wifiId} \nPASSWORD : ${wifiPassword}")
                            .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                                order_wifi = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0\", \"value\": {}}], \"id\": \"${random_id}\"}"
                                order_wifi2 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/ssid\", \"value\": \"${wifiId}\"}], \"id\": \"${random_id}\"}"
                                order_wifi3 = "{\"jsonrpc\": \"2.0\", \"method\": \"patch_env\", \"params\": [{\"op\": \"add\", \"path\": \"/network/wifi_sta/0/password\", \"value\": \"${wifiPassword}\"}], \"id\": \"${random_id}\"}"
                                order_wifi4 = "{\"jsonrpc\": \"2.0\", \"method\": \"get_env\", \"params\": {\"path\": \"/network/wifi_sta/0\"}, \"id\": \"${random_id}\"}"

                                //client.subscribe("${TOPIC}/${gatewayNumber}")

                                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_wifi}".toByteArray()))
                                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_wifi2}".toByteArray()))
                                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_wifi3}".toByteArray()))
                                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_wifi4}".toByteArray()))

                                Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()

                            })
                            .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                                Toast.makeText(this, "취소했습니다.", Toast.LENGTH_SHORT).show()
                            })
                        builder.create()
                        builder.show()


                    } catch (e : Exception) {
                        e.printStackTrace()
                        Log.d("asdf error","error")
                    }
                } else {
                    Toast.makeText(this, "Wi-Fi Id 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun disConnectButton() {
        binding.disConnectButton.setOnClickListener {
            checkWifiButton = false
            if (client.isConnected) {
                client.disconnect()
                disconnect()
            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun infoButton() {
        binding.infoButton.setOnClickListener {
            randomNumber()
            checkWifiButton = true
            if (client.isConnected) {
                gatewayNumber = binding.gateWayEditText.text.toString()
                order_gateWayInfo = "{\"jsonrpc\": \"2.0\", \"method\": \"hello\", \"id\": \"${random_id}\"}"

                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_gateWayInfo}".toByteArray()))

            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkWifiButton() {
        binding.checkWifiButton.setOnClickListener {
            randomNumber()
            checkWifiButton = true
            Log.d("asdf checkWifiButton click","click")

            if(client.isConnected) {
                gatewayNumber = binding.gateWayEditText.text.toString()
                order_checkWifi = "{\"jsonrpc\" : \"2.0\", \"method\": \"get_env\", \"params\": {\"path\": \"/network\"}, \"id\": \"${random_id}\"}"

                //client.subscribe("${TOPIC}/${gatewayNumber}")
                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_checkWifi}".toByteArray()))

                val wifiInfo = client.getTopic("${TOPIC_REQUEST}/${gatewayNumber}")
                Log.d("asdf wifiInfo","${wifiInfo}")
            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun commitButton() {
        binding.commitButton.setOnClickListener {
            checkWifiButton = false
            randomNumber()

            if(client.isConnected) {
                Log.d("asdf commitButton click","click")

                gatewayNumber = binding.gateWayEditText.text.toString()
                order_commit = "{\"jsonrpc\": \"2.0\", \"method\": \"commit_env\", \"id\": \"${random_id}\"}"

                //client.subscribe("${TOPIC}/${gatewayNumber}")
                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_commit}".toByteArray()))
            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetButton() {
        binding.resetButton.setOnClickListener {
            checkWifiButton = false
            randomNumber()
            if(client.isConnected) {
                gatewayNumber = binding.gateWayEditText.text.toString()
                order_reset = "{\"jsonrpc\": \"2.0\", \"method\": \"reset\", \"id\": \"${random_id}\"}"

                //client.subscribe("${TOPIC}/${gatewayNumber}")
                client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_reset}".toByteArray()))

            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun factoryResetButton() {
        binding.factoryResetButton.setOnClickListener {
            randomNumber()
            checkWifiButton = false
            if(client.isConnected) {
                try {
                    gatewayNumber = binding.gateWayEditText.text.toString()
                    order_factory_reset = "{\"jsonrpc\": \"2.0\", \"method\": \"factory_reset\", \"id\": \"${random_id}\"}"

                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("정말 초기화 하시겠습니까?")
                        .setTitle("모든 WIFI 정보가 삭제됩니다.")
                        .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                            //client.subscribe("${TOPIC}/${gatewayNumber}")
                            client.publish("${TOPIC_REQUEST}/${gatewayNumber}",MqttMessage("${order_factory_reset}".toByteArray()))

                            Toast.makeText(this, "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
                        })
                        .setNegativeButton("아니오",DialogInterface.OnClickListener { dialog, which ->
                            Toast.makeText(this, "취소했습니다.", Toast.LENGTH_SHORT).show()
                        })
                    builder.create()
                    builder.show()

                } catch (e : Exception) {
                    e.printStackTrace()
                    Log.d("asdf error","error")
                }
            } else {
                Toast.makeText(this, "게이트웨이 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearButton()  {
        binding.clearButton.setOnClickListener {
            Log.d("asdf infoButton click","click")
            binding.wifiInfoTextView.text = null
        }
    }

    private fun randomNumber() {
        val random_eng1 = (Math.random()*26+97).toInt().toChar()
        val random_eng2 = (Math.random()*26+97).toInt().toChar()
        val random_eng3 = (Math.random()*26+97).toInt().toChar()
        val random_eng4 = (Math.random()*26+97).toInt().toChar()
        val random_int1 = (Math.random()*10).toInt()
        val random_int2 = (Math.random()*10).toInt()
        val random_int3 = (Math.random()*10).toInt()
        val random_int4 = (Math.random()*10).toInt()

        random_id = "${random_eng1}${random_eng2}${random_eng3}${random_eng4}${random_int1}${random_int2}${random_int3}${random_int4}"
        Log.d("asdf random_id","${random_id}")
    }

    private fun disconnect() {
        gatewayNumber = binding.gateWayEditText.text.toString()
        Toast.makeText(this, "${gatewayNumber} 게이트웨이와 연결을 해제했습니다.", Toast.LENGTH_SHORT).show()
        binding.connectButton.isClickable = true
        binding.gateWayEditText.text = null
        binding.wifiInfoTextView.text = null
        Log.d("asdf client.isConnected disConnectButton","${client.isConnected}")
    }

    companion object {
        val TOPIC_REQUEST = "lcg100-request"
        val TOPIC_RESPONSE = "lcg100-response"
        val MQTT_PASSWORD = "V1ZJDQHQ4QFUjUZ3yqY0HrhhFWDMv"
        val MQTT_USERNAME = "mqttmasteruser"
        val MQTT_SERVER_URL = "tcp://wnt.litmuscloud.com:1883"
    }

    override fun onResume() {
        super.onResume()
        checkWifiButton = false
    }

    override fun onDestroy() {
        super.onDestroy()
        checkWifiButton = false
        client.disconnect()
        disconnect()
    }

    override fun onPause() {
        super.onPause()
        checkWifiButton = false
    }

}














































