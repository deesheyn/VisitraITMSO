package com.visitraitmso.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

private val Navy = Color(0xFF0B1220)
private val Blue1 = Color(0xFF1E3A8A)
private val Blue2 = Color(0xFF2563EB)
private val CardBg = Color(0xFFF1F5FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginVisitScreen(navController: NavHostController) {

    var studentId by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("Select Purpose") }
    var expanded by remember { mutableStateOf(false) }

    var timeIn by remember { mutableStateOf("") }
    var timeOut by remember { mutableStateOf("") }
    var otherConcern by remember { mutableStateOf("") }

    // ✅ Dialog state
    var showDialog by remember { mutableStateOf(false) }

    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val suggestions = remember(studentId) {
        if (studentId.isBlank()) emptyList()
        else AppStorage.students.filter {
            it.studentId.contains(studentId, ignoreCase = true)
        }
    }

    val selectedStudent = AppStorage.students.find {
        it.studentId.equals(studentId, ignoreCase = true)
    }

    // ✅ Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false

                        // ✅ CLEAR FORM AFTER CONFIRMATION
                        studentId = ""
                        purpose = "Select Purpose"
                        otherConcern = ""
                        timeIn = ""
                        timeOut = ""
                    }
                ) {
                    Text("OK")
                }
            },
            title = { Text("Visit Completed") },
            text = { Text("The visit has been successfully recorded.") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visit Entry", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blue1)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(Navy, Blue1, Blue2))
                ),
            contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "ITMSO Service Portal",
                        fontWeight = FontWeight.Bold,
                        color = Blue1
                    )

                    Text(
                        "Student Visit Tracking System",
                        color = Color.DarkGray
                    )

                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    if (suggestions.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                suggestions.forEach { student ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .clickable {
                                                studentId = student.studentId
                                            }
                                    ) {
                                        Text(student.studentId, fontWeight = FontWeight.Bold)
                                        Text(student.name, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    selectedStudent?.let { student ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Text("Name: ${student.name}")
                                Text("Course: ${student.course}")
                                Text("Year: ${student.yearLevel}")
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = purpose,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Purpose of Visit") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf(
                                "MS 365 Assistance",
                                "School Automate Assistance",
                                "Canva Registration",
                                "Request Voucher",
                                "ID Printing",
                                "Other Concerns"
                            ).forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        purpose = it
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (purpose == "Other Concerns") {
                        OutlinedTextField(
                            value = otherConcern,
                            onValueChange = { otherConcern = it },
                            label = { Text("Specify Concern") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (timeIn.isNotEmpty()) {
                        Text("Time In: $timeIn", color = Blue1)
                    }

                    if (timeOut.isNotEmpty()) {
                        Text("Time Out: $timeOut", color = Blue1)
                    }

                    Button(
                        onClick = {

                            val now = formatter.format(Date())

                            timeIn = now
                            timeOut = ""

                            val finalPurpose =
                                if (purpose == "Other Concerns") otherConcern
                                else purpose

                            AppStorage.visits.add(
                                Visit(
                                    studentId = studentId,
                                    purpose = finalPurpose,
                                    timeIn = System.currentTimeMillis()
                                )
                            )
                        },
                        enabled = selectedStudent != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue1)
                    ) {
                        Text("TIME IN", color = Color.White)
                    }

                    Button(
                        onClick = {

                            val now = formatter.format(Date())
                            timeOut = now

                            val index = AppStorage.visits.indexOfLast {
                                it.studentId == studentId && it.timeOut == null
                            }

                            if (index != -1) {
                                val old = AppStorage.visits[index]
                                AppStorage.visits[index] =
                                    old.copy(timeOut = System.currentTimeMillis())
                            }

                            // ✅ Show confirmation dialog
                            showDialog = true
                        },
                        enabled = timeIn.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue2)
                    ) {
                        Text("TIME OUT", color = Color.White)
                    }
                }
            }
        }
    }
}