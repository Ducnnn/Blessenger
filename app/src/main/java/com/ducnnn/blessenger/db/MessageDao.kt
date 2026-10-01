package com.ducnnn.blessenger.db

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    suspend fun getAll(): List<DatabaseMessage>

    @Query(
        """
        SELECT * FROM messages
        WHERE (sender_id = :userId AND target_id = :senderId) OR (sender_id = :senderId AND target_id = :userId)
        ORDER BY uid  ASC
        """
    )
    fun findBySenderId(userId : String, senderId: String): Flow<List<DatabaseMessage>>

    @Insert
    suspend fun insertAll(vararg users: DatabaseMessage)

    @Insert
    suspend fun insert(user: DatabaseMessage)

    @Delete
    suspend fun delete(user: DatabaseMessage)
}