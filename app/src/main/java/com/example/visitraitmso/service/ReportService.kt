package com.visitraitmso.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

interface ReportService {
    fun exportPDF(context: Context, visits: List<Visit>, getName: (Visit) -> String)
    fun exportCSV(context: Context, visits: List<Visit>, getName: (Visit) -> String)
}

class AndroidReportService : ReportService {

    override fun exportPDF(context: Context, visits: List<Visit>, getName: (Visit) -> String) {
        val pdf    = PdfDocument()
        val W      = 595
        val H      = 842
        val margin = 36f

        var pageNum = 1
        var page    = pdf.startPage(PdfDocument.PageInfo.Builder(W, H, pageNum).create())
        var canvas  = page.canvas

        val pTitle = Paint().apply {
            textSize = 16f; typeface = Typeface.DEFAULT_BOLD
            color    = android.graphics.Color.parseColor("#1a1a2e")
        }
        val pHead = Paint().apply {
            textSize = 9f; typeface = Typeface.DEFAULT_BOLD
            color    = android.graphics.Color.parseColor("#5C7CFA")
        }
        val pBody = Paint().apply {
            textSize = 9f
            color    = android.graphics.Color.parseColor("#374151")
        }
        val pSub = Paint().apply {
            textSize = 8f
            color    = android.graphics.Color.parseColor("#9CA3AF")
        }
        val pLine = Paint().apply {
            color = android.graphics.Color.parseColor("#E5E7EB"); strokeWidth = 0.5f
        }
        val pRowBg = Paint().apply {
            color = android.graphics.Color.parseColor("#F9FAFB")
        }

        val now = System.currentTimeMillis()
        var y = margin + 20f
        canvas.drawText("VISITRA ITMSO — Visit Report", margin, y, pTitle)
        y += 16f
        canvas.drawText(
            "Generated: ${DateTimeUtils.formatExport(now)}   |   Total: ${visits.size}",
            margin, y, pSub
        )
        y += 8f
        pLine.strokeWidth = 1f
        canvas.drawLine(margin, y, W - margin, y, pLine)
        pLine.strokeWidth = 0.5f
        y += 14f

        val cols = listOf(
            "NAME"     to margin,
            "TYPE"     to margin + 100f,
            "PURPOSE"  to margin + 160f,
            "TIME IN"  to margin + 300f,
            "TIME OUT" to margin + 400f,
            "STATUS"   to margin + 500f
        )
        cols.forEach { (lbl, x) -> canvas.drawText(lbl, x, y, pHead) }
        y += 5f
        pLine.strokeWidth = 1f
        canvas.drawLine(margin, y, W - margin, y, pLine)
        pLine.strokeWidth = 0.5f
        y += 13f

        visits.forEachIndexed { idx, v ->
            if (y > H - margin - 24f) {
                canvas.drawText(
                    "St. Bridget College Batangas · ITMSO   Page $pageNum",
                    margin, H - 18f, pSub
                )
                pdf.finishPage(page); pageNum++
                page   = pdf.startPage(PdfDocument.PageInfo.Builder(W, H, pageNum).create())
                canvas = page.canvas; y = margin + 20f
            }
            if (idx % 2 == 0) canvas.drawRect(margin - 4f, y - 10f, W - margin + 4f, y + 5f, pRowBg)
            canvas.drawText(getName(v).take(18),                         cols[0].second, y, pBody)
            canvas.drawText(v.visitorType,                               cols[1].second, y, pBody)
            canvas.drawText(v.purpose.take(18),                          cols[2].second, y, pBody)
            canvas.drawText(DateTimeUtils.formatTime(v.timeIn),                           cols[3].second, y, pBody)
            canvas.drawText(v.timeOut?.let { DateTimeUtils.formatTime(it) } ?: "—",      cols[4].second, y, pBody)
            canvas.drawText(if (v.timeOut != null) "Done" else "Active", cols[5].second, y, pBody)
            y += 16f
            canvas.drawLine(margin, y - 4f, W - margin, y - 4f, pLine)
        }
        canvas.drawText(
            "St. Bridget College Batangas · ITMSO   Page $pageNum",
            margin, H - 18f, pSub
        )
        pdf.finishPage(page)

        saveFile(context, "visitra_${DateTimeUtils.formatFileName(now)}.pdf", "application/pdf") { stream ->
            pdf.writeTo(stream)
        }
        pdf.close()
    }

    override fun exportCSV(context: Context, visits: List<Visit>, getName: (Visit) -> String) {
        val header = "Name,Visitor Type,ID,Purpose,Time In,Time Out,Status\n"
        val rows   = visits.joinToString("\n") { v ->
            listOf(
                getName(v),
                v.visitorType,
                v.studentId,
                v.purpose,
                DateTimeUtils.formatTime(v.timeIn),
                v.timeOut?.let { DateTimeUtils.formatTime(it) } ?: "Still In",
                if (v.timeOut != null) "Completed" else "Active"
            ).joinToString(",") { "\"$it\"" }
        }
        val now = System.currentTimeMillis()
        saveFile(context, "visitra_${DateTimeUtils.formatFileName(now)}.csv", "text/csv") { stream ->
            stream.write((header + rows).toByteArray())
        }
    }

    private fun saveFile(
        context:   Context,
        fileName:  String,
        mimeType:  String,
        writeData: (java.io.OutputStream) -> Unit
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE,    mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
                )
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use(writeData)
                    Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Export failed: could not create file", Toast.LENGTH_LONG).show()
                }
            } else {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!dir.exists()) dir.mkdirs()
                FileOutputStream(File(dir, fileName)).use(writeData)
                Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
