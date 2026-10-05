package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ondevice.hardware.DeviceCapabilityDetector
import com.example.ondevice.manager.LocalModelManager
import com.example.ondevice.models.DeviceCapability
import com.example.ondevice.models.KnowledgeVaultType
import com.example.ondevice.models.LocalKnowledgeAsset
import com.example.ondevice.models.LocalMediaInput
import com.example.ondevice.models.LocalMedicaResult
import com.example.ondevice.models.LocalModelSpec
import com.example.ondevice.models.ModalityType
import com.example.ondevice.models.MultimodalActionPlan
import com.example.ondevice.neural.ExternalNeuralVaultNetwork
import com.example.ondevice.rag.LocalRagEngine
import com.example.ondevice.runtime.LocalInferenceEngine
import com.example.ondevice.security.LocalPrivacySecurityManager
import com.example.ondevice.vault.ComprehensiveLocalMedicalVault
import com.example.ui.theme.MedicaAccentBlue
import com.example.ui.theme.MedicaBorderDark
import com.example.ui.theme.MedicaCardDark
import com.example.ui.theme.MedicaGreenDot
import com.example.ui.theme.MedicaRed
import com.example.ui.theme.MedicaTextPrimary
import com.example.ui.theme.MedicaTextSecondary
import kotlinx.coroutines.launch

/**
 * Production-ready On-Device AI Architecture Visualizer for Medica.
 *
 * Demonstrates how Medica runs AI entirely on the responder's phone with ZERO internet dependency:
 *
 * Phone Hardware
 *     ↓
 * Device Capability Detection
 *     ↓
 * Local AI Runtime (AICore, LiteRT, ONNX Mobile)
 *     ↓
 * On-device Multimodal Model (Text, Image, Audio, Video)
 *     ↓
 * Local Context Builder
 *     ↓
 * Local RAG Engine (Embedding Model, Vector Search, Metadata Filtering, Knowledge Retrieval)
 *     ↓
 * Local Medical Knowledge Vault (Protocols, Documents, Images, Diagrams, Flowcharts, Decision Trees, Videos, Audio, Structured Data)
 *     ↓
 * Evidence Assembly
 *     ↓
 * Local Medical AI Reasoning
 *     ↓
 * Structured Medica Result
 *
 * Central Message: "Medica can carry its AI and its medical knowledge with it."
 */
