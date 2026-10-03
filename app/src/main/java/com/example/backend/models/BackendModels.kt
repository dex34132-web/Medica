package com.example.backend.models

import com.example.model.UrgencyLevel

/**
 * Production-ready backend data models representing API Gateway payloads,
 * AI Orchestrator contracts, Context Scoping specifications, and Storage Boundaries.
 */

data class ApiGatewayRequest(
    val requestId: String,
    val clientVersion: String = "1.0.0-field",
    val timestampMs: Long = System.currentTimeMillis(),
    val clientToken: String,
    val caseId: String,
    val chiefComplaint: String,
    val patientDemographics: String,
    val observations: String,
    val attachedMediaCount: Int,
    val locationCoordinates: String? = null
)

data class ApiGatewayResponse<T>(
    val statusCode: Int,
    val requestId: String,
    val latencyMs: Long,
    val rateLimitRemaining: Int,
    val rateLimitResetSec: Int,
    val payload: T?,
    val errorMessage: String? = null
)

data class ValidationResult(
    val isValid: Boolean,
    val sanitizedCaseId: String,
    val sanitizedComplaint: String,
    val violations: List<String> = emptyList()
)

data class ServerSidePromptPolicy(
    val version: String = "policy-v2026.4",
    val systemInstruction: String,
    val clinicalDirectives: List<String>,
    val prohibitedBehaviors: List<String>,
    val outputJsonSchema: String,
    val requiredEvidenceSourcesCount: Int = 2
)

data class ContextScopedRequest(
    val requestId: String,
    val caseInput: Map<String, String>,
    val relevantMedicalEvidence: List<String>,
    val retrievedKnowledgeSnippets: List<String>,
    val serverSideAiInstructions: String,
    val safetyAndOutputPolicies: List<String>,
    val targetModel: String = "Gemini 1.5 Pro (Cloud)"
)

data class EvidenceProvenanceItem(
    val resourceId: String,
    val title: String,
    val version: String,
    val sourceAgency: String,
    val citationClause: String,
    val integritySha256: String
)

data class OrchestratedDecisionResponse(
    val caseId: String,
    val urgencyLevel: UrgencyLevel,
    val triageCode: String,
    val clinicalObservations: List<String>,
    val possibleExplanations: List<String>,
    val immediateActions: List<String>,
    val warningsAndContraindications: List<String>,
    val uncertaintyMetric: Float,
    val uncertaintyRationale: String,
    val retrievedEvidence: List<EvidenceProvenanceItem>,
    val provenanceHash: String,
    val auditLogId: String
)

data class AuditLogEntry(
    val eventId: String,
    val timestamp: String,
    val clientIpHash: String,
    val rateLimitBucket: String,
    val modelRoute: String,
    val latencyMs: Long,
    val ragSnippetsRetrieved: Int,
    val secretKeyFingerprint: String,
    val safetyPassed: Boolean
)

enum class StorageBoundary {
    CASE_DATA_STORE,
    MEDIA_BLOB_STORE,
    MEDICAL_KNOWLEDGE_VAULT,
    AI_CONFIGURATION_STORE,
    SECRETS_MANAGEMENT_STORE,
    TELEMETRY_AND_AUDIT_STORE
}

data class StorageBoundarySpec(
    val boundary: StorageBoundary,
    val displayName: String,
    val storageTechnology: String,
    val securityLevel: String,
    val accessPolicy: String,
    val description: String
)
