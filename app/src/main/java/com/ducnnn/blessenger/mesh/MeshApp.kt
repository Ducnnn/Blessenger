package com.ducnnn.blessenger.mesh
import android.app.Application
import com.ducnnn.blessenger.user.UserDataManager

class MeshApp : Application(){
    override fun onCreate() {
        super.onCreate()
        UserDataManager.init(this)
        BleManager.init(this)
    }
}
