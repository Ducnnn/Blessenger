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
import com.ducnnn.blessenger.user.UserDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap


object BleManager {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var advertisePresenceCallback: AdvertisingSetCallback? = null
    private val _leDeviceList = MutableStateFlow<List<DeviceNode>>(emptyList())
    private val deviceMap = ConcurrentHashMap<String, DeviceNode>()
    private val APP_UUID = ParcelUuid.fromString("0000b1e5-0000-1000-8000-00805f9b34fb")
    private val MESSAGE_UUID = ParcelUuid.fromString("6180a7f4-65f2-4955-9347-7bdd46136e0e")
    private var isPresenceScanning = false
    private var isPresenceAdvertising = false
    private var isMessageScanning = false
    private var isMessageAdvertising = false
    private lateinit var appContext: Context
    val leDeviceList: StateFlow<List<DeviceNode>> = _leDeviceList.asStateFlow()


    fun init(context: Context) {
        appContext = context.applicationContext
        val bluetoothManager =
            appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        startPresenceAdvertising()
    }

    fun startMessageAdvertising(message: NetworkMeshMessage) {
        if (isMessageAdvertising) return
        val messageBytes = message.toByteArray()
        if (messageBytes.size > 234) return
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
            .addServiceData(MESSAGE_UUID, messageBytes)
            .build()

        val advertiseMessageCallback = object : AdvertisingSetCallback() {
            override fun onAdvertisingSetStarted(
                advertisingSet: AdvertisingSet?,
                txPower: Int,
                status: Int
            ) {
                super.onAdvertisingSetStarted(advertisingSet, txPower, status)
                Log.i(
                    "BleManager",
                    ("onMessageAdvertisingSetStarted(): txPower:" + txPower + " , status: " + status)
                )
            }

            override fun onAdvertisingDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onAdvertisingDataSet(advertisingSet, status)
                Log.i("BleManager", "onMessageAdvertisingDataSet() :status:$status")
            }

            override fun onScanResponseDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onScanResponseDataSet(advertisingSet, status)
                Log.i("BleManager", "onMessageScanResponseDataSet(): status:$status")
            }

            override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet?) {
                super.onAdvertisingSetStopped(advertisingSet)
                Log.i("BleManager", "onMessageAdvertisingSetStopped():")
            }
        }
        MeshRouter.notifyMessageSent(message.targetId)
        advertiser?.startAdvertisingSet(
            advertiseSetParameters,
            advertiseData,
            null,
            null,
            null,
            10000,
            0,
            advertiseMessageCallback
        )
    }

    fun startPresenceAdvertising() {
        if (isPresenceAdvertising) return
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
        val userIdBytes = UserDataManager.getSavedId().hexToByteArray()
        val advertiseData = AdvertiseData.Builder()
            .addServiceData(APP_UUID, userIdBytes)
            .build()

        advertisePresenceCallback = object : AdvertisingSetCallback() {
            override fun onAdvertisingSetStarted(
                advertisingSet: AdvertisingSet?,
                txPower: Int,
                status: Int
            ) {
                super.onAdvertisingSetStarted(advertisingSet, txPower, status)
                Log.i(
                    "BleManager",
                    ("onPresenceAdvertisingSetStarted(): txPower:" + txPower + " , status: " + status)
                )
            }

            override fun onAdvertisingDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onAdvertisingDataSet(advertisingSet, status)
                Log.i("BleManager", "onPresenceAdvertisingDataSet() :status:$status")
            }

            override fun onScanResponseDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onScanResponseDataSet(advertisingSet, status)
                Log.i("BleManager", "onPresenceScanResponseDataSet(): status:$status")
            }

            override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet?) {
                super.onAdvertisingSetStopped(advertisingSet)
                Log.i("BleManager", "onPresenceAdvertisingSetStopped():")
            }
        }
        advertiser?.startAdvertisingSet(
            advertiseSetParameters,
            advertiseData,
            null,
            null,
            null,
            advertisePresenceCallback
        )
        isPresenceAdvertising = true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun stopPresenceAdvertising() {
        if (!isPresenceAdvertising) return
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        advertiser?.stopAdvertisingSet(advertisePresenceCallback)
        isPresenceAdvertising = false
    }

    fun startMessageScan() {
        if (isMessageScanning) return
        Log.i("BleManager", "Starting to scan for incoming messages")
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        val filter = ScanFilter.Builder()
            .setServiceData(
                MESSAGE_UUID,
                byteArrayOf()
            )
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setLegacy(false)
            .build()

        scanner?.startScan(listOf(filter), settings, messageLeScanCallback)
        isMessageScanning = true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopMessageScan() {
        if (!isMessageScanning) return
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(messageLeScanCallback)
        isMessageScanning = false
        Log.i("BleManager", "Ble scan stopped")
    }

    fun startPresenceScan() {
        if (isPresenceScanning) return
        Log.i("BleManager", "Starting to scan for presence")
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        val filter = ScanFilter.Builder()
            .setServiceData(
                APP_UUID,
                byteArrayOf()
            )
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setLegacy(false)
            .build()
        scanner?.startScan(listOf(filter), settings, presenceLeScanCallback)
        isPresenceScanning = true

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopPresenceScan() {
        if (!isPresenceScanning) return
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(presenceLeScanCallback)
        isPresenceScanning = false
        Log.i("BleManager", "Ble scan stopped")
    }


    private fun updateCurrentList() {
        _leDeviceList.value = deviceMap.values.toList()
    }

    private val presenceLeScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val serviceDataBytes: ByteArray? = result.scanRecord?.getServiceData(APP_UUID)
            val broadcastedName = serviceDataBytes?.toHexString() ?: "No Name Broadcasted"
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
    private val messageLeScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val serviceDataBytes: ByteArray? = result.scanRecord?.getServiceData(MESSAGE_UUID)
            if (serviceDataBytes != null) {
                val message = NetworkMeshMessage.fromByteArray(serviceDataBytes)
                Log.i("BleManager", "Received message: \n$message")
                MeshRouter.onMessageReceived(message)
            }
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


