package com.visitraitmso.app

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

private val BgDeep       = Color(0xFF0A0F1E)
private val BgCard       = Color(0xFF111827)
private val BgTopBar     = Color(0xFF0D1426)
private val BgInput      = Color(0xFF0A0F1E)
private val AccentBlue   = Color(0xFF5C7CFA)
private val AccentGreen  = Color(0xFF10B981)
private val AccentPurple = Color(0xFF8B5CF6)
private val AccentAmber  = Color(0xFFF59E0B)
private val AccentRed    = Color(0xFFEF4444)
private val TextPrimary  = Color(0xFFE0E8FF)
private val TextMuted    = Color(0xFF6B7FC4)
private val TextSub      = Color(0xFF4A5680)
private val BorderColor  = Color(0xFF1E2D5A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var registerTab by remember { mutableStateOf(0) }

    // Firebase Auth States
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Profile States
    var studentName by remember { mutableStateOf("") }
    var studentId   by remember { mutableStateOf("") }
    var studentType by remember { mutableStateOf("College") }
    var course      by remember { mutableStateOf("") }
    var yearLevel   by remember { mutableStateOf("") }

    var facultyName by remember { mutableStateOf("") }
    var employeeId  by remember { mutableStateOf("") }
    var department  by remember { mutableStateOf("") }

    var extName     by remember { mutableStateOf("") }
    var extOrg      by remember { mutableStateOf("") }

    var agreed       by remember { mutableStateOf(false) }
    var showDialog   by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var isLoading    by remember { mutableStateOf(false) }

    fun resetAll() {
        email = ""; password = ""
        studentName  = ""; studentId   = ""; course     = ""; yearLevel  = ""
        facultyName  = ""; employeeId  = ""; department = ""
        extName      = ""; extOrg      = ""
        agreed       = false; studentType = "College"; yearExpanded = false
    }

    val tabAccent = when (registerTab) {
        0    -> AccentBlue
        1    -> AccentPurple
        else -> AccentAmber
    }

    val canSave = agreed && email.contains("@") && password.length >= 6 && when (registerTab) {
        0    -> studentName.isNotBlank() && studentId.isNotBlank()
        1    -> facultyName.isNotBlank() && employeeId.isNotBlank() && department.isNotBlank()
        else -> extName.isNotBlank()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor        = TextPrimary,
        unfocusedTextColor      = TextPrimary,
        cursorColor             = tabAccent,
        focusedBorderColor      = tabAccent,
        unfocusedBorderColor    = BorderColor,
        focusedContainerColor   = BgInput,
        unfocusedContainerColor = BgInput
    )

    Column(Modifier.fillMaxSize().background(BgDeep)) {

        // ── TOP BAR ──────────────────────────
        Row(
            Modifier.fillMaxWidth().background(BgTopBar).padding(16.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(BorderColor)
                    .clickable { navController.navigateUp() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = TextMuted, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Account Registration", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text("St. Bridget College - Visitra", color = TextMuted, fontSize = 11.sp)
            }
        }

        // ── TYPE TABS ─────────────────────────
        Row(
            Modifier.fillMaxWidth().background(BgTopBar).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Student", "Faculty", "External").forEachIndexed { i, label ->
                val selected = registerTab == i
                val accent   = when (i) { 0 -> AccentBlue; 1 -> AccentPurple; else -> AccentAmber }
                Box(
                    Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (selected) accent else BgCard)
                        .border(1.dp, if (selected) accent else BorderColor, RoundedCornerShape(10.dp))
                        .clickable { registerTab = i; resetAll() }.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selected) Color.White else TextMuted, fontSize = 13.sp)
                }
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── CREDENTIALS ──────────────────
            RegSection(title = "Login Credentials", accent = tabAccent) {
                RegField(email, { email = it }, "Email Address", Icons.Outlined.Email, "example@sbc.edu.ph", fieldColors)
                RegField(password, { password = it }, "Password", Icons.Outlined.Lock, "Min. 6 characters", fieldColors, PasswordVisualTransformation())
            }

            // ── PROFILE DATA ─────────────────
            if (registerTab == 0) {
                RegSection(title = "Student Details", accent = AccentBlue) {
                    RegField(studentName, { studentName = it }, "Full Name", Icons.Outlined.Person, "e.g. Maria Santos", fieldColors)
                    RegField(studentId, { studentId = it }, "Student ID", Icons.Outlined.Info, "e.g. 2024-00123", fieldColors)
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("College", "IBED").forEach { option ->
                            val selected = studentType == option
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (selected) AccentBlue else BgInput)
                                    .border(1.dp, if (selected) AccentBlue else BorderColor, RoundedCornerShape(10.dp))
                                    .clickable { studentType = option; if(option == "IBED") course = "" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(option, color = if (selected) Color.White else TextMuted, fontSize = 14.sp)
                            }
                        }
                    }

                    if (studentType == "College") {
                        RegField(course, { course = it }, "Course", Icons.Outlined.Build, "e.g. BSIT", fieldColors)
                        ExposedDropdownMenuBox(expanded = yearExpanded, onExpandedChange = { yearExpanded = !yearExpanded }) {
                            OutlinedTextField(
                                value = yearLevel, onValueChange = {}, readOnly = true,
                                label = { Text("Year Level", fontSize = 13.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                                colors = fieldColors, shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }, modifier = Modifier.background(BgCard)) {
                                listOf("1st Year", "2nd Year", "3rd Year", "4th Year").forEach { yr ->
                                    DropdownMenuItem(text = { Text(yr, color = TextPrimary) }, onClick = { yearLevel = yr; yearExpanded = false })
                                }
                            }
                        }
                    } else {
                        RegField(yearLevel, { yearLevel = it }, "Grade Level", Icons.Outlined.DateRange, "e.g. Grade 10", fieldColors)
                    }
                }
            } else if (registerTab == 1) {
                RegSection(title = "Faculty Details", accent = AccentPurple) {
                    RegField(facultyName, { facultyName = it }, "Full Name", Icons.Outlined.Person, "e.g. Dr. Juan Dela Cruz", fieldColors)
                    RegField(employeeId, { employeeId = it }, "Employee ID", Icons.Outlined.Lock, "e.g. EMP-2024", fieldColors)
                    RegField(department, { department = it }, "Department", Icons.Outlined.Home, "e.g. College of IT", fieldColors)
                }
            } else {
                RegSection(title = "Visitor Details", accent = AccentAmber) {
                    RegField(extName, { extName = it }, "Full Name", Icons.Outlined.Person, "e.g. Peter Parker", fieldColors)
                    RegField(extOrg, { extOrg = it }, "Organization / Purpose", Icons.Outlined.Info, "e.g. DepEd / Meeting", fieldColors)
                }
            }

            // ── AGREEMENT ────────────────────
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 100.dp)) {
                Checkbox(agreed, { agreed = it }, colors = CheckboxDefaults.colors(checkedColor = tabAccent))
                Text("I agree to the SBC Privacy Policy.", color = TextMuted, fontSize = 13.sp)
            }
        }

        // ── SAVE BUTTON ───────────────────────
        Surface(color = BgTopBar, modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        if (!canSave) return@Button
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    // Save to memory for session lookup
                                    when (registerTab) {
                                        0 -> AppStorage.students.add(Student(studentName, studentId, studentType, course, yearLevel))
                                        1 -> AppStorage.faculties.add(Faculty(facultyName, employeeId, department))
                                    }
                                    showDialog = true
                                } else {
                                    Toast.makeText(context, "Registration Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    },
                    enabled = canSave && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tabAccent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("CREATE ACCOUNT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                }, colors = ButtonDefaults.buttonColors(containerColor = tabAccent)) { Text("Back to Home") }
            },
            title = { Text("Account Created!", color = TextPrimary) },
            text = { Text("You can now use your email to log in to the system services.", color = TextMuted) },
            containerColor = BgCard
        )
    }
}

@Composable
fun RegSection(title: String, accent: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(BgCard).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(3.dp).height(16.dp).background(accent))
            Spacer(Modifier.width(8.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
fun RegField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String = "",
    colors: TextFieldColors,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { Text(placeholder, fontSize = 13.sp) },
        leadingIcon = { Icon(icon, null, modifier = Modifier.size(20.dp)) },
        singleLine = true,
        colors = colors,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = visualTransformation
    )
}
