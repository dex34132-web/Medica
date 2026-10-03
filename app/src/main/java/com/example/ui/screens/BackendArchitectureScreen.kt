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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schema
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.backend.gateway.ApiGatewayService
import com.example.backend.models.ApiGatewayRequest
import com.example.backend.models.ApiGatewayResponse
import com.example.backend.models.OrchestratedDecisionResponse
import com.example.backend.models.StorageBoundarySpec
import com.example.backend.orchestrator.AiOrchestrator
import com.example.backend.rag.MedicalKnowledgeVaultBackend
import com.example.backend.secrets.SecretsManager
import com.example.backend.storage.StorageBoundaries
import com.example.ui.theme.MedicaAccentBlue
import com.example.ui.theme.MedicaBorderDark
import com.example.ui.theme.MedicaCardDark
import com.example.ui.theme.MedicaGreenDot
import com.example.ui.theme.MedicaRed
import com.example.ui.theme.MedicaTextPrimary
import com.example.ui.theme.MedicaTextSecondary
import kotlinx.coroutines.launch

/**
 * Production-ready backend architecture visualizer for Medica.
 *
 * Adheres strictly to the architectural specifications:
 * - API Gateway (Request validation, Rate limiting, Abuse protection)
 * - Zero user authentication or accounts systems
 * - AI Orchestrator (Major Component: invisible server prompts, model instructions, safety policies,
 *   output schemas, context scoping, evidence requirements, uncertainty handling, tool permissions, model selection)
 * - Strict Context Scoping (Case Input + Evidence + Knowledge + Prompts + Policies -> AI Request)
 * - Targeted Medical Knowledge Vault retrieval (Protocols, Documents, Images, Diagrams, Flowcharts,
 *   Decision trees, Videos, Audio, Structured medical information, Vector search, Metadata DB)
 * - Separate storage boundaries (Case, Media, Vault, AI Config, Secrets, Telemetry)
 * - Cloud Architecture vs Completely Separate Offline Path
 * - Server-side Secrets Management (Cloud AI API keys never leak to client)
 * - External Boundary: Emergency services outside Medica decision support
 * - Dark, minimal, technically accurate architecture-diagram aesthetic
 */
