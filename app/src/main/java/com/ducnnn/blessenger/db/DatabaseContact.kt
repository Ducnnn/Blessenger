package com.ducnnn.blessenger.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "contacts")
data class DatabaseContact(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "contact_name") val contactName: String,
)