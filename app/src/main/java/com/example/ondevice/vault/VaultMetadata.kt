package com.example.ondevice.vault

import com.example.ondevice.models.KnowledgeVaultType
import com.example.ondevice.models.LocalKnowledgeAsset

/**
 * Simplified media types for filtering the 25.6 GB Medical Knowledge Vault.
 * Directly supports filtering by video, flowchart, text, image, audio, and structured data.
 */
enum class VaultMediaType(val label: String, val extensions: List<String>) {
    TEXT("Clinical Text", listOf("txt", "md", "pdf", "protocol")),
    IMAGE("Medical Imagery", listOf("jpg", "png", "avif", "webp")),
    VIDEO("Procedural Video", listOf("mp4", "webm", "av1")),
    FLOWCHART("Decision Flowchart", listOf("svg", "graph", "tree")),
    AUDIO("Auscultation Audio", listOf("m4a", "opus", "wav")),
    STRUCTURED("Structured Dosing", listOf("json", "sqlite", "parquet"))
}

/**
 * Rich metadata descriptor for items in the 25.6 GB Medical Knowledge Vault.
 */
data class VaultAssetMetadata(
    val id: String,
    val title: String,
    val originalType: KnowledgeVaultType,
    val mediaType: VaultMediaType,
    val category: String,
    val sourceOrganization: String,
    val version: String,
    val uncompressedSizeBytes: Long,
    val compressedSizeBytes: Long,
    val compressionRatio: String,
    val compressionCodec: String,
    val fileFormat: String,
    val summary: String,
    val clinicalKeywords: List<String>,
    val anatomicalLandmarks: List<String>,
    val provenanceHash: String,
    val urgencyTier: String,
    val hasVisualSteps: Boolean = false,
    val hasVideoGuide: Boolean = false,
    val hasFlowchartLogic: Boolean = false
)

/**
 * Helper class to manage metadata for the 25.6 GB Medical Knowledge Vault.
 * Allows the local RAG engine and External Neural Network to filter results by type
 * (video, flowchart, text, image, audio, structured data) before sending to the model.
 */
object VaultMetadataHelper {

    /**
     * Maps the underlying KnowledgeVaultType to the high-level VaultMediaType.
     */
    fun mapKnowledgeVaultTypeToMediaType(type: KnowledgeVaultType): VaultMediaType {
        return when (type) {
            KnowledgeVaultType.PROTOCOL,
            KnowledgeVaultType.DOCUMENT -> VaultMediaType.TEXT

            KnowledgeVaultType.IMAGE,
            KnowledgeVaultType.DIAGRAM -> VaultMediaType.IMAGE

            KnowledgeVaultType.VIDEO -> VaultMediaType.VIDEO

            KnowledgeVaultType.FLOWCHART,
            KnowledgeVaultType.DECISION_TREE -> VaultMediaType.FLOWCHART

            KnowledgeVaultType.AUDIO -> VaultMediaType.AUDIO

            KnowledgeVaultType.STRUCTURED_DATA -> VaultMediaType.STRUCTURED
        }
    }

