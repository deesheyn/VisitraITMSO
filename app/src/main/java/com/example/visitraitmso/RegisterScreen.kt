package com.visitraitmso.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

private val DarkBlue = Color(0xFF0F2A5F)
private val BlueMain = Color(0xFF1E3A8A)
private val BlueSoft = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {

    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("College") }
    var course by remember { mutableStateOf("") }
    var yearLevel by remember { mutableStateOf("") }

    var agreed by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }

    val bg = Brush.verticalGradient(
        listOf(DarkBlue, BlueMain, BlueSoft)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Register Student",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                "Enter student details below",
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ================= FORM =================
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.12f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = type == "College",
                            onClick = { type = "College" },
                            label = { Text("College") }
                        )

                        FilterChip(
                            selected = type == "IBED",
                            onClick = { type = "IBED" },
                            label = { Text("IBED") }
                        )
                    }

                    if (type == "College") {
                        OutlinedTextField(
                            value = course,
                            onValueChange = { course = it },
                            label = { Text("Course") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = yearLevel,
                            onValueChange = { yearLevel = it },
                            label = { Text("Grade Level") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ================= PRIVACY =================
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.12f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Privacy & Agreement",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        "Your data is collected for ITMSO service tracking and reporting purposes.",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = agreed,
                                onCheckedChange = { agreed = it }
                            )

                            Text("I agree to the terms.", color = Color.White)
                        }

                        TextButton(
                            onClick = { showPrivacy = true }
                        ) {
                            Text("View details")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ================= BUTTON =================
            Button(
                onClick = {

                    if (name.isBlank() || studentId.isBlank() || !agreed) return@Button

                    // 🔥 FIXED: SINGLE SOURCE OF TRUTH
                    AppStorage.students.add(
                        Student(
                            name = name,
                            studentId = studentId,
                            type = type,
                            course = course,
                            yearLevel = yearLevel
                        )
                    )

                    showDialog = true
                },
                enabled = agreed,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue
                )
            ) {
                Text("SAVE STUDENT", fontWeight = FontWeight.Bold)
            }
        }
    }

    // ================= SUCCESS =================
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            title = { Text("Success 🎉") },
            text = { Text("Student registered successfully!") }
        )
    }

    // ================= PRIVACY =================
    if (showPrivacy) {
        AlertDialog(
            onDismissRequest = { showPrivacy = false },
            confirmButton = {
                TextButton(onClick = { showPrivacy = false }) {
                    Text("Close")
                }
            },
            title = { Text("Privacy Policy") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        """
1. Data Collection
We collect student name, ID, course, and visit logs.

2. Purpose
Used for ITMSO visit tracking system.

3. Usage
Data is used only for school reporting and monitoring.

4. Security
Data is stored securely and accessed only by authorized staff.

5. Consent
By registering, you agree to data collection and processing.
                        """.trimIndent()
                    )
                }
            }
        )
    }
}