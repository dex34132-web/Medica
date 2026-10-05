package com.example.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * Room FTS4 full-text search entity indexing the 25.6 GB Medical Knowledge Vault.
 * Provides high-speed offline token matching across clinical steps, landmarks,
 * and precomputed semantic vector representations.
 */
@Fts4
@Entity(tableName = "vault_fts_index")
data class VaultFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowid: Int = 0,
    val assetId: String,
    val title: String,
    val mediaType: String, // "VIDEO", "FLOWCHART", "TEXT", "IMAGE", "AUDIO", "STRUCTURED"
    val originalType: String, // "PROTOCOL", "DOCUMENT", "IMAGE", etc.
    val category: String,
    val summary: String,
    val clinicalSteps: String,
    val contraindications: String,
    val keywords: String,
    val anatomicalLandmarks: String,
    val embeddingVector: String, // Comma-separated 384-dimensional dense float vector
    val provenanceHash: String
)