    /**
     * Converts a LocalKnowledgeAsset into a rich VaultAssetMetadata instance.
     */
    fun extractMetadata(asset: LocalKnowledgeAsset): VaultAssetMetadata {
        val mediaType = mapKnowledgeVaultTypeToMediaType(asset.type)
        val keywords = extractKeywords(asset)
        val landmarks = extractLandmarks(asset)

        val uncompressedBytes = asset.uncompressedSizeKb * 1024L
        val compressedBytes = asset.compressedSizeKb * 1024L

        val format = when (mediaType) {
            VaultMediaType.TEXT -> "zstd-markdown"
            VaultMediaType.IMAGE -> "avif-lossless"
            VaultMediaType.VIDEO -> "av1-crf28"
            VaultMediaType.FLOWCHART -> "svg-vector"
            VaultMediaType.AUDIO -> "opus-24k"
            VaultMediaType.STRUCTURED -> "columnar-dict"
        }

        val urgency = when {
            asset.category.contains("Cardiac", ignoreCase = true) ||
            asset.category.contains("Hemostasis", ignoreCase = true) ||
            asset.title.contains("Arrest", ignoreCase = true) -> "CRITICAL_P1"

            asset.category.contains("Respiratory", ignoreCase = true) ||
            asset.category.contains("Burn", ignoreCase = true) ||
            asset.category.contains("Trauma", ignoreCase = true) -> "URGENT_P2"

            else -> "STANDARD_P3"
        }

        return VaultAssetMetadata(
            id = asset.id,
            title = asset.title,
            originalType = asset.type,
            mediaType = mediaType,
            category = asset.category,
            sourceOrganization = asset.sourceOrganization,
            version = asset.version,
            uncompressedSizeBytes = uncompressedBytes,
            compressedSizeBytes = compressedBytes,
            compressionRatio = asset.compressionRatio,
            compressionCodec = asset.compressionCodec,
            fileFormat = format,
            summary = asset.summary,
            clinicalKeywords = keywords,
            anatomicalLandmarks = landmarks,
            provenanceHash = asset.evidenceProvenanceHash,
            urgencyTier = urgency,
            hasVisualSteps = mediaType == VaultMediaType.IMAGE || asset.type == KnowledgeVaultType.DIAGRAM,
            hasVideoGuide = mediaType == VaultMediaType.VIDEO,
            hasFlowchartLogic = mediaType == VaultMediaType.FLOWCHART || asset.type == KnowledgeVaultType.DECISION_TREE
        )
    }

    /**
     * Returns rich metadata for all assets in the Comprehensive Medical Vault.
     */
    fun getAllMetadata(): List<VaultAssetMetadata> {
        return ComprehensiveLocalMedicalVault.getAllAssets().map { extractMetadata(it) }
    }

    /**
     * Looks up metadata for a specific asset by its ID.
     */
    fun getMetadataForAsset(assetId: String): VaultAssetMetadata? {
        val asset = ComprehensiveLocalMedicalVault.getAllAssets().firstOrNull { it.id == assetId }
        return asset?.let { extractMetadata(it) }
    }

    /**
     * Filters assets strictly by a single simplified VaultMediaType (e.g. VIDEO, FLOWCHART, TEXT, IMAGE).
     */
    fun filterByMediaType(assets: List<LocalKnowledgeAsset>, mediaType: VaultMediaType): List<LocalKnowledgeAsset> {
        return assets.filter { mapKnowledgeVaultTypeToMediaType(it.type) == mediaType }
    }

    /**
     * Alias for filterByMediaType for ergonomic caller access.
     */
    fun filterByType(assets: List<LocalKnowledgeAsset>, mediaType: VaultMediaType): List<LocalKnowledgeAsset> {
        return filterByMediaType(assets, mediaType)
    }

    /**
     * Filters assets by a set of permitted VaultMediaTypes.
     */
    fun filterByMediaTypes(assets: List<LocalKnowledgeAsset>, types: Set<VaultMediaType>): List<LocalKnowledgeAsset> {
        if (types.isEmpty()) return assets
        return assets.filter { types.contains(mapKnowledgeVaultTypeToMediaType(it.type)) }
    }

    /**
     * Alias for filterByMediaTypes for ergonomic caller access.
     */
    fun filterByTypes(assets: List<LocalKnowledgeAsset>, types: Set<VaultMediaType>): List<LocalKnowledgeAsset> {
        return filterByMediaTypes(assets, types)
    }

    /**
     * Checks if a knowledge asset matches a specific VaultMediaType.
     */
    fun assetMatchesType(asset: LocalKnowledgeAsset, mediaType: VaultMediaType): Boolean {
        return mapKnowledgeVaultTypeToMediaType(asset.type) == mediaType
    }

    /**
     * Returns all supported media types for vault filtering.
     */
    fun getSupportedMediaTypes(): List<VaultMediaType> {
        return VaultMediaType.values().toList()
    }

    /**
     * Filters assets by original KnowledgeVaultType.
     */
    fun filterByOriginalType(assets: List<LocalKnowledgeAsset>, type: KnowledgeVaultType): List<LocalKnowledgeAsset> {
        return assets.filter { it.type == type }
    }

    /**
     * Filters vault metadata by allowed media types.
     */
    fun filterMetadataByTypes(types: Set<VaultMediaType>): List<VaultAssetMetadata> {
        val all = getAllMetadata()
        if (types.isEmpty()) return all
        return all.filter { types.contains(it.mediaType) }
    }

