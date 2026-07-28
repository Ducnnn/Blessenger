package com.ducnnn.blessenger.mesh
import android.app.Application

class MeshApp : Application(){
    override fun onCreate() {
        super.onCreate()
        BleManager.init(this)
    }
}