@Composable
fun BackendArchitectureScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf("CLOUD_PIPELINE") }
    var selectedNodeDetail by remember { mutableStateOf<String?>(null) }
    var simulationResult by remember { mutableStateOf<ApiGatewayResponse<OrchestratedDecisionResponse>?>(null) }
    var isSimulating by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val sectionTabs = listOf(
        "CLOUD_PIPELINE" to "Cloud Architecture",
        "CONTEXT_SCOPING" to "Context Scoping",
        "KNOWLEDGE_VAULT" to "Knowledge Vault (9 Types)",
        "STORAGE_BOUNDARIES" to "Storage Boundaries",
        "OFFLINE_SEPARATION" to "Offline Separation",
        "LIVE_SIMULATOR" to "Pipeline Runner"
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
                modifier = Modifier.testTag("arch_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MedicaTextPrimary
                )
            }
            Column {
                Text(
                    text = "Production Backend Architecture",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicaTextPrimary
                )
                Text(
                    text = "API Gateway · AI Orchestrator · Knowledge Vault · Secrets",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaTextSecondary
                )
            }
        }

        // ARCHITECTURE SECTION CHIPS
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
                    modifier = Modifier.testTag("arch_tab_$key")
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
            when (selectedSection) {
                "CLOUD_PIPELINE" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "END-TO-END CLOUD ORCHESTRATION PIPELINE",
                            subtitle = "Mobile Client → API Gateway → Validation + Rate Limiting → AI Orchestrator → Cloud AI Provider"
                        )
                    }

                    // 1. Mobile App Node
                    item {
                        ArchitectureServiceBox(
                            title = "Mobile Application (Client)",
                            badge = "CLIENT LAYER",
                            badgeColor = MedicaAccentBlue,
                            icon = Icons.Default.PhoneAndroid,
                            specs = listOf(
                                "Zero Cloud Provider Secrets: App never receives Cloud AI API keys",
                                "Zero User Accounts / Auth: Stateless client token authorization without account systems",
                                "Transmits minimal Case Payload + Client Token over TLS 1.3",
                                "Receives only verified, structured Decision Support response"
                            ),
                            isSelected = selectedNodeDetail == "MOBILE_CLIENT",
                            onClick = { selectedNodeDetail = if (selectedNodeDetail == "MOBILE_CLIENT") null else "MOBILE_CLIENT" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 2. API Gateway
                    item {
                        ArchitectureServiceBox(
                            title = "Secure API Gateway",
                            badge = "SECURITY INGRESS",
                            badgeColor = MedicaGreenDot,
                            icon = Icons.Default.Shield,
                            specs = listOf(
                                "TLS 1.3 Termination & Request Authentication (Stateless Device Tokens)",
                                "Pre-ingress Ingress Boundary: Isolates external mobile requests from internal VPC services",
                                "Routes validated payloads directly to the AI Orchestration layer",
                                "Zero User Authentication: Completely decoupled from user identity systems"
                            ),
                            isSelected = selectedNodeDetail == "API_GATEWAY",
                            onClick = { selectedNodeDetail = if (selectedNodeDetail == "API_GATEWAY") null else "API_GATEWAY" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 3. Validation + Rate Limiting Gate
                    item {
                        ArchitectureServiceBox(
                            title = "Validation + Rate Limiting",
                            badge = "INGRESS CONTROLLER",
                            badgeColor = Color(0xFFD29922),
                            icon = Icons.Default.Speed,
                            specs = listOf(
                                "Request Validation: Structural schema enforcement & payload size limits",
                                "Rate Limiting: Token-bucket algorithm (60 req/min per field client token)",
                                "Abuse Protection: Prompt injection detection & sanitization filters",
                                "Circuit Breaker: Automatic shed for abnormal traffic spikes"
                            ),
                            isSelected = selectedNodeDetail == "VALIDATION_GATE",
                            onClick = { selectedNodeDetail = if (selectedNodeDetail == "VALIDATION_GATE") null else "VALIDATION_GATE" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 4. AI Orchestrator (Major Component)
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, MedicaAccentBlue, RoundedCornerShape(10.dp)),
                            color = Color(0xFF131922)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccountTree,
                                            contentDescription = null,
                                            tint = MedicaAccentBlue,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "AI Orchestrator (Core Server-Side Engine)",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaTextPrimary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MedicaAccentBlue.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "MAJOR COMPONENT",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MedicaAccentBlue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "The AI Orchestrator manages invisible server-side system prompts, model instructions, safety policies, output schemas, context scoping, evidence requirements, uncertainty handling, tool permissions, and model selection. These prompts and policies must NEVER be exposed to the mobile client or end user.",
                                    fontSize = 12.sp,
                                    color = MedicaTextSecondary,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // The 5 Required Core Branches
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0F1318))
                                        .border(1.dp, MedicaBorderDark, RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "AI ORCHESTRATOR CORE SERVICES",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicaAccentBlue
                                    )
                                    SubServiceRow("Prompt / Policy Manager", "Invisible clinical safety guardrails, non-diagnostic constraints & instructions")
                                    SubServiceRow("Context Scoping", "Assembles Case Input + Relevant Evidence + Retrieved Knowledge + System Prompts")
                                    SubServiceRow("RAG / Retrieval", "Queries Medical Knowledge Vault (vector search + metadata DB, top 2-3 matches)")
                                    SubServiceRow("Evidence Service", "Generates SHA-256 provenance integrity checksums for all clinical citations")
                                    SubServiceRow("Model Router", "Evaluates latency, token budget & routes to Gemini 1.5 Pro / Cloud AI")
                                    SubServiceRow("Multimodal Processing", "Auscultation audio spectrograms, wound image parsing, motion video inspection")
                                    SubServiceRow("Uncertainty Handling", "Mandatory confidence quantification, field limitation & contraindication warnings")
                                    SubServiceRow("Tool Permissions", "Restricts model capabilities to read-only clinical citations & schemas")
                                    SubServiceRow("Logging & Monitoring", "Prometheus / OpenTelemetry latency tracking, token usage & error alerting")
                                    SubServiceRow("Error Handling", "Circuit breakers & graceful fallback paths for model timeout or rate limits")
                                }
                            }
                        }
                    }

                    item { DownConnectorLine() }

                    // 5. Cloud AI Provider
                    item {
                        ArchitectureServiceBox(
                            title = "Cloud AI Provider (e.g. Gemini 1.5 Pro)",
                            badge = "EXTERNAL AI FOUNDATION",
                            badgeColor = Color(0xFFA371F7),
                            icon = Icons.Default.Cloud,
                            specs = listOf(
                                "Server-Side Calling Only: Authenticated via SecretsManager KMS Key",
                                "Strict Context Payload Only: Zero access to whole medical database",
                                "Structured Output Format: Returns JSON decision support payload",
                                "Mobile client has zero direct connection or provider credentials"
                            ),
                            isSelected = selectedNodeDetail == "CLOUD_PROVIDER",
                            onClick = { selectedNodeDetail = if (selectedNodeDetail == "CLOUD_PROVIDER") null else "CLOUD_PROVIDER" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 6. Structured Response back to Client
                    item {
                        ArchitectureServiceBox(
                            title = "Structured Response → Mobile App",
                            badge = "EGRESS AUDIT VERIFIED",
                            badgeColor = MedicaGreenDot,
                            icon = Icons.Default.CheckCircle,
                            specs = listOf(
                                "Urgency Level, Triage Code, and Media-derived Observations",
                                "Non-Definitive Explanations & Prioritized Immediate Actions",
                                "Quantified Uncertainty & Clinical Field Warnings",
                                "Verified Knowledge Vault Citations with SHA-256 Proof"
                            ),
                            isSelected = selectedNodeDetail == "EGRESS",
                            onClick = { selectedNodeDetail = if (selectedNodeDetail == "EGRESS") null else "EGRESS" }
                        )
                    }

                    item { DownConnectorLine() }

                    // 7. Mobile App (Receives structured result)
                    item {
                        ArchitectureServiceBox(
                            title = "Mobile Application (Responder UI)",
                            badge = "LOCAL PRESENTATION",
                            badgeColor = MedicaAccentBlue,
                            icon = Icons.Default.PhoneAndroid,
                            specs = listOf(
                                "Renders clinical observations, triage recommendation, and immediate action checklist",
                                "Direct navigation to local verified evidence and flowchart pathways",
                                "Zero exposure to backend server prompts, policies, or KMS API keys"
                            ),
                            isSelected = selectedNodeDetail == "MOBILE_RECEIVER",
                            onClick = { selectedNodeDetail = if (selectedNodeDetail == "MOBILE_RECEIVER") null else "MOBILE_RECEIVER" }
                        )
                    }

                    // 8. External Boundary Note (Emergency Services Outside)
                    item {
                        ExternalBoundaryCard()
                    }
                }

                "CONTEXT_SCOPING" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "STRICT CONTEXT SCOPING ARCHITECTURE",
                            subtitle = "Case Input + Relevant Evidence + Retrieved Knowledge + Server-Side Instructions + Safety Policies → AI Request"
                        )
                    }

                    item {
                        ContextScopingVisualCard()
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
                                    text = "Targeted Retrieval vs Full Database",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaAccentBlue
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "The backend retrieves relevant resources from the Medical Knowledge Vault rather than sending the entire medical database to the model. This bounds token consumption, reduces latency, eliminates hallucination risks, and maintains a strict cryptographic provenance trail.",
                                    fontSize = 12.sp,
                                    color = MedicaTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                "KNOWLEDGE_VAULT" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "MEDICAL KNOWLEDGE VAULT & RAG PIPELINE",
                            subtitle = "Targeted retrieval from 9 verified medical resource modalities + Vector Search & Metadata DB"
                        )
                    }

                    item {
                        KnowledgeVaultOverviewCard()
                    }

                    item {
                        Text(
                            text = "KNOWLEDGE VAULT MODALITIES (ALL 9 TYPES)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicaAccentBlue
                        )
                    }

                    item {
                        KnowledgeVaultNineTypesGrid()
                    }

                    item {
                        VectorAndMetadataDatabaseCard()
                    }
                }

                "STORAGE_BOUNDARIES" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "SEPARATE STORAGE BOUNDARIES",
                            subtitle = "Zero cross-contamination between Case Data, Medical Knowledge, AI Configuration, Secrets, and Telemetry"
                        )
                    }

                    items(StorageBoundaries.allBoundaries) { spec ->
                        StorageBoundaryCard(spec = spec)
                    }

                    item {
                        SecretsManagementDeepDiveCard()
                    }
                }

                "OFFLINE_SEPARATION" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "COMPLETELY SEPARATE OFFLINE & CLOUD PATHS",
                            subtitle = "Offline mode does NOT depend on the API Gateway, Cloud Orchestrator, or external servers"
                        )
                    }

                    item {
                        DualPathComparisonCard()
                    }
                }

                "LIVE_SIMULATOR" -> {
                    item {
                        ArchitectureNoticeCard(
                            title = "BACKEND PIPELINE RUNNER",
                            subtitle = "Execute real end-to-end Gateway, Validation, Scoping, RAG, KMS Secrets, and Audit flow"
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
                                    text = "Simulate Case Ingress & AI Orchestration",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Dispatches a live emergency case payload through ApiGatewayService (rate limit & validation) → AiOrchestrator (scoping & RAG) → SecretsManager (sealed server key verification) → Audit Log.",
                                    fontSize = 12.sp,
                                    color = MedicaTextSecondary
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isSimulating = true
                                            val simRequest = ApiGatewayRequest(
                                                requestId = "req-sim-9901",
                                                clientToken = "tok-responder-field-unit-04",
                                                caseId = "MED-78901",
                                                chiefComplaint = "Male, 54, severe substernal chest pressure radiating to left arm",
                                                patientDemographics = "(M, 54)",
                                                observations = "Diaphoretic, SpO2 93%, tachycardic, tachypneic",
                                                attachedMediaCount = 2
                                            )
                                            val resp = ApiGatewayService.handleClientCaseDecisionRequest(simRequest)
                                            simulationResult = resp
                                            isSimulating = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicaAccentBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("run_backend_simulation_button")
                                ) {
                                    if (isSimulating) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Executing Gateway & Orchestration Pipeline...")
                                    } else {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("EXECUTE LIVE BACKEND REQUEST")
                                    }
                                }
                            }
                        }
                    }

                    if (simulationResult != null) {
                        item {
                            SimulationOutputCard(response = simulationResult!!)
                        }
                    }

                    item {
                        AuditTrailTelemetryCard()
                    }
                }
            }
        }
    }
}

