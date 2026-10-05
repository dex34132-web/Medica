package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VaultSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<VaultFtsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VaultFtsEntity)

    @Query("SELECT rowid, * FROM vault_fts_index WHERE vault_fts_index MATCH :query")
    suspend fun searchFts(query: String): List<VaultFtsEntity>

    @Query("SELECT rowid, * FROM vault_fts_index WHERE mediaType = :mediaType AND vault_fts_index MATCH :query")
    suspend fun searchFtsByType(query: String, mediaType: String): List<VaultFtsEntity>

    @Query("SELECT rowid, * FROM vault_fts_index WHERE mediaType = :mediaType")
    suspend fun getByMediaType(mediaType: String): List<VaultFtsEntity>

    @Query("SELECT rowid, * FROM vault_fts_index WHERE assetId = :assetId LIMIT 1")
    suspend fun getByAssetId(assetId: String): VaultFtsEntity?

    @Query("SELECT rowid, * FROM vault_fts_index")
    suspend fun getAll(): List<VaultFtsEntity>

    @Query("SELECT count(*) FROM vault_fts_index")
    suspend fun getCount(): Int

    @Query("DELETE FROM vault_fts_index")
    suspend fun clearIndex()
}
