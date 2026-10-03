package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MockDemoCases
import com.example.data.MockKnowledgeRepository
import com.example.data.room.CaseJsonHelper
import com.example.model.Case
import com.example.model.CaseMedia
import com.example.model.CaseStatus
import com.example.model.EscalationStatus
import com.example.model.KnowledgeAsset
import com.example.model.KnowledgeType
import com.example.model.MediaType
import com.example.model.MedicaResult
import com.example.model.UrgencyLevel
import com.example.service.MockAIService
import com.example.service.MockKnowledgeService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies Medica app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Medica", appName)
    }

    @Test
    fun `frontend data architecture models instantiate correctly`() {
        val media = CaseMedia(
            id = "MED-01",
            type = MediaType.IMAGE,
            filename = "stridor.jpg",
            thumbnail = "thumb_01",
            duration = "00:15",
            size = "2.1 MB",
            local = true,
            status = "READY"
        )
        assertEquals("MED-01", media.id)
        assertEquals(MediaType.IMAGE, media.type)
        assertEquals("stridor.jpg", media.filename)
        assertTrue(media.local)

        val result = MedicaResult(
            caseId = "MDC-100",
            urgencyLevel = UrgencyLevel.URGENT,
            triageCode = "YELLOW - DELAYED",
            observations = listOf("Stridor observed"),
            possibleConditions = listOf("Foreign Body"),
            immediateActions = emptyList(),
            warnings = listOf("Monitor airway"),
            uncertainty = "Confidence: 85%",
            confidence = 0.85f,
            evidence = listOf("VAULT-PR-01"),
            escalationRecommended = true
        )
        assertEquals("MDC-100", result.caseId)
        assertEquals(UrgencyLevel.URGENT, result.urgencyLevel)
        assertEquals("VAULT-PR-01", result.evidence.first())

        val case = Case(
            id = "MDC-100",
            createdAt = 1700000000L,
            createdAtFormatted = "Today, 10:00",
            media = listOf(media),
            context = "Responder observations",
            status = CaseStatus.COMPLETED,
            result = result,
            evidence = listOf("VAULT-PR-01"),
            escalationStatus = EscalationStatus.RECOMMENDED
        )
        assertEquals("MDC-100", case.id)
        assertEquals(1, case.media.size)
        assertEquals(EscalationStatus.RECOMMENDED, case.escalationStatus)

        val asset = KnowledgeAsset(
            id = "VAULT-PR-01",
            title = "Airway Protocol",
            type = KnowledgeType.PROTOCOL,
            category = "Airway & Breathing",
            source = "Field Protocol 2026",
            version = "3.2",
            date = "2026-01-01",
            description = "Emergency Airway Assessment Guide",
            tags = listOf("Airway", "Stridor"),
            local = true,
            thumbnail = "thumb_pr01",
            content = "Clinical protocol content",
            relatedAssets = listOf("VAULT-VD-01")
        )
        assertEquals("VAULT-PR-01", asset.id)
        assertTrue(asset.local)
        assertEquals("VAULT-VD-01", asset.relatedAssets.first())
    }

    @Test
    fun `knowledge repository contains 20+ offline medical assets across all types`() {
        val assets = MockKnowledgeRepository.assets
        assertTrue("Expected 20+ assets but found ${assets.size}", assets.size >= 20)

        val types = assets.map { it.type }.toSet()
        assertTrue("Flowcharts missing", types.contains(KnowledgeType.FLOWCHART))
        assertTrue("Protocols missing", types.contains(KnowledgeType.PROTOCOL))
        assertTrue("Diagrams missing", types.contains(KnowledgeType.DIAGRAM))
        assertTrue("Videos missing", types.contains(KnowledgeType.VIDEO))
        assertTrue("Audio missing", types.contains(KnowledgeType.AUDIO))
        assertTrue("Documents missing", types.contains(KnowledgeType.DOCUMENT))
        assertTrue("Images missing", types.contains(KnowledgeType.IMAGE))

        // All assets must be marked local
        assertTrue(assets.all { it.local })
    }

    @Test
    fun `knowledge service supports searching and filtering by type`() {
        val service = MockKnowledgeService()

        // Filter by Protocols
        val protocols = service.searchAssets("", KnowledgeType.PROTOCOL)
        assertTrue(protocols.isNotEmpty())
        assertTrue(protocols.all { it.type == KnowledgeType.PROTOCOL })

        // Filter by Documents
        val docs = service.searchAssets("", KnowledgeType.DOCUMENT)
        assertTrue(docs.isNotEmpty())
        assertTrue(docs.all { it.type == KnowledgeType.DOCUMENT })

        // Filter by Videos
        val videos = service.searchAssets("", KnowledgeType.VIDEO)
        assertTrue(videos.isNotEmpty())
        assertTrue(videos.all { it.type == KnowledgeType.VIDEO })

        // Search query
        val searchStridor = service.searchAssets("stridor", null)
        assertTrue(searchStridor.isNotEmpty())
    }

    @Test
    fun `interactive flowchart model contains valid branching structure`() {
        val flowchartAsset = MockKnowledgeRepository.assets.find { it.type == KnowledgeType.FLOWCHART }
        assertNotNull("Flowchart asset should exist", flowchartAsset)
        val model = flowchartAsset?.flowchartData
        assertNotNull("FlowchartModel should not be null", model)

        val startNode = model?.nodes?.get(model.startNodeId)
        assertNotNull("Start node should exist", startNode)
        assertTrue(startNode?.yesNodeId != null)
        assertTrue(startNode?.noNodeId != null)
    }

    @Test
    fun `case json helper roundtrip preserves case data integrity`() {
        val sampleCase = MockDemoCases.initialCases.first()
        val entity = CaseJsonHelper.caseToEntity(sampleCase)
        val reconstructed = CaseJsonHelper.entityToCase(entity)

        assertEquals(sampleCase.id, reconstructed.id)
        assertEquals(sampleCase.title, reconstructed.title)
        assertEquals(sampleCase.media.size, reconstructed.media.size)
        assertEquals(sampleCase.result?.urgencyLevel, reconstructed.result?.urgencyLevel)
        assertEquals(sampleCase.result?.immediateActions?.size, reconstructed.result?.immediateActions?.size)
    }

    @Test
    fun `ai service generates valid decision support result`() = runBlocking {
        val aiService = MockAIService()
        val testCase = Case(
            id = "MDC-9999",
            title = "Test Airway Stridor",
            createdAtFormatted = "Today, 10:00",
            media = listOf(
                CaseMedia(
                    id = "M-1",
                    type = MediaType.AUDIO,
                    filename = "stridor.m4a",
                    size = "500 KB"
                )
            ),
            context = "High-pitched inspiratory sound, sternal retraction"
        )

        val result = aiService.generateResult(testCase)
        assertNotNull(result)
        assertEquals("MDC-9999", result.caseId)
        assertTrue(result.urgencyLevel == UrgencyLevel.URGENT || result.urgencyLevel == UrgencyLevel.CRITICAL)
        assertTrue(result.observations.isNotEmpty())
        assertTrue(result.immediateActions.isNotEmpty())
        assertTrue(result.warnings.isNotEmpty())
        assertTrue(result.evidence.isNotEmpty() || result.evidenceIds.isNotEmpty())
    }
}