@Composable
fun OnDeviceArchitectureScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedSection by remember { mutableStateOf("LOCAL_PIPELINE") }
    var selectedDetailNode by remember { mutableStateOf<String?>(null) }

    // Live Hardware Capability State
    val deviceCapability = remember { DeviceCapabilityDetector.detectCapabilities(context) }
    val activeModel by LocalModelManager.activeModel.collectAsState()

    // Live On-Device Runner State
    var simulationResult by remember { mutableStateOf<LocalMedicaResult?>(null) }
    var isSimulating by remember { mutableStateOf(false) }

    // External Neural Network State
    var neuralNetQuery by remember { mutableStateOf("Trauma arterial bleed from leg with high-pressure spurting") }
    var neuralNetPlan by remember { mutableStateOf<MultimodalActionPlan?>(null) }
    var isNeuralNetExecuting by remember { mutableStateOf(false) }

    val sectionTabs = listOf(
        "LOCAL_PIPELINE" to "Local Pipeline",
        "EXTERNAL_NEURAL_NET" to "External Neural Net",
        "CAPABILITY_LAYER" to "Capability Layer",
        "MODEL_MANAGER" to "Model Manager",
        "LOCAL_RAG" to "Local RAG & Assembly",
        "KNOWLEDGE_VAULT" to "Medical Vault (9 Types)",
        "PRIVACY_BOUNDARIES" to "Privacy Boundaries",
        "ON_DEVICE_RUNNER" to "Local Runner"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("ondevice_arch_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MedicaTextPrimary
                )
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "On-Device AI Architecture",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MedicaGreenDot.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "AIR-GAPPED",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MedicaGreenDot
                        )
                    }
                }
                Text(
                    text = "Hardware Detection · Local RAG · Multimodal Model · Zero Cloud",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaTextSecondary
                )
            }
        }

        // ARCHITECTURE SECTION TABS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sectionTabs.forEach { (key, label) ->
                val isSelected = selectedSection == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSection = key },
                    label = {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MedicaAccentBlue,
                        selectedLabelColor = Color.White,
                        containerColor = MedicaCardDark,
                        labelColor = MedicaTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MedicaBorderDark,
                        selectedBorderColor = MedicaAccentBlue
                    ),
                    modifier = Modifier.testTag("ondevice_tab_$key")
                )
            }
        }

        HorizontalDivider(color = MedicaBorderDark, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Central Architectural Anthem Banner
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MedicaAccentBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    color = Color(0xFF131922)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MedicaAccentBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MedicaAccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CORE ARCHITECTURAL PRINCIPLE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicaAccentBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Medica can carry its AI and its medical knowledge with it.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MedicaTextPrimary
                            )
                            Text(
                                text = "100% of reasoning, vector search, and knowledge assets reside locally.",
                                fontSize = 11.sp,
                                color = MedicaTextSecondary
                            )
                        }
                    }
                }
            }

            when (selectedSection) {
                "LOCAL_PIPELINE" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "COMPLETE ON-DEVICE AI EXECUTION PIPELINE",
                            subtitle = "Hardware → Capability Detection → Runtime → Multimodal Model → Context Builder → Local RAG → Vault → Evidence → Reasoning → Result"
                        )
                    }

                    // 1. Phone Hardware
                    item {
                        PipelineNodeCard(
                            stepNumber = "1",
                            title = "Phone Hardware (Compute & Silicon)",
                            badge = "DEVICE TIER",
                            badgeColor = MedicaGreenDot,
                            icon = Icons.Default.DeveloperBoard,
                            summary = "System RAM (${deviceCapability.totalRamGb} GB), Storage (${deviceCapability.storageFreeGb} GB Free), Accelerator: ${deviceCapability.acceleratorType.label}",
                            details = listOf(
                                "Hardware: ${deviceCapability.acceleratorType.description}",
                                "Dynamic Memory Headroom: ${deviceCapability.availableRamGb} GB currently free for model weights",
                                "Tier Classification: ${deviceCapability.deviceTier.label}"
                            ),
                            isSelected = selectedDetailNode == "HW",
                            onClick = { selectedDetailNode = if (selectedDetailNode == "HW") null else "HW" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 2. Device Capability Detection
                    item {
                        PipelineNodeCard(
                            stepNumber = "2",
                            title = "Device Capability Detection Layer",
                            badge = "SILICON PROFILER",
                            badgeColor = MedicaAccentBlue,
                            icon = Icons.Default.Devices,
                            summary = "Profiles memory, thermal budget, instruction sets (NEON/Vulkan), and NPU delegates before allocating model tensors.",
                            details = listOf(
                                "Inspects ActivityManager memory pressure thresholds",
                                "Probes for Qualcomm Hexagon / Google Tensor NPU driver availability",
                                "Recommends optimal model variant: ${deviceCapability.recommendedModelId}"
                            ),
                            isSelected = selectedDetailNode == "CAP",
                            onClick = { selectedDetailNode = if (selectedDetailNode == "CAP") null else "CAP" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 3. Local AI Runtime
                    item {
                        PipelineNodeCard(
                            stepNumber = "3",
                            title = "Local AI Runtime Abstraction",
                            badge = "RUNTIME ENGINE",
                            badgeColor = Color(0xFFA371F7),
                            icon = Icons.Default.Memory,
                            summary = "Decoupled runtime layer supporting Android AICore (Gemini Nano), Google LiteRT, ONNX Mobile, and ExecuTorch.",
                            details = listOf(
                                "Active Runtime: ${activeModel.targetRuntime.label} (${activeModel.targetRuntime.provider})",
                                "Decoupled Interface: Allows seamless migration between AICore system service and LiteRT mobile runtime",
                                "Zero Cloud Relay: Executes purely within local application address space"
                            ),
                            isSelected = selectedDetailNode == "RUNTIME",
                            onClick = { selectedDetailNode = if (selectedDetailNode == "RUNTIME") null else "RUNTIME" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 4. On-Device Multimodal Model
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, MedicaAccentBlue, RoundedCornerShape(10.dp)),
                            color = Color(0xFF131922)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = MedicaAccentBlue, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "4. On-Device Multimodal Model", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MedicaAccentBlue.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = activeModel.quantization, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Active: ${activeModel.name} (${activeModel.parameterCount}, ${activeModel.storageSizeMb} MB, RAM req: ${activeModel.requiredRamGb} GB)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MedicaTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // The 4 Multimodal Sub-Branches
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0F1318))
                                        .border(1.dp, MedicaBorderDark, RoundedCornerShape(6.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "ON-DEVICE MULTIMODAL CAPABILITIES", fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
                                    SubServiceRow("Text Understanding", "Parses responder notes, symptoms, vital sign trends, and clinical context")
                                    SubServiceRow("Image Understanding", "Inspects wound surface, pupil dilation, burn margins, and limb deformities")
                                    SubServiceRow("Audio Understanding", "Extracts acoustic signatures from auscultation clips (stridor, wheezing, crackles)")
                                    SubServiceRow("Video Understanding", "Analyzes keyframes for respiratory chest wall excursion, work of breathing, and tremor")
                                }
                            }
                        }
                    }

                    item { DownConnectorLine() }

                    // 5. Local Context Builder
                    item {
                        PipelineNodeCard(
                            stepNumber = "5",
                            title = "Local Context Builder",
                            badge = "CONTEXT BOUNDING",
                            badgeColor = Color(0xFFD29922),
                            icon = Icons.Default.Layers,
                            summary = "Fuses case inputs with top retrieved evidence snippets, local system safety prompts, and strict JSON output schema.",
                            details = listOf(
                                "Strict Context Isolation: Bounded to 2-3 clinical evidence citations",
                                "Encapsulates non-diagnostic emergency decision support instructions",
                                "Zero Network Calling: Context is formatted purely in local memory"
                            ),
                            isSelected = selectedDetailNode == "CTX",
                            onClick = { selectedDetailNode = if (selectedDetailNode == "CTX") null else "CTX" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 6. Local RAG Engine
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
                            color = MedicaCardDark
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.FindInPage, contentDescription = null, tint = MedicaAccentBlue, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "6. Local RAG Engine", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MedicaAccentBlue.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "ON-DEVICE VECTOR INDEX", fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0F1318))
                                        .border(1.dp, MedicaBorderDark, RoundedCornerShape(6.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    SubServiceRow("Embedding Model", "384-dimensional quantized on-device embedding model (MobileBERT / BGE-small)")
                                    SubServiceRow("Vector Search", "Cosine similarity calculation over memory-mapped local vector index")
                                    SubServiceRow("Metadata Filtering", "Filters by emergency category (Trauma, Airway, Cardio, Burns, Stroke)")
                                    SubServiceRow("Knowledge Retrieval", "Extracts top 2-3 matched assets rather than passing the whole database")
                                }
                            }
                        }
                    }

                    item { DownConnectorLine() }

                    // 7. Local Medical Knowledge Vault
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, MedicaGreenDot, RoundedCornerShape(10.dp)),
                            color = Color(0xFF0F1813)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = MedicaGreenDot, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "7. Local Medical Knowledge Vault", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MedicaGreenDot.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "THOUSANDS OF ASSETS", fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaGreenDot)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Preloaded multimodal repository on internal flash storage. Contains thousands of resources across all 9 required clinical modalities:",
                                    fontSize = 12.sp,
                                    color = MedicaTextSecondary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val vaultModalities = listOf(
                                    "Protocols", "Documents", "Images",
                                    "Diagrams", "Flowcharts", "Decision Trees",
                                    "Videos", "Audio", "Structured Medical Data"
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    vaultModalities.forEach { modality ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF16231A))
                                                .border(1.dp, MedicaGreenDot.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = modality, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaGreenDot)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { DownConnectorLine() }

                    // 8. Evidence Assembly
                    item {
                        PipelineNodeCard(
                            stepNumber = "8",
                            title = "Evidence Assembly & Provenance",
                            badge = "CRYPTOGRAPHIC PROVENANCE",
                            badgeColor = MedicaAccentBlue,
                            icon = Icons.Default.CheckCircle,
                            summary = "Pairs clinical citations with precomputed SHA-256 integrity hashes to guarantee traceability without internet access.",
                            details = listOf(
                                "Attaches source organization and guideline version (AHA, ACS, TCCC)",
                                "Cryptographic hash verification ensures knowledge asset was not altered"
                            ),
                            isSelected = selectedDetailNode == "EVID",
                            onClick = { selectedDetailNode = if (selectedDetailNode == "EVID") null else "EVID" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 9. Local Medical AI Reasoning
                    item {
                        PipelineNodeCard(
                            stepNumber = "9",
                            title = "Local Medical AI Reasoning",
                            badge = "INFERENCE EXECUTION",
                            badgeColor = Color(0xFFA371F7),
                            icon = Icons.Default.Psychology,
                            summary = "The quantized model processes the bounded context directly on mobile NPU/GPU in <250ms with zero network transmission.",
                            details = listOf(
                                "Synthesizes triage urgency (Critical, Urgent, Moderate, Low)",
                                "Formulates differential considerations and immediate life-saving steps",
                                "Quantifies clinical uncertainty and field contraindications"
                            ),
                            isSelected = selectedDetailNode == "REASON",
                            onClick = { selectedDetailNode = if (selectedDetailNode == "REASON") null else "REASON" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 10. Structured Medica Result
                    item {
                        PipelineNodeCard(
                            stepNumber = "10",
                            title = "Structured Medica Result",
                            badge = "LOCAL OUTPUT",
                            badgeColor = MedicaGreenDot,
                            icon = Icons.Default.CheckCircle,
                            summary = "Delivers verified decision support directly into the mobile UI for the field responder.",
                            details = listOf(
                                "Triage code, action checklist, and verified citation evidence",
                                "Zero egress, zero API keys, 100% air-gapped on the responder's phone"
                            ),
                            isSelected = selectedDetailNode == "RES",
                            onClick = { selectedDetailNode = if (selectedDetailNode == "RES") null else "RES" }
                        )
                    }
                }

                "EXTERNAL_NEURAL_NET" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "EXTERNAL NEURAL VAULT RECOGNITION NETWORK",
                            subtitle = "Specialized Multimodal Cross-Encoder pairing with local model & 25.6 GB Medical Vault"
                        )
                    }

                    // Neural Network Architecture Specification Card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MedicaAccentBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                            color = Color(0xFF101723)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = MedicaAccentBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = ExternalNeuralVaultNetwork.NETWORK_NAME,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaAccentBlue
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MedicaGreenDot.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "LOCAL NPU / TPU",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaGreenDot
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Cross-references multi-modal responder inputs (wounds, auscultations, video motions, clinical vitals) against the 25.6 GB local knowledge vault. Recognizes the critical emergency presentation and synthesizes comprehensive image steps, text steps, video keyframes, flowchart decisions, acoustic steps, and dosing matrices.",
                                    fontSize = 12.sp,
                                    color = MedicaTextSecondary,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF090D14))
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Text(text = "TENSOR EMBEDDING", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                                            Text(text = "${ExternalNeuralVaultNetwork.EMBEDDING_DIMENSION}-dim Dense PQ", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF090D14))
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Text(text = "QUANTIZATION", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                                            Text(text = ExternalNeuralVaultNetwork.INFERENCE_PRECISION, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaGreenDot)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF090D14))
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Text(text = "VAULT INDEX", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                                            Text(text = "25.6 GB (14,280 docs)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Query Console & Preset Scenarios
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
                            color = MedicaCardDark
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Interactive Neural Recognition Console",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Choose an emergency scenario or type any presentation to trigger local neural recognition:",
                                    fontSize = 11.sp,
                                    color = MedicaTextSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                val presets = listOf(
                                    "🩸 Arterial Bleed" to "Trauma arterial hemorrhage spurting from thigh with pale cold skin",
                                    "⚡ Pulseless CPR" to "Sudden collapse, unresponsive adult, absent carotid pulse, ventricular fibrillation",
                                    "🐝 Anaphylaxis" to "Severe acute allergic anaphylaxis, facial angioedema, inspiratory stridor, hives",
                                    "🔥 30% Burn" to "Partial and full thickness flame burns over torso and upper extremities, Parkland formula",
                                    "🫁 Pneumothorax" to "Penetrating sucking chest wound, absent unilateral breath sounds, tracheal shift",
                                    "🗣️ Choking" to "Adult complete foreign body airway obstruction, inability to speak, cyanosis",
                                    "🧠 Seizure" to "Active tonic-clonic status epilepticus convulsion lasting over 6 minutes",
                                    "🚨 Stroke" to "Sudden unilateral facial droop, right arm pronator drift, slurred aphasia",
                                    "💨 Asthma" to "Severe acute asthma bronchospasm, silent chest, severe retractions, exhaustion"
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    presets.forEach { (label, queryText) ->
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(1.dp, MedicaBorderDark, RoundedCornerShape(6.dp))
                                                .clickable {
                                                    neuralNetQuery = queryText
                                                },
                                            color = if (neuralNetQuery == queryText) MedicaAccentBlue.copy(alpha = 0.2f) else Color(0xFF0F141C)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (neuralNetQuery == queryText) MedicaAccentBlue else MedicaTextSecondary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = neuralNetQuery,
                                    onValueChange = { neuralNetQuery = it },
                                    modifier = Modifier.fillMaxWidth().testTag("neural_net_query_input"),
                                    placeholder = { Text("Describe field presentation or emergency symptom...", fontSize = 12.sp, color = MedicaTextSecondary) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MedicaAccentBlue,
                                        unfocusedBorderColor = MedicaBorderDark,
                                        focusedContainerColor = Color(0xFF0A0E14),
                                        unfocusedContainerColor = Color(0xFF0A0E14),
                                        focusedTextColor = MedicaTextPrimary,
                                        unfocusedTextColor = MedicaTextPrimary
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isNeuralNetExecuting = true
                                            val plan = ExternalNeuralVaultNetwork.queryNeuralVault(neuralNetQuery)
                                            neuralNetPlan = plan
                                            isNeuralNetExecuting = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicaAccentBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("execute_neural_net_button")
                                ) {
                                    if (isNeuralNetExecuting) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Neural Network Traversal & Recognition...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("RECOGNIZE & FETCH MULTIMODAL STEPS (NPU)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Render Result Plan
                    neuralNetPlan?.let { plan ->
                        item {
                            ExternalNeuralPlanResultCard(plan = plan)
                        }
                    }
                }

                "CAPABILITY_LAYER" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "DEVICE CAPABILITY & SILICON ADAPTATION",
                            subtitle = "Automatic hardware inspection ensuring safe model execution without OOM crashes"
                        )
                    }

                    item {
                        DeviceHardwareInspectionCard(capability = deviceCapability)
                    }

                    item {
                        HardwareTierAdaptationCard()
                    }
                }

                "MODEL_MANAGER" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "LOCAL MODEL MANAGER",
                            subtitle = "Discovery, compatibility validation, dynamic RAM mounting, and quantized variants"
                        )
                    }

                    item {
                        ModelManagerSummaryCard(capability = deviceCapability)
                    }

                    item {
                        Text(
                            text = "QUANTIZED ON-DEVICE MODEL CATALOG",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicaAccentBlue
                        )
                    }

                    items(LocalModelManager.getCatalog()) { model ->
                        ModelSpecCard(
                            spec = model,
                            isActive = model.modelId == activeModel.modelId,
                            onSelect = {
                                LocalModelManager.selectModel(model.modelId, deviceCapability)
                            }
                        )
                    }
                }

                "LOCAL_RAG" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "LOCAL RAG & MULTIMODAL RETRIEVAL PIPELINE",
                            subtitle = "Case → Local Embedding → Vector + Metadata Search → Top Resources → Context Builder → On-Device Model"
                        )
                    }

                    item {
                        LocalRagStepByStepFlowCard()
                    }

                    item {
                        MultimodalRetrievalDetailCard()
                    }
                }

                "KNOWLEDGE_VAULT" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "LOCAL MEDICAL KNOWLEDGE VAULT (ALL 9 MODALITIES)",
                            subtitle = "Thousands of preloaded clinical emergency assets carried completely on the phone"
                        )
                    }

                    item {
                        VaultModalitiesDetailCard()
                    }

                    item {
                        SampleProceduresViewerCard()
                    }
                }

                "PRIVACY_BOUNDARIES" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "LOCAL PRIVACY & ENCRYPTED STORAGE BOUNDARIES",
                            subtitle = "Guaranteed 100% on-device operation with zero cloud requests or key leakage"
                        )
                    }

                    item {
                        PrivacyGuaranteesListCard()
                    }
                }

                "ON_DEVICE_RUNNER" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "LIVE ON-DEVICE PIPELINE RUNNER",
                            subtitle = "Run an end-to-end local inference execution in <200ms on the phone"
                        )
                    }

                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
                            color = MedicaCardDark
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Execute Air-Gapped On-Device Analysis",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Runs: Device Hardware Check → Local RAG Search (Cosine match) → Context Bounding → Local Model Reasoning (${activeModel.name}). Zero network packets emitted.",
                                    fontSize = 12.sp,
                                    color = MedicaTextSecondary
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isSimulating = true
                                            val localMedia = listOf(
                                                LocalMediaInput("m1", ModalityType.AUDIO, "tracheal_auscultation.m4a", "High-pitch inspiratory stridor peak at 850 Hz"),
                                                LocalMediaInput("m2", ModalityType.IMAGE, "cervical_soft_tissue.jpg", "Anterior neck swelling, no stridor deviation")
                                            )
                                            val contextScope = LocalRagEngine.buildLocalContext(
                                                caseId = "AIRGAP-4890",
                                                chiefComplaint = "Acute laryngeal stridor and upper airway distress in adult male",
                                                demographics = "(M, 42)",
                                                observations = "Tripoding, SpO2 91%, respiratory rate 28/min, audible inspiratory crowing",
                                                attachedMedia = localMedia
                                            )
                                            val result = LocalInferenceEngine.synthesizeLocalReasoning(
                                                context = contextScope,
                                                modelSpec = activeModel,
                                                runtime = activeModel.targetRuntime
                                            )
                                            simulationResult = result
                                            isSimulating = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicaAccentBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("run_ondevice_simulation_button")
                                ) {
                                    if (isSimulating) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Executing On-Device Inference Pipeline...")
                                    } else {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("EXECUTE LOCAL ON-DEVICE ANALYSIS")
                                    }
                                }
                            }
                        }
                    }

                    if (simulationResult != null) {
                        item {
                            LocalInferenceResultCard(result = simulationResult!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PipelineNodeCard(
    stepNumber: String,
    title: String,
    badge: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    summary: String,
    details: List<String>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MedicaAccentBlue else MedicaBorderDark,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stepNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeColor, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = summary, fontSize = 12.sp, color = MedicaTextSecondary, lineHeight = 16.sp)

            if (isSelected) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F1318))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    details.forEach { detail ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "• ", fontSize = 11.sp, color = badgeColor, fontWeight = FontWeight.Bold)
                            Text(text = detail, fontSize = 11.sp, color = MedicaTextPrimary, lineHeight = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceHardwareInspectionCard(capability: DeviceCapability) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DEVICE SILICON & COMPUTE INVENTORY",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaAccentBlue
            )
            Spacer(modifier = Modifier.height(10.dp))

            HardwareMetricRow("System RAM (Total / Available)", "${capability.totalRamGb} GB / ${capability.availableRamGb} GB Free")
            HorizontalDivider(color = MedicaBorderDark, thickness = 0.8.dp, modifier = Modifier.padding(vertical = 6.dp))
            HardwareMetricRow("Storage Partition (Flash)", "${capability.storageFreeGb} GB Free space")
            HorizontalDivider(color = MedicaBorderDark, thickness = 0.8.dp, modifier = Modifier.padding(vertical = 6.dp))
            HardwareMetricRow("Hardware Accelerator", capability.acceleratorType.label)
            HorizontalDivider(color = MedicaBorderDark, thickness = 0.8.dp, modifier = Modifier.padding(vertical = 6.dp))
            HardwareMetricRow("Hardware Tier Classification", capability.deviceTier.label)
            HorizontalDivider(color = MedicaBorderDark, thickness = 0.8.dp, modifier = Modifier.padding(vertical = 6.dp))
            HardwareMetricRow("Supported On-Device Runtimes", capability.supportedRuntimes.joinToString { it.label })
        }
    }
}

