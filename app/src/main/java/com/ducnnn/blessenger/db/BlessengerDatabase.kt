package com.ducnnn.blessenger.db

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(entities = [DatabaseMessage::class, DatabaseContact::class], version = 1)
abstract class BlessengerDatabase : RoomDatabase(){
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao
}