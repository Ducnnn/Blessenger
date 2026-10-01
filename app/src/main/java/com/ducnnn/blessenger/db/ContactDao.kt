package com.ducnnn.blessenger.db

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY contact_name")
    fun observeAll(): Flow<List<DatabaseContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(contact: DatabaseContact)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(id: String)
}