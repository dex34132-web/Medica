package com.example.model

enum class MediaType(val label: String) {
    PHOTO("Photo"),
    VIDEO("Video"),
    AUDIO("Audio")
}

data class UploadedMedia(
    val id: String,
    val type: MediaType,
    val name: String,
    val uriString: String? = null,
    val durationOrSize: String = "Local file",
    val timestamp: Long = System.currentTimeMillis()
)

data class CaseRecord(
    val id: String,
    val title: String,
    val demographic: String = "(M, 54)",
    val timeAgo: String = "Just now",
    val timestampMs: Long = System.currentTimeMillis(),
    val status: String = "Active", // "Active", "Pending"
    val notes: String = "",
    val mediaItems: List<UploadedMedia> = emptyList(),
    val isExpanded: Boolean = false
)

data class VaultCaseFile(
    val id: String,
    val filename: String,
    val fileType: String, // "XLSX", "PDF", "DOC"
    val statusText: String, // "Access-ready 2m ago"
    val isLocked: Boolean = true,
    val isPrimary: Boolean = false
)

data class VaultMediaCard(
    val id: String,
    val title: String,
    val type: String, // "VIDEO", "FLOWCHART", "IMAGE"
    val subtitle: String = "Offline Reference",
    val duration: String? = null
)

data class LocalAiModel(
    val id: String,
    val name: String,
    val size: String,
    val statusText: String,
    val isCurrentlyActive: Boolean = false,
    val downloadProgress: Float? = null,
    val iconType: String = "META" // "META", "ORANGE", "GRID"
)
