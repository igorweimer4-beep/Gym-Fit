package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.WorkoutSession
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun exportToPdf(
        context: Context,
        userEmail: String,
        sessions: List<WorkoutSession>,
        totalWaterMl: Int,
        currentWeightKg: Double,
        aiCoachText: String,
        onComplete: (File?) -> Unit
    ) {
        try {
            val pdfDocument = PdfDocument()
            // Standard A4 dimensions in postscript points: 595 x 842
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Paints
            val textPaint = Paint().apply {
                isAntiAlias = true
                color = Color.BLACK
            }
            val titlePaint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val sectionHeaderPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#1B5E20") // Slate Dark Green accent
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val boldPaint = Paint().apply {
                isAntiAlias = true
                color = Color.BLACK
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val normalPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#333333")
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val shadowPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#E0E0E0")
            }

            var y = 40f
            val marginX = 40f
            val printableWidth = 595f - (marginX * 2)

            // 1. Draw Title Header Banner
            val bannerHeight = 65f
            val bannerPaint = Paint().apply {
                color = Color.parseColor("#121212") // Dark charcoal gray fits gym look
            }
            canvas.drawRect(marginX, y, marginX + printableWidth, y + bannerHeight, bannerPaint)

            canvas.drawText("FITLOG - RELATÓRIO DE DESEMPENHO", marginX + 15f, y + 28f, titlePaint)
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateStr = sdf.format(Date())
            val subtitlePaint = Paint().apply {
                color = Color.parseColor("#CCCCCC")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }
            canvas.drawText("Atleta: $userEmail  |  Gerado em: $dateStr", marginX + 15f, y + 48f, subtitlePaint)
            y += bannerHeight + 25f

            // 2. Draw General Metrics Section
            canvas.drawText("1. MÉTRICAS ACUMULADAS DO CONTROLE FISÍCO", marginX, y, sectionHeaderPaint)
            y += 6f
            canvas.drawLine(marginX, y, marginX + printableWidth, y, Paint().apply { color = Color.parseColor("#C8E6C9"); strokeWidth = 1.5f })
            y += 20f

            // Metrics Box
            canvas.drawRect(marginX, y, marginX + printableWidth, y + 65f, Paint().apply { color = Color.parseColor("#F5F5F5") })
            canvas.drawRect(marginX, y, marginX + printableWidth, y + 65f, Paint().apply { color = Color.parseColor("#E0E0E0"); style = Paint.Style.STROKE; strokeWidth = 1f })

            val colWidth = printableWidth / 3f
            // Col 1: Workout volumes
            canvas.drawText("Total de Treinos", marginX + 15f, y + 22f, boldPaint)
            canvas.drawText("${sessions.size} sessões completadas", marginX + 15f, y + 42f, normalPaint)

            // Col 2: Water tracker stats
            canvas.drawText("Água Ingerida", marginX + colWidth + 15f, y + 22f, boldPaint)
            canvas.drawText("${totalWaterMl} mL cadastrados", marginX + colWidth + 15f, y + 42f, normalPaint)

            // Col 3: Weight Tracking
            canvas.drawText("Peso Cadastrado", marginX + (colWidth * 2) + 15f, y + 22f, boldPaint)
            canvas.drawText(if (currentWeightKg > 0.0) "$currentWeightKg kg" else "Não informado", marginX + (colWidth * 2) + 15f, y + 42f, normalPaint)
            y += 90f

            // 3. Draw Sessions List
            canvas.drawText("2. HISTÓRICO RECENTE DE SEÇÕES COMPLETADAS", marginX, y, sectionHeaderPaint)
            y += 6f
            canvas.drawLine(marginX, y, marginX + printableWidth, y, Paint().apply { color = Color.parseColor("#C8E6C9"); strokeWidth = 1.5f })
            y += 20f

            // Table headers
            val rowHeight = 22f
            canvas.drawRect(marginX, y, marginX + printableWidth, y + rowHeight, Paint().apply { color = Color.parseColor("#E8E8E8") })
            canvas.drawText("Data", marginX + 10f, y + 15f, boldPaint)
            canvas.drawText("Nome do Treino / Atividade", marginX + 110f, y + 15f, boldPaint)
            canvas.drawText("Duração", marginX + 410f, y + 15f, boldPaint)
            canvas.drawText("Obs", marginX + 480f, y + 15f, boldPaint)
            y += rowHeight

            val tableLimit = 6
            val sessionsToShow = sessions.take(tableLimit)
            val sessionDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            if (sessionsToShow.isEmpty()) {
                canvas.drawText("Nenhuma sessão de treino cadastrada até o momento.", marginX + 10f, y + 20f, normalPaint)
                y += 35f
            } else {
                for (session in sessionsToShow) {
                    canvas.drawRect(marginX, y, marginX + printableWidth, y + rowHeight, Paint().apply { color = Color.WHITE })
                    // Draw bottom border for rows
                    canvas.drawLine(marginX, y + rowHeight, marginX + printableWidth, y + rowHeight, Paint().apply { color = Color.parseColor("#EEEEEE"); strokeWidth = 1f })

                    val sessionDate = sessionDateFormat.format(Date(session.dateMillis))
                    canvas.drawText(sessionDate, marginX + 10f, y + 15f, normalPaint)

                    val titleText = if (session.title.length > 32) session.title.take(30) + "..." else session.title
                    canvas.drawText(titleText, marginX + 110f, y + 15f, normalPaint)

                    val durationStr = "${session.durationSeconds / 60} min"
                    canvas.drawText(durationStr, marginX + 410f, y + 15f, normalPaint)

                    val notesText = if (session.notes.length > 12) session.notes.take(10) + "..." else if (session.notes.isEmpty()) "N/A" else session.notes
                    canvas.drawText(notesText, marginX + 480f, y + 15f, normalPaint)

                    y += rowHeight
                }
                if (sessions.size > tableLimit) {
                    y += 5f
                    canvas.drawText("* Exibindo apenas as $tableLimit sessões de treino mais recentes de um total de ${sessions.size}.", marginX + 10f, y + 10f, Paint().apply { color = Color.GRAY; textSize = 9f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC) })
                    y += 15f
                }
            }
            y += 25f

            // 4. Draw AI Coach Expert Assessment
            canvas.drawText("3. DIRETRIZES E PARECER DE EVOLUÇÃO (AI COACH IA)", marginX, y, sectionHeaderPaint)
            y += 6f
            canvas.drawLine(marginX, y, marginX + printableWidth, y, Paint().apply { color = Color.parseColor("#C8E6C9"); strokeWidth = 1.5f })
            y += 20f

            // Frame background
            val coachFrameHeight = 220f
            canvas.drawRect(marginX, y, marginX + printableWidth, y + coachFrameHeight, Paint().apply { color = Color.parseColor("#F5FBFA") })
            canvas.drawRect(marginX, y, marginX + printableWidth, y + coachFrameHeight, Paint().apply { color = Color.parseColor("#B2DFDB"); style = Paint.Style.STROKE; strokeWidth = 1f })

            // Draw text wrap for AI lines
            val wrappedLines = mutableListOf<String>()
            val textPaintCoach = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#263238")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }

            val words = aiCoachText.split(" ")
            var currentLine = ""
            val paddingCoach = 15f
            val maxLineWidth = printableWidth - (paddingCoach * 2)

            for (word in words) {
                // If contains break lines, split them
                if (word.contains("\n")) {
                    val parts = word.split("\n")
                    for (i in parts.indices) {
                        val testLine = if (currentLine.isEmpty()) parts[i] else "$currentLine ${parts[i]}"
                        val widthMeasured = textPaintCoach.measureText(testLine)
                        if (widthMeasured > maxLineWidth) {
                            wrappedLines.add(currentLine)
                            currentLine = parts[i]
                        } else {
                            currentLine = testLine
                        }
                        if (i < parts.size - 1) {
                            wrappedLines.add(currentLine)
                            currentLine = ""
                        }
                    }
                } else {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val widthMeasured = textPaintCoach.measureText(testLine)
                    if (widthMeasured > maxLineWidth) {
                        wrappedLines.add(currentLine)
                        currentLine = word
                    } else {
                        currentLine = testLine
                    }
                }
            }
            if (currentLine.isNotEmpty()) {
                wrappedLines.add(currentLine)
            }

            var textY = y + 25f
            val lineSpacing = 13.5f
            for (line in wrappedLines.take(15)) { // Max lines to not overflow boundaries
                canvas.drawText(line, marginX + paddingCoach, textY, textPaintCoach)
                textY += lineSpacing
            }

            // Footer of page
            val footerPaint = Paint().apply {
                color = Color.GRAY
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }
            canvas.drawText("FitLog - Saúde, Disciplina e Alta Performance Sincronizada em Nuvem.", marginX + 10f, 810f, footerPaint)
            val pageNumberPaint = Paint().apply {
                color = Color.GRAY
                textSize = 9f
            }
            canvas.drawText("Página 1 de 1", 595f - marginX - 60f, 810f, pageNumberPaint)

            pdfDocument.finishPage(page)

            // Save document file to cache directory so we can share it easily
            val file = File(context.cacheDir, "Relatorio_Mensal_Treino.pdf")
            if (file.exists()) {
                file.delete()
            }
            pdfDocument.writeTo(file.outputStream())
            pdfDocument.close()

            onComplete(file)
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(null)
        }
    }

    fun sharePdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Relatório Mensal de Desempenho - FitLog")
                putExtra(Intent.EXTRA_TEXT, "Confira meu relatório de evolução de treino exportado diretamente do FitLog!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Exportar Relatório PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
