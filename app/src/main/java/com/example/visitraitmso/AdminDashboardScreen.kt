package com.visitraitmso.app

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

// ================= MODELS =================

data class VisitDisplay(
    val studentId: String,
    val name: String,
    val type: String,
    val courseOrYear: String,
    val purpose: String,
    val timeIn: Long,
    val timeOut: Long?
)

// ================= STORAGE =================

object SharedStorageBridge {
    val students = AppStorage.students
    val visits = AppStorage.visits
}

// ================= MAPPER =================

fun buildVisitDisplay(): List<VisitDisplay> {
    return AppStorage.visits.mapNotNull { visit ->

        val student = AppStorage.students.find {
            it.studentId == visit.studentId
        } ?: return@mapNotNull null

        val courseOrYear = if (student.type == "College") {
            student.course ?: "-"
        } else {
            student.yearLevel ?: "-"   // IBED replaces SHS
        }

        VisitDisplay(
            studentId = student.studentId,
            name = student.name,
            type = student.type,
            courseOrYear = courseOrYear,
            purpose = visit.purpose,
            timeIn = visit.timeIn,
            timeOut = visit.timeOut
        )
    }
}

// ================= ROOT =================

@Composable
fun AdminScreen() {

    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        AdminLogin(
            onLogin = { isLoggedIn = true }
        )
    } else {
        AdminDashboard(
            onLogout = { isLoggedIn = false }
        )
    }
}

// ================= LOGIN =================

@Composable
fun AdminLogin(onLogin: () -> Unit) {

    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1220)),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Admin Login", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red)
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (user == "admin" && pass == "sbc@1913") {
                            onLogin()
                        } else {
                            error = "Invalid credentials"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOGIN")
                }
            }
        }
    }
}

// ================= DASHBOARD =================

@Composable
fun AdminDashboard(onLogout: () -> Unit) {

    val context = LocalContext.current

    val students = SharedStorageBridge.students
    val visits = buildVisitDisplay()

    val total = visits.size
    val active = visits.count { it.timeOut == null }
    val done = visits.count { it.timeOut != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1220))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(20.dp))

        Text(
            "Admin Dashboard",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            "Welcome back, Admin!",
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(20.dp))

        // ================= STATS =================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("VISITS", total)
            StatCard("ACTIVE", active)
            StatCard("DONE", done)
        }

        Spacer(Modifier.height(16.dp))

        // ================= VISIT RECORDS =================
        Text(
            "Visit Records",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(visits) { v ->

                Card {
                    Column(
                        Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Text("ID: ${v.studentId}")
                        Text("Name: ${v.name}")
                        Text("Type: ${v.type}")
                        Text("Course/Grade: ${v.courseOrYear}")
                        Text("Purpose: ${v.purpose}")
                        Text("Time In: ${v.timeIn}")

                        Text(
                            "Status: " + if (v.timeOut == null) "ACTIVE" else "DONE",
                            color = if (v.timeOut == null)
                                Color(0xFF10B981)
                            else Color.Gray
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))

                Text(
                    "Registered Students",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            items(students) { s ->
                Card {
                    Column(
                        Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Text(s.name)
                        Text(s.studentId)
                    }
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
            }
        }

        // ================= EXPORT =================
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = { exportPdf(context, AppStorage.visits) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export PDF")
            }

            Button(
                onClick = { exportCsv(context, AppStorage.visits) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Excel (CSV)")
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout")
            }
        }
    }
}

// ================= STAT CARD =================

@Composable
fun StatCard(title: String, value: Int) {
    Card {
        Column(
            modifier = Modifier.padding(10.dp).width(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
            Text(value.toString(), fontWeight = FontWeight.Bold)
        }
    }
}

// ================= EXPORT PDF =================

fun exportPdf(context: Context, visits: List<Visit>) {

    val pdf = PdfDocument()
    val page = pdf.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
    val canvas = page.canvas
    val paint = Paint()

    var y = 50

    canvas.drawText("ADMIN REPORT", 200f, y.toFloat(), paint)
    y += 40

    visits.forEach {
        canvas.drawText("${it.studentId} | ${it.purpose}", 20f, y.toFloat(), paint)
        y += 25
    }

    pdf.finishPage(page)

    val file = File(context.getExternalFilesDir(null), "report.pdf")
    pdf.writeTo(FileOutputStream(file))
    pdf.close()
}

// ================= EXPORT CSV =================

fun exportCsv(context: Context, visits: List<Visit>) {

    val file = File(context.getExternalFilesDir(null), "report.csv")

    val text = buildString {
        append("StudentID,Purpose,Status\n")
        visits.forEach {
            append("${it.studentId},${it.purpose},${if (it.timeOut == null) "ACTIVE" else "DONE"}\n")
        }
    }

    file.writeText(text)
}