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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private val BgDeep       = Color(0xFF0A0F1E)
private val BgCard       = Color(0xFF111827)
private val BgTopBar     = Color(0xFF0D1426)
private val BgInput      = Color(0xFF0A0F1E)
private val AccentBlue   = Color(0xFF5C7CFA)
private val AccentGreen  = Color(0xFF10B981)
private val AccentRed    = Color(0xFFEF4444)
private val AccentAmber  = Color(0xFFF59E0B)
private val AccentPurple = Color(0xFF8B5CF6)
private val TextPrimary  = Color(0xFFE0E8FF)
private val TextMuted    = Color(0xFF6B7FC4)
private val TextSub      = Color(0xFF4A5680)
private val BorderColor  = Color(0xFF1E2D5A)

// ── TIMEZONE FIX ─────────────────────────────
private val PH_TIMEZONE = TimeZone.getTimeZone("Asia/Manila")

private fun formatTime(ts: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TIMEZONE
    return sdf.format(Date(ts))
}

private fun formatDateTime(ts: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TIMEZONE
    return sdf.format(Date(ts))
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance(PH_TIMEZONE).apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance(PH_TIMEZONE).apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR)        == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginVisitScreen(navController: NavHostController) {

    var visitorTab    by remember { mutableStateOf(0) }
    var studentId     by remember { mutableStateOf("") }
    var employeeId    by remember { mutableStateOf("") }
    var extName       by remember { mutableStateOf("") }
    var extOrg        by remember { mutableStateOf("") }
    var extContact    by remember { mutableStateOf("") }
    var purpose       by remember { mutableStateOf("") }
    var expanded      by remember { mutableStateOf(false) }
    var otherConcern  by remember { mutableStateOf("") }
    var timeIn        by remember { mutableStateOf<Long?>(null) }
    var timeOut       by remember { mutableStateOf<Long?>(null) }
    var showSuccess   by remember { mutableStateOf(false) }
    var showDuplicate by remember { mutableStateOf(false) }

    fun resetAll() {
        studentId    = ""; employeeId   = ""
        extName      = ""; extOrg       = ""; extContact = ""
        purpose      = ""; otherConcern = ""
        timeIn       = null; timeOut    = null
        expanded     = false
    }

    val student = remember(studentId) {
        AppStorage.students.find { it.studentId.equals(studentId, ignoreCase = true) }
    }
    val studentSuggestions = remember(studentId) {
        if (studentId.isBlank()) emptyList()
        else AppStorage.students.filter { it.studentId.contains(studentId, true) }.take(4)
    }

    val faculty = remember(employeeId) {
        AppStorage.faculties.find { it.employeeId.equals(employeeId, ignoreCase = true) }
    }
    val facultySuggestions = remember(employeeId) {
        if (employeeId.isBlank()) emptyList()
        else AppStorage.faculties.filter { it.employeeId.contains(employeeId, true) }.take(4)
    }

    LaunchedEffect(showSuccess)   { if (showSuccess)   { delay(2000); showSuccess   = false } }
    LaunchedEffect(showDuplicate) { if (showDuplicate) { delay(2000); showDuplicate = false } }

    val purposes = listOf(
        "MS 365 Assistance",
        "School Automate Assistance",
        "Canva Registration",
        "Request Voucher",
        "ID Printing",
        "Other Concerns"
    )

    val finalPurpose = if (purpose == "Other Concerns") otherConcern else purpose

    val canTimeIn = when (visitorTab) {
        0    -> studentId.isNotBlank() && finalPurpose.isNotBlank() && timeIn == null
        1    -> employeeId.isNotBlank() && finalPurpose.isNotBlank() && timeIn == null
        else -> extName.isNotBlank() && finalPurpose.isNotBlank() && timeIn == null
    }
    val canTimeOut = timeIn != null

    val visitorId = when (visitorTab) {
        0    -> studentId
        1    -> employeeId
        else -> "EXT-${System.currentTimeMillis()}"
    }
    val visitorName = when (visitorTab) {
        0    -> student?.name ?: studentId
        1    -> faculty?.name ?: employeeId
        else -> extName
    }
    val visitorType = when (visitorTab) {
        0    -> "Student"
        1    -> "Faculty"
        else -> "External"
    }

    val tabAccent = when (visitorTab) {
        0    -> AccentBlue
        1    -> AccentPurple
        else -> AccentAmber
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor        = TextPrimary,
        unfocusedTextColor      = TextPrimary,
        cursorColor             = AccentBlue,
        focusedBorderColor      = AccentBlue,
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BorderColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { navController.navigate("home") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint     = TextMuted,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "Visit Logging",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
                Text(
                    "Student · Faculty · External",
                    color    = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        // ── VISITOR TYPE TABS ─────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 12.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Student", "Faculty", "External").forEachIndexed { i, label ->
                val selected = visitorTab == i
                val accent   = when (i) { 0 -> AccentBlue; 1 -> AccentPurple; else -> AccentAmber }
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) accent else BgCard)
                        .border(1.dp, if (selected) accent else BorderColor, RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { visitorTab = i; resetAll() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color      = if (selected) Color.White else TextMuted,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize   = 12.sp
                    )
                }
            }
        }

        // ── SCROLLABLE BODY ──────────────────
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
                .padding(top = 10.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ══ STUDENT TAB ══════════════════
            if (visitorTab == 0) {
                CompactSection(title = "Student Lookup", accent = AccentBlue) {

                    OutlinedTextField(
                        value         = studentId,
                        onValueChange = { studentId = it; timeIn = null; timeOut = null },
                        label         = { Text("Student ID", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("Type to search...", color = TextSub, fontSize = 12.sp) },
                        leadingIcon   = {
                            Icon(Icons.Outlined.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = if (studentId.isNotEmpty()) {{
                            IconButton(onClick = { studentId = ""; timeIn = null; timeOut = null }) {
                                Icon(Icons.Outlined.Close, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }} else null,
                        singleLine = true,
                        colors     = fieldColors,
                        shape      = RoundedCornerShape(10.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )

                    if (studentSuggestions.isNotEmpty() && student == null) {
                        SuggestionList(accent = AccentBlue) {
                            studentSuggestions.forEachIndexed { idx, s ->
                                SuggestionItem(
                                    name        = s.name,
                                    subLabel    = s.studentId,
                                    accent      = AccentBlue,
                                    onClick     = { studentId = s.studentId },
                                    showDivider = idx < studentSuggestions.lastIndex
                                )
                            }
                        }
                    }

                    student?.let { s ->
                        MatchedPersonCard(
                            initial  = s.name.take(1).uppercase(),
                            name     = s.name,
                            detail   = if (!s.course.isNullOrBlank()) s.course
                            else if (!s.yearLevel.isNullOrBlank()) s.yearLevel
                            else "N/A",
                            subLabel = s.studentId,
                            accent   = AccentBlue
                        )
                    }
                }
            }

            // ══ FACULTY TAB ══════════════════
            if (visitorTab == 1) {
                CompactSection(title = "Faculty Lookup", accent = AccentPurple) {

                    OutlinedTextField(
                        value         = employeeId,
                        onValueChange = { employeeId = it; timeIn = null; timeOut = null },
                        label         = { Text("Employee ID", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("Type to search...", color = TextSub, fontSize = 12.sp) },
                        leadingIcon   = {
                            Icon(Icons.Outlined.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = if (employeeId.isNotEmpty()) {{
                            IconButton(onClick = { employeeId = ""; timeIn = null; timeOut = null }) {
                                Icon(Icons.Outlined.Close, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }} else null,
                        singleLine = true,
                        colors     = fieldColors,
                        shape      = RoundedCornerShape(10.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )

                    if (facultySuggestions.isNotEmpty() && faculty == null) {
                        SuggestionList(accent = AccentPurple) {
                            facultySuggestions.forEachIndexed { idx, f ->
                                SuggestionItem(
                                    name        = f.name,
                                    subLabel    = f.employeeId,
                                    accent      = AccentPurple,
                                    onClick     = { employeeId = f.employeeId },
                                    showDivider = idx < facultySuggestions.lastIndex
                                )
                            }
                        }
                    }

                    faculty?.let { f ->
                        MatchedPersonCard(
                            initial  = f.name.take(1).uppercase(),
                            name     = f.name,
                            detail   = f.department,
                            subLabel = f.employeeId,
                            accent   = AccentPurple
                        )
                    }
                }
            }

            // ══ EXTERNAL TAB ═════════════════
            if (visitorTab == 2) {
                CompactSection(title = "Visitor Information", accent = AccentAmber) {

                    OutlinedTextField(
                        value         = extName,
                        onValueChange = { extName = it },
                        label         = { Text("Full Name", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("e.g. Juan dela Cruz", color = TextSub, fontSize = 12.sp) },
                        leadingIcon   = {
                            Icon(Icons.Outlined.Person, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors     = fieldColors,
                        shape      = RoundedCornerShape(10.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value         = extOrg,
                        onValueChange = { extOrg = it },
                        label         = { Text("Organization (optional)", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("e.g. DepEd, Parent, etc.", color = TextSub, fontSize = 12.sp) },
                        leadingIcon   = {
                            Icon(Icons.Outlined.Person, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors     = fieldColors,
                        shape      = RoundedCornerShape(10.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value         = extContact,
                        onValueChange = { extContact = it },
                        label         = { Text("Contact Number (optional)", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("e.g. 09XX-XXX-XXXX", color = TextSub, fontSize = 12.sp) },
                        leadingIcon   = {
                            Icon(Icons.Outlined.Person, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors     = fieldColors,
                        shape      = RoundedCornerShape(10.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )
                }
            }

            // ══ PURPOSE ══════════════════════
            CompactSection(title = "Visit Purpose", accent = tabAccent) {

                ExposedDropdownMenuBox(
                    expanded         = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value         = purpose,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Select Purpose", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("Tap to select...", color = TextSub, fontSize = 12.sp) },
                        trailingIcon  = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors   = fieldColors,
                        shape    = RoundedCornerShape(10.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded         = expanded,
                        onDismissRequest = { expanded = false },
                        modifier         = Modifier.background(BgCard)
                    ) {
                        purposes.forEach { item ->
                            DropdownMenuItem(
                                text     = { Text(item, color = TextPrimary, fontSize = 13.sp) },
                                onClick  = { purpose = item; expanded = false },
                                modifier = Modifier.background(BgCard)
                            )
                        }
                    }
                }

                if (purpose == "Other Concerns") {
                    OutlinedTextField(
                        value         = otherConcern,
                        onValueChange = { otherConcern = it },
                        label         = { Text("Specify Concern", color = TextMuted, fontSize = 12.sp) },
                        singleLine    = true,
                        colors        = fieldColors,
                        shape         = RoundedCornerShape(10.dp),
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── ACTIVE SESSION BANNER ─────────
            if (timeIn != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentGreen.copy(alpha = 0.08f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Session Active",
                            color      = AccentGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 12.sp
                        )
                        Text(
                            "Time In: ${formatDateTime(timeIn!!)}",
                            color    = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            "Visitor: $visitorName",
                            color    = TextSub,
                            fontSize = 10.sp
                        )
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AccentGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "ACTIVE",
                            color      = AccentGreen,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ── STICKY BUTTONS ────────────────────
        Surface(color = BgTopBar, tonalElevation = 0.dp) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val now = System.currentTimeMillis()
                            val id  = if (visitorTab == 2) "EXT-$now" else visitorId

                            val hasDuplicate = AppStorage.visits.any {
                                it.studentId == id &&
                                        isSameDay(it.timeIn, now) &&
                                        it.purpose == finalPurpose
                            }
                            if (hasDuplicate) { showDuplicate = true; return@Button }

                            timeIn  = now
                            timeOut = null

                            AppStorage.visits.add(
                                Visit(
                                    studentId   = id,
                                    purpose     = finalPurpose,
                                    timeIn      = now,
                                    visitorType = visitorType,
                                    visitorName = visitorName +
                                            if (visitorTab == 2 && extOrg.isNotBlank()) " ($extOrg)" else ""
                                )
                            )
                        },
                        enabled  = canTimeIn,
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = AccentGreen,
                            disabledContainerColor = AccentGreen.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "TIME IN",
                            color         = Color.White,
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Button(
                        onClick = {
                            val now   = System.currentTimeMillis()
                            val id    = if (visitorTab == 2) {
                                AppStorage.visits.lastOrNull {
                                    it.visitorType == "External" &&
                                            it.visitorName.startsWith(extName) &&
                                            it.timeOut == null
                                }?.studentId ?: ""
                            } else visitorId

                            val index = AppStorage.visits.indexOfLast {
                                it.studentId == id && it.timeOut == null
                            }
                            if (index != -1) {
                                val old = AppStorage.visits[index]
                                AppStorage.visits[index] = old.copy(timeOut = now)
                            }
                            resetAll()
                            showSuccess = true
                        },
                        enabled  = canTimeOut,
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = AccentRed,
                            disabledContainerColor = AccentRed.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "TIME OUT",
                            color         = Color.White,
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Text(
                    when {
                        timeIn != null  -> "Session active — tap Time Out to end"
                        visitorTab == 0 -> "Enter Student ID and purpose to time in"
                        visitorTab == 1 -> "Enter Employee ID and purpose to time in"
                        else            -> "Enter visitor name and purpose to time in"
                    },
                    color    = TextSub,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    // ── SUCCESS OVERLAY ───────────────────────
    if (showSuccess) {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgCard)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = AccentGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Time Out Successful",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                Text("Visit session completed", color = TextMuted, fontSize = 12.sp)
            }
        }
    }

    // ── DUPLICATE OVERLAY ─────────────────────
    if (showDuplicate) {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgCard)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", color = AccentAmber, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Duplicate Visit Blocked",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Same purpose already recorded today",
                    color    = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  REUSABLE: COMPACT SECTION
// ─────────────────────────────────────────────

@Composable
fun CompactSection(
    title:   String,
    accent:  Color = Color(0xFF5C7CFA),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111827))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.padding(bottom = 2.dp)
        ) {
            Box(
                Modifier
                    .width(3.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                title,
                color      = Color(0xFFE0E8FF),
                fontWeight = FontWeight.SemiBold,
                fontSize   = 12.sp
            )
        }
        HorizontalDivider(color = Color(0xFF1E2D5A), thickness = 0.5.dp)
        content()
    }
}

// ── Keep VisitSection as alias for compatibility
@Composable
fun VisitSection(
    title:   String,
    accent:  Color = Color(0xFF5C7CFA),
    content: @Composable ColumnScope.() -> Unit
) = CompactSection(title = title, accent = accent, content = content)

// ─────────────────────────────────────────────
//  REUSABLE: SUGGESTION LIST
// ─────────────────────────────────────────────

@Composable
fun SuggestionList(accent: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0A0F1E))
            .border(1.dp, Color(0xFF1E2D5A), RoundedCornerShape(8.dp))
    ) { content() }
}

// ─────────────────────────────────────────────
//  REUSABLE: SUGGESTION ITEM
// ─────────────────────────────────────────────

@Composable
fun SuggestionItem(
    name:        String,
    subLabel:    String,
    accent:      Color,
    onClick:     () -> Unit,
    showDivider: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Person,
                null,
                tint     = accent,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(name,     color = Color(0xFFE0E8FF), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(subLabel, color = Color(0xFF4A5680), fontSize = 10.sp)
        }
    }
    if (showDivider) HorizontalDivider(color = Color(0xFF1E2D5A), thickness = 0.5.dp)
}

// ─────────────────────────────────────────────
//  REUSABLE: MATCHED PERSON CARD
// ─────────────────────────────────────────────

@Composable
fun MatchedPersonCard(
    initial:  String,
    name:     String,
    detail:   String,
    subLabel: String,
    accent:   Color
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                initial,
                color      = accent,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp
            )
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(name,     color = Color(0xFFE0E8FF), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(detail,   color = Color(0xFF6B7FC4), fontSize = 11.sp)
            Text(subLabel, color = Color(0xFF4A5680), fontSize = 10.sp)
        }
    }
}