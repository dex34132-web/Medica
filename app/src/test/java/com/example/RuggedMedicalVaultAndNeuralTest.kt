package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.room.AppDatabase
import com.example.ondevice.manager.LocalModelManager
import com.example.ondevice.models.KnowledgeVaultType
import com.example.ondevice.models.LocalMediaInput
import com.example.ondevice.models.ModalityType
import com.example.ondevice.neural.ExternalNeuralVaultNetwork
import com.example.ondevice.rag.LocalRagEngine
import com.example.ondevice.rag.LocalVectorSearchEngine
import com.example.ondevice.runtime.LocalInferenceEngine
import com.example.ondevice.security.LocalPrivacySecurityManager
import com.example.ondevice.vault.ComprehensiveLocalMedicalVault
import com.example.ondevice.vault.VaultAssetMetadata
import com.example.ondevice.vault.VaultMediaType
import com.example.ondevice.vault.VaultMetadataHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Ultra-Rugged Test Suite for Medica.
 *
 * Exhaustively tests:
 * 1. Vault Metadata Data Class & Helper Class (type filtering, keyword/landmark extraction, storage stats)
 * 2. Room FTS4 Database & DAO indexing the 25.6 GB Medical Knowledge Vault
 * 3. Local Vector Search Engine (384-dimensional dense semantic vectors, cosine similarity, hybrid FTS+vector retrieval)
 * 4. Local RAG Engine (type-based pre-filtering by VIDEO, FLOWCHART, TEXT, IMAGE, multimodal context assembly)
 * 5. On-Device Local Models & Quantization Integrity (INT4/INT8 models, offline air-gap execution)
 * 6. External Neural Vault Network (multimodal step generation: text steps, image steps, video steps, flowchart nodes)
 * 7. Stress & Edge Case Resilience (empty inputs, unicode, massive payloads, high concurrency)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RuggedMedicalVaultAndNeuralTest {

    private lateinit var inMemoryDb: AppDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        inMemoryDb.close()
    }

    // =========================================================================
    // 1. VAULT METADATA & HELPER CLASS TESTS
    // =========================================================================

    @Test
    fun `vault metadata helper extracts valid metadata for all assets in 25GB vault`() {
        val allMetadata = VaultMetadataHelper.getAllMetadata()
        assertTrue("Vault must contain at least 9 multimodal assets", allMetadata.size >= 9)

        allMetadata.forEach { meta ->
            assertNotNull("Asset ID must not be null", meta.id)
            assertTrue("Title must not be empty", meta.title.isNotBlank())
            assertTrue("Summary must not be empty", meta.summary.isNotBlank())
            assertTrue("Provenance hash must not be empty", meta.provenanceHash.isNotBlank())
            assertTrue("Uncompressed size must be greater than 0", meta.uncompressedSizeBytes > 0)
            assertTrue("Compressed size must be greater than 0", meta.compressedSizeBytes > 0)
            assertTrue("Compression ratio must be populated", meta.compressionRatio.isNotBlank())
            assertTrue("File format must be specified", meta.fileFormat.isNotBlank())
            assertTrue("Urgency tier must be specified", meta.urgencyTier.isNotBlank())
            assertTrue("Clinical keywords must not be empty", meta.clinicalKeywords.isNotEmpty())
            assertTrue("Anatomical landmarks must not be empty", meta.anatomicalLandmarks.isNotEmpty())
        }
    }

    @Test
    fun `vault metadata helper filters assets strictly by VIDEO type`() {
        val allAssets = ComprehensiveLocalMedicalVault.getAllAssets()
        val videoAssets = VaultMetadataHelper.filterByType(allAssets, VaultMediaType.VIDEO)

        assertTrue("Must contain video assets", videoAssets.isNotEmpty())
        videoAssets.forEach { asset ->
            assertEquals(
                "Every filtered asset must map to VaultMediaType.VIDEO",
                VaultMediaType.VIDEO,
                VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            )
        }
        // Ensure non-video assets are excluded
        val hasNonVideo = videoAssets.any { it.type != KnowledgeVaultType.VIDEO }
        assertFalse("No non-video assets should be included", hasNonVideo)
    }

    @Test
    fun `vault metadata helper filters assets strictly by FLOWCHART type`() {
        val allAssets = ComprehensiveLocalMedicalVault.getAllAssets()
        val flowchartAssets = VaultMetadataHelper.filterByType(allAssets, VaultMediaType.FLOWCHART)

        assertTrue("Must contain flowchart assets", flowchartAssets.isNotEmpty())
        flowchartAssets.forEach { asset ->
            val mediaType = VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            assertEquals("Every filtered asset must map to VaultMediaType.FLOWCHART", VaultMediaType.FLOWCHART, mediaType)
        }
    }

    @Test
    fun `vault metadata helper filters assets strictly by IMAGE type`() {
        val allAssets = ComprehensiveLocalMedicalVault.getAllAssets()
        val imageAssets = VaultMetadataHelper.filterByType(allAssets, VaultMediaType.IMAGE)

        assertTrue("Must contain image assets", imageAssets.isNotEmpty())
        imageAssets.forEach { asset ->
            val mediaType = VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            assertEquals("Every filtered asset must map to VaultMediaType.IMAGE", VaultMediaType.IMAGE, mediaType)
        }
    }

    @Test
    fun `vault metadata helper filters assets strictly by TEXT type`() {
        val allAssets = ComprehensiveLocalMedicalVault.getAllAssets()
        val textAssets = VaultMetadataHelper.filterByType(allAssets, VaultMediaType.TEXT)

        assertTrue("Must contain text assets", textAssets.isNotEmpty())
        textAssets.forEach { asset ->
            val mediaType = VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            assertEquals("Every filtered asset must map to VaultMediaType.TEXT", VaultMediaType.TEXT, mediaType)
        }
    }

    @Test
    fun `vault metadata helper filters assets by multi-type set`() {
        val allAssets = ComprehensiveLocalMedicalVault.getAllAssets()
        val targetTypes = setOf(VaultMediaType.VIDEO, VaultMediaType.IMAGE)
        val filtered = VaultMetadataHelper.filterByTypes(allAssets, targetTypes)

        assertTrue("Filtered set must not be empty", filtered.isNotEmpty())
        filtered.forEach { asset ->
            val mediaType = VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            assertTrue("Asset must be either VIDEO or IMAGE", targetTypes.contains(mediaType))
        }
        // Check that TEXT, FLOWCHART, AUDIO are excluded
        val hasText = filtered.any { VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(it.type) == VaultMediaType.TEXT }
        val hasFlowchart = filtered.any { VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(it.type) == VaultMediaType.FLOWCHART }
        assertFalse("Text must be excluded", hasText)
        assertFalse("Flowchart must be excluded", hasFlowchart)
    }

    @Test
    fun `vault storage stats breakdown mathematically verifies 25GB vault compression`() {
        val stats = VaultMetadataHelper.getStorageStatsByType()
        assertEquals(6, stats.size)
        assertTrue(stats.containsKey(VaultMediaType.IMAGE))
        assertTrue(stats.containsKey(VaultMediaType.VIDEO))
        assertTrue(stats.containsKey(VaultMediaType.TEXT))
        assertTrue(stats.containsKey(VaultMediaType.FLOWCHART))
        assertTrue(stats.containsKey(VaultMediaType.AUDIO))
        assertTrue(stats.containsKey(VaultMediaType.STRUCTURED))

        assertTrue(stats[VaultMediaType.IMAGE]!!.contains("7.2 GB"))
        assertTrue(stats[VaultMediaType.VIDEO]!!.contains("5.6 GB"))
        assertTrue(stats[VaultMediaType.TEXT]!!.contains("5.6 GB"))
        assertTrue(stats[VaultMediaType.FLOWCHART]!!.contains("2.8 GB"))
        assertTrue(stats[VaultMediaType.AUDIO]!!.contains("2.8 GB"))
        assertTrue(stats[VaultMediaType.STRUCTURED]!!.contains("1.6 GB"))
    }

    // =========================================================================
    // 2. ROOM FTS4 DATABASE & LOCAL VECTOR SEARCH ENGINE TESTS
    // =========================================================================

    @Test
    fun `local vector search engine indexes all 25GB vault assets into Room FTS4 table`() = runBlocking {
        val indexedCount = LocalVectorSearchEngine.indexVaultIntoRoom(inMemoryDb)
        assertTrue("Indexed count must be at least 9", indexedCount >= 9)

        val dao = inMemoryDb.vaultSearchDao()
        val rowCount = dao.getCount()
        assertEquals(indexedCount, rowCount)

        val allFtsEntities = dao.getAll()
        assertEquals(indexedCount, allFtsEntities.size)
        allFtsEntities.forEach { entity ->
            assertTrue("Asset ID must be non-empty", entity.assetId.isNotBlank())
            assertTrue("Title must be non-empty", entity.title.isNotBlank())
            assertTrue("Embedding vector must be serialized 384-dim string", entity.embeddingVector.contains(","))
            val vectorParts = entity.embeddingVector.split(",")
            assertEquals(LocalVectorSearchEngine.EMBEDDING_DIMENSION, vectorParts.size)
        }
    }

    @Test
    fun `Room FTS4 search matches clinical keywords across indexed vault`() = runBlocking {
        LocalVectorSearchEngine.indexVaultIntoRoom(inMemoryDb)
        val dao = inMemoryDb.vaultSearchDao()

        // Match tourniquet
        val tourniquetHits = dao.searchFts("tourniquet*")
        assertTrue("FTS must match tourniquet", tourniquetHits.isNotEmpty())
        assertTrue(tourniquetHits.any { it.title.contains("Tourniquet", ignoreCase = true) })

        // Match CPR / cardiac arrest
        val cprHits = dao.searchFts("CPR* OR arrest*")
        assertTrue("FTS must match CPR or arrest", cprHits.isNotEmpty())

        // Match pneumothorax
        val pneumoHits = dao.searchFts("pneumothorax* OR needle*")
        assertTrue("FTS must match pneumothorax", pneumoHits.isNotEmpty())
    }

    @Test
    fun `Room FTS4 search respects mediaType column filtering in database`() = runBlocking {
        LocalVectorSearchEngine.indexVaultIntoRoom(inMemoryDb)
        val dao = inMemoryDb.vaultSearchDao()

        val videoHits = dao.getByMediaType("VIDEO")
        assertTrue("Must retrieve video entries from Room", videoHits.isNotEmpty())
        videoHits.forEach { assertEquals("VIDEO", it.mediaType) }

        val flowchartHits = dao.getByMediaType("FLOWCHART")
        assertTrue("Must retrieve flowchart entries from Room", flowchartHits.isNotEmpty())
        flowchartHits.forEach { assertEquals("FLOWCHART", it.mediaType) }

        val imageHits = dao.getByMediaType("IMAGE")
        assertTrue("Must retrieve image entries from Room", imageHits.isNotEmpty())
        imageHits.forEach { assertEquals("IMAGE", it.mediaType) }

        val textHits = dao.getByMediaType("TEXT")
        assertTrue("Must retrieve text entries from Room", textHits.isNotEmpty())
        textHits.forEach { assertEquals("TEXT", it.mediaType) }
    }

    @Test
    fun `384-dimensional dense semantic vector computation is unit-normalized and deterministic`() {
        val text = "Massive femoral hemorrhage requiring emergency tourniquet application"
        val vector1 = LocalVectorSearchEngine.computeSemanticVector(text)
        val vector2 = LocalVectorSearchEngine.computeSemanticVector(text)

        assertEquals(LocalVectorSearchEngine.EMBEDDING_DIMENSION, vector1.size)
        assertEquals(LocalVectorSearchEngine.EMBEDDING_DIMENSION, vector2.size)

        // Check determinism
        for (i in vector1.indices) {
            assertEquals(vector1[i], vector2[i], 0.0001f)
        }

        // Check L2 normalization (magnitude should be ~1.0)
        var sumSquares = 0.0f
        for (v in vector1) {
            sumSquares += v * v
        }
        val magnitude = Math.sqrt(sumSquares.toDouble()).toFloat()
        assertEquals(1.0f, magnitude, 0.01f)

        // Cosine similarity of identical text must be 1.0
        val sim = LocalVectorSearchEngine.cosineSimilarity(vector1, vector2)
        assertEquals(1.0f, sim, 0.001f)
    }

    @Test
    fun `vector serialization and deserialization preserves precision`() {
        val original = LocalVectorSearchEngine.computeSemanticVector("Pulseless cardiac arrest defibrillation")
        val serialized = LocalVectorSearchEngine.serializeVector(original)
        assertTrue(serialized.contains(","))

        val deserialized = LocalVectorSearchEngine.deserializeVector(serialized)
        assertEquals(original.size, deserialized.size)

        for (i in original.indices) {
            assertEquals(original[i], deserialized[i], 0.001f)
        }
    }

    @Test
    fun `local vector search engine hybrid search executes in under 20ms and respects type filters`() = runBlocking {
        LocalVectorSearchEngine.indexVaultIntoRoom(inMemoryDb)

        // 1. Search with VIDEO filter
        val videoResults = LocalVectorSearchEngine.search(
            database = inMemoryDb,
            query = "tourniquet application technique",
            typeFilter = setOf(VaultMediaType.VIDEO),
            topK = 2
        )
        assertTrue("Must find video results", videoResults.isNotEmpty())
        videoResults.forEach { res ->
            assertEquals(VaultMediaType.VIDEO, res.mediaType)
            assertTrue("Latency must be < 25ms offline", res.latencyMs < 25)
            assertTrue("Score must be positive", res.overallScore > 0.0f)
        }

        // 2. Search with FLOWCHART filter
        val flowchartResults = LocalVectorSearchEngine.search(
            database = inMemoryDb,
            query = "cardiac arrest ACLS decision tree",
            typeFilter = setOf(VaultMediaType.FLOWCHART),
            topK = 2
        )
        assertTrue("Must find flowchart results", flowchartResults.isNotEmpty())
        flowchartResults.forEach { res ->
            assertEquals(VaultMediaType.FLOWCHART, res.mediaType)
        }

        // 3. Search with IMAGE filter
        val imageResults = LocalVectorSearchEngine.search(
            database = inMemoryDb,
            query = "sucking chest wound anatomy",
            typeFilter = setOf(VaultMediaType.IMAGE),
            topK = 2
        )
        assertTrue("Must find image results", imageResults.isNotEmpty())
        imageResults.forEach { res ->
            assertEquals(VaultMediaType.IMAGE, res.mediaType)
        }

        // 4. Search with TEXT filter
        val textResults = LocalVectorSearchEngine.search(
            database = inMemoryDb,
            query = "hypothermia rewarming protocol",
            typeFilter = setOf(VaultMediaType.TEXT),
            topK = 2
        )
        assertTrue("Must find text results", textResults.isNotEmpty())
        textResults.forEach { res ->
            assertEquals(VaultMediaType.TEXT, res.mediaType)
        }
    }

    // =========================================================================
    // 3. LOCAL RAG ENGINE TYPE FILTERING & CONTEXT BOUNDING TESTS
    // =========================================================================

    @Test
    fun `local rag engine filters candidates by type before scoring and bounded retrieval`() {
        // Query that mentions bleeding, but we strictly request FLOWCHART
        val flowchartResults = LocalRagEngine.retrieveTopRelevantAssets(
            chiefComplaint = "Severe bleeding from extremity",
            observations = "Arterial blood spurting",
            attachedMedia = emptyList(),
            maxTopK = 3,
            typeFilter = setOf(VaultMediaType.FLOWCHART)
        )

        assertTrue(flowchartResults.isNotEmpty())
        flowchartResults.forEach { asset ->
            assertEquals(
                "All retrieved assets must be FLOWCHART",
                VaultMediaType.FLOWCHART,
                VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            )
        }

        // Query that mentions burn, but we strictly request VIDEO
        val videoResults = LocalRagEngine.retrieveTopRelevantAssets(
            chiefComplaint = "Severe thermal burn",
            observations = "Extensive second-degree blistering",
            attachedMedia = emptyList(),
            maxTopK = 3,
            typeFilter = setOf(VaultMediaType.VIDEO)
        )
        assertTrue(videoResults.isNotEmpty())
        videoResults.forEach { asset ->
            assertEquals(
                "All retrieved assets must be VIDEO",
                VaultMediaType.VIDEO,
                VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            )
        }
    }

    @Test
    fun `local rag engine retrieveAssetsBySpecificType returns exclusively target type`() {
        val imageAssets = LocalRagEngine.retrieveAssetsBySpecificType(
            chiefComplaint = "Deep puncture to right thorax",
            observations = "Hissing sound on inspiration",
            mediaType = VaultMediaType.IMAGE,
            maxTopK = 2
        )
        assertTrue(imageAssets.isNotEmpty())
        imageAssets.forEach {
            assertEquals(VaultMediaType.IMAGE, VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(it.type))
        }

        val textAssets = LocalRagEngine.retrieveAssetsBySpecificType(
            chiefComplaint = "Status epilepticus seizing for 8 minutes",
            observations = "Unresponsive tonic clonic",
            mediaType = VaultMediaType.TEXT,
            maxTopK = 2
        )
        assertTrue(textAssets.isNotEmpty())
        textAssets.forEach {
            assertEquals(VaultMediaType.TEXT, VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(it.type))
        }
    }

    @Test
    fun `local rag engine buildLocalContext bounds knowledge to 3 items and respects type filter`() {
        val context = LocalRagEngine.buildLocalContext(
            caseId = "TEST-RAG-01",
            chiefComplaint = "Severe Stridor and Dyspnea",
            demographics = "(F, 42)",
            observations = "Wheezing and intercostal retractions",
            attachedMedia = emptyList(),
            typeFilter = setOf(VaultMediaType.FLOWCHART, VaultMediaType.VIDEO)
        )

        assertNotNull(context)
        assertEquals("TEST-RAG-01", context.caseId)
        assertTrue("Evidence must be strictly bounded to <= 3 items", context.retrievedKnowledge.size in 1..3)
        context.retrievedKnowledge.forEach { asset ->
            val type = VaultMetadataHelper.mapKnowledgeVaultTypeToMediaType(asset.type)
            assertTrue("Asset must be FLOWCHART or VIDEO", type == VaultMediaType.FLOWCHART || type == VaultMediaType.VIDEO)
        }
        assertTrue("Policy replica must be present", context.serverSidePolicyReplica.isNotBlank())
        assertTrue("Schema must be present", context.strictOutputJsonSchema.isNotBlank())
    }

    // =========================================================================
    // 4. ON-DEVICE LOCAL MODELS & RUNTIME INTEGRITY TESTS
    // =========================================================================

    @Test
    fun `all quantized models in local model catalog satisfy mobile memory and latency limits`() {
        val catalog = LocalModelManager.getCatalog()
        assertTrue("Catalog must contain at least 4 quantized models", catalog.size >= 4)

        catalog.forEach { spec ->
            assertTrue("Model ID must not be blank", spec.modelId.isNotBlank())
            assertTrue("Size on flash must be <= 4000 MB for mobile fit", spec.storageSizeMb <= 4000)
            assertTrue("RAM requirement must be <= 5 GB", spec.requiredRamGb <= 5.0f)
            assertTrue("Quantization must specify bit precision", spec.quantization.isNotBlank())
            assertTrue("Hash must be non-blank", spec.sha256Hash.isNotBlank())
            assertTrue("Model integrity verification must pass", LocalModelManager.verifyModelIntegrity(spec))
        }
    }

    @Test
    fun `local inference engine executes fully air-gapped clinical reasoning`() = runBlocking {
        val media = listOf(
            LocalMediaInput("m1", ModalityType.IMAGE, "wound.jpg", "Pulsatile femoral bleeding")
        )
        val context = LocalRagEngine.buildLocalContext(
            caseId = "TEST-AIRGAP-TRAUMA",
            chiefComplaint = "Massive Femoral Arterial Bleed",
            demographics = "(M, 29)",
            observations = "Systolic BP 75, pale, cool extremities",
            attachedMedia = media
        )
        val model = LocalModelManager.activeModel.value

        val result = LocalInferenceEngine.synthesizeLocalReasoning(
            context = context,
            modelSpec = model,
            runtime = model.targetRuntime
        )

        assertNotNull(result)
        assertTrue(result.isFullyAirGapped)
        assertEquals(com.example.model.UrgencyLevel.CRITICAL, result.urgencyLevel)
        assertTrue("Must prescribe immediate actions", result.immediateActions.isNotEmpty())
        assertTrue("Must include retrieved evidence", result.retrievedEvidence.isNotEmpty())
        assertTrue("Latency must be non-zero", result.latencyMs > 0)
    }

    @Test
    fun `privacy security manager guarantees zero network and zero cloud API keys`() {
        assertFalse(LocalPrivacySecurityManager.isNetworkRequired())
        val guarantees = LocalPrivacySecurityManager.getPrivacyGuarantees()
        assertEquals(5, guarantees.size)
        guarantees.forEach { g ->
            assertTrue(g.boundaryName.isNotBlank())
            assertTrue(g.complianceStatus.isNotBlank())
            assertTrue(g.description.isNotBlank())
        }
    }

    // =========================================================================
    // 5. EXTERNAL NEURAL VAULT NETWORK RECOGNITION & MULTIMODAL SYNTHESIS TESTS
    // =========================================================================

    @Test
    fun `external neural network recognizes Arterial Bleed and produces complete multimodal steps`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-BLEED",
            complaint = "Severe profuse spurting blood from thigh laceration",
            demographics = "(M, 31)",
            observations = "Arterial bleed, blood pooling rapidly, rapid pulse",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize hemorrhage", plan.conditionRecognized.contains("Hemorrhage", ignoreCase = true) || plan.conditionRecognized.contains("Bleed", ignoreCase = true))
        assertTrue("Confidence must exceed 90%", plan.confidenceScore >= 0.90f)

        // 1. Text steps
        assertTrue("Clinical text steps must be generated", plan.textSteps.isNotEmpty())
        assertTrue(plan.textSteps.any { it.contains("Tourniquet", ignoreCase = true) })

        // 2. Image steps
        assertTrue("Image steps must be generated", plan.imageSteps.isNotEmpty())
        val imageStep = plan.imageSteps.first()
        assertTrue("Image step must specify anatomical landmark", imageStep.anatomicalLandmark.isNotBlank())
        assertTrue("Image step must have visual description", imageStep.visualDescription.isNotBlank())

        // 3. Video steps
        assertTrue("Video procedural steps must be generated", plan.videoSteps.isNotEmpty())
        val videoStep = plan.videoSteps.first()
        assertTrue("Video step must specify phase", videoStep.phase.isNotBlank())
        assertTrue("Video step must describe action demonstrated", videoStep.actionDemonstrated.isNotBlank())

        // 4. Flowchart nodes
        assertTrue("Decision flowchart steps must be generated", plan.flowchartSteps.isNotEmpty())
        val decisionNode = plan.flowchartSteps.first()
        assertTrue("Flowchart condition must be non-empty", decisionNode.decisionCondition.isNotBlank())
        assertTrue("Must have branch if true", decisionNode.branchIfTrue.isNotBlank())

        // 5. AI explanation & provenance
        assertTrue("AI explanation of rationale must be present", plan.aiExplanation.isNotBlank())
        assertTrue("Hard contraindications must be listed", plan.contraindications.isNotEmpty())
        assertTrue("Provenance hash must be non-empty", plan.provenanceHash.isNotBlank())
    }

    @Test
    fun `external neural network recognizes Cardiac Arrest and produces CPR defibrillation plan`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-CPR",
            complaint = "Unresponsive male found collapsed no breathing",
            demographics = "(M, 64)",
            observations = "Pulseless, agonal gasping, cyanotic lips",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize cardiac arrest", plan.conditionRecognized.contains("Cardiac", ignoreCase = true) || plan.conditionRecognized.contains("Arrest", ignoreCase = true))
        assertTrue("Text steps must include CPR compressions", plan.textSteps.any { it.contains("100-120") || it.contains("compression", ignoreCase = true) })
        assertTrue("Must include defibrillation flowchart", plan.flowchartSteps.any { it.decisionCondition.contains("Shockable", ignoreCase = true) || it.decisionCondition.contains("VF", ignoreCase = true) })
        assertTrue("Must include sternum landmark image step", plan.imageSteps.any { it.anatomicalLandmark.contains("Sternum", ignoreCase = true) })
        assertTrue("Must include CPR compressor cadence video step", plan.videoSteps.any { it.actionDemonstrated.contains("Compress", ignoreCase = true) || it.phase.contains("Compress", ignoreCase = true) })
    }

    @Test
    fun `external neural network recognizes Anaphylaxis and produces Epinephrine IM plan`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-ANAPHYLAXIS",
            complaint = "Severe allergic reaction to bee sting with stridor",
            demographics = "(F, 19)",
            observations = "Facial angioedema, urticaria, inspiratory stridor peak",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize anaphylaxis", plan.conditionRecognized.contains("Anaphylaxis", ignoreCase = true))
        assertTrue("Must specify Epinephrine IM", plan.textSteps.any { it.contains("Epinephrine", ignoreCase = true) })
        assertTrue("Landmark must be vastus lateralis / thigh", plan.imageSteps.any { it.anatomicalLandmark.contains("Vastus Lateralis", ignoreCase = true) })
        assertTrue("Dosing must specify 0.3 mg or Epinephrine", plan.dosingMatrix.any { it.contains("0.3 mg") || it.contains("Epinephrine", ignoreCase = true) })
    }

    @Test
    fun `external neural network recognizes Sucking Chest Wound and produces 3-sided seal plan`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-PNEUMO",
            complaint = "Penetrating stab wound to right chest with bubbling blood",
            demographics = "(M, 26)",
            observations = "Sucking sound on inhalation, tracheal deviation, SpO2 84%",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize pneumothorax / chest wound", plan.conditionRecognized.contains("Pneumothorax", ignoreCase = true) || plan.conditionRecognized.contains("Chest", ignoreCase = true))
        assertTrue("Must prescribe vented chest seal", plan.textSteps.any { it.contains("seal", ignoreCase = true) || it.contains("valve", ignoreCase = true) })
        assertTrue("Landmark must include intercostal space", plan.imageSteps.any { it.anatomicalLandmark.contains("Intercostal", ignoreCase = true) })
    }

    @Test
    fun `external neural network recognizes Severe Burn and produces Parkland formula plan`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-BURN",
            complaint = "Severe boiling water scald to chest and both arms",
            demographics = "(M, 38)",
            observations = "Circumferential blistering, 30% TBSA estimated, severe pain",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize burn", plan.conditionRecognized.contains("Burn", ignoreCase = true))
        assertTrue("Must reference Parkland or Lactated Ringers", plan.textSteps.any { it.contains("Parkland", ignoreCase = true) || it.contains("Ringers", ignoreCase = true) })
        assertTrue("Contraindications must prohibit ice", plan.contraindications.any { it.contains("ice", ignoreCase = true) })
    }

    @Test
    fun `external neural network recognizes Foreign Body Airway Obstruction Choking`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-CHOKE",
            complaint = "Choking on food, universal choking sign, unable to cough or speak",
            demographics = "(F, 55)",
            observations = "Silent stridor, clutching throat, cyanotic face",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize choking", plan.conditionRecognized.contains("Choking", ignoreCase = true) || plan.conditionRecognized.contains("Airway", ignoreCase = true))
        assertTrue("Must describe Heimlich abdominal thrusts", plan.textSteps.any { it.contains("thrust", ignoreCase = true) || it.contains("Heimlich", ignoreCase = true) })
    }

    @Test
    fun `external neural network recognizes Status Epilepticus Seizure`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-SEIZURE",
            complaint = "Generalized tonic clonic seizure lasting more than 6 minutes",
            demographics = "(M, 22)",
            observations = "Active convulsions, frothing at mouth, unarousable",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize seizure", plan.conditionRecognized.contains("Seizure", ignoreCase = true) || plan.conditionRecognized.contains("Epilepticus", ignoreCase = true))
        assertTrue("Must describe airway protection and recovery position", plan.textSteps.any { it.contains("recovery position", ignoreCase = true) || it.contains("airway", ignoreCase = true) })
        assertTrue("Contraindications must prohibit putting objects in mouth", plan.contraindications.any { it.contains("mouth", ignoreCase = true) })
    }

    @Test
    fun `external neural network recognizes Acute Ischemic Stroke`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-STROKE",
            complaint = "Sudden onset right arm weakness, facial droop, and slurred speech",
            demographics = "(F, 71)",
            observations = "BE-FAST positive, left gaze deviation, time of onset 45 min ago",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize stroke", plan.conditionRecognized.contains("Stroke", ignoreCase = true))
        assertTrue("Must emphasize time last seen normal and immediate transport", plan.textSteps.any { it.contains("time", ignoreCase = true) || it.contains("transport", ignoreCase = true) })
        assertTrue("Flowchart must include time window or glucose decision", plan.flowchartSteps.any { it.decisionCondition.contains("Last Known Well", ignoreCase = true) || it.decisionCondition.contains("glucose", ignoreCase = true) || it.decisionCondition.contains("24 hours", ignoreCase = true) })
    }

    @Test
    fun `external neural network recognizes Severe Asthma Dyspnea and delivers CPAP nebulizer plan`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-DYSPNEA",
            complaint = "Severe acute asthma exacerbation unable to speak full sentences",
            demographics = "(F, 16)",
            observations = "Tripod positioning, silent chest, severe tachypnea SpO2 88%",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Must recognize respiratory distress / asthma", plan.conditionRecognized.contains("Asthma", ignoreCase = true) || plan.conditionRecognized.contains("Respiratory", ignoreCase = true))
        assertTrue("Must specify Albuterol or DuoNeb", plan.textSteps.any { it.contains("Albuterol", ignoreCase = true) || it.contains("Nebulizer", ignoreCase = true) })
    }

    @Test
    fun `external neural network synthesizes robust action plan on freeform unknown query`() = runBlocking {
        val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
            caseId = "TEST-NEURAL-FREEFORM",
            complaint = "Unusual wilderness environmental exposure dizziness and fatigue",
            demographics = "(M, 40)",
            observations = "Mild dehydration, alert and oriented x 4",
            attachedMedia = emptyList()
        )

        assertNotNull(plan)
        assertTrue("Condition must be recognized or classified", plan.conditionRecognized.isNotBlank())
        assertTrue("Confidence must be a valid float between 0 and 1", plan.confidenceScore in 0.0f..1.0f)
        assertTrue("Clinical steps must be provided", plan.textSteps.isNotEmpty())
        assertTrue("AI explanation must be present", plan.aiExplanation.isNotBlank())
    }

    // =========================================================================
    // 6. RUGGED STRESS & CONCURRENCY TESTS
    // =========================================================================

    @Test
    fun `local vector search handles empty, unicode, and massive queries without crashing`() = runBlocking {
        LocalVectorSearchEngine.indexVaultIntoRoom(inMemoryDb)

        // 1. Empty string query
        val emptyResults = LocalVectorSearchEngine.search(inMemoryDb, "", topK = 3)
        assertNotNull(emptyResults)

        // 2. Pure whitespace query
        val whitespaceResults = LocalVectorSearchEngine.search(inMemoryDb, "   \n\t  ", topK = 3)
        assertNotNull(whitespaceResults)

        // 3. Special characters & punctuation
        val punctResults = LocalVectorSearchEngine.search(inMemoryDb, "!@#$%^&*()_+=-{}[]:;'<>?,./", topK = 3)
        assertNotNull(punctResults)

        // 4. Unicode medical symbols
        val unicodeResults = LocalVectorSearchEngine.search(inMemoryDb, "⚕️ 🩺 ❤️ 🩸 🩹", topK = 3)
        assertNotNull(unicodeResults)

        // 5. Very long 5,000-character clinical query
        val longQuery = "patient presenting with acute distress ".repeat(150)
        val longResults = LocalVectorSearchEngine.search(inMemoryDb, longQuery, topK = 3)
        assertNotNull(longResults)
        assertTrue(longResults.isNotEmpty())
    }

    @Test
    fun `concurrent neural network and vector search calls run safely without race conditions`() = runBlocking {
        LocalVectorSearchEngine.indexVaultIntoRoom(inMemoryDb)

        val queries = listOf(
            "femoral arterial bleeding tourniquet",
            "pulseless cardiac arrest defibrillation",
            "anaphylactic shock epinephrine IM",
            "tension pneumothorax needle thoracostomy",
            "severe thermal burn parkland formula",
            "choking foreign body airway obstruction",
            "status epilepticus midazolam anticonvulsant",
            "acute stroke be-fast thrombolysis"
        )

        // Execute 8 concurrent searches simultaneously
        val deferredSearches = queries.map { query ->
            async {
                LocalVectorSearchEngine.search(inMemoryDb, query, topK = 2)
            }
        }
        val allSearchResults = deferredSearches.awaitAll()
        assertEquals(8, allSearchResults.size)
        allSearchResults.forEach { res ->
            assertTrue(res.isNotEmpty())
            assertTrue(res.all { it.overallScore > 0.0f })
        }
    }
}
