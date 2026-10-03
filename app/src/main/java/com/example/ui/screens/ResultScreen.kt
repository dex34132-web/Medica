package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActionItem
import com.example.model.KnowledgeAsset
import com.example.model.KnowledgeType
import com.example.model.MedicaResult
import com.example.ui.components.StatusHeader
import com.example.ui.components.UrgencyBanner
import com.example.ui.theme.MedicaAmber
import com.example.ui.theme.MedicaBlue
import com.example.ui.theme.MedicaGreen
import com.example.ui.theme.MedicaRed
import com.example.viewmodel.MainTab
import com.example.viewmodel.MedicaViewModel
import com.example.viewmodel.Screen

@Composable
fun ResultScreen(
    caseId: String,
    viewModel: MedicaViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCase by viewModel.selectedCase.collectAsState()
    val draftCase by viewModel.draftCase.collectAsState()
    val targetCase = if (selectedCase?.id == caseId) selectedCase else draftCase
    val result = targetCase?.result

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StatusHeader(
            title = "MEDICA RESULT",
            subtitle = "Case #$caseId Decision Support",
            navigationIcon = {
                IconButton(
                    onClick = {
                        viewModel.switchTab(MainTab.HOME)
                    },
                    modifier = Modifier.testTag("result_home_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return Home",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )

        if (result == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No result generated for this case.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. CLEAR URGENCY / TRIAGE STATUS
                item {
                    UrgencyBanner(
                        level = result.urgencyLevel,
                        triageCode = result.triageCode,
                        modifier = Modifier.testTag("result_urgency_banner")
                    )
                }

                // ESCALATION CALLOUT (if recommended)
                if (result.escalationRecommended) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, MedicaRed, RoundedCornerShape(8.dp)),
                            color = MedicaRed.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = MedicaRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ESCALATION RECOMMENDED",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MedicaRed,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = result.escalationReason ?: "Professional medical ALS assistance should be considered immediately.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { viewModel.navigateTo(Screen.Escalation(caseId)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicaRed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("open_escalation_confirmation")
                                ) {
                                    Text("REVIEW & CONFIRM ESCALATION", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // 2. OBSERVATIONS (Possible findings derived from submitted media)
                item {
                    SectionCard(title = "OBSERVATIONS (MEDIA-DERIVED FINDINGS)") {
                        result.observations.forEachIndexed { i, obs ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    color = MedicaBlue,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = obs,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 3. POSSIBLE EXPLANATIONS (Clearly NOT a definitive diagnosis)
                item {
                    SectionCard(title = "POSSIBLE EXPLANATIONS (NON-DEFINITIVE)") {
                        Text(
                            text = "Automated multimodal differential possibilities. Requires clinical physician examination.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        result.possibleConditions.forEach { cond ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cond,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 4. IMMEDIATE ACTIONS
                item {
                    SectionCard(title = "IMMEDIATE ACTIONS") {
                        result.immediateActions.sortedBy { it.priority }.forEach { action ->
                            ActionItemRow(action = action)
                        }
                    }
                }

                // 5. WARNINGS & LIMITATIONS
                item {
                    SectionCard(
                        title = "WARNINGS & FIELD CONTRAINDICATIONS",
                        accentColor = MedicaAmber
                    ) {
                        result.warnings.forEach { warning ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MedicaAmber,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = warning,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 6. UNCERTAINTY & CONFIDENCE
                item {
                    SectionCard(title = "AI UNCERTAINTY & CONFIDENCE") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MODEL CONFIDENCE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "${(result.confidence * 100).toInt()}%",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (result.confidence > 0.85f) MedicaGreen else MedicaAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { result.confidence },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (result.confidence > 0.85f) MedicaGreen else MedicaAmber,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = result.uncertainty,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }

                // 7. EVIDENCE (Retrieved local medical resources, ALL TAPPABLE)
                item {
                    SectionCard(title = "RETRIEVED MEDICAL EVIDENCE (${result.evidenceIds.size})") {
                        Text(
                            text = "Tap any resource below to inspect diagrams, flowcharts, or reference video segments:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        result.evidenceIds.forEach { evidenceId ->
                            val asset = viewModel.getAssetById(evidenceId)
                            if (asset != null) {
                                EvidenceItemRow(
                                    asset = asset,
                                    onClick = {
                                        if (asset.type == KnowledgeType.FLOWCHART) {
                                            viewModel.navigateTo(Screen.FlowchartViewer(asset.id))
                                        } else {
                                            viewModel.navigateTo(Screen.EvidenceViewer(asset.id, fromCaseId = caseId))
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }

            // BOTTOM TOOLBAR: SAVE / VIEW DOSSIER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.CaseDetail(caseId)) },
                        modifier = Modifier.weight(1f).testTag("view_full_case_dossier")
                    ) {
                        Text("View Full Case Dossier")
                    }

                    Button(
                        onClick = { viewModel.switchTab(MainTab.HOME) },
                        modifier = Modifier.weight(1f).testTag("save_and_finish_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicaGreen)
                    ) {
                        Text("Save & Exit", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    accentColor: Color = MedicaBlue,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun ActionItemRow(action: ActionItem) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(
                1.dp,
                if (action.isCritical) MedicaRed.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(6.dp)
            ),
        color = if (action.isCritical) MedicaRed.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (action.isCritical) MedicaRed else MedicaBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${action.priority}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = action.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EvidenceItemRow(
    asset: KnowledgeAsset,
    onClick: () -> Unit
) {
    val typeColor = when (asset.type) {
        KnowledgeType.PROTOCOL -> MedicaGreen
        KnowledgeType.FLOWCHART -> MedicaBlue
        KnowledgeType.DIAGRAM -> MedicaAmber
        KnowledgeType.VIDEO -> MedicaRed
        KnowledgeType.AUDIO -> MedicaAmber
        KnowledgeType.DOCUMENT -> MedicaBlue
        KnowledgeType.IMAGE -> MedicaGreen
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .testTag("evidence_item_${asset.id}"),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(typeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = asset.type.label.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = asset.title,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${asset.source} · v${asset.version}" + if (asset.videoTimestamp != null) " · ⏱ ${asset.videoTimestamp}" else "",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
