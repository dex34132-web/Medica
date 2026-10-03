package com.example.model

enum class UrgencyLevel(val label: String, val code: String) {
    CRITICAL("CRITICAL", "RED - IMMEDIATE"),
    URGENT("URGENT", "YELLOW - DELAYED"),
    MODERATE("MODERATE", "GREEN - MINOR"),
    LOW("LOW", "WHITE - NON-URGENT")
}

enum class KnowledgeType(val label: String) {
    PROTOCOL("Protocol"),
    DOCUMENT("Document"),
    DIAGRAM("Diagram"),
    FLOWCHART("Flowchart"),
    VIDEO("Video"),
    AUDIO("Audio"),
    IMAGE("Image")
}

enum class MediaType(val label: String) {
    IMAGE("Photo"),
    VIDEO("Video"),
    AUDIO("Audio"),
    DOCUMENT("Document")
}

enum class CaseStatus(val label: String) {
    DRAFT("Draft"),
    ANALYZING("Analyzing"),
    COMPLETED("Analyzed"),
    ESCALATED("Escalated")
}

enum class EscalationStatus(val label: String) {
    NOT_REQUIRED("Not Required"),
    RECOMMENDED("Recommended"),
    CONFIRMED("Confirmed & Dispatched"),
    REJECTED("Declined by Responder")
}

data class CaseMedia(
    val id: String,
    val type: MediaType,
    val filename: String,
    val thumbnail: String? = null,
    val duration: String? = null,
    val size: String = "1.2 MB",
    val local: Boolean = true,
    val status: String = "READY",
    val description: String = "",
    val uriString: String? = null,
    val mockPreviewType: String = "STANDARD"
)

data class ActionItem(
    val priority: Int,
    val title: String,
    val detail: String,
    val isCritical: Boolean = false
)

data class MedicaResult(
    val caseId: String,
    val urgencyLevel: UrgencyLevel,
    val triageCode: String = "YELLOW - DELAYED",
    val observations: List<String>,
    val possibleConditions: List<String>,
    val immediateActions: List<ActionItem>,
    val warnings: List<String>,
    val uncertainty: String,
    val confidence: Float,
    val evidence: List<String> = emptyList(),
    val evidenceIds: List<String> = evidence,
    val escalationRecommended: Boolean,
    val escalationReason: String? = null,
    val limitations: String = "Field decision-support derived from on-device heuristics. Does not replace physician diagnosis."
)

data class FlowNode(
    val id: String,
    val title: String,
    val questionOrDetail: String,
    val yesNodeId: String? = null,
    val noNodeId: String? = null,
    val outcomeLevel: UrgencyLevel? = null,
    val actionAdvice: String? = null,
    val isTerminal: Boolean = false
)

data class FlowchartModel(
    val startNodeId: String,
    val nodes: Map<String, FlowNode>
)

data class KnowledgeAsset(
    val id: String,
    val title: String,
    val type: KnowledgeType,
    val category: String,
    val source: String,
    val version: String,
    val date: String,
    val description: String,
    val tags: List<String>,
    val local: Boolean = true,
    val thumbnail: String? = null,
    val content: String,
    val relatedAssets: List<String> = emptyList(),
    val relatedAssetIds: List<String> = relatedAssets,
    val videoTimestamp: String? = null,
    val duration: String? = null,
    val flowchartData: FlowchartModel? = null
)

data class TimelineEvent(
    val timestamp: String,
    val title: String,
    val description: String
)

data class Case(
    val id: String,
    val createdAt: Long = System.currentTimeMillis(),
    val createdAtFormatted: String = "",
    val timestampMs: Long = createdAt,
    val title: String = "",
    val media: List<CaseMedia> = emptyList(),
    val context: String = "",
    val status: CaseStatus = CaseStatus.DRAFT,
    val result: MedicaResult? = null,
    val evidence: List<String> = result?.evidence ?: emptyList(),
    val escalationStatus: EscalationStatus = EscalationStatus.NOT_REQUIRED,
    val escalationContact: String = "Metro Emergency Dispatch / Medical Control",
    val escalationNotes: String = "",
    val timeline: List<TimelineEvent> = emptyList()
)

data class AnalysisProgress(
    val stepIndex: Int,
    val stepTitle: String,
    val percentage: Float,
    val details: String,
    val completed: Boolean = false
)
