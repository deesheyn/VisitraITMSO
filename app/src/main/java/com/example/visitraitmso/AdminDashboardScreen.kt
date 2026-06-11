package com.visitraitmso.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private val BgDeep       = Color(0xFF0A0F1E)
private val BgCard       = Color(0xFF111827)
private val BgTopBar     = Color(0xFF0D1426)
private val AccentBlue   = Color(0xFF5C7CFA)
private val AccentGreen  = Color(0xFF10B981)
private val AccentAmber  = Color(0xFFF59E0B)
private val AccentRed    = Color(0xFFEF4444)
private val AccentPurple = Color(0xFF8B5CF6)
private val TextPrimary  = Color(0xFFE0E8FF)
private val TextMuted    = Color(0xFF6B7FC4)
private val TextSub      = Color(0xFF4A5680)
private val BorderColor  = Color(0xFF1E2D5A)

private val PH_TZ = TimeZone.getTimeZone("Asia/Manila")

private fun fmtTime(ts: Long): String {
    val sdf = SimpleDateFormat("MMM dd \u00b7 hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

private fun fmtDate(ts: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

private fun fmtExport(ts: Long): String {
    val sdf = SimpleDateFormat("MMMM dd, yyyy  hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

private fun fmtFileName(ts: Long): String {
    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

@Composable
fun AdminScreen(
    navController: NavHostController,
    repository:    AppRepository
) {
    val viewModel: AdminViewModel = viewModel { AdminViewModel(repository) }

    when {
        viewModel.checkingAuth -> {
            Box(
                Modifier.fillMaxSize().background(BgDeep),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AccentBlue)
                    Spacer(Modifier.height(12.dp))
                    Text("Loading...", color = TextMuted, fontSize = 13.sp)
                }
            }
        }
        viewModel.loggedInUser == null -> {
            AdminLogin(viewModel = viewModel, navController = navController)
        }
        else -> {
            AdminDashboard(
                viewModel = viewModel,
                onLogout  = {
                    viewModel.logout {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

// ── ADMIN LOGIN — same design as Staff Login, blue theme ──
@Composable
fun AdminLogin(viewModel: AdminViewModel, navController: NavHostController) {
    Box(
        Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        // Blue gradient at top
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(Modifier.fillMaxSize()) {

            // Top bar
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
                    "St. Bridget College \u00b7 ITMSO",
                    color    = TextMuted,
                    fontSize = 11.sp
                )
            }

            // Centered content
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Circle avatar
                    Box(
                        Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.3f),
                                        AccentBlue.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .border(2.dp, AccentBlue.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint     = AccentBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Title + subtitle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Admin Portal",
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 22.sp
                        )
                        Text(
                            "Sign in with your credentials",
                            color    = TextSub,
                            fontSize = 12.sp
                        )
                    }

                    // Card with fields
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(BgCard)
                            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                        OutlinedTextField(
                            value         = viewModel.email,
                            onValueChange = { viewModel.email = it },
                            label         = { Text("Email Address", color = TextMuted) },
                            singleLine    = true,
                            leadingIcon   = { Icon(Icons.Outlined.Email, null, tint = AccentBlue) },
                            colors        = adminFieldColors(),
                            shape         = RoundedCornerShape(12.dp),
                            modifier      = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value                = viewModel.password,
                            onValueChange        = { viewModel.password = it },
                            label                = { Text("Password", color = TextMuted) },
                            singleLine           = true,
                            leadingIcon          = { Icon(Icons.Outlined.Lock, null, tint = AccentBlue) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors               = adminFieldColors(),
                            shape                = RoundedCornerShape(12.dp),
                            modifier             = Modifier.fillMaxWidth()
                        )

                        if (viewModel.loginError.isNotEmpty()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentRed.copy(alpha = 0.08f))
                                    .border(1.dp, AccentRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Warning, null, tint = AccentRed, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    viewModel.loginError,
                                    color    = AccentRed,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick  = { viewModel.login {} },
                            enabled  = !viewModel.isLoggingIn,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor         = AccentBlue,
                                disabledContainerColor = AccentBlue.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            if (viewModel.isLoggingIn) {
                                CircularProgressIndicator(
                                    color       = Color.White,
                                    modifier    = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun adminFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    cursorColor             = AccentBlue,
    focusedBorderColor      = AccentBlue,
    unfocusedBorderColor    = BorderColor,
    focusedContainerColor   = BgDeep,
    unfocusedContainerColor = BgDeep
)

@Composable
private fun SummaryItem(label: String, value: Int, accent: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            value.toString(),
            color      = accent,
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color         = TextMuted,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(viewModel: AdminViewModel, onLogout: () -> Unit) {

    val context    = LocalContext.current
    val allVisits  by viewModel.allVisits.collectAsState()
    val students   by viewModel.students.collectAsState()
    val faculties  by viewModel.faculties.collectAsState()
    val isSuper    = viewModel.loggedInUser?.role == "Super Admin"

    var currentTab by remember { mutableStateOf(0) }

    fun getName(v: Visit): String = v.visitorName.ifBlank { v.studentId }

    var statusExpanded   by remember { mutableStateOf(false) }
    var typeExpanded     by remember { mutableStateOf(false) }
    var showFromPicker   by remember { mutableStateOf(false) }
    var showToPicker     by remember { mutableStateOf(false) }
    var visitToDelete    by remember { mutableStateOf<Visit?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val sdfKey = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply { timeZone = PH_TZ }
    }

    val filtered = remember(
        viewModel.search, viewModel.dateFrom, viewModel.dateTo,
        viewModel.statusFilter, viewModel.typeFilter, allVisits.size
    ) {
        allVisits.filter { v ->
            val name        = getName(v)
            val matchSearch = viewModel.search.isEmpty() ||
                    v.studentId.contains(viewModel.search, true) ||
                    name.contains(viewModel.search, true) ||
                    v.purpose.contains(viewModel.search, true)
            val dateStr     = fmtDate(v.timeIn)
            val matchFrom   = viewModel.dateFrom.isEmpty() || dateStr >= viewModel.dateFrom
            val matchTo     = viewModel.dateTo.isEmpty()   || dateStr <= viewModel.dateTo
            val matchStatus = when (viewModel.statusFilter) {
                "Active" -> v.timeOut == null
                "Done"   -> v.timeOut != null
                else     -> true
            }
            val matchType = viewModel.typeFilter == "All" || v.visitorType == viewModel.typeFilter
            matchSearch && matchFrom && matchTo && matchStatus && matchType
        }
    }

    val totalCount    = allVisits.size
    val activeCount   = allVisits.count { it.timeOut == null }
    val doneCount     = allVisits.count { it.timeOut != null }
    val studentCount  = allVisits.count { it.visitorType == "Student" }
    val facultyCount  = allVisits.count { it.visitorType == "Faculty" }
    val externalCount = allVisits.count { it.visitorType == "External" }

    if (showFromPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { viewModel.dateFrom = sdfKey.format(Date(it)) }
                    showFromPicker = false
                }) { Text("OK", color = AccentBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showFromPicker = false }) { Text("Cancel", color = TextMuted) }
            }
        ) { DatePicker(state = state) }
    }

    if (showToPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { viewModel.dateTo = sdfKey.format(Date(it)) }
                    showToPicker = false
                }) { Text("OK", color = AccentBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showToPicker = false }) { Text("Cancel", color = TextMuted) }
            }
        ) { DatePicker(state = state) }
    }

    if (showDeleteDialog && visitToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; visitToDelete = null },
            containerColor   = BgCard,
            icon = {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Delete, null, tint = AccentRed, modifier = Modifier.size(24.dp))
                }
            },
            title = { Text("Delete Record?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text  = {
                Text(
                    "Permanently remove this visit record for \"${getName(visitToDelete!!)}\". This cannot be undone.",
                    color    = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        visitToDelete?.let { viewModel.deleteVisit(it) }
                        showDeleteDialog = false
                        visitToDelete   = null
                        Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape  = RoundedCornerShape(10.dp)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; visitToDelete = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor   = BgCard,
            icon = {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Share, null, tint = AccentBlue, modifier = Modifier.size(24.dp))
                }
            },
            title = { Text("Export Report", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "File will be saved to your phone's Downloads folder.",
                    color    = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick  = { showExportDialog = false; exportPDF(context, filtered, students, faculties) { v -> getName(v) } },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape    = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.Share, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Export as PDF", fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick  = { showExportDialog = false; exportCSV(context, filtered, students, faculties) { v -> getName(v) } },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                        shape    = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.List, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Export as CSV", fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(
                        onClick  = { showExportDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cancel", color = TextMuted) }
                }
            },
            dismissButton = {}
        )
    }

    Column(Modifier.fillMaxSize().background(BgDeep)) {

        Row(
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter            = painterResource(id = R.drawable.itmso_logo),
                    contentDescription = null,
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "VISITRA",
                    color         = TextPrimary,
                    fontWeight    = FontWeight.Bold,
                    fontSize      = 15.sp,
                    letterSpacing = 1.sp
                )
            }

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val roleLabel = viewModel.loggedInUser?.role ?: ""
                val roleColor = when (roleLabel) {
                    "Admin" -> AccentPurple
                    "Staff" -> AccentBlue
                    else    -> AccentRed
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(roleColor.copy(alpha = 0.15f))
                        .border(1.dp, roleColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        roleLabel,
                        color      = roleColor,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentRed.copy(alpha = 0.1f))
                        .border(1.dp, AccentRed.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { onLogout() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = "Logout",
                        tint     = AccentRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (isSuper) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(BgTopBar)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Visits", "Users").forEachIndexed { i, label ->
                    val sel    = currentTab == i
                    val accent = if (i == 0) AccentBlue else AccentPurple
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sel) accent else BgCard)
                            .border(1.dp, if (sel) accent else BorderColor, RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { currentTab = i }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color      = if (sel) Color.White else TextMuted,
                            fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize   = 13.sp
                        )
                    }
                }
            }
        }

        if (currentTab == 0) {

            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Visit Records",
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 20.sp
                        )
                    }
                }

                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BgCard)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("SESSION OVERVIEW", color = TextSub, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            SummaryItem("Total",  totalCount,  AccentBlue)
                            SummaryItem("Active", activeCount, AccentGreen)
                            SummaryItem("Done",   doneCount,   AccentAmber)
                        }
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                        Text("BY VISITOR TYPE", color = TextSub, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            SummaryItem("Student",  studentCount,  AccentBlue)
                            SummaryItem("Faculty",  facultyCount,  AccentPurple)
                            SummaryItem("External", externalCount, AccentAmber)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value         = viewModel.search,
                        onValueChange = { viewModel.search = it },
                        placeholder   = { Text("Search name, ID, purpose\u2026", color = TextSub, fontSize = 13.sp) },
                        leadingIcon   = { Icon(Icons.Outlined.Search, null, tint = TextMuted) },
                        trailingIcon  = if (viewModel.search.isNotEmpty()) {{
                            IconButton(onClick = { viewModel.search = "" }) {
                                Icon(Icons.Outlined.Close, null, tint = TextMuted)
                            }
                        }} else null,
                        singleLine = true,
                        colors     = adminFieldColors(),
                        shape      = RoundedCornerShape(12.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AdminDateButton(
                            modifier = Modifier.weight(1f),
                            label    = if (viewModel.dateFrom.isEmpty()) "From date" else viewModel.dateFrom,
                            hasValue = viewModel.dateFrom.isNotEmpty(),
                            onClick  = { showFromPicker = true },
                            onClear  = { viewModel.dateFrom = "" }
                        )
                        AdminDateButton(
                            modifier = Modifier.weight(1f),
                            label    = if (viewModel.dateTo.isEmpty()) "To date" else viewModel.dateTo,
                            hasValue = viewModel.dateTo.isNotEmpty(),
                            onClick  = { showToPicker = true },
                            onClear  = { viewModel.dateTo = "" }
                        )
                    }
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BgCard)
                                    .border(1.dp, if (viewModel.statusFilter != "All") AccentBlue.copy(0.5f) else BorderColor, RoundedCornerShape(10.dp))
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { statusExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Status: ${viewModel.statusFilter}", color = if (viewModel.statusFilter != "All") TextPrimary else TextSub, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Icon(Icons.Outlined.KeyboardArrowDown, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                            DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }, modifier = Modifier.background(BgCard)) {
                                listOf("All", "Active", "Done").forEach { option ->
                                    DropdownMenuItem(text = { Text(option, color = TextPrimary, fontSize = 13.sp) }, onClick = { viewModel.statusFilter = option; statusExpanded = false }, modifier = Modifier.background(BgCard))
                                }
                            }
                        }
                        Box(Modifier.weight(1f)) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BgCard)
                                    .border(1.dp, if (viewModel.typeFilter != "All") AccentBlue.copy(0.5f) else BorderColor, RoundedCornerShape(10.dp))
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { typeExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Type: ${viewModel.typeFilter}", color = if (viewModel.typeFilter != "All") TextPrimary else TextSub, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Icon(Icons.Outlined.KeyboardArrowDown, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                            DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }, modifier = Modifier.background(BgCard)) {
                                listOf("All", "Student", "Faculty", "External").forEach { option ->
                                    DropdownMenuItem(text = { Text(option, color = TextPrimary, fontSize = 13.sp) }, onClick = { viewModel.typeFilter = option; typeExpanded = false }, modifier = Modifier.background(BgCard))
                                }
                            }
                        }
                    }
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Showing ${filtered.size} record${if (filtered.size != 1) "s" else ""}", color = TextMuted, fontSize = 12.sp)
                        if (viewModel.search.isNotEmpty() || viewModel.dateFrom.isNotEmpty() || viewModel.dateTo.isNotEmpty() || viewModel.statusFilter != "All" || viewModel.typeFilter != "All") {
                            TextButton(onClick = { viewModel.search = ""; viewModel.dateFrom = ""; viewModel.dateTo = ""; viewModel.statusFilter = "All"; viewModel.typeFilter = "All" }, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                                Text("Clear filters", color = AccentBlue, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (filtered.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(BgCard).padding(36.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Search, null, tint = TextSub, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("No records match your filters", color = TextSub, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    items(filtered.reversed()) { visit ->
                        val studentInfo = students.find { it.studentId.equals(visit.studentId, true) }
                        val facultyInfo = faculties.find { it.employeeId.equals(visit.studentId, true) }
                        AdminVisitRow(
                            visit     = visit,
                            name      = getName(visit),
                            student   = studentInfo,
                            faculty   = facultyInfo,
                            canDelete = isSuper,
                            onDelete  = { visitToDelete = visit; showDeleteDialog = true }
                        )
                    }
                }

                item { Spacer(Modifier.height(4.dp)) }
            }

            Surface(color = BgTopBar, tonalElevation = 0.dp) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick  = { showExportDialog = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Share, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Export Report", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Text(
                        "Exporting ${filtered.size} record${if (filtered.size != 1) "s" else ""} \u00b7 Saved to Downloads",
                        color    = TextSub,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

        } else {
            UserManagementSection(viewModel = viewModel)
        }
    }
}

@Composable
fun AdminVisitRow(
    visit:     Visit,
    name:      String,
    student:   Student? = null,
    faculty:   Faculty? = null,
    canDelete: Boolean,
    onDelete:  () -> Unit
) {
    val isActive   = visit.timeOut == null
    val typeAccent = when (visit.visitorType) {
        "Faculty"  -> AccentPurple
        "External" -> AccentAmber
        else       -> AccentBlue
    }

    Card(
        colors   = CardDefaults.cardColors(containerColor = BgCard),
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.clip(RoundedCornerShape(4.dp)).background(typeAccent.copy(alpha = 0.15f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(visit.visitorType.uppercase(), color = typeAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("ID: ${visit.studentId}", color = TextMuted, fontSize = 11.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .height(28.dp).widthIn(min = 60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) AccentGreen.copy(alpha = 0.15f) else AccentAmber.copy(alpha = 0.15f))
                            .border(0.5.dp, (if (isActive) AccentGreen else AccentAmber).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isActive) "Active" else "Done", color = if (isActive) AccentGreen else AccentAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (canDelete) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            Modifier
                                .size(28.dp).clip(RoundedCornerShape(8.dp))
                                .background(AccentRed.copy(alpha = 0.1f))
                                .border(0.5.dp, AccentRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Delete, null, tint = AccentRed, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (visit.visitorType == "Student" && student != null) {
                    DetailLine("Course:",     student.course    ?: "N/A")
                    DetailLine("Year Level:", student.yearLevel ?: "N/A")
                } else if (visit.visitorType == "Faculty" && faculty != null) {
                    DetailLine("Department:", faculty.department)
                } else if (visit.visitorType == "External") {
                    if (visit.visitorOrg.isNotBlank())     DetailLine("Organization:", visit.visitorOrg)
                    if (visit.visitorContact.isNotBlank()) DetailLine("Contact:",      visit.visitorContact)
                }
                DetailLine("Purpose:", visit.purpose)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(BgDeep.copy(alpha = 0.5f)).padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdminTimeCell(Icons.Outlined.CheckCircle, "Time In",  fmtTime(visit.timeIn))
                AdminTimeCell(Icons.Outlined.CheckCircle, "Time Out", visit.timeOut?.let { fmtTime(it) } ?: "\u2014")
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(label, color = TextSub, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(80.dp))
        Text(value, color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AdminTimeCell(icon: ImageVector, label: String, value: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = TextMuted, modifier = Modifier.size(11.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, color = TextSub, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Text(value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AdminDateButton(
    modifier: Modifier,
    label:    String,
    hasValue: Boolean,
    onClick:  () -> Unit,
    onClear:  () -> Unit
) {
    Row(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BgCard)
            .border(1.dp, if (hasValue) AccentBlue.copy(0.5f) else BorderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.DateRange, null, tint = if (hasValue) AccentBlue else TextMuted, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = if (hasValue) TextPrimary else TextSub, fontSize = 12.sp, modifier = Modifier.weight(1f))
        if (hasValue) {
            IconButton(onClick = onClear, modifier = Modifier.size(18.dp)) {
                Icon(Icons.Outlined.Close, null, tint = TextMuted, modifier = Modifier.size(12.dp))
            }
        } else {
            TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(18.dp)) {
                Text("Pick", color = AccentBlue, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun UserManagementSection(viewModel: AdminViewModel) {
    val users   by viewModel.users.collectAsState()
    val context = LocalContext.current

    var showCreateDialog  by remember { mutableStateOf(false) }
    var userToEdit        by remember { mutableStateOf<User?>(null) }
    var userToDelete      by remember { mutableStateOf<User?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    var newName            by remember { mutableStateOf("") }
    var newEmail           by remember { mutableStateOf("") }
    var newPassword        by remember { mutableStateOf("") }
    var newRole            by remember { mutableStateOf("Admin") }
    var createRoleExpanded by remember { mutableStateOf(false) }
    var createError        by remember { mutableStateOf("") }
    var isCreating         by remember { mutableStateOf(false) }

    var editName         by remember { mutableStateOf("") }
    var editRole         by remember { mutableStateOf("Admin") }
    var editRoleExpanded by remember { mutableStateOf(false) }
    var isSaving         by remember { mutableStateOf(false) }

    val roles = listOf("Admin", "Staff")

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isCreating) {
                    showCreateDialog = false
                    newName = ""; newEmail = ""; newPassword = ""; newRole = "Admin"; createError = ""
                }
            },
            containerColor = BgCard,
            icon = {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AccentGreen.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.AccountCircle, null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                }
            },
            title = { Text("New User", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value         = newName,
                        onValueChange = { newName = it },
                        label         = { Text("Full Name", color = TextMuted) },
                        singleLine    = true,
                        colors        = adminFieldColors(),
                        shape         = RoundedCornerShape(10.dp),
                        modifier      = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value         = newEmail,
                        onValueChange = { newEmail = it },
                        label         = { Text("Email Address", color = TextMuted) },
                        singleLine    = true,
                        leadingIcon   = { Icon(Icons.Outlined.Email, null, tint = TextMuted) },
                        colors        = adminFieldColors(),
                        shape         = RoundedCornerShape(10.dp),
                        modifier      = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value                = newPassword,
                        onValueChange        = { newPassword = it },
                        label                = { Text("Password", color = TextMuted) },
                        singleLine           = true,
                        leadingIcon          = { Icon(Icons.Outlined.Lock, null, tint = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors               = adminFieldColors(),
                        shape                = RoundedCornerShape(10.dp),
                        modifier             = Modifier.fillMaxWidth()
                    )
                    Box {
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(BgDeep).border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { createRoleExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Person, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(newRole, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.KeyboardArrowDown, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = createRoleExpanded, onDismissRequest = { createRoleExpanded = false }, modifier = Modifier.background(BgCard)) {
                            roles.forEach { r ->
                                DropdownMenuItem(text = { Text(r, color = TextPrimary, fontSize = 13.sp) }, onClick = { newRole = r; createRoleExpanded = false }, modifier = Modifier.background(BgCard))
                            }
                        }
                    }
                    if (createError.isNotEmpty()) Text(createError, color = AccentRed, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            newName.isBlank()      -> createError = "Name is required"
                            newEmail.isBlank()     -> createError = "Email is required"
                            newPassword.length < 6 -> createError = "Password must be at least 6 characters"
                            else -> {
                                isCreating  = true
                                createError = ""
                                viewModel.createUser(newName.trim(), newEmail.trim(), newPassword, newRole) { ok, msg ->
                                    isCreating = false
                                    if (ok) {
                                        showCreateDialog = false
                                        newName = ""; newEmail = ""; newPassword = ""; newRole = "Admin"
                                        Toast.makeText(context, "User created", Toast.LENGTH_SHORT).show()
                                    } else {
                                        createError = msg
                                    }
                                }
                            }
                        }
                    },
                    enabled  = !isCreating,
                    colors   = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    if (isCreating) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Create User", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (!isCreating) {
                        showCreateDialog = false
                        newName = ""; newEmail = ""; newPassword = ""; newRole = "Admin"; createError = ""
                    }
                }) { Text("Cancel", color = TextMuted) }
            }
        )
    }

    userToEdit?.let { u ->
        LaunchedEffect(u) { editName = u.name; editRole = u.role }
        AlertDialog(
            onDismissRequest = { if (!isSaving) userToEdit = null },
            containerColor   = BgCard,
            icon = {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AccentBlue.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Edit, null, tint = AccentBlue, modifier = Modifier.size(24.dp))
                }
            },
            title = { Text("Edit User", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value         = editName,
                        onValueChange = { editName = it },
                        label         = { Text("Full Name", color = TextMuted) },
                        singleLine    = true,
                        colors        = adminFieldColors(),
                        shape         = RoundedCornerShape(10.dp),
                        modifier      = Modifier.fillMaxWidth()
                    )
                    Text(u.email, color = TextMuted, fontSize = 12.sp)
                    Box {
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(BgDeep).border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { editRoleExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(editRole, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.KeyboardArrowDown, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = editRoleExpanded, onDismissRequest = { editRoleExpanded = false }, modifier = Modifier.background(BgCard)) {
                            roles.forEach { r ->
                                DropdownMenuItem(text = { Text(r, color = TextPrimary, fontSize = 13.sp) }, onClick = { editRole = r; editRoleExpanded = false }, modifier = Modifier.background(BgCard))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            isSaving = true
                            viewModel.updateUser(u.copy(name = editName.trim(), role = editRole)) { ok ->
                                isSaving   = false
                                userToEdit = null
                                if (ok) Toast.makeText(context, "User updated", Toast.LENGTH_SHORT).show()
                                else    Toast.makeText(context, "Update failed",  Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled  = !isSaving && editName.isNotBlank(),
                    colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Save Changes", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!isSaving) userToEdit = null }) { Text("Cancel", color = TextMuted) }
            }
        )
    }

    if (showDeleteConfirm && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false; userToDelete = null },
            containerColor   = BgCard,
            icon = {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AccentRed.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Delete, null, tint = AccentRed, modifier = Modifier.size(24.dp))
                }
            },
            title = { Text("Remove User?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will permanently delete the account for \"${userToDelete!!.name.ifBlank { userToDelete!!.email }}\". This cannot be undone.",
                    color = TextMuted, fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uid = userToDelete!!.uid
                        viewModel.deleteUser(uid) { Toast.makeText(context, "User removed", Toast.LENGTH_SHORT).show() }
                        showDeleteConfirm = false
                        userToDelete      = null
                    },
                    colors  = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape   = RoundedCornerShape(10.dp)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false; userToDelete = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Users", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("${users.size} account${if (users.size != 1) "s" else ""}", color = TextMuted, fontSize = 12.sp)
            }
            Button(
                onClick  = { showCreateDialog = true },
                colors   = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape    = RoundedCornerShape(10.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add User", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(users, key = { it.uid }) { u ->
                val roleAccent = when (u.role) {
                    "Super Admin" -> AccentRed
                    "Admin"       -> AccentPurple
                    else          -> AccentBlue
                }

                Card(
                    colors   = CardDefaults.cardColors(containerColor = BgCard),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(roleAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                u.name.take(1).uppercase().ifBlank { "?" },
                                color      = roleAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                u.name.ifBlank { "No Name" },
                                color      = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 14.sp
                            )
                            Text(u.email, color = TextMuted, fontSize = 11.sp)
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(roleAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    u.role,
                                    color      = roleAccent,
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            if (u.role == "Staff") {
                                val isAvail    = u.isAvailable
                                val availColor = if (isAvail) AccentGreen else AccentRed
                                val availLabel = if (isAvail) "Available" else "Unavailable"
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(availColor.copy(alpha = 0.12f))
                                        .border(1.dp, availColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication        = null
                                        ) {
                                            viewModel.setStaffAvailability(u.uid, !isAvail)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            Modifier
                                                .size(7.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(availColor)
                                        )
                                        Text(
                                            availLabel,
                                            color      = availColor,
                                            fontSize   = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Box(
                                Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentBlue.copy(alpha = 0.1f))
                                    .border(0.5.dp, AccentBlue.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) { userToEdit = u },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AccentBlue, modifier = Modifier.size(16.dp))
                            }

                            Box(
                                Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentRed.copy(alpha = 0.1f))
                                    .border(0.5.dp, AccentRed.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) { userToDelete = u; showDeleteConfirm = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
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
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use(writeData)
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
                Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Export failed: could not create file", Toast.LENGTH_LONG).show()
            }
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use(writeData)
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
            Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun exportPDF(context: Context, visits: List<Visit>, students: List<Student>, faculties: List<Faculty>, getName: (Visit) -> String) {
    val pdf    = PdfDocument()
    val W      = 842
    val H      = 595
    val margin = 36f

    var pageNum = 1
    var page    = pdf.startPage(PdfDocument.PageInfo.Builder(W, H, pageNum).create())
    var canvas  = page.canvas

    val pTitle = Paint().apply { textSize = 16f; typeface = Typeface.DEFAULT_BOLD; color = android.graphics.Color.parseColor("#1a1a2e") }
    val pHead  = Paint().apply { textSize = 9f;  typeface = Typeface.DEFAULT_BOLD; color = android.graphics.Color.parseColor("#5C7CFA") }
    val pBody  = Paint().apply { textSize = 8f;  color = android.graphics.Color.parseColor("#374151") }
    val pSub   = Paint().apply { textSize = 7f;  color = android.graphics.Color.parseColor("#9CA3AF") }
    val pLine  = Paint().apply { color = android.graphics.Color.parseColor("#E5E7EB"); strokeWidth = 0.5f }
    val pRowBg = Paint().apply { color = android.graphics.Color.parseColor("#F9FAFB") }

    val now = System.currentTimeMillis()
    var y = margin + 20f

    canvas.drawText("VISITRA ITMSO \u2014 Visit Report", margin, y, pTitle); y += 16f
    canvas.drawText("Generated: ${fmtExport(now)}   |   Total: ${visits.size}", margin, y, pSub); y += 8f
    pLine.strokeWidth = 1f; canvas.drawLine(margin, y, W - margin, y, pLine); pLine.strokeWidth = 0.5f; y += 14f

    val cols = listOf(
        "NAME" to margin, "TYPE" to margin + 110f, "ID" to margin + 170f,
        "INFO (Course/Dept)" to margin + 240f, "PURPOSE" to margin + 440f,
        "TIME IN" to margin + 580f, "TIME OUT" to margin + 680f, "STATUS" to margin + 760f
    )
    cols.forEach { (lbl, x) -> canvas.drawText(lbl, x, y, pHead) }
    y += 5f; pLine.strokeWidth = 1f; canvas.drawLine(margin, y, W - margin, y, pLine); pLine.strokeWidth = 0.5f; y += 13f

    visits.forEachIndexed { idx, v ->
        if (y > H - margin - 24f) {
            canvas.drawText("VISITRA ITMSO   Page $pageNum", margin, H - 18f, pSub)
            pdf.finishPage(page); pageNum++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(W, H, pageNum).create()); canvas = page.canvas; y = margin + 20f
        }
        val student   = students.find { it.studentId.equals(v.studentId, true) }
        val faculty   = faculties.find { it.employeeId.equals(v.studentId, true) }
        val extraInfo = when (v.visitorType) {
            "Student" -> "${student?.course ?: "N/A"} - ${student?.yearLevel ?: "N/A"}"
            "Faculty" -> faculty?.department ?: "N/A"
            else      -> v.visitorOrg.ifBlank { "N/A" }
        }
        if (idx % 2 == 0) canvas.drawRect(margin - 4f, y - 10f, W - margin + 4f, y + 5f, pRowBg)
        canvas.drawText(getName(v).take(22),                         cols[0].second, y, pBody)
        canvas.drawText(v.visitorType,                               cols[1].second, y, pBody)
        canvas.drawText(v.studentId.take(12),                        cols[2].second, y, pBody)
        canvas.drawText(extraInfo.take(45),                          cols[3].second, y, pBody)
        canvas.drawText(v.purpose.take(30),                          cols[4].second, y, pBody)
        canvas.drawText(fmtTime(v.timeIn),                           cols[5].second, y, pBody)
        canvas.drawText(v.timeOut?.let { fmtTime(it) } ?: "\u2014",  cols[6].second, y, pBody)
        canvas.drawText(if (v.timeOut != null) "Done" else "Active", cols[7].second, y, pBody)
        y += 16f; canvas.drawLine(margin, y - 4f, W - margin, y - 4f, pLine)
    }
    canvas.drawText("VISITRA ITMSO   Page $pageNum", margin, H - 18f, pSub)
    pdf.finishPage(page)
    saveFile(context, "visitra_${fmtFileName(now)}.pdf", "application/pdf") { stream -> pdf.writeTo(stream) }
    pdf.close()
}

fun exportCSV(context: Context, visits: List<Visit>, students: List<Student>, faculties: List<Faculty>, getName: (Visit) -> String) {
    val header = "Name,Visitor Type,ID,Course/Dept,Year Level,Organization,Contact,Purpose,Time In,Time Out,Status\n"
    val rows   = visits.joinToString("\n") { v ->
        val student = students.find { it.studentId.equals(v.studentId, true) }
        val faculty = faculties.find { it.employeeId.equals(v.studentId, true) }
        listOf(
            getName(v), v.visitorType, v.studentId,
            if (v.visitorType == "Student") student?.course ?: "" else if (v.visitorType == "Faculty") faculty?.department ?: "" else "",
            if (v.visitorType == "Student") student?.yearLevel ?: "" else "",
            v.visitorOrg, v.visitorContact, v.purpose,
            fmtTime(v.timeIn), v.timeOut?.let { fmtTime(it) } ?: "Still In",
            if (v.timeOut != null) "Completed" else "Active"
        ).joinToString(",") { "\"$it\"" }
    }
    val now = System.currentTimeMillis()
    saveFile(context, "visitra_${fmtFileName(now)}.csv", "text/csv") { stream -> stream.write((header + rows).toByteArray()) }
}