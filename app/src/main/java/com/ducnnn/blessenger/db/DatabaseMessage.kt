package com.ducnnn.blessenger.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "messages")
data class DatabaseMessage(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    @ColumnInfo(name = "target_id") val targetId: String,
    @ColumnInfo(name = "message_body") val text: String,
    @ColumnInfo(name = "timestamp", defaultValue = "CURRENT_TIMESTAMP") val timestamp: String = "0"
)