@Composable
fun ArchitectureNoticeCard(title: String, subtitle: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(8.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaAccentBlue,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MedicaTextSecondary
            )
        }
    }
}

@Composable
fun ArchitectureServiceBox(
    title: String,
    badge: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    specs: List<String>,
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
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
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

            Spacer(modifier = Modifier.height(10.dp))

            specs.forEach { spec ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                    Text(
                        text = spec,
                        fontSize = 12.sp,
                        color = MedicaTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DownConnectorLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width = 2.dp, height = 20.dp)) {
            drawLine(
                color = MedicaBorderDark,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
fun SubServiceRow(name: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "├─ ",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = MedicaAccentBlue
        )
        Column {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = MedicaTextPrimary
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = MedicaTextSecondary
            )
        }
    }
}

@Composable
fun ContextScopingVisualCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "STRICT CONTEXT ASSEMBLY FORMULA",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaAccentBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContextBlockItem("1. Case Input", "Case ID, Chief Complaint, Demographics, Field observations, Uploaded media modality counts")
            ContextPlusOperator()
            ContextBlockItem("2. Relevant Medical Evidence", "Top 2-3 matched evidence citations with SHA-256 hashes from Knowledge Vault")
            ContextPlusOperator()
            ContextBlockItem("3. Retrieved Knowledge", "Targeted protocol snippets and flowchart branches (never the full medical database)")
            ContextPlusOperator()
            ContextBlockItem("4. Server-Side AI Instructions", "Invisible clinical role, XABCDE priority ordering, non-diagnostic constraints")
            ContextPlusOperator()
            ContextBlockItem("5. Safety / Output Policies", "Enforced JSON output schema, mandatory uncertainty disclosure, contraindications")

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "↓", fontSize = 18.sp, color = MedicaAccentBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1B232F))
                    .border(1.dp, MedicaAccentBlue, RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "OPTIMIZED AI REQUEST (Scoped & Enforced)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Dispatched to Cloud AI via SecretsManager Server-Side API Key",
                        fontSize = 11.sp,
                        color = MedicaTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ContextBlockItem(title: String, detail: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(6.dp)),
        color = Color(0xFF0F1318)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = detail, fontSize = 11.sp, color = MedicaTextSecondary)
        }
    }
}

