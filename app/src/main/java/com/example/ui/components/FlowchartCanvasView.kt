package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.model.FlowNode
import com.example.model.FlowchartModel
import com.example.model.UrgencyLevel
import com.example.ui.theme.MedicaAmber
import com.example.ui.theme.MedicaBlue
import com.example.ui.theme.MedicaGreen
import com.example.ui.theme.MedicaRed

@Composable
fun FlowchartInteractiveView(
    model: FlowchartModel,
    modifier: Modifier = Modifier
) {
    var currentNodeId by remember(model) { mutableStateOf(model.startNodeId) }
    val history = remember(model) { mutableStateListOf(model.startNodeId) }

    val currentNode = model.nodes[currentNodeId] ?: model.nodes[model.startNodeId] ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Flowchart Header & Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MedicaBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DECISION TREE RUNNER",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicaBlue,
                    letterSpacing = 0.5.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Step ${history.size}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        history.clear()
                        history.add(model.startNodeId)
                        currentNodeId = model.startNodeId
                    },
                    modifier = Modifier.size(32.dp).testTag("flowchart_restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart flowchart",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // History Breadcrumb Trail
        if (history.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Path: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                history.forEachIndexed { index, nodeId ->
                    val node = model.nodes[nodeId]
                    val isCurrent = index == history.lastIndex
                    Text(
                        text = node?.title?.take(16)?.plus("…") ?: nodeId,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isCurrent) MedicaBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                    if (index < history.lastIndex) {
                        Text(
                            text = " → ",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        // Active Node Card
        val (nodeBg, nodeBorder) = when (currentNode.outcomeLevel) {
            UrgencyLevel.CRITICAL -> Pair(MedicaRed.copy(alpha = 0.15f), MedicaRed)
            UrgencyLevel.URGENT -> Pair(MedicaAmber.copy(alpha = 0.15f), MedicaAmber)
            UrgencyLevel.MODERATE -> Pair(MedicaBlue.copy(alpha = 0.15f), MedicaBlue)
            UrgencyLevel.LOW -> Pair(MedicaGreen.copy(alpha = 0.15f), MedicaGreen)
            null -> Pair(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outline)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.5.dp, nodeBorder, RoundedCornerShape(8.dp)),
            color = nodeBg
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentNode.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (currentNode.outcomeLevel != null) {
                        UrgencyBadge(level = currentNode.outcomeLevel)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentNode.questionOrDetail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                if (currentNode.actionAdvice != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MedicaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Field Action: " + currentNode.actionAdvice,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Decision Branch Buttons
        if (currentNode.isTerminal || (currentNode.yesNodeId == null && currentNode.noNodeId == null)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MedicaGreen.copy(alpha = 0.15f))
                    .border(1.dp, MedicaGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MedicaGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Protocol Branch Completed. Re-evaluate as clinical status changes.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MedicaGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    history.clear()
                    history.add(model.startNodeId)
                    currentNodeId = model.startNodeId
                },
                modifier = Modifier.fillMaxWidth().testTag("flowchart_evaluate_again")
            ) {
                Text("Re-evaluate from Start")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentNode.yesNodeId != null) {
                    Button(
                        onClick = {
                            val nextId = currentNode.yesNodeId
                            if (model.nodes.containsKey(nextId)) {
                                history.add(nextId)
                                currentNodeId = nextId
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicaRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("flowchart_yes_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "YES", fontWeight = FontWeight.Bold)
                    }
                }

                if (currentNode.noNodeId != null) {
                    Button(
                        onClick = {
                            val nextId = currentNode.noNodeId
                            if (model.nodes.containsKey(nextId)) {
                                history.add(nextId)
                                currentNodeId = nextId
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicaBlue),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("flowchart_no_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "NO", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (history.size > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        history.removeAt(history.lastIndex)
                        currentNodeId = history.last()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("flowchart_step_back")
                ) {
                    Text("← Step Back to Previous Node")
                }
            }
        }
    }
}
