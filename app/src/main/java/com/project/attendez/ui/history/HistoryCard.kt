package com.project.attendez.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.attendez.R
import com.project.attendez.data.local.util.Summary
import com.project.attendez.ui.theme.BackgroundGradient

@Composable
fun HistoryCard(summary: Summary) {
    val presentRatio = if (summary.total == 0) 0f else summary.present.toFloat() / summary.total

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(BackgroundGradient)
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
        Text(
            text = summary.eventName.replaceFirstChar { it.uppercase() },
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = summary.description.replaceFirstChar { it.uppercase() },
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.date),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = summary.date,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun Counts(summary: Summary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatChip("Present", summary.present, Color(0xFF34EE3E))
        StatChip("Absent", summary.absent, Color(0xFFF12828))
        StatChip("Total", summary.total, Color(0xFFFFD600))
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
        color = Color(0xFF34EE3E),
        trackColor = Color(0xFFF12828),
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}

@Composable
private fun ColumnScope.Percentage(presentRatio: Float) {
    Text(
        text = "${(presentRatio * 100).toInt()}% Present",
        fontWeight = FontWeight.Bold,
        color = Color(0xFF304FFE),
        modifier = Modifier.align(Alignment.End)
    )
}

@Composable
private fun StatChip(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}