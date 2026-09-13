package com.ducnnn.blessenger.db

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.ducnnn.blessenger.mesh.NetworkMeshMessage

object DatabaseManager {
    lateinit var db: RoomDatabase

    fun init(context: Context) {
        db = Room.databaseBuilder<BlessengerDatabase>(context, "messages")
            .setDriver(AndroidSQLiteDriver())
            .enableMultiInstanceInvalidation()
            .build()
    }
    fun addMessage(networkMeshMessage: NetworkMeshMessage) {
        val dbMessage = DatabaseMessage(
            messageId = networkMeshMessage.messageId,
            senderId = networkMeshMessage.senderId,
            targetId = networkMeshMessage.targetId,
            text = networkMeshMessage.text
        )
    }
}