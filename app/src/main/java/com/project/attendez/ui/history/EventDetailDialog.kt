package com.project.attendez.ui.history

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.util.AttendanceWithAttendeeRaw
import com.project.attendez.ui.attendee.DailyAttendanceUI
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.Typography
import com.project.attendez.viewmodel.AttendanceViewModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EventDetailDialog(
    history: EventHistoryUI,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    var selectedDay by remember { mutableStateOf<DailyAttendanceUI?>(null) }
    var attendees by remember { mutableStateOf<List<AttendanceWithAttendeeRaw>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        AttendanceStatus.PRESENT,
        AttendanceStatus.ABSENT,
        AttendanceStatus.EXCUSE
    )

    LaunchedEffect(selectedDay) {
        selectedDay?.let { day ->
            isLoading = true

            val start = day.date.atStartOfDay()
            val end = day.date.atTime(23, 59, 59)

            attendees = attendanceViewModel.getAttendanceWithAttendeesByDate(
                history.event.id,
                start,
                end
            )

            isLoading = false
        }
    }

    val filtered = if (attendees.isEmpty()) emptyList()
    else attendees.filter { it.status == tabs[selectedTab] }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    exportPdfToDownloads(context, history, selectedDay, attendees)
                }) {
                    Text("Export PDF")
                }

                Button(onClick = {
                    exportExcelToDownloads(context, selectedDay, attendees)
                }) {
                    Text("Export Excel")
                }
            }
        },
        title = {
            Text(history.event.name, style = Typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {

                // DAY SELECTOR
                Text("Select Day", style = Typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(history.days) { day ->
                        val selected = selectedDay == day

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) BluePrimary else Color.LightGray)
                                .clickable { selectedDay = day }
                                .padding(10.dp)
                        ) {
                            Text(
                                day.dayLabel,
                                color = if (selected) Color.White else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    selectedDay == null -> {
                        Text("Select a day to view attendance", color = Color.Gray)
                    }

                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    attendees.isEmpty() -> {
                        Text("No attendance records", color = Color.Gray)
                    }

                    else -> {

                        // TABS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            tabs.forEachIndexed { index, status ->
                                val selected = selectedTab == index

                                Text(
                                    text = "${status.name} (${attendees.count { it.status == status }})",
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable { selectedTab = index }
                                        .background(if (selected) BluePrimary else Color.Transparent)
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = if (selected) Color.White else Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtered) { attendee ->
                                AttendeeRow(attendee)
                            }
                        }
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
fun exportPdfToDownloads(
    context: Context,
    history: EventHistoryUI,
    day: DailyAttendanceUI?,
    attendees: List<AttendanceWithAttendeeRaw>
) {
    if (day == null) return

    val pdfDocument = PdfDocument()

    val paint = Paint().apply {
        textSize = 10f
    }

    val pageInfo = PdfDocument.PageInfo.Builder(300, 800, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas

    var y = 40

    paint.textSize = 14f
    paint.isFakeBoldText = true
    canvas.drawText("Attendance Report", 60f, y.toFloat(), paint)

    y += 30

    paint.textSize = 10f
    paint.isFakeBoldText = false

    canvas.drawText("Event: ${history.event.name}", 10f, y.toFloat(), paint)
    y += 15

    canvas.drawText("Day: ${day.dayLabel}", 10f, y.toFloat(), paint)
    y += 25

    paint.isFakeBoldText = true
    canvas.drawText("Name", 10f, y.toFloat(), paint)
    canvas.drawText("ID", 120f, y.toFloat(), paint)
    canvas.drawText("Status", 200f, y.toFloat(), paint)

    y += 15
    canvas.drawLine(10f, y.toFloat(), 290f, y.toFloat(), paint)
    y += 15

    paint.isFakeBoldText = false

    attendees.forEach {
        if (y > 750) {
            pdfDocument.finishPage(page)
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = 40
        }

        canvas.drawText(it.fullName.take(15), 10f, y.toFloat(), paint)
        canvas.drawText(it.studentId, 120f, y.toFloat(), paint)
        canvas.drawText(it.status.name, 200f, y.toFloat(), paint)

        y += 15
    }

    pdfDocument.finishPage(page)

    val fileName = "attendance_${System.currentTimeMillis()}.pdf"

    try {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()

            openFile(context, uri, "application/pdf")
        } else {
            Toast.makeText(context, "Failed to create PDF", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "PDF export failed", Toast.LENGTH_SHORT).show()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun exportExcelToDownloads(
    context: Context,
    day: DailyAttendanceUI?,
    attendees: List<AttendanceWithAttendeeRaw>
) {
    if (day == null) return

    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Attendance")

    // Header
    val header = sheet.createRow(0)
    header.createCell(0).setCellValue("Name")
    header.createCell(1).setCellValue("Student ID")
    header.createCell(2).setCellValue("Status")

    attendees.forEachIndexed { index, attendee ->
        val row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(attendee.fullName)
        row.createCell(1).setCellValue(attendee.studentId)
        row.createCell(2).setCellValue(attendee.status.name)
    }

    val fileName = "attendance_${System.currentTimeMillis()}.xlsx"

    try {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                workbook.write(outputStream)
            }

            workbook.close()

            Toast.makeText(context, "Excel saved to Downloads", Toast.LENGTH_SHORT).show()

            openFile(context, uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        } else {
            Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Excel export failed", Toast.LENGTH_SHORT).show()
    }
}

fun openFile(context: Context, uri: Uri, mimeType: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Open with"))
}

@Composable
private fun AttendeeRow(attendee: AttendanceWithAttendeeRaw) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF2196F3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = attendee.fullName.first().toString(),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(attendee.fullName, color = Color.Black)
            Text("ID: ${attendee.studentId}", color = Color.Gray, fontSize = 12.sp)
            Text(
                "${attendee.course ?: ""} ${attendee.yearLevel ?: ""}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}