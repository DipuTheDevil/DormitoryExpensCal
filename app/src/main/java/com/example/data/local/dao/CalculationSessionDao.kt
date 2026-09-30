package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CalculationSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Calculation Session snapshots and history logs.
 */
@Dao
interface CalculationSessionDao {

    @Query("SELECT * FROM calculation_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<CalculationSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: CalculationSessionEntity)

    @Query("DELETE FROM calculation_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)

    @Query("DELETE FROM calculation_sessions")
    suspend fun clearAllSessions()
}
