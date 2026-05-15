package com.visitraitmso.app

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────
//  THEME
// ─────────────────────────────────────────────

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

// ─────────────────────────────────────────────
//  SCREEN
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {

    var registerTab by remember { mutableStateOf(0) }

    // Student
    var studentName by remember { mutableStateOf("") }
    var studentId   by remember { mutableStateOf("") }
    var studentType by remember { mutableStateOf("College") }
    var course      by remember { mutableStateOf("") }
    var yearLevel   by remember { mutableStateOf("") }

    // Faculty
    var facultyName by remember { mutableStateOf("") }
    var employeeId  by remember { mutableStateOf("") }
    var department  by remember { mutableStateOf("") }

    // External
    var extName by remember { mutableStateOf("") }
    var extOrg  by remember { mutableStateOf("") }

    // Shared
    var agreed       by remember { mutableStateOf(false) }
    var showDialog   by remember { mutableStateOf(false) }
    var showPrivacy  by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var isLoading    by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun resetAll() {
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

    val canSave = agreed && when (registerTab) {
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
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BorderColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { navController.navigateUp() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint     = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Register",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
                Text(
                    when (registerTab) {
                        0    -> "Create a new student record"
                        1    -> "Create a new faculty record"
                        else -> "Register an external visitor"
                    },
                    color    = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // ── TYPE TABS ─────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Student", "Faculty", "External").forEachIndexed { i, label ->
                val selected = registerTab == i
                val accent   = when (i) { 0 -> AccentBlue; 1 -> AccentPurple; else -> AccentAmber }
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) accent else BgCard)
                        .border(1.dp, if (selected) accent else BorderColor, RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { registerTab = i; resetAll() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color      = if (selected) Color.White else TextMuted,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize   = 13.sp
                    )
                }
            }
        }

        // ── SCROLLABLE BODY ──────────────────
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ══ STUDENT TAB ══════════════════
            if (registerTab == 0) {

                RegSection(title = "Personal Information", accent = AccentBlue) {
                    RegField(
                        value         = studentName,
                        onValueChange = { studentName = it },
                        label         = "Full Name",
                        icon          = Icons.Outlined.Person,
                        placeholder   = "e.g. Maria Santos",
                        colors        = fieldColors
                    )
                    RegField(
                        value         = studentId,
                        onValueChange = { studentId = it },
                        label         = "Student ID",
                        icon          = Icons.Outlined.Lock,
                        placeholder   = "e.g. 2024-00123",
                        colors        = fieldColors
                    )
                }

                RegSection(title = "Student Type", accent = AccentBlue) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("College", "IBED").forEach { option ->
                            val selected = studentType == option
                            Box(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) AccentBlue else BgInput)
                                    .border(
                                        1.dp,
                                        if (selected) AccentBlue else BorderColor,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) {
                                        studentType  = option
                                        yearLevel    = ""
                                        course       = ""
                                        yearExpanded = false
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    option,
                                    color      = if (selected) Color.White else TextMuted,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    if (studentType == "College") {
                        RegField(
                            value         = course,
                            onValueChange = { course = it },
                            label         = "Course",
                            icon          = Icons.Outlined.Person,
                            placeholder   = "e.g. BS Information Technology",
                            colors        = fieldColors
                        )

                        // Year Level dropdown
                        ExposedDropdownMenuBox(
                            expanded         = yearExpanded,
                            onExpandedChange = { yearExpanded = !yearExpanded }
                        ) {
                            OutlinedTextField(
                                value         = yearLevel,
                                onValueChange = {},
                                readOnly      = true,
                                label         = { Text("Year Level", color = TextMuted, fontSize = 13.sp) },
                                placeholder   = { Text("Select year level...", color = TextSub, fontSize = 13.sp) },
                                trailingIcon  = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded)
                                },
                                colors   = fieldColors,
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded         = yearExpanded,
                                onDismissRequest = { yearExpanded = false },
                                modifier         = Modifier.background(BgCard)
                            ) {
                                listOf("1st Year", "2nd Year", "3rd Year", "4th Year").forEach { yr ->
                                    DropdownMenuItem(
                                        text     = { Text(yr, color = TextPrimary, fontSize = 13.sp) },
                                        onClick  = { yearLevel = yr; yearExpanded = false },
                                        modifier = Modifier.background(BgCard)
                                    )
                                }
                            }
                        }
                    } else {
                        RegField(
                            value         = yearLevel,
                            onValueChange = { yearLevel = it },
                            label         = "Grade Level",
                            icon          = Icons.Outlined.Person,
                            placeholder   = "e.g. Grade 10",
                            colors        = fieldColors
                        )
                    }
                }
            }

            // ══ FACULTY TAB ══════════════════
            if (registerTab == 1) {

                RegSection(title = "Faculty Information", accent = AccentPurple) {
                    RegField(
                        value         = facultyName,
                        onValueChange = { facultyName = it },
                        label         = "Full Name",
                        icon          = Icons.Outlined.Person,
                        placeholder   = "e.g. Mr. Juan dela Cruz",
                        colors        = fieldColors
                    )
                    RegField(
                        value         = employeeId,
                        onValueChange = { employeeId = it },
                        label         = "Employee ID",
                        icon          = Icons.Outlined.Lock,
                        placeholder   = "e.g. EMP-2024-001",
                        colors        = fieldColors
                    )
                    RegField(
                        value         = department,
                        onValueChange = { department = it },
                        label         = "Department",
                        icon          = Icons.Outlined.Person,
                        placeholder   = "e.g. College of IT",
                        colors        = fieldColors
                    )
                }

                // Info note
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentPurple.copy(alpha = 0.08f))
                        .border(1.dp, AccentPurple.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ℹ", color = AccentPurple, fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Faculty records allow Employee ID-based lookup during visit logging.",
                        color      = TextMuted,
                        fontSize   = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // ══ EXTERNAL TAB ═════════════════
            if (registerTab == 2) {

                // Info note
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentAmber.copy(alpha = 0.08f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ℹ", color = AccentAmber, fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "External visitors do not need a pre-existing ID. Only their name is required.",
                        color      = TextMuted,
                        fontSize   = 12.sp,
                        lineHeight = 18.sp
                    )
                }

                RegSection(title = "Visitor Information", accent = AccentAmber) {
                    RegField(
                        value         = extName,
                        onValueChange = { extName = it },
                        label         = "Full Name",
                        icon          = Icons.Outlined.Person,
                        placeholder   = "e.g. Juan dela Cruz",
                        colors        = fieldColors
                    )
                    RegField(
                        value         = extOrg,
                        onValueChange = { extOrg = it },
                        label         = "Organization / Purpose (optional)",
                        icon          = Icons.Outlined.Person,
                        placeholder   = "e.g. DepEd, Parent, Meeting",
                        colors        = fieldColors
                    )
                }
            }

            // ── PRIVACY & AGREEMENT ───────────
            RegSection(title = "Privacy & Agreement", accent = tabAccent) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgInput)
                        .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked         = agreed,
                            onCheckedChange = { agreed = it },
                            colors          = CheckboxDefaults.colors(
                                checkedColor   = tabAccent,
                                uncheckedColor = TextSub
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "I agree to the privacy policy.",
                            color    = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                    TextButton(onClick = { showPrivacy = true }) {
                        Text("View", color = tabAccent, fontSize = 13.sp)
                    }
                }
                if (!agreed) {
                    Text(
                        "You must agree to continue.",
                        color    = AccentRed.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // ── STICKY SAVE BUTTON ────────────────
        Surface(color = BgTopBar, tonalElevation = 0.dp) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (!canSave) return@Button
                        isLoading = true
                        when (registerTab) {
                            0 -> AppStorage.saveStudent(
                                Student(studentName, studentId, studentType, course, yearLevel)
                            )
                            1 -> AppStorage.saveFaculty(
                                Faculty(facultyName, employeeId, department)
                            )
                        }
                        isLoading = false
                        showDialog = true
                    },
                    enabled  = canSave && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = tabAccent,
                        disabledContainerColor = tabAccent.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color       = Color.White,
                            modifier    = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            when (registerTab) {
                                0    -> "SAVE STUDENT"
                                1    -> "SAVE FACULTY"
                                else -> "LOG EXTERNAL VISITOR"
                            },
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Text(
                    when (registerTab) {
                        0    -> "Name and Student ID are required"
                        1    -> "Name, Employee ID, and Department are required"
                        else -> "Only Full Name is required for external visitors"
                    },
                    color    = TextSub,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    // ── SUCCESS DIALOG ────────────────────────
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor   = BgCard,
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        resetAll()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = tabAccent),
                    shape  = RoundedCornerShape(10.dp)
                ) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; resetAll() }) {
                    Text("Register Another", color = TextMuted)
                }
            },
            icon = {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = AccentGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    when (registerTab) {
                        0    -> "Student Registered!"
                        1    -> "Faculty Registered!"
                        else -> "Visitor Logged!"
                    },
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    when (registerTab) {
                        0    -> "Student record saved successfully."
                        1    -> "Faculty record saved successfully."
                        else -> "External visitor recorded. They can now time in from the Visit screen."
                    },
                    color = TextMuted
                )
            }
        )
    }

    // ── PRIVACY BOTTOM SHEET ──────────────────
    if (showPrivacy) {
        ModalBottomSheet(
            onDismissRequest = { showPrivacy = false },
            sheetState       = sheetState,
            containerColor   = BgCard,
            dragHandle = {
                Box(
                    Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BorderColor)
                )
            }
        ) {
            // Fixed header
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Privacy Policy & Agreements",
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                        Text(
                            "St. Bridget College Batangas — ITMSO",
                            color    = TextSub,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BorderColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { showPrivacy = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = null,
                            tint     = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            }

            // Scrollable content
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 48.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                listOf(
                    "1. Privacy and Data Protection" to
                            "Your personal data is securely stored and used only for system tracking and reporting within the ITMSO office.",
                    "2. Visit Record and Logging" to
                            "All time-in and time-out activities are recorded for monitoring, verification, and institutional reporting.",
                    "3. Proper Use of System" to
                            "The system must only be used for academic and institutional purposes as authorized by the ITMSO office.",
                    "4. Accuracy of Information" to
                            "All submitted information must be true, accurate, and correct. Falsified data is subject to disciplinary action.",
                    "5. Monitoring and Accountability" to
                            "Visit activity may be monitored by authorized administrators at any time for accountability purposes."
                ).forEach { (title, body) ->
                    PrivacyBlock(title, body)
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick  = { agreed = true; showPrivacy = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tabAccent),
                    shape  = RoundedCornerShape(12.dp)
                ) {
                    Text("I Understand & Agree.", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  REUSABLE: SECTION CARD
// ─────────────────────────────────────────────

@Composable
fun RegSection(
    title:   String,
    accent:  Color = Color(0xFF5C7CFA),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111827))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                color      = Color(0xFFE0E8FF),
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp
            )
        }
        HorizontalDivider(color = Color(0xFF1E2D5A), thickness = 0.5.dp)
        content()
    }
}

// ─────────────────────────────────────────────
//  REUSABLE: FORM FIELD
// ─────────────────────────────────────────────

@Composable
fun RegField(
    value:         String,
    onValueChange: (String) -> Unit,
    label:         String,
    icon:          ImageVector,
    placeholder:   String         = "",
    colors:        TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, color = Color(0xFF6B7FC4), fontSize = 13.sp) },
        placeholder   = { Text(placeholder, color = Color(0xFF4A5680), fontSize = 13.sp) },
        leadingIcon   = {
            Icon(
                icon,
                contentDescription = null,
                tint     = Color(0xFF6B7FC4),
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        colors     = colors,
        shape      = RoundedCornerShape(12.dp),
        modifier   = Modifier.fillMaxWidth()
    )
}

// ─────────────────────────────────────────────
//  REUSABLE: PRIVACY BLOCK
// ─────────────────────────────────────────────

@Composable
fun PrivacyBlock(title: String, desc: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .padding(top = 6.dp)
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF5C7CFA))
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    title,
                    color      = Color(0xFFE0E8FF),
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    desc,
                    color      = Color(0xFF6B7FC4),
                    fontSize   = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFF1E2D5A), thickness = 0.5.dp)
    }
}