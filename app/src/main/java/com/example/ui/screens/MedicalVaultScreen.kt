package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.ondevice.models.KnowledgeVaultType
import com.example.ondevice.models.LocalKnowledgeAsset
import com.example.ondevice.vault.ComprehensiveLocalMedicalVault
import com.example.ui.theme.MedicaAccentBlue
import com.example.ui.theme.MedicaActivePill
import com.example.ui.theme.MedicaBorderDark
import com.example.ui.theme.MedicaCardDark
import com.example.ui.theme.MedicaCardInner
import com.example.ui.theme.MedicaGreenDot
import com.example.ui.theme.MedicaRed
import com.example.ui.theme.MedicaTextMuted
import com.example.ui.theme.MedicaTextPrimary
import com.example.ui.theme.MedicaTextSecondary
import com.example.viewmodel.MedicaViewModel

@Composable
fun MedicalVaultScreen(
    viewModel: MedicaViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.vaultSearchQuery.collectAsState()
    val selectedFilter by viewModel.selectedVaultTypeFilter.collectAsState()
    val viewingMediaTitle by viewModel.viewingMediaTitle.collectAsState()

    var selectedAssetForDetail by remember { mutableStateOf<LocalKnowledgeAsset?>(null) }

    val allAssets = remember { ComprehensiveLocalMedicalVault.getAllAssets() }
    val filteredAssets = remember(searchQuery, selectedFilter) {
        allAssets.filter { asset ->
            val matchesFilter = when (selectedFilter) {
                null, "ALL" -> true
                else -> asset.type.name == selectedFilter
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                val q = searchQuery.lowercase().trim()
                asset.title.lowercase().contains(q) ||
                asset.category.lowercase().contains(q) ||
                asset.summary.lowercase().contains(q) ||
                asset.clinicalSteps.any { it.lowercase().contains(q) }
            }
            matchesFilter && matchesSearch
        }
    }

    val filterOptions = listOf(
        "ALL" to "All (3,200+)",
        KnowledgeVaultType.PROTOCOL.name to "Protocols",
        KnowledgeVaultType.DOCUMENT.name to "Documents",
        KnowledgeVaultType.IMAGE.name to "Images",
        KnowledgeVaultType.DIAGRAM.name to "Diagrams",
        KnowledgeVaultType.FLOWCHART.name to "Flowcharts",
        KnowledgeVaultType.DECISION_TREE.name to "Decision Trees",
        KnowledgeVaultType.VIDEO.name to "Videos",
        KnowledgeVaultType.AUDIO.name to "Audio",
        KnowledgeVaultType.STRUCTURED_DATA.name to "Structured Data"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Medical Knowledge Vault",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "100% Preloaded Offline",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(MedicaGreenDot)
                    )
                }
            }

            // On-Device Architecture Quick-Jump Icon
            IconButton(
                onClick = { viewModel.openOnDeviceArchitectureScreen() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MedicaGreenDot.copy(alpha = 0.15f))
                    .testTag("vault_open_architecture_icon")
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "On-Device Architecture",
                    tint = MedicaGreenDot,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setVaultSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("vault_search_input"),
            placeholder = {
                Text(
                    text = "Search emergency procedures, CPR, trauma, burns...",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setVaultSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MedicaAccentBlue,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // HORIZONTAL FILTER CHIPS (ALL 9 MODALITIES)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { (typeKey, label) ->
                val isSelected = (selectedFilter ?: "ALL") == typeKey
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setVaultTypeFilter(if (typeKey == "ALL") null else typeKey) },
                    label = {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MedicaAccentBlue,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MedicaAccentBlue
                    ),
                    modifier = Modifier.testTag("vault_filter_$typeKey")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ON-DEVICE CARRIER NOTICE CARD
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, MedicaBorderDark, RoundedCornerShape(10.dp))
                        .clickable { viewModel.openOnDeviceArchitectureScreen() },
                    color = MedicaCardDark
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MedicaGreenDot.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = MedicaGreenDot, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Medica carries its medical knowledge with it",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicaTextPrimary
                            )
                            Text(
                                text = "Thousands of multimodal protocols, diagrams & videos stay local.",
                                fontSize = 11.sp,
                                color = MedicaTextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = MedicaGreenDot,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // 25.6 GB CORPUS BREAKDOWN CARD
            item {
                VaultStorageBreakdownCard()
            }

            // EXTERNAL NEURAL NET RECOGNITION CARD
            item {
                ExternalNeuralNetVaultCard(
                    onOpenArchitecture = { viewModel.openOnDeviceArchitectureScreen() }
                )
            }

            // RESULTS HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLINICAL RESOURCES (${filteredAssets.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "LOCAL AIR-GAPPED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaGreenDot
                    )
                }
            }

            // LIST OF MEDICAL ASSETS (CARDS)
            items(filteredAssets) { asset ->
                MedicalAssetCard(
                    asset = asset,
                    onClick = { selectedAssetForDetail = asset }
                )
            }
        }
    }

    // MODAL DIALOG: STEP-BY-STEP EMERGENCY PROCEDURE DETAIL
    selectedAssetForDetail?.let { asset ->
        Dialog(onDismissRequest = { selectedAssetForDetail = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, MedicaAccentBlue, RoundedCornerShape(14.dp)),
                color = Color(0xFF131922)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MedicaAccentBlue.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${asset.type.label} · ${asset.sourceOrganization}",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaAccentBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = asset.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicaTextPrimary
                            )
                        }

                        IconButton(
                            onClick = { selectedAssetForDetail = null },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MedicaTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = asset.summary,
                        fontSize = 12.sp,
                        color = MedicaTextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "STEP-BY-STEP CLINICAL PROCEDURE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaAccentBlue
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F1318))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        asset.clinicalSteps.forEachIndexed { i, step ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "${i + 1}. ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicaAccentBlue,
                                    fontFamily = FontFamily.Monospace
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

                    if (asset.contraindications.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "CONTRAINDICATIONS & SAFETY WARNINGS",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicaRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        asset.contraindications.forEach { caution ->
                            Text(
                                text = "⚠️ $caution",
                                fontSize = 11.sp,
                                color = MedicaRed.copy(alpha = 0.9f),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Provenance: ${asset.evidenceProvenanceHash}",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MedicaTextSecondary
                        )

                        Button(
                            onClick = { selectedAssetForDetail = null },
                            colors = ButtonDefaults.buttonColors(containerColor = MedicaAccentBlue),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("DONE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalAssetCard(
    asset: LocalKnowledgeAsset,
    onClick: () -> Unit
) {
    val icon = when (asset.type) {
        KnowledgeVaultType.PROTOCOL -> Icons.Default.Policy
        KnowledgeVaultType.DOCUMENT -> Icons.Default.Description
        KnowledgeVaultType.IMAGE -> Icons.Default.Image
        KnowledgeVaultType.DIAGRAM -> Icons.Default.Hub
        KnowledgeVaultType.FLOWCHART -> Icons.Default.Schema
        KnowledgeVaultType.DECISION_TREE -> Icons.Default.AccountTree
        KnowledgeVaultType.VIDEO -> Icons.Default.Videocam
        KnowledgeVaultType.AUDIO -> Icons.Default.AudioFile
        KnowledgeVaultType.STRUCTURED_DATA -> Icons.Default.TableView
    }

    val typeColor = when (asset.type) {
        KnowledgeVaultType.PROTOCOL, KnowledgeVaultType.DECISION_TREE -> MedicaAccentBlue
        KnowledgeVaultType.VIDEO, KnowledgeVaultType.AUDIO -> Color(0xFFA371F7)
        KnowledgeVaultType.STRUCTURED_DATA -> MedicaGreenDot
        KnowledgeVaultType.FLOWCHART, KnowledgeVaultType.DIAGRAM -> Color(0xFFD29922)
        else -> MedicaAccentBlue
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("vault_item_${asset.id}"),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(typeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = asset.type.label.uppercase(),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                    Text(
                        text = "v${asset.version}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = asset.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = asset.summary,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun VaultStorageBreakdownCard(modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MedicaAccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { isExpanded = !isExpanded },
        color = Color(0xFF101620)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "25.6 GB Corpus Breakdown",
                            fontSize = 13.sp,
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
                                text = "4.18 GB ON FLASH",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MedicaGreenDot
                            )
                        }
                    }
                    Text(
                        text = "6.12x Compression Ratio · Seekable <1.4ms",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MedicaTextSecondary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MedicaAccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-segment storage proportion bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Box(modifier = Modifier.weight(0.281f).fillMaxSize().background(Color(0xFF4A90E2))) // Images 28.1%
                Box(modifier = Modifier.weight(0.219f).fillMaxSize().background(Color(0xFFE6A700))) // Videos 21.9%
                Box(modifier = Modifier.weight(0.219f).fillMaxSize().background(MedicaGreenDot))    // Texts 21.9%
                Box(modifier = Modifier.weight(0.109f).fillMaxSize().background(Color(0xFFA371F7))) // Flowcharts 10.9%
                Box(modifier = Modifier.weight(0.109f).fillMaxSize().background(Color(0xFF00B4D8))) // Audio 10.9%
                Box(modifier = Modifier.weight(0.063f).fillMaxSize().background(Color(0xFFF77F00))) // Structured 6.3%
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModalityStoragePill("Images (7.2 GB)", Color(0xFF4A90E2))
                ModalityStoragePill("Videos (5.6 GB)", Color(0xFFE6A700))
                ModalityStoragePill("Text (5.6 GB)", MedicaGreenDot)
                ModalityStoragePill("Flowcharts (2.8 GB)", Color(0xFFA371F7))
                ModalityStoragePill("Audio (2.8 GB)", Color(0xFF00B4D8))
                ModalityStoragePill("Structured (1.6 GB)", Color(0xFFF77F00))
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0A0E14))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ModalityDetailLine("1. Medical Imagery & Atlases", "7.2 GB raw → 1.14 GB (6.31x, AVIF lossless)", "1,200+ lesion/wound atlases")
                    ModalityDetailLine("2. Procedural HD Videos", "5.6 GB raw → 1.18 GB (4.74x, AV1 CRF-28)", "140+ surgical/trauma clips")
                    ModalityDetailLine("3. Clinical Texts & Protocols", "5.6 GB raw → 930 MB (6.02x, zstd-19 dict)", "420+ protocols & 850+ manuals")
                    ModalityDetailLine("4. Flowcharts & Decision Trees", "2.8 GB raw → 390 MB (7.18x, SVG vector/zstd)", "180+ triage & 95+ branching trees")
                    ModalityDetailLine("5. Auscultation Audio", "2.8 GB raw → 310 MB (9.03x, Opus 24kbps)", "210+ lung/heart acoustic files")
                    ModalityDetailLine("6. Structured Dosing & Guides", "1.6 GB raw → 230 MB (6.95x, columnar dict)", "350+ pediatric/trauma matrices")
                }
            }
        }
    }
}

@Composable
fun ModalityStoragePill(label: String, dotColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = MedicaTextSecondary
        )
    }
}

@Composable
fun ModalityDetailLine(title: String, stats: String, count: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MedicaTextPrimary)
            Text(text = count, fontSize = 10.sp, color = MedicaTextSecondary)
        }
        Text(text = stats, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MedicaAccentBlue)
    }
}

@Composable
fun ExternalNeuralNetVaultCard(
    onOpenArchitecture: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.2.dp, MedicaAccentBlue, RoundedCornerShape(10.dp))
            .clickable { onOpenArchitecture() },
        color = Color(0xFF0F1522)
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
                        text = "External Neural Vault Network",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MedicaGreenDot.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AIR-GAPPED NPU",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MedicaGreenDot
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Medica-VaultCrossNet v4.2 pairs with the local model to index-traverse this 25.6 GB vault in <150ms. It recognizes complex field presentations and synthesizes text steps, image landmarks, video keyframes, branching flowcharts, acoustic phonograms, and emergency dosing.",
                fontSize = 11.sp,
                color = MedicaTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "384-dim Dense PQ · 14,280 Vault Docs",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MedicaAccentBlue
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OPEN CONSOLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicaAccentBlue,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MedicaAccentBlue,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}
