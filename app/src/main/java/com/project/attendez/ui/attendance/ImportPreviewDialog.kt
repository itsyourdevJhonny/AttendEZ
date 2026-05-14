package com.project.attendez.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueSecondary
import com.project.attendez.ui.theme.Typography
import com.project.attendez.ui.util.ImportPreview

@Composable
fun ImportPreviewDialog(
    previewList: List<ImportPreview>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(color = BluePrimary)
                    Text(text = "Importing...", color = BlueSecondary)
                }
            } else {
                Text(
                    text = "Preview (${previewList.size} rows)",
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    style = Typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))

                HorizontalDivider(color = Color.Black, thickness = 0.5.dp)

                val validCount = previewList.count { !it.isDuplicate }
                val duplicateCount = previewList.count { it.isDuplicate }

                Text(
                    text = "✔ $validCount valid • ⚠ $duplicateCount duplicates",
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                HorizontalDivider(color = Color.Black, thickness = 0.5.dp)

                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(previewList) { item ->

                        val color = if (item.isDuplicate) Color.Red else Color.Black

                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "${item.fullName} (${item.studentId})",
                                color = color,
                                fontWeight = FontWeight.Black
                            )

                            if (item.isDuplicate) {
                                Text(text = "⚠ ${item.reason}", color = Color.Red)
                            }/* else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Status: ",
                                        color = Color.Black
                                    )

                                    Text(
                                        text = "${item.status ?: "DEFAULT"}",
                                        color = when (item.status) {
                                            AttendanceStatus.PRESENT -> BluePrimary
                                            AttendanceStatus.ABSENT -> Color.Red
                                            AttendanceStatus.EXCUSE -> Color.Green
                                            else -> Color.DarkGray
                                        },
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }*/
                        }
                    }
                }

                HorizontalDivider(color = Color.Black, thickness = 0.5.dp)

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onCancel() }) {
                        Text(text = "Cancel", color = Color.Red.copy(alpha = 0.7f))
                    }

                    TextButton(onClick = { onConfirm(); isLoading = true }) {
                        Text(text = "Import", fontWeight = FontWeight.Black, color = BluePrimary)
                    }
                }
            }
        }
    }
}