@Composable
fun HardwareMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MedicaTextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MedicaTextPrimary, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun HardwareTierAdaptationCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "MULTI-TIER ANDROID ADAPTABILITY",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaAccentBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Rather than assuming every field phone has identical hardware, Medica adapts its quantized model variant based on verified device capabilities:",
                fontSize = 12.sp,
                color = MedicaTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TierRow("Tier 1: Entry Field Phone (4-6 GB RAM)", "Runs Gemma 2B INT4 or MobileBERT (RAM: 1.4 GB) with zero lag", MedicaBorderDark)
                TierRow("Tier 2: Standard Responder Phone (8 GB RAM)", "Runs Llama 3.2 3B Q4_K_M (RAM: 2.4 GB) with full clinical reasoning", MedicaAccentBlue)
                TierRow("Tier 3: Flagship NPU Device (12-16 GB RAM)", "Runs Gemini Nano (Android AICore) or Llama 3 8B INT4 via dedicated NPU", MedicaGreenDot)
            }
        }
    }
}

@Composable
fun TierRow(title: String, desc: String, color: Color) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F1318))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 11.sp, color = MedicaTextSecondary)
        }
    }
}

@Composable
fun ModelManagerSummaryCard(capability: DeviceCapability) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Local Model Manager Engine",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Guarantees safe on-device model discovery, memory-budget enforcement, SHA-256 weight validation, and atomic updates without bricking offline operation.",
                fontSize = 12.sp,
                color = MedicaTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ModelSpecCard(
    spec: LocalModelSpec,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) MedicaAccentBlue else MedicaBorderDark,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onSelect() },
        color = if (isActive) Color(0xFF131922) else MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = spec.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                        if (isActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MedicaAccentBlue)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(text = "ACTIVE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                    Text(text = "${spec.parameterCount} Params · ${spec.quantization} · Runtime: ${spec.targetRuntime.label}", fontSize = 11.sp, color = MedicaTextSecondary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "${spec.storageSizeMb} MB", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary, fontFamily = FontFamily.Monospace)
                    Text(text = "Req RAM: ${spec.requiredRamGb} GB", fontSize = 10.sp, color = MedicaTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Hash: ${spec.sha256Hash.take(18)}...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                Text(text = if (spec.isLoadedInMemory) "Resident in RAM" else "Stored on Flash", fontSize = 10.sp, color = if (spec.isLoadedInMemory) MedicaGreenDot else MedicaTextSecondary)
            }
        }
    }
}

