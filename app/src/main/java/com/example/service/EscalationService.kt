package com.example.service

import com.example.model.Case
import com.example.model.EscalationStatus
import kotlinx.coroutines.delay

interface EscalationService {
    suspend fun confirmEscalation(case: Case, contact: String, notes: String): Case
    suspend fun rejectEscalation(case: Case, reason: String): Case
}

class MockEscalationService(private val caseService: CaseService) : EscalationService {

    override suspend fun confirmEscalation(case: Case, contact: String, notes: String): Case {
        delay(300)
        val updated = case.copy(
            escalationStatus = EscalationStatus.CONFIRMED,
            escalationContact = contact,
            escalationNotes = notes.ifEmpty { "Dispatched to $contact with multimodal telemetry packet." }
        )
        caseService.saveCase(updated)
        return updated
    }

    override suspend fun rejectEscalation(case: Case, reason: String): Case {
        delay(200)
        val updated = case.copy(
            escalationStatus = EscalationStatus.REJECTED,
            escalationNotes = "Escalation declined by responder: ${reason.ifEmpty { "Field observation deemed sufficient" }}"
        )
        caseService.saveCase(updated)
        return updated
    }
}
