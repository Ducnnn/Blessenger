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
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ConcurrentHashMap


object BleManager {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val _leDeviceList = MutableStateFlow<List<DeviceNode>>(emptyList())
    private val deviceMap = ConcurrentHashMap<String, DeviceNode>()
    private val APP_UUID = ParcelUuid.fromString("b27edf49-3d14-4942-a67a-5e14860babcf")
    private var isScanning = false


    fun init(context: Context) {
        val appContext = context.applicationContext
        val bluetoothManager =
            appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        startAdvertising(advertiser)

    }

    fun startAdvertising(advertiser: BluetoothLeAdvertiser?) {
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
            object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                    println("Successfully broadcasting BLE signal")
                }
            })
    }


    fun startScan() {
        if (isScanning) return
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
        }
    }

    fun removeStaleDevices() {
        val now = System.currentTimeMillis()
        val removed = deviceMap.entries.removeAll { now - it.value.lastSeenMs > 5_000L }
        if (removed) updateCurrentList()
    }
    private data class DeviceNode(
        val device: BluetoothDevice,
        val rssi: Int,
        var lastSeenMs: Long = System.currentTimeMillis()
    )
}