@Composable
fun LocalRagStepByStepFlowCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "LOCAL RAG RETRIEVAL ASSEMBLY FLOW",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaAccentBlue
            )
            Spacer(modifier = Modifier.height(10.dp))

            RagFlowStep("1. Case Ingress", "Responder notes + vitals + attached media modal descriptors")
            RagArrow()
            RagFlowStep("2. Local Embedding", "Quantized on-device model computes 384-dimensional vector embedding")
            RagArrow()
            RagFlowStep("3. Vector + Metadata Search", "Cosine similarity calculation against SQLite-VSS index & category filter")
            RagArrow()
            RagFlowStep("4. Top Relevant Resources", "Selects only top 2-3 matched assets with SHA-256 integrity hashes")
            RagArrow()
            RagFlowStep("5. Context Builder", "Fuses retrieved evidence + case + local non-diagnostic safety prompts")
            RagArrow()
            RagFlowStep("6. On-Device Model", "Feeds bounded prompt into quantized local model (Zero full DB exposure)")
        }
    }
}

@Composable
fun RagFlowStep(title: String, desc: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F1318))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicaAccentBlue, fontFamily = FontFamily.Monospace)
            Text(text = desc, fontSize = 11.sp, color = MedicaTextSecondary)
        }
    }
}

@Composable
fun RagArrow() {
    Box(modifier = Modifier.fillMaxWidth().height(16.dp), contentAlignment = Alignment.Center) {
        Text(text = "↓", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
    }
}

@Composable
fun MultimodalRetrievalDetailCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Multimodal Retrieval Channels",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "An uploaded wound image, respiratory movement video, auscultation audio memo, or text observation can all independently or collectively activate corresponding Knowledge Vault items.",
                fontSize = 12.sp,
                color = MedicaTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun VaultModalitiesDetailCard() {
    val stats = ComprehensiveLocalMedicalVault.getVaultStats()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "MULTIMODAL KNOWLEDGE VAULT STATS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaGreenDot
            )
            Spacer(modifier = Modifier.height(8.dp))

            stats.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = key, fontSize = 12.sp, color = MedicaTextSecondary)
                    Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MedicaTextPrimary)
                }
            }
        }
    }
}