@Composable
fun ContextPlusOperator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicaTextSecondary)
    }
}

@Composable
fun KnowledgeVaultOverviewCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Targeted Medical Knowledge Vault Pipeline",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Rather than sending the entire medical database into the language model context window, Medica's RAG pipeline indexes multimodal clinical resources across 9 distinct categories and retrieves only the top 2-3 highest scoring assets.",
                fontSize = 12.sp,
                color = MedicaTextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun KnowledgeVaultNineTypesGrid() {
    val types = listOf(
        Triple("Medical Protocols", "Clinical emergency guidelines (AHA, ACLS, ATLS)", Icons.Default.Policy),
        Triple("Reference Documents", "Level 1 trauma triage and diagnostic criteria", Icons.Default.Description),
        Triple("Medical Images", "Surface anatomy and pathology lesion atlases", Icons.Default.Image),
        Triple("Diagrams", "Coronary distribution and conduction pathways", Icons.Default.Hub),
        Triple("Flowcharts", "Interactive airway and dyspnea decision pathways", Icons.Default.Schema),
        Triple("Decision Trees", "Acute abdominal pain differential branching", Icons.Default.AccountTree),
        Triple("Videos", "Bedside physical exam technique demonstrations", Icons.Default.Videocam),
        Triple("Audio", "Auscultation library (wheezes, stridor, S3 gallop)", Icons.Default.AudioFile),
        Triple("Structured Information", "Weight-based medication dosing matrices (JSON)", Icons.Default.TableView)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        types.forEach { (name, desc, icon) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MedicaBorderDark, RoundedCornerShape(8.dp)),
                color = Color(0xFF0F1318)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MedicaAccentBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = MedicaAccentBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MedicaTextPrimary)
                        Text(text = desc, fontSize = 11.sp, color = MedicaTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun VectorAndMetadataDatabaseCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Vector Search & Metadata Database",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaAccentBlue
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "VECTOR SEARCH INDEX", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                    Text(text = "HNSW / pgvector (1536-dim)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                    Text(text = "Calculates cosine similarity over clinical tokens to find nearest protocols.", fontSize = 11.sp, color = MedicaTextSecondary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "METADATA DATABASE", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                    Text(text = "PostgreSQL / Spanner", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                    Text(text = "Stores verified citations, SHA-256 provenance hashes, and version constraints.", fontSize = 11.sp, color = MedicaTextSecondary)
                }
            }
        }
    }
}