    /**
     * Extracts clinically meaningful keywords from an asset for indexing and vector search.
     */
    fun extractKeywords(asset: LocalKnowledgeAsset): List<String> {
        val keywords = mutableSetOf<String>()
        val combined = "${asset.title} ${asset.category} ${asset.summary}".lowercase()

        val terms = listOf(
            "cpr", "arrest", "defibrillation", "aed", "epinephrine", "amiodarone",
            "tourniquet", "hemorrhage", "hemostasis", "bleeding", "wound", "cat", "txa",
            "stridor", "airway", "respiratory", "bronchospasm", "albuterol", "ipratropium", "cpap",
            "burn", "parkland", "rule of nines", "inhalation", "lactated ringers",
            "stroke", "be-fast", "thrombolysis", "thrombectomy", "neurological",
            "choking", "fbao", "heimlich", "back blow", "chest thrust",
            "seizure", "epilepticus", "midazolam", "recovery position",
            "pneumothorax", "chest seal", "needle decompression", "thoracostomy",
            "hypothermia", "rewarming", "frostbite", "cold injury",
            "hyperthermia", "heat stroke", "active cooling", "immersion",
            "cervical", "spinal", "c-collar", "motion restriction", "nexus",
            "triage", "start", "jumpstart", "salt", "mci",
            "delivery", "apgar", "neonatal", "obstetric", "umbilical",
            "broselow", "pediatric", "weight-based", "dosing", "pharmacology"
        )

        terms.forEach { term ->
            if (combined.contains(term)) {
                keywords.add(term)
            }
        }

        if (keywords.isEmpty()) {
            keywords.addAll(asset.title.lowercase().split("\\s+".toRegex()).filter { it.length > 3 })
        }

        return keywords.toList()
    }

    /**
     * Extracts anatomical landmarks referenced in the clinical procedure.
     */
    fun extractLandmarks(asset: LocalKnowledgeAsset): List<String> {
        val landmarks = mutableListOf<String>()
        val text = "${asset.title} ${asset.clinicalSteps.joinToString(" ")}".lowercase()

        if (text.contains("sternum") || text.contains("chest")) landmarks.add("Lower Half of Sternum")
        if (text.contains("midclavicular") || text.contains("2nd intercostal")) landmarks.add("2nd Intercostal Space Midclavicular Line")
        if (text.contains("axillary") || text.contains("5th intercostal")) landmarks.add("5th Intercostal Space Anterior Axillary Line")
        if (text.contains("femoral") || text.contains("thigh") || text.contains("limb")) landmarks.add("2-3 Inches Proximal to Wound (Limb Axis)")
        if (text.contains("carotid")) landmarks.add("Carotid Artery Pulse Point")
        if (text.contains("navel") || text.contains("umbilicus") || text.contains("epigastr")) landmarks.add("Mid-Epigastrium above Umbilicus")
        if (text.contains("vastus lateralis") || text.contains("lateral thigh")) landmarks.add("Anterolateral Thigh (Vastus Lateralis)")
        if (text.contains("cervical") || text.contains("neck")) landmarks.add("Cervical Spine Inline Neutral")
        if (text.contains("facial") || text.contains("oropharynx") || text.contains("trachea")) landmarks.add("Trachea & Upper Oropharynx")

        if (landmarks.isEmpty()) {
            landmarks.add("Systemic / General Assessment")
        }

        return landmarks
    }

    /**
     * Returns a human-readable storage distribution summary for the 25.6 GB vault.
     */
    fun getStorageStatsByType(): Map<VaultMediaType, String> {
        return mapOf(
            VaultMediaType.IMAGE to "7.2 GB raw → 1.14 GB (6.31x, AVIF lossless)",
            VaultMediaType.VIDEO to "5.6 GB raw → 1.18 GB (4.74x, AV1 CRF-28)",
            VaultMediaType.TEXT to "5.6 GB raw → 930 MB (6.02x, zstd-19 dict)",
            VaultMediaType.FLOWCHART to "2.8 GB raw → 390 MB (7.18x, SVG vector/zstd)",
            VaultMediaType.AUDIO to "2.8 GB raw → 310 MB (9.03x, Opus 24kbps)",
            VaultMediaType.STRUCTURED to "1.6 GB raw → 230 MB (6.95x, columnar dict)"
        )
    }
}
