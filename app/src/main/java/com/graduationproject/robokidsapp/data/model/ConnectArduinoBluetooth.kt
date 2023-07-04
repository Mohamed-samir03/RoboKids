package com.graduationproject.robokidsapp.data.model

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import java.io.IOException
import java.util.*

class ConnectArduinoBluetooth {
    var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var bluetoothSocket: BluetoothSocket? = null
    lateinit var progress: ProgressDialog
    lateinit var bluetoothAdapter: BluetoothAdapter

    companion object{
        var isConnected: Boolean = false
    }


    fun sendMessage(message: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(message.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }



    inner class ConnectToDevice(val context: Context, val address: String) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()
            // هنا لو انت عاوز تحط اي حاجه تلود وهو بيتصل
//            progress = ProgressDialog.show(context, "Connecting...", "please wait")
            Toast.makeText(context, "connecting now....", Toast.LENGTH_SHORT).show()
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String {
            try {
                if (bluetoothSocket == null || !isConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Toast.makeText(context, "couldn't connect", Toast.LENGTH_SHORT).show()
            } else {
                isConnected = true
                Toast.makeText(context, "success connect", Toast.LENGTH_SHORT).show()
            }
        }
    }

}