package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAtFormatted: String,
    val timestampMs: Long,
    val mediaJson: String,
    val context: String,
    val status: String,
    val resultJson: String?,
    val escalationStatus: String,
    val escalationContact: String,
    val escalationNotes: String,
    val timelineJson: String
)
