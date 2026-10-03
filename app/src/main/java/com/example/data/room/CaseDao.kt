package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {

    @Query("SELECT * FROM cases ORDER BY timestampMs DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id LIMIT 1")
    fun getCaseById(id: String): Flow<CaseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: CaseEntity)

    @Query("DELETE FROM cases WHERE id = :id")
    suspend fun deleteCaseById(id: String)

    @Query("UPDATE cases SET escalationStatus = :status, escalationNotes = :notes WHERE id = :id")
    suspend fun updateEscalation(id: String, status: String, notes: String)

    @Query("SELECT COUNT(*) FROM cases")
    suspend fun getCaseCount(): Int
}
