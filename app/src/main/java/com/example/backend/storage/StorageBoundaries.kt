package com.example.backend.storage

import com.example.backend.models.StorageBoundary
import com.example.backend.models.StorageBoundarySpec

object StorageBoundaries {

    val allBoundaries: List<StorageBoundarySpec> = listOf(
        StorageBoundarySpec(
            boundary = StorageBoundary.CASE_DATA_STORE,
            displayName = "Case Data Store",
            storageTechnology = "Relational Encrypted DB (PostgreSQL / Spanner)",
            securityLevel = "HIPAA-Compliant / AES-256 at Rest",
            accessPolicy = "Restricted to Backend Case Worker & Ingress Gateway",
            description = "Stores structured patient metadata, chief complaints, timestamps, and case lifecycle state."
        ),
        StorageBoundarySpec(
            boundary = StorageBoundary.MEDIA_BLOB_STORE,
            displayName = "Media Blob Storage",
            storageTechnology = "Secured Object Store (GCS / S3 with Signed URLs)",
            securityLevel = "Envelope Encryption (Customer-Managed Encryption Keys)",
            accessPolicy = "Time-limited signed URLs for multimodal ingestion",
            description = "Isolates large patient photos, audio auscultation clips, and clinical video streams."
        ),
        StorageBoundarySpec(
            boundary = StorageBoundary.MEDICAL_KNOWLEDGE_VAULT,
            displayName = "Medical Knowledge Vault",
            storageTechnology = "Vector Search Index + Document Store",
            securityLevel = "Read-Only Clinical Reference Vault",
            accessPolicy = "Internal AI Orchestrator RAG Pipeline only",
            description = "Contains verified medical protocols, reference documents, medical images, diagrams, flowcharts, decision trees, videos, and structured clinical rules."
        ),
        StorageBoundarySpec(
            boundary = StorageBoundary.AI_CONFIGURATION_STORE,
            displayName = "AI Configuration & Policy Store",
            storageTechnology = "Internal Versioned Policy Store",
            securityLevel = "Strict Server-Side / Zero Client Egress",
            accessPolicy = "AI Orchestrator Engine only",
            description = "Hosts invisible system prompts, medical safety guardrails, non-diagnostic constraints, and strict output JSON schemas."
        ),
        StorageBoundarySpec(
            boundary = StorageBoundary.SECRETS_MANAGEMENT_STORE,
            displayName = "Secrets Management Vault",
            storageTechnology = "Hardware Security Module (Cloud KMS / Secret Manager)",
            securityLevel = "FIPS 140-2 Level 3 / Strict Air-Gap from Mobile App",
            accessPolicy = "Backend Outbound Cloud AI Connector only",
            description = "Encapsulates Cloud AI provider API keys and signing tokens. The mobile app never receives credentials."
        ),
        StorageBoundarySpec(
            boundary = StorageBoundary.TELEMETRY_AND_AUDIT_STORE,
            displayName = "Telemetry & Audit Trail Store",
            storageTechnology = "Immutable Append-Only Log Store",
            securityLevel = "Cryptographic Tamper-Evident Hashing",
            accessPolicy = "Audit Service & Observability Collectors",
            description = "Maintains immutable provenance trails, rate limit stats, latency percentiles, and request hashes."
        )
    )
}
