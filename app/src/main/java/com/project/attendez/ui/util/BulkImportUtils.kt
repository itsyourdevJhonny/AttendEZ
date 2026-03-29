package com.project.attendez.ui.util

import android.content.Context
import android.net.Uri
import com.project.attendez.data.local.entity.AttendanceStatus
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object BulkImportUtils {
    fun detectColumns(headerRow: Row): Map<String, Int> {
        val map = mutableMapOf<String, Int>()

        headerRow.forEachIndexed { index, cell ->
            val value = cell.toString().lowercase()

            when {
                "student" in value || "id" in value -> map["studentId"] = index
                "name" in value -> map["fullName"] = index
                "course" in value || "program" in value -> map["course"] = index
                "year" in value -> map["yearLevel"] = index
                "status" in value || "attendance" in value -> map["status"] = index
            }
        }

        return map
    }

    fun parseExcel(context: Context, uri: Uri): List<ImportPreview> {
        val list = mutableListOf<ImportPreview>()

        context.contentResolver.openInputStream(uri)?.use { input ->
            val workbook = XSSFWorkbook(input)
            val sheet = workbook.getSheetAt(0)

            val header = sheet.getRow(0)
            val columns = detectColumns(header)

            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue

                fun getString(key: String) =
                    columns[key]?.let { row.getCell(it)?.toString() }

                fun getInt(key: String) =
                    columns[key]?.let { row.getCell(it)?.numericCellValue?.toInt() }

                val status = getString("status")?.uppercase()?.let {
                    when (it) {
                        "PRESENT" -> AttendanceStatus.PRESENT
                        "ABSENT" -> AttendanceStatus.ABSENT
                        "EXCUSE" -> AttendanceStatus.EXCUSE
                        else -> null
                    }
                }

                list.add(
                    ImportPreview(
                        studentId = getString("studentId"),
                        fullName = getString("fullName"),
                        course = getString("course"),
                        yearLevel = getInt("yearLevel"),
                        status = status
                    )
                )
            }
        }

        return list
    }
}