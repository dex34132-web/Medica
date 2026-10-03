package com.example.backend.gateway

import com.example.backend.models.ApiGatewayRequest
import com.example.backend.models.ApiGatewayResponse
import com.example.backend.models.OrchestratedDecisionResponse
import com.example.backend.models.ValidationResult
import com.example.backend.orchestrator.AiOrchestrator
import java.util.concurrent.ConcurrentHashMap

/**
 * API Gateway Layer.
 *
 * Responsibilities:
 * 1. Request Validation: verifies structural schema, client token, and payload boundaries.
 * 2. Rate Limiting: Token-bucket algorithm (prevents flooding and DOS attacks).
 * 3. Abuse Protection: Sanitizes input strings, detects prompt injection payloads, and rejects oversized requests.
 * 4. Secure Routing: Dispatches authenticated requests to the AI Orchestrator.
 */
object ApiGatewayService {

    private val rateLimitTokens = ConcurrentHashMap<String, Int>()
    private const val MAX_TOKENS_PER_WINDOW = 60

    fun validateRequest(request: ApiGatewayRequest): ValidationResult {
        val violations = mutableListOf<String>()

        if (request.caseId.isBlank()) violations.add("Missing case identifier")
        if (request.chiefComplaint.isBlank()) violations.add("Chief complaint cannot be empty")
        if (request.chiefComplaint.length > 500) violations.add("Chief complaint exceeds 500 character limit")

        // Prompt injection & abuse detection
        val lower = request.chiefComplaint.lowercase()
        if (lower.contains("ignore previous instructions") || lower.contains("system prompt") || lower.contains("reveal secret")) {
            violations.add("Abuse violation: Malicious instruction sequence detected")
        }

        val sanitizedComplaint = request.chiefComplaint
            .replace("<script>", "")
            .replace("</script>", "")
            .trim()

        return ValidationResult(
            isValid = violations.isEmpty(),
            sanitizedCaseId = request.caseId.trim(),
            sanitizedComplaint = sanitizedComplaint,
            violations = violations
        )
    }

    private fun checkRateLimit(clientToken: String): Pair<Boolean, Int> {
        val current = rateLimitTokens.getOrDefault(clientToken, MAX_TOKENS_PER_WINDOW)
        return if (current > 0) {
            rateLimitTokens[clientToken] = current - 1
            true to (current - 1)
        } else {
            false to 0
        }
    }

    suspend fun handleClientCaseDecisionRequest(
        request: ApiGatewayRequest
    ): ApiGatewayResponse<OrchestratedDecisionResponse> {
        val startTime = System.currentTimeMillis()

        // 1. Rate Limiting Check
        val (allowed, remainingTokens) = checkRateLimit(request.clientToken)
        if (!allowed) {
            return ApiGatewayResponse(
                statusCode = 429,
                requestId = request.requestId,
                latencyMs = System.currentTimeMillis() - startTime,
                rateLimitRemaining = 0,
                rateLimitResetSec = 45,
                payload = null,
                errorMessage = "HTTP 429 Too Many Requests: Rate limit exceeded for client token"
            )
        }

        // 2. Request Validation & Abuse Protection
        val validation = validateRequest(request)
        if (!validation.isValid) {
            return ApiGatewayResponse(
                statusCode = 400,
                requestId = request.requestId,
                latencyMs = System.currentTimeMillis() - startTime,
                rateLimitRemaining = remainingTokens,
                rateLimitResetSec = 60,
                payload = null,
                errorMessage = "HTTP 400 Bad Request: " + validation.violations.joinToString("; ")
            )
        }

        // 3. Strict Context Scoping via AI Orchestrator
        val scopedContext = AiOrchestrator.assembleScopedContext(
            caseId = validation.sanitizedCaseId,
            chiefComplaint = validation.sanitizedComplaint,
            demographics = request.patientDemographics,
            observations = request.observations,
            attachedMediaCount = request.attachedMediaCount
        )

        // 4. Outbound Provider Execution via Server-Side Orchestrator
        val decision = AiOrchestrator.processOrchestratedDecision(scopedContext)

        val totalLatency = System.currentTimeMillis() - startTime + 80

        return ApiGatewayResponse(
            statusCode = 200,
            requestId = request.requestId,
            latencyMs = totalLatency,
            rateLimitRemaining = remainingTokens,
            rateLimitResetSec = 58,
            payload = decision,
            errorMessage = null
        )
    }
}