@Composable
fun StorageBoundaryCard(spec: StorageBoundarySpec) {
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
                Text(
                    text = spec.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicaTextPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MedicaBorderDark)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = spec.boundary.name,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MedicaAccentBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = spec.description, fontSize = 12.sp, color = MedicaTextSecondary)

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "STORAGE TECH", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                    Text(text = spec.storageTechnology, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MedicaTextPrimary)
                }
                Column {
                    Text(text = "SECURITY BOUNDARY", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                    Text(text = spec.securityLevel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MedicaGreenDot)
                }
            }
        }
    }
}

@Composable
fun SecretsManagementDeepDiveCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, Color(0xFFD29922), RoundedCornerShape(10.dp)),
        color = Color(0xFF1B1914)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = Color(0xFFD29922),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Server-Side Secrets Boundary (Strict Zero Client Leakage)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD29922)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Architectural Guarantee: Cloud AI Provider credentials (including the server API key) are stored strictly in SecretsManager on the backend. The mobile application NEVER receives, stores, or handles provider credentials.",
                fontSize = 12.sp,
                color = MedicaTextPrimary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F1318))
                    .padding(10.dp)
            ) {
                Column {
                    Text(text = "ACTIVE BACKEND KEY ID: ${SecretsManager.getKeyId()}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                    Text(text = "TELEMETRY MASKED FINGERPRINT: ${SecretsManager.getMaskedKeyFingerprint()}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaGreenDot)
                    Text(text = "MOBILE ACCESS PERMISSION: REJECTED (Zero Egress)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaRed)
                }
            }
        }
    }
}

