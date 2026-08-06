package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditDao {
    @Query("SELECT * FROM saved_audits ORDER BY timestamp DESC")
    fun getAllAudits(): Flow<List<SavedAuditEntity>>

    @Query("SELECT * FROM saved_audits WHERE id = :id")
    suspend fun getAuditById(id: Long): SavedAuditEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: SavedAuditEntity): Long

    @Query("DELETE FROM saved_audits WHERE id = :id")
    suspend fun deleteAuditById(id: Long)

    @Query("DELETE FROM saved_audits")
    suspend fun clearAll()
}
