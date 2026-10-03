package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val demographic: String,
    val timeAgo: String,
    val timestampMs: Long,
    val status: String,
    val notes: String,
    val mediaJson: String
)
