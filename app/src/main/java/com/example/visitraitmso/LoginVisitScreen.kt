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
import androidx.compose.ui.text.style.TextAlign
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

private val PH_TIMEZONE = TimeZone.getTimeZone("Asia/Manila")

private fun fmtTime(ts: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TIMEZONE
    return sdf.format(Date(ts))
}

private fun fmtFull(ts: Long): String {
    val sdf = SimpleDateFormat("MMM dd · hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TIMEZONE
    return sdf.format(Date(ts))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginVisitScreen(
    navController: NavHostController,
    viewModel:     VisitViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.showSuccess) {
        if (viewModel.showSuccess) { delay(2200); viewModel.showSuccess = false }
    }
    LaunchedEffect(viewModel.showDuplicate) {
        if (viewModel.showDuplicate) { delay(2200); viewModel.showDuplicate = false }
    }

    val purposes = listOf(
        "MS 365 Assistance",
        "School Automate Assistance",
        "Canva Registration",
        "Request Voucher",
        "ID Printing",
        "Other Concerns"
    )

    val tabAccent = when (viewModel.visitorTab) {
        0    -> AccentBlue
        1    -> AccentPurple
        else -> AccentAmber
    }

    val visitorName: String = when (viewModel.visitorTab) {
        0    -> viewModel.selectedStudent?.name ?: viewModel.studentId
        1    -> viewModel.selectedFaculty?.name ?: viewModel.employeeId
        else -> viewModel.extName
    }

    val visitorType: String = when (viewModel.visitorTab) {
        0    -> "Student"
        1    -> "Faculty"
        else -> "External"
    }

    val hasActiveSession = viewModel.activeVisit != null && visitorName.isNotBlank()

    // ── NO STAFF AVAILABLE CHECK ──────────────
    val noStaffAvailable = viewModel.staffs.none { it.isAvailable }
    val canTimeInFinal   = viewModel.canTimeIn && !noStaffAvailable

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
                    ) { navController.popBackStack() },
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
            if (hasActiveSession) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(AccentGreen)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Active",
                        color      = AccentGreen,
                        fontSize   = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ── SCROLLABLE BODY ──────────────────
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── NO STAFF WARNING BANNER ───────
            if (noStaffAvailable) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentRed.copy(alpha = 0.08f))
                        .border(1.dp, AccentRed.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔴", fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "No staff currently available",
                            color      = AccentRed,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Time-in is disabled. Please wait or come back later.",
                            color    = AccentRed.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

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
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(tabAccent.copy(alpha = 0.12f))
                        .border(1.dp, tabAccent.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        when (viewModel.visitorTab) { 0 -> "🎓"; 1 -> "👤"; else -> "🪪" },
                        fontSize = 22.sp
                    )
                }
                Text(
                    "Visit Logging",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    textAlign  = TextAlign.Center
                )
                Text(
                    "Select visitor type and fill in the form.",
                    color     = TextSub,
                    fontSize  = 11.sp,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("Student",  AccentBlue,   0),
                        Triple("Faculty",  AccentPurple, 1),
                        Triple("External", AccentAmber,  2)
                    ).forEach { (label, accent, idx) ->
                        val sel = viewModel.visitorTab == idx
                        Column(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) accent else BgInput)
                                .border(1.dp, if (sel) accent else BorderColor, RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) { viewModel.visitorTab = idx; viewModel.resetAll() }
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

            // ── STUDENT LOOKUP ───────────────
            if (viewModel.visitorTab == 0) {
                LvSection("Student Lookup", AccentBlue) {
                    LvSearchField(
                        value         = viewModel.studentId,
                        onValueChange = { viewModel.studentId = it },
                        label         = "Student ID or Name",
                        suggestions   = viewModel.studentSuggestions,
                        onSelect      = { viewModel.studentId = it.studentId },
                        fieldColors   = fieldColors,
                        accent        = AccentBlue
                    )
                    viewModel.selectedStudent?.let { s ->
                        LvPersonCard(
                            initial = s.name.take(1).uppercase(),
                            name    = s.name,
                            detail  = if (!s.course.isNullOrBlank()) s.course
                            else s.yearLevel ?: "N/A",
                            sub     = s.studentId,
                            accent  = AccentBlue
                        )
                    }
                }
            }

            // ── FACULTY LOOKUP ───────────────
            if (viewModel.visitorTab == 1) {
                LvSection("Faculty Lookup", AccentPurple) {
                    LvSearchField(
                        value         = viewModel.employeeId,
                        onValueChange = { viewModel.employeeId = it },
                        label         = "Employee ID or Name",
                        suggestions   = viewModel.facultySuggestions.map {
                            Student(it.name, it.employeeId, "")
                        },
                        onSelect      = { viewModel.employeeId = it.studentId },
                        fieldColors   = fieldColors,
                        accent        = AccentPurple
                    )
                    viewModel.selectedFaculty?.let { f ->
                        LvPersonCard(
                            initial = f.name.take(1).uppercase(),
                            name    = f.name,
                            detail  = f.department,
                            sub     = f.employeeId,
                            accent  = AccentPurple
                        )
                    }
                }
            }

            // ── EXTERNAL VISITOR ─────────────
            if (viewModel.visitorTab == 2) {
                LvSection("Visitor Information", AccentAmber) {
                    LvExternalSearchField(
                        value         = viewModel.extName,
                        onValueChange = { viewModel.extName = it },
                        label         = "Full Name",
                        suggestions   = viewModel.externalSuggestions,
                        onSelect      = { viewModel.selectExternalVisitor(it) },
                        fieldColors   = fieldColors,
                        accent        = AccentAmber
                    )
                    OutlinedTextField(
                        value         = viewModel.extOrg,
                        onValueChange = { viewModel.extOrg = it },
                        label         = { Text("Organization (optional)", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("e.g. DepEd, Parent…", color = TextSub, fontSize = 12.sp) },
                        leadingIcon   = {
                            Icon(Icons.Outlined.Person, null, tint = TextMuted, modifier = Modifier.size(17.dp))
                        },
                        singleLine = true,
                        colors     = fieldColors,
                        shape      = RoundedCornerShape(9.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value         = viewModel.extContact,
                        onValueChange = { viewModel.extContact = it },
                        label         = { Text("Contact number (optional)", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("e.g. 09XX-XXX-XXXX", color = TextSub, fontSize = 12.sp) },
                        leadingIcon   = {
                            Icon(Icons.Outlined.Person, null, tint = TextMuted, modifier = Modifier.size(17.dp))
                        },
                        singleLine = true,
                        colors     = fieldColors,
                        shape      = RoundedCornerShape(9.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── VISIT PURPOSE ────────────────
            LvSection("Visit Purpose", tabAccent) {
                ExposedDropdownMenuBox(
                    expanded         = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value         = viewModel.purpose,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Select purpose", color = TextMuted, fontSize = 12.sp) },
                        placeholder   = { Text("Tap to select…", color = TextSub, fontSize = 12.sp) },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors        = fieldColors,
                        shape         = RoundedCornerShape(9.dp),
                        modifier      = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded         = expanded,
                        onDismissRequest = { expanded = false },
                        modifier         = Modifier.background(BgCard)
                    ) {
                        purposes.forEach { item ->
                            DropdownMenuItem(
                                text     = { Text(item, color = TextPrimary, fontSize = 13.sp) },
                                onClick  = { viewModel.purpose = item; expanded = false },
                                modifier = Modifier.background(BgCard)
                            )
                        }
                    }
                }
                if (viewModel.purpose == "Other Concerns") {
                    OutlinedTextField(
                        value         = viewModel.otherConcern,
                        onValueChange = { viewModel.otherConcern = it },
                        label         = { Text("Specify concern", color = TextMuted, fontSize = 12.sp) },
                        singleLine    = true,
                        colors        = fieldColors,
                        shape         = RoundedCornerShape(9.dp),
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── SESSION BANNER ────────────────
            if (hasActiveSession) {
                val active = viewModel.activeVisit!!
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGreen.copy(alpha = 0.07f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Session started",
                            color      = AccentGreen,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(fmtFull(active.timeIn), color = TextMuted, fontSize = 10.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            visitorName,
                            color      = TextPrimary,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (active.queueNumber > 0) {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentBlue.copy(alpha = 0.15f))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "#${active.queueNumber}",
                                        color      = AccentBlue,
                                        fontSize   = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(active.assignedStaffName, color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabAccent.copy(alpha = 0.15f))
                            .border(1.dp, tabAccent.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            visitorType,
                            color      = tabAccent,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
        }

        // ── BOTTOM ACTIONS ────────────────────
        Surface(color = BgTopBar, tonalElevation = 0.dp) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // TIME IN
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    noStaffAvailable -> AccentRed.copy(alpha = 0.15f)
                                    canTimeInFinal   -> AccentGreen
                                    else             -> BgCard
                                }
                            )
                            .border(
                                1.dp,
                                when {
                                    noStaffAvailable -> AccentRed.copy(alpha = 0.4f)
                                    canTimeInFinal   -> AccentGreen
                                    else             -> BorderColor
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                enabled           = canTimeInFinal
                            ) { viewModel.timeIn() }
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            "Time In",
                            color = when {
                                noStaffAvailable -> AccentRed
                                canTimeInFinal   -> Color.White
                                else             -> TextSub
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                        Text(
                            when {
                                noStaffAvailable -> "No staff available"
                                canTimeInFinal   -> fmtTime(System.currentTimeMillis())
                                else             -> "Fill fields first"
                            },
                            color = when {
                                noStaffAvailable -> AccentRed.copy(alpha = 0.7f)
                                canTimeInFinal   -> Color.White.copy(alpha = 0.7f)
                                else             -> TextSub.copy(alpha = 0.5f)
                            },
                            fontSize = 10.sp
                        )
                    }

                    // TIME OUT
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (viewModel.canTimeOut) AccentRed else BgCard)
                            .border(
                                1.dp,
                                if (viewModel.canTimeOut) AccentRed else BorderColor,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                enabled           = viewModel.canTimeOut
                            ) { viewModel.timeOut() }
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            "Time Out",
                            color      = if (viewModel.canTimeOut) Color.White else TextSub,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                        Text(
                            if (viewModel.canTimeOut) "Tap to end session"
                            else "No active session",
                            color    = if (viewModel.canTimeOut) Color.White.copy(alpha = 0.7f)
                            else TextSub.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    when {
                        noStaffAvailable  -> "All staff are currently unavailable. Please wait."
                        hasActiveSession  -> "Queue #${viewModel.activeVisit!!.queueNumber} · ${viewModel.activeVisit!!.assignedStaffName}"
                        viewModel.visitorTab == 0 -> "Enter student ID and select a purpose"
                        viewModel.visitorTab == 1 -> "Enter employee ID and select a purpose"
                        else              -> "Enter visitor name and select a purpose"
                    },
                    color = when {
                        noStaffAvailable -> AccentRed.copy(alpha = 0.8f)
                        hasActiveSession -> AccentGreen.copy(alpha = 0.8f)
                        else             -> TextSub
                    },
                    fontSize  = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // ── QUEUE NUMBER OVERLAY ──────────────────
    if (viewModel.showQueueOverlay) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .padding(32.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BgCard)
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🎟️", fontSize = 36.sp)
                Text("Queue Number", color = TextMuted, fontSize = 13.sp)
                Box(
                    Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentBlue.copy(alpha = 0.12f))
                        .border(2.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#${viewModel.assignedQueueNumber}",
                        color      = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 46.sp
                    )
                }
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Assigned Staff", color = TextSub, fontSize = 11.sp)
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentPurple.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                viewModel.assignedStaffName.take(1).uppercase(),
                                color      = AccentPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp
                            )
                        }
                        Text(
                            viewModel.assignedStaffName,
                            color      = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 16.sp
                        )
                    }
                }
                Text(
                    "Please wait for your number to be called.",
                    color     = TextMuted,
                    fontSize  = 11.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentBlue)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { viewModel.showQueueOverlay = false }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Got it", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }

    // ── SUCCESS OVERLAY ───────────────────────
    if (viewModel.showSuccess) {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .padding(40.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BgCard)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = AccentGreen, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
                Text("Visit completed",               color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Time out recorded successfully", color = TextMuted,   fontSize = 12.sp)
                Text(fmtTime(System.currentTimeMillis()), color = AccentGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    // ── DUPLICATE OVERLAY ─────────────────────
    if (viewModel.showDuplicate) {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .padding(40.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BgCard)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AccentAmber.copy(alpha = 0.15f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", color = AccentAmber, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
                Text("Already recorded",               color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Same purpose already logged today", color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  SECTION CARD
// ─────────────────────────────────────────────

@Composable
fun LvSection(
    title:   String,
    accent:  Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                color      = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 12.sp,
                modifier   = Modifier.weight(1f)
            )
            Box(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(accent.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    when {
                        title.contains("Student") -> "Student"
                        title.contains("Faculty") -> "Faculty"
                        title.contains("Visitor") -> "External"
                        title.contains("Purpose") -> "Purpose"
                        else                      -> ""
                    },
                    color         = accent,
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp
                )
            }
        }
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
        content()
    }
}

// ─────────────────────────────────────────────
//  SEARCH FIELD — Students / Faculty
// ─────────────────────────────────────────────

@Composable
fun LvSearchField(
    value:         String,
    onValueChange: (String) -> Unit,
    label:         String,
    suggestions:   List<Student>,
    onSelect:      (Student) -> Unit,
    fieldColors:   TextFieldColors,
    accent:        Color = AccentBlue
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = { Text(label, fontSize = 12.sp) },
            leadingIcon   = {
                Icon(Icons.Outlined.Search, null, tint = TextMuted, modifier = Modifier.size(17.dp))
            },
            trailingIcon = if (value.isNotEmpty()) {{
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Outlined.Close, null, tint = TextMuted, modifier = Modifier.size(15.dp))
                }
            }} else null,
            colors   = fieldColors,
            shape    = RoundedCornerShape(9.dp),
            modifier = Modifier.fillMaxWidth()
        )
        if (suggestions.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .background(BgInput)
                    .border(1.dp, BorderColor, RoundedCornerShape(9.dp))
            ) {
                suggestions.forEachIndexed { i, s ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { onSelect(s) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(accent.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Person, null, tint = accent, modifier = Modifier.size(13.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(s.name,      color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(s.studentId, color = TextSub,     fontSize = 10.sp)
                        }
                    }
                    if (i < suggestions.lastIndex) HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  SEARCH FIELD — External Visitors
// ─────────────────────────────────────────────

@Composable
fun LvExternalSearchField(
    value:         String,
    onValueChange: (String) -> Unit,
    label:         String,
    suggestions:   List<Visitor>,
    onSelect:      (Visitor) -> Unit,
    fieldColors:   TextFieldColors,
    accent:        Color = AccentAmber
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = { Text(label, fontSize = 12.sp) },
            leadingIcon   = {
                Icon(Icons.Outlined.Search, null, tint = TextMuted, modifier = Modifier.size(17.dp))
            },
            trailingIcon = if (value.isNotEmpty()) {{
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Outlined.Close, null, tint = TextMuted, modifier = Modifier.size(15.dp))
                }
            }} else null,
            colors   = fieldColors,
            shape    = RoundedCornerShape(9.dp),
            modifier = Modifier.fillMaxWidth()
        )
        if (suggestions.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .background(BgInput)
                    .border(1.dp, BorderColor, RoundedCornerShape(9.dp))
            ) {
                suggestions.forEachIndexed { i, v ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { onSelect(v) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(accent.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Person, null, tint = accent, modifier = Modifier.size(13.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(v.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            if (v.organization.isNotEmpty()) {
                                Text(v.organization, color = TextSub, fontSize = 10.sp)
                            }
                        }
                    }
                    if (i < suggestions.lastIndex) HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  PERSON CARD
// ─────────────────────────────────────────────

@Composable
fun LvPersonCard(
    initial: String,
    name:    String,
    detail:  String,
    sub:     String,
    accent:  Color
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.07f))
            .border(1.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, color = accent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(name,   color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(detail, color = TextMuted,   fontSize = 11.sp)
            Text(sub,    color = TextSub,     fontSize = 10.sp)
        }
    }
}