@Composable
fun DualPathComparisonCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ARCHITECTURAL DUAL-PATH COMPARISON",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MedicaAccentBlue
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Cloud Pipeline Path
            Text(text = "1. CLOUD ORCHESTRATION ARCHITECTURE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
            Spacer(modifier = Modifier.height(6.dp))
            PathwayFlowBox(
                steps = listOf(
                    "Mobile App (Client Ingress)",
                    "API Gateway (TLS 1.3 Termination)",
                    "Validation + Rate Limiting (Token-Bucket & Injection Filter)",
                    "AI Orchestrator (Server Prompts, Scoping, RAG, Evidence Service, Model Router)",
                    "Cloud AI Provider (Gemini 1.5 Pro via Server SecretsManager Key)",
                    "Structured Response (Schema Validated → Mobile App)"
                ),
                color = MedicaAccentBlue
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Completely Separate Offline Path
            Text(text = "2. AIR-GAPPED OFFLINE PATHWAY (Completely Separate)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicaGreenDot)
            Spacer(modifier = Modifier.height(6.dp))
            PathwayFlowBox(
                steps = listOf(
                    "Mobile App (Standalone Client Runtime)",
                    "On-device AI (Llama 3 8B / Mistral 7B ONNX/NPU)",
                    "Local RAG (Quantized vector embeddings)",
                    "Local Medical Knowledge Vault (22 preloaded offline assets)",
                    "Structured Response (Zero Network Packets Transmitted)"
                ),
                color = MedicaGreenDot
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F1813))
                    .border(1.dp, MedicaGreenDot.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "VISUAL VERIFICATION: Offline mode does NOT depend on the API Gateway, Cloud Orchestrator, or external servers. When network is severed, field responders retain continuous medical decision support.",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaGreenDot,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun PathwayFlowBox(steps: List<String>, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F1318))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        steps.forEachIndexed { i, step ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${i + 1}. ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = FontFamily.Monospace)
                Text(text = step, fontSize = 11.sp, color = MedicaTextPrimary)
            }
        }
    }
}

@Composable
fun ExternalBoundaryCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFF85149).copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        color = Color(0xFF1D1415)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Emergency,
                contentDescription = null,
                tint = MedicaRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "External Boundary: Emergency Services",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicaRed
                )
                Text(
                    text = "Emergency dispatch and 911/ALS services remain outside Medica's backend architecture. Medica is strictly a decision-support layer and does not replace emergency response infrastructure.",
                    fontSize = 11.sp,
                    color = MedicaTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun SimulationOutputCard(response: ApiGatewayResponse<OrchestratedDecisionResponse>) {
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
                Text(
                    text = "GATEWAY INGRESS RESPONSE (HTTP ${response.statusCode})",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MedicaGreenDot
                )
                Text(
                    text = "Latency: ${response.latencyMs}ms",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            response.payload?.let { decision ->
                Text(text = "Triage: ${decision.triageCode}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MedicaTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Observations: " + decision.clinicalObservations.firstOrNull(), fontSize = 12.sp, color = MedicaTextSecondary)
                Text(text = "Action: " + decision.immediateActions.firstOrNull(), fontSize = 12.sp, color = MedicaTextSecondary)
                Text(text = "Evidence Retrieved: ${decision.retrievedEvidence.size} items (${decision.retrievedEvidence.joinToString { it.resourceId }})", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaAccentBlue)
                Text(text = "Provenance Hash: ${decision.provenanceHash}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                Text(text = "Audit Log UUID: ${decision.auditLogId}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
            }
        }
    }
}

@Composable
fun AuditTrailTelemetryCard() {
    val logs = AiOrchestrator.getAuditLogs()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp)),
        color = MedicaCardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "IMMUTABLE AUDIT LOGS & PROVENANCE TRAIL",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MedicaTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (logs.isEmpty()) {
                Text(text = "No request entries in current session. Execute live request to populate audit trail.", fontSize = 11.sp, color = MedicaTextSecondary)
            } else {
                logs.takeLast(3).forEach { log ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F1318))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = log.eventId, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MedicaAccentBlue)
                                Text(text = "${log.latencyMs}ms", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MedicaGreenDot)
                            }
                            Text(text = "Route: ${log.modelRoute} · RAG matches: ${log.ragSnippetsRetrieved}", fontSize = 11.sp, color = MedicaTextSecondary)
                            Text(text = "KMS Fingerprint: ${log.secretKeyFingerprint}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaTextSecondary)
                        }
                    }
                }
            }
        }
    }
}
