package com.project.attendez.ui.attendee

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.util.DailyAttendanceRaw
import com.project.attendez.ui.theme.Typography

data class DailyAttendanceUI(
    val dayLabel: String, // "Day 1"
    val total: Int,
    val present: Int,
    val absent: Int,
    val excuse: Int
)

@Composable
fun AttendanceAnalyticsContent(
    days: List<DailyAttendanceUI>,
    onBarClick: (DailyAttendanceUI, AttendanceStatus) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(days) { day ->
            DayAnalyticsCard(day, onBarClick)
        }
    }
}

@Composable
fun DayAnalyticsCard(
    day: DailyAttendanceUI,
    onBarClick: (DailyAttendanceUI, AttendanceStatus) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        // 🔹 Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day.dayLabel,
                style = Typography.titleMedium,
                color = Color.Black
            )

            Text(
                text = "Total: ${day.total}",
                style = Typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnalyticsBar(
            label = "Present",
            value = day.present,
            total = day.total,
            color = Color(0xFF4CAF50),
            onClick = { onBarClick(day, AttendanceStatus.PRESENT) }
        )

        AnalyticsBar(
            label = "Absent",
            value = day.absent,
            total = day.total,
            color = Color(0xFFF44336),
            onClick = { onBarClick(day, AttendanceStatus.ABSENT) }
        )

        AnalyticsBar(
            label = "Excuse",
            value = day.excuse,
            total = day.total,
            color = Color(0xFFFFC107),
            onClick = { onBarClick(day, AttendanceStatus.EXCUSE) }
        )
    }
}

@Composable
fun AnalyticsBar(
    label: String,
    value: Int,
    total: Int,
    color: Color,
    onClick: () -> Unit
) {
    val percentage = if (total == 0) 0f else value.toFloat() / total
    val animatedProgress = animateValue(percentage)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.Black)
            Text("$value (${(percentage * 100).toInt()}%)", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress) // ✅ animated width
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(50))
            )
        }
    }
}

@Composable
fun animateValue(target: Float): Float {
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(800),
        label = ""
    )
    return animated
}

fun mapToDailyUI(data: List<DailyAttendanceRaw>): List<DailyAttendanceUI> {
    val grouped = data.groupBy { it.day }

    return grouped.entries.mapIndexed { index, entry ->
        val counts = entry.value.associate { it.status to it.count }

        val present = counts[AttendanceStatus.PRESENT] ?: 0
        val absent = counts[AttendanceStatus.ABSENT] ?: 0
        val excuse = counts[AttendanceStatus.EXCUSE] ?: 0

        DailyAttendanceUI(
            dayLabel = "Day ${index + 1}",
            total = present + absent + excuse,
            present = present,
            absent = absent,
            excuse = excuse
        )
    }
}