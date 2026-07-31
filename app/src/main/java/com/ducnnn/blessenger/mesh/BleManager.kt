package com.ducnnn.blessenger.mesh

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap


object BleManager {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var adverseCallback: AdvertiseCallback? = null
    private val _leDeviceList = MutableStateFlow<List<DeviceNode>>(emptyList())
    private val deviceMap = ConcurrentHashMap<String, DeviceNode>()
    private val APP_UUID = ParcelUuid.fromString("0000b1e5-0000-1000-8000-00805f9b34fb")
    private var isScanning = false
    private lateinit var appContext: Context
    val leDeviceList: StateFlow<List<DeviceNode>> = _leDeviceList.asStateFlow()


    fun init(context: Context) {
        appContext = context.applicationContext
        val bluetoothManager =
            appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        adverseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.i("BleManager", "Successfully started advertising BLE signal")
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e("BleManager", "Failed to start advertising with error code: $errorCode")
            }
        }
        startAdvertising()
    }

    fun startAdvertising() {
        if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        val advertiseSettings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(false)
            .build()
        val advertiseData = AdvertiseData.Builder()
            .addServiceUuid(APP_UUID)
            .build()
        advertiser?.startAdvertising(
            advertiseSettings,
            advertiseData,
            adverseCallback
        )
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun stopAdvertising() {
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        advertiser?.stopAdvertising(adverseCallback)
        Log.i("BleManager", "Stopped Advertising")
    }

    fun startScan() {
        if (isScanning) return
        Log.i("BleManager", "Starting to scan")
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        val filter = ScanFilter.Builder()
            .setServiceUuid(APP_UUID)
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner?.startScan(listOf(filter), settings, leScanCallback)
        isScanning = true

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        isScanning = false
        Log.i("BleManager", "Ble scan stopped")
    }


    private fun updateCurrentList() {
        _leDeviceList.value = deviceMap.values.toList()
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val address = result.device.address
            deviceMap[address] = DeviceNode(
                device = result.device,
                rssi = result.rssi,
                lastSeenMs = System.currentTimeMillis()
            )
            updateCurrentList()
            Log.i("BleManager", "Received Scan Callback")
        }
    }

    fun removeStaleDevices() {
        val now = System.currentTimeMillis()
        val removed = deviceMap.entries.removeAll { now - it.value.lastSeenMs > 5_000L }
        if (removed) updateCurrentList()
    }

    data class DeviceNode(
        val device: BluetoothDevice,
        val rssi: Int,
        var lastSeenMs: Long = System.currentTimeMillis()
    )
}