@Composable
fun SampleProceduresViewerCard() {
    val sampleAssets = remember { ComprehensiveLocalMedicalVault.getAllAssets() }
    var expandedAssetId by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        sampleAssets.forEach { asset ->
            val isExpanded = expandedAssetId == asset.id
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, if (isExpanded) MedicaAccentBlue else MedicaBorderDark, RoundedCornerShape(8.dp))
                    .clickable { expandedAssetId = if (isExpanded) null else asset.id },
                color = Color(0xFF0F1318)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = asset.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                            Text(text = "${asset.type.label} · ${asset.sourceOrganization}", fontSize = 11.sp, color = MedicaTextSecondary)
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MedicaTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = asset.summary, fontSize = 12.sp, color = MedicaTextSecondary, lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "STEP-BY-STEP PROCEDURAL STEPS:", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        asset.clinicalSteps.forEachIndexed { i, step ->
                            Text(text = "${i + 1}. $step", fontSize = 11.sp, color = MedicaTextPrimary, lineHeight = 15.sp, modifier = Modifier.padding(vertical = 2.dp))
                        }

                        if (asset.contraindications.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "CONTRAINDICATIONS / CAUTIONS:", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaRed)
                            asset.contraindications.forEach { caution ->
                                Text(text = "⚠️ $caution", fontSize = 11.sp, color = MedicaRed.copy(alpha = 0.9f), lineHeight = 15.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Provenance: ${asset.evidenceProvenanceHash}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyGuaranteesListCard() {
    val guarantees = LocalPrivacySecurityManager.getPrivacyGuarantees()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        guarantees.forEach { guarantee ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MedicaBorderDark, RoundedCornerShape(8.dp)),
                color = Color(0xFF0F1318)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = guarantee.boundaryName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MedicaGreenDot.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(text = guarantee.complianceStatus, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaGreenDot)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = guarantee.technicalMechanism, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaAccentBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = guarantee.description, fontSize = 11.sp, color = MedicaTextSecondary, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
fun LocalInferenceResultCard(result: LocalMedicaResult) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaGreenDot, RoundedCornerShape(10.dp)),
        color = Color(0xFF0F1813)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ON-DEVICE INFERENCE RESULT", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaGreenDot)
                Text(text = "${result.latencyMs} ms · Local NPU", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaGreenDot)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Triage: ${result.triageCode}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Model: ${result.usedModelName} (${result.usedRuntime.label})", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaAccentBlue)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Immediate Actions (From Local Vault):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MedicaTextPrimary)
            result.immediateActions.forEach { act ->
                Text(text = "• $act", fontSize = 11.sp, color = MedicaTextSecondary, lineHeight = 15.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Retrieved Evidence Citations (${result.retrievedEvidence.size}):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MedicaTextPrimary)
            result.retrievedEvidence.forEach { ev ->
                Text(text = "✓ [${ev.id}] ${ev.title} (Cosine: ${ev.cosineSimilarity})", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaGreenDot)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = result.uncertaintyDisclosure, fontSize = 10.sp, color = MedicaTextSecondary)
            Text(text = "Integrity Hash: ${result.provenanceHash}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
        }
    }
}

@Composable
fun ExternalNeuralPlanResultCard(
    plan: MultimodalActionPlan,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("TEXT") }

    val stepTabs = listOf(
        "TEXT" to "Text Steps (${plan.textSteps.size})",
        "IMAGE" to "Image Steps (${plan.imageSteps.size})",
        "VIDEO" to "Video Steps (${plan.videoSteps.size})",
        "FLOWCHART" to "Flowchart (${plan.flowchartSteps.size})",
        "AUDIO" to "Audio (${plan.audioSteps.size})",
        "DOSING" to "Dosing Matrix",
        "CAUTIONS" to "Contraindications",
        "EVIDENCE" to "Vault Evidence (${plan.retrievedVaultAssets.size})"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, MedicaAccentBlue, RoundedCornerShape(12.dp)),
        color = Color(0xFF0F1520)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Recognized condition + NPU metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MedicaAccentBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEURAL NETWORK RECOGNITION",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MedicaAccentBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plan.conditionRecognized,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MedicaGreenDot.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${plan.neuralNetworkLatencyMs} ms · ${(plan.confidenceScore * 100).toInt()}% CONF",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MedicaGreenDot
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Explanation & Clinical Rationale Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF090D14))
                    .border(1.dp, MedicaBorderDark, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "CLINICAL AI EXPLANATION & PHYSIOLOGY",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MedicaAccentBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plan.aiExplanation,
                        fontSize = 12.sp,
                        color = MedicaTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal step navigation tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                stepTabs.forEach { (tabKey, label) ->
                    val isSelected = selectedTab == tabKey
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(
                                1.dp,
                                if (isSelected) MedicaAccentBlue else MedicaBorderDark,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedTab = tabKey },
                        color = if (isSelected) MedicaAccentBlue.copy(alpha = 0.2f) else Color(0xFF090D14)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MedicaAccentBlue else MedicaTextSecondary,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Tab Content Area
            when (selectedTab) {
                "TEXT" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A0E15))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "STANDARD-OF-CARE CLINICAL PROCEDURE",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MedicaGreenDot
                        )
                        plan.textSteps.forEachIndexed { idx, step ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "${idx + 1}. ",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaGreenDot
                                )
                                Text(
                                    text = step,
                                    fontSize = 12.sp,
                                    color = MedicaTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                "IMAGE" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.imageSteps.forEach { img ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MedicaAccentBlue.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                                color = Color(0xFF0A0E15)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Step ${img.stepNumber}: ${img.title}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaAccentBlue
                                        )
                                        Text(
                                            text = img.atlasReference,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MedicaTextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📍 Anatomical Landmark: ${img.anatomicalLandmark}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MedicaGreenDot
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = img.visualDescription,
                                        fontSize = 12.sp,
                                        color = MedicaTextPrimary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                "VIDEO" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.videoSteps.forEach { vid ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFE6A700).copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                color = Color(0xFF0A0E15)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE6A700).copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "KEYFRAME ${vid.timestamp}",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFE6A700)
                                            )
                                        }
                                        Text(
                                            text = vid.phase,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaTextPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = vid.actionDemonstrated,
                                        fontSize = 12.sp,
                                        color = MedicaTextPrimary,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "💡 Pro Tip: ${vid.technicalTip}",
                                        fontSize = 11.sp,
                                        color = Color(0xFFE6A700)
                                    )
                                }
                            }
                        }
                    }
                }

                "FLOWCHART" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.flowchartSteps.forEach { fc ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFA371F7).copy(alpha = 0.45f), RoundedCornerShape(8.dp)),
                                color = Color(0xFF0A0E15)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "[${fc.nodeId}] DECISION: ${fc.decisionCondition}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA371F7)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row {
                                        Text(
                                            text = "✓ IF TRUE: ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaGreenDot,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = fc.branchIfTrue,
                                            fontSize = 11.sp,
                                            color = MedicaTextPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row {
                                        Text(
                                            text = "✗ IF FALSE: ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaRed,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = fc.branchIfFalse,
                                            fontSize = 11.sp,
                                            color = MedicaTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "AUDIO" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.audioSteps.forEach { aud ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF00B4D8).copy(alpha = 0.45f), RoundedCornerShape(8.dp)),
                                color = Color(0xFF0A0E15)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${aud.acousticTrack}: ${aud.soundType}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00B4D8)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📍 Auscultation Site: ${aud.auscultationPoint}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MedicaGreenDot
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = aud.clinicalSignificance,
                                        fontSize = 12.sp,
                                        color = MedicaTextPrimary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                "DOSING" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A0E15))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "EMERGENCY RESUSCITATION PHARMACOLOGY",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MedicaAccentBlue
                        )
                        plan.dosingMatrix.forEach { dose ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "💊 ", fontSize = 11.sp)
                                Text(
                                    text = dose,
                                    fontSize = 12.sp,
                                    color = MedicaTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                "CAUTIONS" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A0E15))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "HARD CONTRAINDICATIONS & SAFETY WARNINGS",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MedicaRed
                        )
                        plan.contraindications.forEach { caution ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "⚠️ ", fontSize = 11.sp)
                                Text(
                                    text = caution,
                                    fontSize = 12.sp,
                                    color = MedicaRed.copy(alpha = 0.95f),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                "EVIDENCE" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.retrievedVaultAssets.forEach { asset ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MedicaBorderDark, RoundedCornerShape(8.dp)),
                                color = Color(0xFF0A0E15)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "[${asset.type.label}] ${asset.title}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaTextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${(asset.cosineSimilarity * 100).toInt()}% sim",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MedicaGreenDot
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${asset.sourceOrganization} · v${asset.version} · ${asset.compressionCodec} (${asset.compressionRatio})",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MedicaTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = asset.summary,
                                        fontSize = 11.sp,
                                        color = MedicaTextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer with Provenance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF070A0F))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prov: ${plan.provenanceHash}",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaTextSecondary
                )
                Text(
                    text = "Air-Gapped NPU Execution",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MedicaGreenDot
                )
            }
        }
    }
}
