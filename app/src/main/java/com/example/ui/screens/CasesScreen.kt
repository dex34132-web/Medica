package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MediaType
import com.example.model.UrgencyLevel
import com.example.ondevice.models.LocalMedicaResult
import com.example.ui.theme.MedicaAccentBlue
import com.example.ui.theme.MedicaBorderDark
import com.example.ui.theme.MedicaCardDark
import com.example.ui.theme.MedicaGreenDot
import com.example.ui.theme.MedicaRed
import com.example.ui.theme.MedicaTextPrimary
import com.example.ui.theme.MedicaTextSecondary
import com.example.viewmodel.MainTab
import com.example.viewmodel.MedicaViewModel

@Composable
fun CasesScreen(
    viewModel: MedicaViewModel,
    modifier: Modifier = Modifier
) {
    val allCases by viewModel.allCases.collectAsState()
    val currentFilterTab by viewModel.casesFilterTab.collectAsState()
    val searchQuery by viewModel.casesSearchQuery.collectAsState()
    val isSearchActive by viewModel.isCasesSearchActive.collectAsState()
    val expandedIds by viewModel.expandedCaseIds.collectAsState()
    val activeCaseEvaluation by viewModel.activeCaseEvaluation.collectAsState()
    val evaluatingCaseId by viewModel.evaluatingCaseId.collectAsState()

    val tabs = listOf("Active", "Pending", "All")

    val filteredList = allCases.filter { c ->
        val matchesTab = when (currentFilterTab) {
            "Active" -> c.status == "Active"
            "Pending" -> c.status == "Pending"
            else -> true
        }
        val q = searchQuery.trim().lowercase()
        val matchesQuery = q.isEmpty() ||
                c.title.lowercase().contains(q) ||
                c.id.lowercase().contains(q) ||
                c.demographic.lowercase().contains(q) ||
                c.notes.lowercase().contains(q)
        matchesTab && matchesQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP HEADER: Back arrow <, Title "Cases", Search icon 🔍, Plus icon +
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.switchTab(MainTab.CHAT) },
                modifier = Modifier.testTag("cases_back_to_home")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleCasesSearch() },
                    modifier = Modifier.testTag("cases_search_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isSearchActive) MedicaAccentBlue else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.openNewCaseScreen() },
                    modifier = Modifier.testTag("cases_add_new_case")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Case",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Title "Cases"
        Text(
            text = "Cases",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        // Expandable search field
        AnimatedVisibility(visible = isSearchActive) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setCasesSearchQuery(it) },
                    placeholder = { Text("Filter cases by symptom, ID...", fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cases_search_input"),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setCasesSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MedicaAccentBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TAB BAR: Active, Pending, All
        val selectedIndex = tabs.indexOf(currentFilterTab).coerceAtLeast(0)
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            contentColor = MedicaAccentBlue,
            divider = {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            },
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = MedicaAccentBlue,
                        height = 2.dp
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            tabs.forEach { tabName ->
                val isSelected = currentFilterTab == tabName
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.setCasesFilterTab(tabName) },
                    text = {
                        Text(
                            text = tabName,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MedicaAccentBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("cases_tab_$tabName")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // GROUPED CASES LIST CONTAINER
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No cases found in this section.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            filteredList.forEachIndexed { index, itemCase ->
                                val isExpanded = expandedIds.contains(itemCase.id)

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.toggleCaseExpanded(itemCase.id) }
                                            .padding(horizontal = 14.dp, vertical = 14.dp)
                                            .testTag("case_row_${itemCase.id}"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // [+] Medical Kit Icon
                                            Icon(
                                                imageVector = Icons.Outlined.MedicalServices,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = itemCase.title,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "ID: ${itemCase.id} - ${itemCase.timeAgo}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = itemCase.demographic,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = "Toggle",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    // Accordion details
                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                        ) {
                                            if (itemCase.notes.isNotBlank()) {
                                                Text(
                                                    text = "Clinical Notes:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = itemCase.notes,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }

                                            // Display attached photos, videos, and microphone recordings
                                            if (itemCase.mediaItems.isNotEmpty()) {
                                                Text(
                                                    text = "Attached Media (${itemCase.mediaItems.size}):",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))

                                                itemCase.mediaItems.forEach { media ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 3.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = when (media.type) {
                                                                MediaType.PHOTO -> Icons.Default.Image
                                                                MediaType.VIDEO -> Icons.Default.Videocam
                                                                MediaType.AUDIO -> Icons.Default.Audiotrack
                                                            },
                                                            contentDescription = null,
                                                            tint = MedicaAccentBlue,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = media.name,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = "(${media.durationOrSize})",
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }

                                            // On-Device AI Decision Support section
                                            val isEvaluatingThisCase = evaluatingCaseId == itemCase.id
                                            val evaluationResult = if (activeCaseEvaluation?.caseId == itemCase.id) activeCaseEvaluation else null

                                            if (evaluationResult != null) {
                                                CaseAiDecisionSupportCard(
                                                    result = evaluationResult,
                                                    onDismiss = { viewModel.dismissCaseEvaluation() }
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (isEvaluatingThisCase) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(16.dp),
                                                            strokeWidth = 2.dp,
                                                            color = MedicaGreenDot
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = "Querying 25 GB Vault & NPU...",
                                                            fontSize = 11.sp,
                                                            fontFamily = FontFamily.Monospace,
                                                            color = MedicaGreenDot
                                                        )
                                                    }
                                                } else {
                                                    Button(
                                                        onClick = { viewModel.evaluateCaseLocally(itemCase) },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MedicaCardDark,
                                                            contentColor = MedicaGreenDot
                                                        ),
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MedicaGreenDot.copy(alpha = 0.5f)),
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                        modifier = Modifier.testTag("run_ai_case_${itemCase.id}")
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Memory,
                                                            contentDescription = null,
                                                            tint = MedicaGreenDot,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = if (evaluationResult == null) "RUN ON-DEVICE AI" else "RE-EVALUATE",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = FontFamily.Monospace,
                                                            color = MedicaGreenDot
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = { viewModel.deleteCase(itemCase.id) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Case",
                                                        tint = MedicaRed,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (index < filteredList.size - 1) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaseAiDecisionSupportCard(
    result: LocalMedicaResult,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.2.dp, MedicaGreenDot, RoundedCornerShape(10.dp)),
        color = Color(0xFF0F141C)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Triage Code & Air-Gapped chip & Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (result.urgencyLevel) {
                                    UrgencyLevel.CRITICAL -> MedicaRed.copy(alpha = 0.25f)
                                    UrgencyLevel.URGENT -> Color(0xFFE6A700).copy(alpha = 0.25f)
                                    else -> MedicaGreenDot.copy(alpha = 0.25f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = result.triageCode,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = when (result.urgencyLevel) {
                                UrgencyLevel.CRITICAL -> MedicaRed
                                UrgencyLevel.URGENT -> Color(0xFFE6A700)
                                else -> MedicaGreenDot
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MedicaGreenDot.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "100% OFFLINE NPU",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MedicaGreenDot
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss AI assessment",
                        tint = MedicaTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Model: ${result.usedModelName} · Runtime: ${result.usedRuntime.label} (${result.latencyMs} ms)",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MedicaTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // External Neural Network Recognized Presentation & AI Explanation
            result.multimodalPlan?.let { plan ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF141C28))
                        .border(1.dp, MedicaAccentBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MedicaAccentBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEURAL NETWORK RECOGNITION & CLINICAL EXPLANATION",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MedicaAccentBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = plan.conditionRecognized,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = plan.aiExplanation,
                            fontSize = 11.sp,
                            color = MedicaTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Retrieved Knowledge Evidence (from 25 GB Vault)
            if (result.retrievedEvidence.isNotEmpty()) {
                Text(
                    text = "RETRIEVED FROM 25 GB VAULT (${result.retrievedEvidence.size} MATCHES)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaAccentBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                result.retrievedEvidence.forEach { asset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(MedicaAccentBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "[${asset.type.label}] ${asset.title}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
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
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // MULTIMODAL STEP GUIDANCE (TEXT, IMAGE, VIDEO, FLOWCHART, DOSING)
            result.multimodalPlan?.let { plan ->
                var selectedModalStepTab by remember { mutableStateOf("TEXT") }
                val modalTabs = listOf(
                    "TEXT" to "Text Steps (${plan.textSteps.size})",
                    "IMAGE" to "Image Steps (${plan.imageSteps.size})",
                    "VIDEO" to "Video Steps (${plan.videoSteps.size})",
                    "FLOWCHART" to "Flowchart (${plan.flowchartSteps.size})",
                    "DOSING" to "Dosing Matrix"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    modalTabs.forEach { (tabKey, label) ->
                        val isSel = selectedModalStepTab == tabKey
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    1.dp,
                                    if (isSel) MedicaGreenDot else MedicaBorderDark,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedModalStepTab = tabKey },
                            color = if (isSel) MedicaGreenDot.copy(alpha = 0.15f) else Color(0xFF0D1117)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MedicaGreenDot else MedicaTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (selectedModalStepTab) {
                    "TEXT" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0B0F15))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            plan.textSteps.forEachIndexed { i, step ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(
                                        text = "${i + 1}. ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicaGreenDot,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(text = step, fontSize = 11.sp, color = MedicaTextPrimary, lineHeight = 15.sp)
                                }
                            }
                        }
                    }
                    "IMAGE" -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            plan.imageSteps.forEach { imgStep ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, MedicaAccentBlue.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                                    color = Color(0xFF0B0F15)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Image Step ${imgStep.stepNumber}: ${imgStep.title}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MedicaAccentBlue
                                            )
                                            Text(
                                                text = imgStep.atlasReference,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = MedicaTextSecondary
                                            )
                                        }
                                        Text(
                                            text = "Landmark: ${imgStep.anatomicalLandmark}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MedicaGreenDot
                                        )
                                        Text(
                                            text = imgStep.visualDescription,
                                            fontSize = 11.sp,
                                            color = MedicaTextPrimary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    "VIDEO" -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            plan.videoSteps.forEach { vidStep ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, Color(0xFFE6A700).copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                                    color = Color(0xFF0B0F15)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(Color(0xFFE6A700).copy(alpha = 0.2f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "TIMESTAMP ${vidStep.timestamp}",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE6A700)
                                                )
                                            }
                                            Text(
                                                text = vidStep.phase,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MedicaTextPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = vidStep.actionDemonstrated,
                                            fontSize = 11.sp,
                                            color = MedicaTextPrimary,
                                            lineHeight = 15.sp
                                        )
                                        Text(
                                            text = "Tip: ${vidStep.technicalTip}",
                                            fontSize = 10.sp,
                                            color = MedicaTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    "FLOWCHART" -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            plan.flowchartSteps.forEach { fc ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, Color(0xFFA371F7).copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                                    color = Color(0xFF0B0F15)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "[${fc.nodeId}] DECISION: ${fc.decisionCondition}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFA371F7)
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row {
                                            Text(text = "✓ IF TRUE: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedicaGreenDot, fontFamily = FontFamily.Monospace)
                                            Text(text = fc.branchIfTrue, fontSize = 10.sp, color = MedicaTextPrimary)
                                        }
                                        Row {
                                            Text(text = "✗ IF FALSE: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedicaRed, fontFamily = FontFamily.Monospace)
                                            Text(text = fc.branchIfFalse, fontSize = 10.sp, color = MedicaTextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "DOSING" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0B0F15))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            plan.dosingMatrix.forEach { dose ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(text = "💊 ", fontSize = 10.sp)
                                    Text(text = dose, fontSize = 11.sp, color = MedicaTextPrimary, lineHeight = 15.sp)
                                }
                            }
                            if (plan.contraindications.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "CONTRAINDICATIONS & CAUTIONS:",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaRed
                                )
                                plan.contraindications.forEach { caution ->
                                    Text(text = "⚠️ $caution", fontSize = 10.sp, color = MedicaRed, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Differential Considerations / Explanations
            Text(
                text = "DIFFERENTIAL CONSIDERATIONS",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MedicaTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            result.possibleExplanations.forEach { exp ->
                Text(
                    text = "• $exp",
                    fontSize = 11.sp,
                    color = MedicaTextPrimary,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prioritized Actions
            Text(
                text = "PRIORITIZED FIELD ACTIONS",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MedicaGreenDot
            )
            Spacer(modifier = Modifier.height(4.dp))
            result.immediateActions.forEachIndexed { i, action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${i + 1}. ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaGreenDot,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = action,
                        fontSize = 11.sp,
                        color = MedicaTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Provenance Hash & Uncertainty disclosure
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF090D14))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prov: ${result.provenanceHash}",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaTextSecondary
                )
                Text(
                    text = "Conf: ${(result.uncertaintyScore * 100).toInt()}%",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MedicaGreenDot
                )
            }
        }
    }
}
