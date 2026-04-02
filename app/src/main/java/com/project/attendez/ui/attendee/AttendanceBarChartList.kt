package com.project.attendez.ui.attendee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun AttendanceBarChartList(days: List<DailyAttendanceUI>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(days) { day ->
            DayBarChart(day)
        }
    }
}

@Composable
fun DayBarChart(day: DailyAttendanceUI) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {

        Text(
            text = day.dayLabel,
            color = Color.White,
            style = Typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            BarItem("Total", day.total, day.total, Color.Gray)
            BarItem("Present", day.present, day.total, Color.Green)
            BarItem("Absent", day.absent, day.total, Color.Red)
            BarItem("Excuse", day.excuse, day.total, Color.Yellow)
        }
    }
}

@Composable
fun BarItem(
    label: String,
    value: Int,
    max: Int,
    color: Color
) {
    val heightRatio = if (max == 0) 0f else value.toFloat() / max.toFloat()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {

        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight(heightRatio)
                .background(color, RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = value.toString(), color = Color.White, fontSize = 12.sp)

        Text(text = label, color = Color.LightGray, fontSize = 10.sp)
    }
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