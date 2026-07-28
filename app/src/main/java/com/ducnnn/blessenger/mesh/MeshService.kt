package com.ducnnn.blessenger.mesh

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ducnnn.blessenger.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MeshService : Service() {
    private val CHANNEL_ID = "mesh_channel"
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> startMeshService()
            Actions.STOP.toString() -> stopSelf()

        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startMeshService(): Int {
        val stopIntent = Intent(this, MeshService::class.java).apply {
            action = Actions.STOP.toString()
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Blessenger mesh service")
            .setContentText("You are an active node in the mesh")
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Exit Blessenger",
                stopPendingIntent
            )
            .build()
        startForeground(1, notification)
        BleManager.startScan()
        serviceScope.launch {
            while (isActive) {
                retrieveNearbyNode()
                delay(5000.milliseconds)
            }
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Mesh Channel",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

    }

    private fun retrieveNearbyNode() {
        BleManager.removeStaleDevices()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        BleManager.stopScan()
        return super.stopService(name)
    }

    enum class Actions {
        START, STOP
    }
}