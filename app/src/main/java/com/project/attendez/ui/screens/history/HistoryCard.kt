package com.project.attendez.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.attendez.data.local.util.Summary

@Composable
fun HistoryCard(summary: Summary) {
    val presentRatio = if (summary.total == 0) 0f else summary.present.toFloat() / summary.total

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EventInformation(summary)
            Counts(summary)
            ProgressBar(presentRatio)
            Percentage(presentRatio)
        }
    }
}

@Composable
private fun EventInformation(summary: Summary) {
    Column {
        Text(text = summary.eventName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = summary.date,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Counts(summary: Summary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatChip("Present", summary.present, Color(0xFF4CAF50))
        StatChip("Absent", summary.absent, Color(0xFFF44336))
        StatChip("Total", summary.total, MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ProgressBar(presentRatio: Float) {
    LinearProgressIndicator(
        progress = { presentRatio },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50)),
        color = Color(0xFF4CAF50),
        trackColor = Color(0xFFF44336),
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}

@Composable
private fun ColumnScope.Percentage(presentRatio: Float) {
    Text(
        text = "${(presentRatio * 100).toInt()}% Present",
        fontWeight = FontWeight.Medium,
        modifier = Modifier.align(Alignment.End)
    )
}

@Composable
private fun StatChip(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}