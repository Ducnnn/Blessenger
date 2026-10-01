package com.ducnnn.blessenger.db

import android.content.Context
import android.util.Log
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.ducnnn.blessenger.mesh.NetworkMeshMessage
import com.ducnnn.blessenger.ui.chat.BLEMessage
import com.ducnnn.blessenger.user.UserDataManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DatabaseManager {
    private lateinit var db: BlessengerDatabase

    fun init(context: Context) {
        db = Room.databaseBuilder<BlessengerDatabase>(context, "blessenger_database")
            .setDriver(AndroidSQLiteDriver())
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration(true)
            .build()
    }

    suspend fun addMessage(networkMeshMessage: NetworkMeshMessage) {
        val dbMessage = DatabaseMessage(
            messageId = networkMeshMessage.messageId,
            senderId = networkMeshMessage.senderId,
            targetId = networkMeshMessage.targetId,
            text = networkMeshMessage.text
        )
        try {
            db.messageDao().insert(dbMessage)
            Log.i("DatabaseManager", "addMessage() added messageId:${dbMessage.messageId}")
        } catch (e: Exception) {
            Log.e("DatabaseManager", "addMessage: Error: $e")
        }

    }
    fun observeMessages(contactId : String):Flow<List<BLEMessage>> {
        val myId = UserDataManager.getSavedId()
        return db.messageDao().findBySenderId(UserDataManager.getSavedId(), contactId).map { rows->
            rows.map {
                BLEMessage(
                    text = it.text,
                    sender = it.senderId,
                    fromCurrentUser = it.senderId == myId
                )
            }
        }
    }
    fun observeContacts(): Flow<Map<String, String>> {
        return db.contactDao().observeAll().map { contacts ->
            contacts.associate { it.id to it.contactName }
        }
    }

    suspend fun addContact(id: String, name: String) {
        try {
            db.contactDao().upsert(DatabaseContact(id, name))
            Log.i("DatabaseManager", "addContact() added contactId:${id}")
        } catch (e: Exception) {
            Log.e("DatabaseManager", "addContact() Error:$e")
        }

    }

    suspend fun deleteContact(id: String) {
        try {
            db.contactDao().deleteById(id)
            Log.i("DatabaseManager", "deleteContact() deleted contactId:${id}")
        } catch (e: Exception) {
            Log.e("DatabaseManager", "deleteContact() Error:$e")
        }
    }
}