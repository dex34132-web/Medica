package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UrgencyLevel
import com.example.ui.theme.MedicaAmber
import com.example.ui.theme.MedicaBlue
import com.example.ui.theme.MedicaGreen
import com.example.ui.theme.MedicaRed

@Composable
fun UrgencyBadge(
    level: UrgencyLevel,
    modifier: Modifier = Modifier,
    showCode: Boolean = false
) {
    val (bgColor, textColor, borderColor) = when (level) {
        UrgencyLevel.CRITICAL -> Triple(
            MedicaRed.copy(alpha = 0.2f),
            MedicaRed,
            MedicaRed.copy(alpha = 0.6f)
        )
        UrgencyLevel.URGENT -> Triple(
            MedicaAmber.copy(alpha = 0.2f),
            MedicaAmber,
            MedicaAmber.copy(alpha = 0.6f)
        )
        UrgencyLevel.MODERATE -> Triple(
            MedicaBlue.copy(alpha = 0.2f),
            MedicaBlue,
            MedicaBlue.copy(alpha = 0.6f)
        )
        UrgencyLevel.LOW -> Triple(
            MedicaGreen.copy(alpha = 0.2f),
            MedicaGreen,
            MedicaGreen.copy(alpha = 0.6f)
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )

            Text(
                text = if (showCode) level.code else level.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun UrgencyBanner(
    level: UrgencyLevel,
    triageCode: String,
    modifier: Modifier = Modifier
) {
    val (containerColor, accentColor) = when (level) {
        UrgencyLevel.CRITICAL -> Pair(MedicaRed.copy(alpha = 0.16f), MedicaRed)
        UrgencyLevel.URGENT -> Pair(MedicaAmber.copy(alpha = 0.16f), MedicaAmber)
        UrgencyLevel.MODERATE -> Pair(MedicaBlue.copy(alpha = 0.16f), MedicaBlue)
        UrgencyLevel.LOW -> Pair(MedicaGreen.copy(alpha = 0.16f), MedicaGreen)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(1.5.dp, accentColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = level.label,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = accentColor
                )

                Text(
                    text = triageCode,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
