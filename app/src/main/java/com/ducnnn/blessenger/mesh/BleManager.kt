package com.ducnnn.blessenger.mesh

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.AdvertisingSetParameters
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
    private var advertiseCallback: AdvertisingSetCallback? = null
    private val _leDeviceList = MutableStateFlow<List<DeviceNode>>(emptyList())
    private val deviceMap = ConcurrentHashMap<String, DeviceNode>()
    private val APP_UUID = ParcelUuid.fromString("0000b1e5-0000-1000-8000-00805f9b34fb")
    private var isScanning = false
    private var isAdvertising = false
    private lateinit var appContext: Context
    val leDeviceList: StateFlow<List<DeviceNode>> = _leDeviceList.asStateFlow()


    fun init(context: Context) {
        appContext = context.applicationContext
        val bluetoothManager =
            appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        startAdvertising()
    }

    fun startAdvertising() {
        if (isAdvertising) return
        if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        val advertiseSetParameters = AdvertisingSetParameters.Builder()
            .setLegacyMode(false)
            .setAnonymous(false)
            .setConnectable(false)
            .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
            .setIncludeTxPower(true)
            .setScannable(false)
            .build()

        val advertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(APP_UUID)
            .build()

        advertiseCallback = object : AdvertisingSetCallback() {
            override fun onAdvertisingSetStarted(
                advertisingSet: AdvertisingSet?,
                txPower: Int,
                status: Int
            ) {
                super.onAdvertisingSetStarted(advertisingSet, txPower, status)
                Log.i("BleManager", ("onAdvertisingSetStarted(): txPower:" + txPower + " , status: " + status))
            }
            override fun onAdvertisingDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onAdvertisingDataSet(advertisingSet, status)
                Log.i("BleManager", "onAdvertisingDataSet() :status:$status")
            }

            override fun onScanResponseDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onScanResponseDataSet(advertisingSet, status)
                Log.i("BleManager", "onScanResponseDataSet(): status:$status")
            }

            override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet?) {
                super.onAdvertisingSetStopped(advertisingSet)
                Log.i("BleManager", "onAdvertisingSetStopped():")
            }
        }
        advertiser?.startAdvertisingSet(
            advertiseSetParameters,
            advertiseData,
            null,
            null,
            null,
            advertiseCallback
        )
        isAdvertising = true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun stopAdvertising() {
        if (!isAdvertising) return
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        advertiser?.stopAdvertisingSet(advertiseCallback)
        isAdvertising = false
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
            .setLegacy(false)
            .build()
        scanner?.startScan(listOf(filter), settings, leScanCallback)
        isScanning = true

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        if (!isScanning) return
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        isScanning = false
        Log.i("BleManager", "Ble scan stopped")
    }


    private fun updateCurrentList() {
        _leDeviceList.value = deviceMap.values.toList()
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val broadcastedName = result.scanRecord?.deviceName ?: "No Name Broadcasted"
            deviceMap[broadcastedName] = DeviceNode(
                device = result.device,
                deviceName = broadcastedName,
                rssi = result.rssi,
                lastSeenMs = System.currentTimeMillis()
            )
            updateCurrentList()
            Log.i("BleManager", "Received Scan Callback from device:$broadcastedName")
        }
    }

    fun removeStaleDevices() {
        val now = System.currentTimeMillis()
        val removed = deviceMap.entries.removeAll { now - it.value.lastSeenMs > 5_000L }
        if (removed) updateCurrentList()
    }

    data class DeviceNode(
        val device: BluetoothDevice,
        val deviceName: String,
        val rssi: Int,
        var lastSeenMs: Long = System.currentTimeMillis()
    )
}


