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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
//  DATA
// ─────────────────────────────────────────────

private val collegeCourses = listOf(
    "BSIT", "BSBA", "BSSW", "BPED", "BSED", "BEED", "BECED"
)

private val collegeYears = listOf(
    "1st Year", "2nd Year", "3rd Year", "4th Year"
)

private val ibedGrades = listOf(
    "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12"
)

private val facultyDeptCollege = listOf(
    "College of IT", "College of Business", "College of Social Work",
    "College of Education", "College of Arts & Sciences"
)

private val facultyDeptIBED = listOf(
    "Junior High School", "Senior High School", "Grade School", "Pre-School"
)

// ─────────────────────────────────────────────
//  SCREEN
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel:     RegisterViewModel
) {
    var showPrivacy    by remember { mutableStateOf(false) }
    var courseExpanded by remember { mutableStateOf(false) }
    var yearExpanded   by remember { mutableStateOf(false) }
    var deptExpanded   by remember { mutableStateOf(false) }
    var levelExpanded  by remember { mutableStateOf(false) }
    var facultyType    by remember { mutableStateOf("College") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val tabAccent = when (viewModel.registerTab) {
        0    -> AccentBlue
        1    -> AccentPurple
        else -> AccentAmber
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

        // ── SLIM TOP BAR ─────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(BorderColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { navController.navigateUp() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    null,
                    tint     = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                "St. Bridget College · ITMSO",
                color    = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // ── SCROLLABLE BODY ──────────────────
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── CARD HEADER ──────────────────
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon badge
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(tabAccent.copy(alpha = 0.12f))
                        .border(1.dp, tabAccent.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        when (viewModel.registerTab) {
                            0    -> "🎓"
                            1    -> "👤"
                            else -> "🪪"
                        },
                        fontSize = 22.sp
                    )
                }

                Text(
                    "Registration",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    textAlign  = TextAlign.Center
                )

                Text(
                    "Select registrant type and fill in the form",
                    color     = TextSub,
                    fontSize  = 11.sp,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                // Tabs inside card
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("Student",  AccentBlue,   0),
                        Triple("Faculty",  AccentPurple, 1),
                        Triple("External", AccentAmber,  2)
                    ).forEach { (label, accent, idx) ->
                        val sel = viewModel.registerTab == idx
                        Column(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) accent else BgInput)
                                .border(
                                    1.dp,
                                    if (sel) accent else BorderColor,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) {
                                    viewModel.registerTab = idx
                                    viewModel.resetFields()
                                    facultyType    = "College"
                                    courseExpanded = false
                                    yearExpanded   = false
                                    deptExpanded   = false
                                    levelExpanded  = false
                                }
                                .padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                label,
                                color      = if (sel) Color.White else TextMuted,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize   = 12.sp
                            )
                        }
                    }
                }
            }

            // ══ STUDENT ══════════════════════
            if (viewModel.registerTab == 0) {

                RegSection("Personal Information", AccentBlue) {
                    RegField(
                        viewModel.studentName, { viewModel.studentName = it },
                        "Full Name", Icons.Outlined.Person, "e.g. Maria Santos", fieldColors
                    )
                    RegField(
                        viewModel.studentId, { viewModel.studentId = it },
                        "Student ID", Icons.Outlined.Lock, "e.g. 2024-00123", fieldColors
                    )
                }

                RegSection("Student Type", AccentBlue) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("College", "IBED").forEach { option ->
                            val sel = viewModel.studentType == option
                            Box(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (sel) AccentBlue else BgInput)
                                    .border(1.dp, if (sel) AccentBlue else BorderColor, RoundedCornerShape(9.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) {
                                        viewModel.studentType = option
                                        viewModel.course      = ""
                                        viewModel.yearLevel   = ""
                                        courseExpanded        = false
                                        yearExpanded          = false
                                        levelExpanded         = false
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    option,
                                    color      = if (sel) Color.White else TextMuted,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 13.sp
                                )
                            }
                        }
                    }

                    if (viewModel.studentType == "College") {
                        // Course dropdown
                        ExposedDropdownMenuBox(
                            expanded         = courseExpanded,
                            onExpandedChange = { courseExpanded = !courseExpanded }
                        ) {
                            OutlinedTextField(
                                value         = viewModel.course,
                                onValueChange = {},
                                readOnly      = true,
                                label         = { Text("Course", color = TextMuted, fontSize = 12.sp) },
                                placeholder   = { Text("Select course…", color = TextSub, fontSize = 12.sp) },
                                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(courseExpanded) },
                                colors        = fieldColors,
                                shape         = RoundedCornerShape(9.dp),
                                modifier      = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded         = courseExpanded,
                                onDismissRequest = { courseExpanded = false },
                                modifier         = Modifier.background(BgCard)
                            ) {
                                collegeCourses.forEach { c ->
                                    DropdownMenuItem(
                                        text     = { Text(c, color = TextPrimary, fontSize = 13.sp) },
                                        onClick  = { viewModel.course = c; courseExpanded = false },
                                        modifier = Modifier.background(BgCard)
                                    )
                                }
                            }
                        }

                        // Year level dropdown
                        ExposedDropdownMenuBox(
                            expanded         = yearExpanded,
                            onExpandedChange = { yearExpanded = !yearExpanded }
                        ) {
                            OutlinedTextField(
                                value         = viewModel.yearLevel,
                                onValueChange = {},
                                readOnly      = true,
                                label         = { Text("Year Level", color = TextMuted, fontSize = 12.sp) },
                                placeholder   = { Text("Select year level…", color = TextSub, fontSize = 12.sp) },
                                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(yearExpanded) },
                                colors        = fieldColors,
                                shape         = RoundedCornerShape(9.dp),
                                modifier      = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded         = yearExpanded,
                                onDismissRequest = { yearExpanded = false },
                                modifier         = Modifier.background(BgCard)
                            ) {
                                collegeYears.forEach { yr ->
                                    DropdownMenuItem(
                                        text     = { Text(yr, color = TextPrimary, fontSize = 13.sp) },
                                        onClick  = { viewModel.yearLevel = yr; yearExpanded = false },
                                        modifier = Modifier.background(BgCard)
                                    )
                                }
                            }
                        }
                    } else {
                        // IBED grade level dropdown
                        ExposedDropdownMenuBox(
                            expanded         = levelExpanded,
                            onExpandedChange = { levelExpanded = !levelExpanded }
                        ) {
                            OutlinedTextField(
                                value         = viewModel.yearLevel,
                                onValueChange = {},
                                readOnly      = true,
                                label         = { Text("Grade Level", color = TextMuted, fontSize = 12.sp) },
                                placeholder   = { Text("Select grade level…", color = TextSub, fontSize = 12.sp) },
                                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(levelExpanded) },
                                colors        = fieldColors,
                                shape         = RoundedCornerShape(9.dp),
                                modifier      = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded         = levelExpanded,
                                onDismissRequest = { levelExpanded = false },
                                modifier         = Modifier.background(BgCard)
                            ) {
                                ibedGrades.forEach { gr ->
                                    DropdownMenuItem(
                                        text     = { Text(gr, color = TextPrimary, fontSize = 13.sp) },
                                        onClick  = { viewModel.yearLevel = gr; levelExpanded = false },
                                        modifier = Modifier.background(BgCard)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ══ FACULTY ══════════════════════
            if (viewModel.registerTab == 1) {

                RegSection("Faculty Information", AccentPurple) {
                    RegField(
                        viewModel.facultyName, { viewModel.facultyName = it },
                        "Full Name", Icons.Outlined.Person, "e.g. Mr. Juan dela Cruz", fieldColors
                    )
                    RegField(
                        viewModel.employeeId, { viewModel.employeeId = it },
                        "Employee ID", Icons.Outlined.Lock, "e.g. EMP-2024-001", fieldColors
                    )
                }

                RegSection("Faculty Type", AccentPurple) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("College", "IBED").forEach { option ->
                            val sel = facultyType == option
                            Box(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (sel) AccentPurple else BgInput)
                                    .border(1.dp, if (sel) AccentPurple else BorderColor, RoundedCornerShape(9.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) {
                                        facultyType          = option
                                        viewModel.department = ""
                                        deptExpanded         = false
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    option,
                                    color      = if (sel) Color.White else TextMuted,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 13.sp
                                )
                            }
                        }
                    }

                    val deptList = if (facultyType == "College") facultyDeptCollege else facultyDeptIBED

                    ExposedDropdownMenuBox(
                        expanded         = deptExpanded,
                        onExpandedChange = { deptExpanded = !deptExpanded }
                    ) {
                        OutlinedTextField(
                            value         = viewModel.department,
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Department", color = TextMuted, fontSize = 12.sp) },
                            placeholder   = { Text("Select department…", color = TextSub, fontSize = 12.sp) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(deptExpanded) },
                            colors        = fieldColors,
                            shape         = RoundedCornerShape(9.dp),
                            modifier      = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded         = deptExpanded,
                            onDismissRequest = { deptExpanded = false },
                            modifier         = Modifier.background(BgCard)
                        ) {
                            deptList.forEach { dept ->
                                DropdownMenuItem(
                                    text     = { Text(dept, color = TextPrimary, fontSize = 13.sp) },
                                    onClick  = { viewModel.department = dept; deptExpanded = false },
                                    modifier = Modifier.background(BgCard)
                                )
                            }
                        }
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentPurple.copy(alpha = 0.07f))
                            .border(1.dp, AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ℹ", color = AccentPurple, fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Faculty records allow Employee ID-based lookup during visit logging.",
                            color      = TextMuted,
                            fontSize   = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // ══ EXTERNAL ═════════════════════
            if (viewModel.registerTab == 2) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(AccentAmber.copy(alpha = 0.07f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.2f), RoundedCornerShape(9.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ℹ", color = AccentAmber, fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "External visitors do not need a pre-existing ID. Only their name is required.",
                        color      = TextMuted,
                        fontSize   = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                RegSection("Visitor Information", AccentAmber) {
                    RegField(
                        viewModel.extName, { viewModel.extName = it },
                        "Full Name", Icons.Outlined.Person, "e.g. Juan dela Cruz", fieldColors
                    )
                    RegField(
                        viewModel.extOrg, { viewModel.extOrg = it },
                        "Organization / Purpose (optional)", Icons.Outlined.Person,
                        "e.g. DepEd, Parent, Meeting", fieldColors
                    )
                }
            }

            // ── PRIVACY ───────────────────────
            RegSection("Privacy & Agreement", tabAccent) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(BgInput)
                        .border(1.dp, BorderColor, RoundedCornerShape(9.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked         = viewModel.agreed,
                            onCheckedChange = { viewModel.agreed = it },
                            colors          = CheckboxDefaults.colors(
                                checkedColor   = tabAccent,
                                uncheckedColor = TextSub
                            )
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "I agree to the privacy policy.",
                            color    = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    TextButton(
                        onClick        = { showPrivacy = true },
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text("View", color = tabAccent, fontSize = 12.sp)
                    }
                }
                if (!viewModel.agreed) {
                    Text(
                        "You must agree to continue.",
                        color    = AccentRed.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
        }

        // ── STICKY SAVE BUTTON ────────────────
        Surface(color = BgTopBar, tonalElevation = 0.dp) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick  = { viewModel.save() },
                    enabled  = viewModel.canSave && !viewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = tabAccent,
                        disabledContainerColor = tabAccent.copy(alpha = 0.25f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color       = Color.White,
                            modifier    = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            when (viewModel.registerTab) {
                                0    -> "Save Student"
                                1    -> "Save Faculty"
                                else -> "Log Visitor"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                    }
                }
                Text(
                    when (viewModel.registerTab) {
                        0    -> "Name and Student ID are required"
                        1    -> "Name, Employee ID, and Department are required"
                        else -> "Only Full Name is required for external visitors"
                    },
                    color    = TextSub,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    // ── SUCCESS DIALOG ────────────────────────
    if (viewModel.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showSuccessDialog = false },
            containerColor   = BgCard,
            icon = {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = AccentGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    when (viewModel.registerTab) {
                        0    -> "Student registered"
                        1    -> "Faculty registered"
                        else -> "Visitor logged"
                    },
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
            },
            text = {
                Text(
                    when (viewModel.registerTab) {
                        0    -> "Student record saved successfully."
                        1    -> "Faculty record saved successfully."
                        else -> "External visitor recorded. They can now time in from the Visit screen."
                    },
                    color     = TextMuted,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.showSuccessDialog = false
                            viewModel.resetFields()
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = tabAccent),
                        shape    = RoundedCornerShape(10.dp)
                    ) { Text("Done") }
                    TextButton(
                        onClick  = { viewModel.showSuccessDialog = false; viewModel.resetFields() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register another", color = TextMuted, fontSize = 13.sp)
                    }
                }
            },
            dismissButton = {}
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
                        .padding(vertical = 10.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BorderColor)
                )
            }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 10.dp)
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
                            fontSize   = 16.sp
                        )
                        Text(
                            "St. Bridget College Batangas — ITMSO",
                            color    = TextSub,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(BorderColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { showPrivacy = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            null,
                            tint     = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 40.dp)
            ) {
                Spacer(Modifier.height(6.dp))

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

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick  = { viewModel.agreed = true; showPrivacy = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = tabAccent),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text("I understand & agree", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  SECTION CARD
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111827))
            .border(1.dp, Color(0xFF1E2D5A), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(3.dp)
                    .height(13.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                title,
                color      = Color(0xFFE0E8FF),
                fontWeight = FontWeight.SemiBold,
                fontSize   = 12.sp,
                modifier   = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = Color(0xFF1E2D5A), thickness = 0.5.dp)
        content()
    }
}

// ─────────────────────────────────────────────
//  FORM FIELD
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
        label         = { Text(label, color = Color(0xFF6B7FC4), fontSize = 12.sp) },
        placeholder   = { Text(placeholder, color = Color(0xFF4A5680), fontSize = 12.sp) },
        leadingIcon   = {
            Icon(icon, null, tint = Color(0xFF6B7FC4), modifier = Modifier.size(18.dp))
        },
        singleLine = true,
        colors     = colors,
        shape      = RoundedCornerShape(9.dp),
        modifier   = Modifier.fillMaxWidth()
    )
}

// ─────────────────────────────────────────────
//  PRIVACY BLOCK
// ─────────────────────────────────────────────

@Composable
fun PrivacyBlock(title: String, desc: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .padding(top = 5.dp)
                    .size(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF5C7CFA))
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    title,
                    color      = Color(0xFFE0E8FF),
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    desc,
                    color      = Color(0xFF6B7FC4),
                    fontSize   = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        HorizontalDivider(color = Color(0xFF1E2D5A), thickness = 0.5.dp)
    }
}