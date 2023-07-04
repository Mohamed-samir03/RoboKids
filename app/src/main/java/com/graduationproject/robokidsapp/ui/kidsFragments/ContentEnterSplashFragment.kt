package com.graduationproject.robokidsapp.ui.kidsFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.graduationproject.robokidsapp.data.model.ConnectArduinoBluetooth
import com.graduationproject.robokidsapp.databinding.FragmentContentEnterSplashBinding
import com.graduationproject.robokidsapp.util.toast
import kotlinx.coroutines.*


class ContentEnterSplashFragment : Fragment() {
    private var _binding: FragmentContentEnterSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController
    private val args by navArgs<ContentEnterSplashFragmentArgs>()

    var m_bluetoothAdapter: BluetoothAdapter? = null
    lateinit var m_pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val arduinoBluetooth = ConnectArduinoBluetooth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContentEnterSplashBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter == null) {
            toast("this device doesn't support bluetooth")
            return
        }
        if (!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            connectWithArduinoBluetooth()
            transferToContent()
        }
    }


    //  this transfer to content fragment
    private fun transferToContent() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(4000)

            val child = args.currentChild
            val action = ContentEnterSplashFragmentDirections.actionContentEnterSplashFragmentToContentFragment(child)
            mNavController.navigate(action)
        }
    }

    // this connect with Arduino Bluetooth
    private fun connectWithArduinoBluetooth() {
        // get arduino address to connect on it
        val arduinoAddress = getArduinoAddress()
        if (arduinoAddress != null) {
            if (!ConnectArduinoBluetooth.isConnected) {
                // this connect with Arduino Bluetooth
                arduinoBluetooth.ConnectToDevice(
                    requireContext(),
                    arduinoAddress.toString()
                ).execute()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getArduinoAddress(): BluetoothDevice? {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                if (device.name == "HC-05") {
//                    showToast("${device.name} ${device.address}")
                    list.add(device)
                }
                Log.i("device", "" + device)
            }
            return list[0]
        } else {
            toast("no paired bluetooth devices found")
            return null
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled.")
                    connectWithArduinoBluetooth()
                    transferToContent()
                } else {
                    toast("Bluetooth has been disabled.")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth enabling has been canceled.")
                transferToContent()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}