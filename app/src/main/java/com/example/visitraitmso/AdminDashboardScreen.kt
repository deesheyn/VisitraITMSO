package com.visitraitmso.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
//  THEME
// ─────────────────────────────────────────────

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

// ─────────────────────────────────────────────
//  TIMEZONE — Philippines
// ─────────────────────────────────────────────

private val PH_TZ = TimeZone.getTimeZone("Asia/Manila")

private fun fmtTime(ts: Long): String {
    val sdf = SimpleDateFormat("MMM dd · hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

private fun fmtDate(ts: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

private fun fmtHeader(ts: Long): String {
    val sdf = SimpleDateFormat("EEE, MMM dd yyyy · hh:mm a", Locale.getDefault())
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

// ─────────────────────────────────────────────
//  ROOT
// ─────────────────────────────────────────────

@Composable
fun AdminScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    var loggedInUser by remember { mutableStateOf<User?>(null) }
    var checkingAuth by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val current = auth.currentUser
        if (current != null) {
            AppStorage.getUser(current.uid) { user ->
                loggedInUser = user
                checkingAuth = false
            }
        } else {
            checkingAuth = false
        }
    }

    when {
        checkingAuth -> {
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
        loggedInUser == null -> {
            AdminLogin(
                onLogin = {
                    val current = auth.currentUser
                    if (current != null) {
                        AppStorage.getUser(current.uid) { user ->
                            loggedInUser = user
                        }
                    }
                },
                navController = navController
            )
        }
        else -> {
            AdminDashboard(
                user     = loggedInUser!!,
                onLogout = {
                    auth.signOut()
                    loggedInUser = null
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────
//  LOGIN
// ─────────────────────────────────────────────

@Composable
fun AdminLogin(onLogin: () -> Unit, navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()
    var email     by remember { mutableStateOf("") }
    var pass      by remember { mutableStateOf("") }
    var error     by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        Modifier.fillMaxSize().background(BgDeep).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors   = CardDefaults.cardColors(containerColor = BgCard),
            shape    = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(24.dp)) {

                // Back button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(bottom = 20.dp)
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
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint     = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("Back to Home", color = TextSub, fontSize = 12.sp)
                }

                // Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter            = painterResource(id = R.drawable.itmso_logo),
                        contentDescription = "ITMSO Logo",
                        contentScale       = ContentScale.Fit,
                        modifier           = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "VISITRA · ITMSO",
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp
                        )
                        Text("Admin Portal", color = TextMuted, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(Modifier.height(16.dp))

                Text(
                    "Sign In",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 18.sp
                )
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = { Text("Email Address", color = TextMuted) },
                    singleLine    = true,
                    leadingIcon   = { Icon(Icons.Outlined.Email, null, tint = TextMuted) },
                    colors        = adminFieldColors(),
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value                = pass,
                    onValueChange        = { pass = it },
                    label                = { Text("Password", color = TextMuted) },
                    singleLine           = true,
                    leadingIcon          = { Icon(Icons.Outlined.Lock, null, tint = TextMuted) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors               = adminFieldColors(),
                    shape                = RoundedCornerShape(12.dp),
                    modifier             = Modifier.fillMaxWidth()
                )

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = AccentRed, fontSize = 13.sp)
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || pass.isBlank()) {
                            error = "Please fill in all fields."
                            return@Button
                        }
                        isLoading = true
                        error     = ""
                        auth.signInWithEmailAndPassword(email.trim(), pass.trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = task.result?.user?.uid ?: ""
                                    AppStorage.getUser(uid) { user ->
                                        isLoading = false
                                        if (user != null &&
                                            (user.role == "Admin" ||
                                                    user.role == "Super Admin" ||
                                                    user.role == "Staff")
                                        ) {
                                            onLogin()
                                        } else if (user == null) {
                                            AppStorage.getAdminUser(uid) { admin ->
                                                if (admin != null) onLogin()
                                                else error = "Access Denied: Unauthorized role."
                                            }
                                        } else {
                                            error = "Access Denied: Unauthorized role."
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    error = task.exception?.message ?: "Login failed."
                                }
                            }
                    },
                    enabled  = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color    = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("LOGIN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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

// ─────────────────────────────────────────────
//  SUMMARY ITEM
// ─────────────────────────────────────────────

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

// ─────────────────────────────────────────────
//  DASHBOARD
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(user: User, onLogout: () -> Unit) {

    val context   = LocalContext.current
    val allVisits = remember { AppStorage.visits }
    val isSuper   = user.role == "Super Admin"

    var currentTab by remember { mutableStateOf(0) }

    fun getName(v: Visit): String = when {
        v.visitorName.isNotBlank() -> v.visitorName
        else -> AppStorage.students.find { it.studentId == v.studentId }?.name ?: v.studentId
    }

    var search         by remember { mutableStateOf("") }
    var dateFrom       by remember { mutableStateOf("") }
    var dateTo         by remember { mutableStateOf("") }
    var statusFilter   by remember { mutableStateOf("All") }
    var typeFilter     by remember { mutableStateOf("All") }
    var statusExpanded by remember { mutableStateOf(false) }
    var typeExpanded   by remember { mutableStateOf(false) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker   by remember { mutableStateOf(false) }
    var visitToDelete    by remember { mutableStateOf<Visit?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val sdfKey = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply { timeZone = PH_TZ }
    }

    val filtered = remember(search, dateFrom, dateTo, statusFilter, typeFilter, allVisits.size) {
        allVisits.filter { v ->
            val name        = getName(v)
            val matchSearch = search.isEmpty() ||
                    v.studentId.contains(search, true) ||
                    name.contains(search, true) ||
                    v.purpose.contains(search, true)
            val dateStr   = fmtDate(v.timeIn)
            val matchFrom = dateFrom.isEmpty() || dateStr >= dateFrom
            val matchTo   = dateTo.isEmpty()   || dateStr <= dateTo
            val matchStatus = when (statusFilter) {
                "Active" -> v.timeOut == null
                "Done"   -> v.timeOut != null
                else     -> true
            }
            val matchType = typeFilter == "All" || v.visitorType == typeFilter
            matchSearch && matchFrom && matchTo && matchStatus && matchType
        }
    }

    val totalCount    = allVisits.size
    val activeCount   = allVisits.count { it.timeOut == null }
    val doneCount     = allVisits.count { it.timeOut != null }
    val studentCount  = allVisits.count { it.visitorType == "Student" }
    val facultyCount  = allVisits.count { it.visitorType == "Faculty" }
    val externalCount = allVisits.count { it.visitorType == "External" }

    val dateTimeStr = remember { fmtHeader(System.currentTimeMillis()) }

    // Date pickers
    if (showFromPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { dateFrom = sdfKey.format(Date(it)) }
                    showFromPicker = false
                }) { Text("OK", color = AccentBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showFromPicker = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        ) { DatePicker(state = state) }
    }

    if (showToPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { dateTo = sdfKey.format(Date(it)) }
                    showToPicker = false
                }) { Text("OK", color = AccentBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showToPicker = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        ) { DatePicker(state = state) }
    }

    // Delete dialog
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
            title = {
                Text("Delete Record?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "This will permanently remove the visit record for \"${getName(visitToDelete!!)}\". This cannot be undone.",
                    color    = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        visitToDelete?.let { AppStorage.deleteVisit(it) }
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

    // Export dialog
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
            title = {
                Text("Export Report", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Choose a format. Saved to phone Downloads folder.",
                    color    = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            showExportDialog = false
                            exportPDF(context, filtered) { v -> getName(v) }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape    = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.Share, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Export as PDF", fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = {
                            showExportDialog = false
                            exportCSV(context, filtered) { v -> getName(v) }
                        },
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
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            },
            dismissButton = {}
        )
    }

    Column(Modifier.fillMaxSize().background(BgDeep)) {

        // ── TOP BAR ──────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter            = painterResource(id = R.drawable.itmso_logo),
                contentDescription = "ITMSO Logo",
                contentScale       = ContentScale.Fit,
                modifier           = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "VISITRA · ITMSO",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp
                )
                Text(dateTimeStr, color = TextMuted, fontSize = 10.sp)
            }

            // Role badge
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        when (user.role) {
                            "Super Admin" -> AccentRed.copy(alpha = 0.15f)
                            "Admin"       -> AccentAmber.copy(alpha = 0.15f)
                            else          -> AccentGreen.copy(alpha = 0.15f)
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    user.role,
                    color = when (user.role) {
                        "Super Admin" -> AccentRed
                        "Admin"       -> AccentAmber
                        else          -> AccentGreen
                    },
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.width(8.dp))

            // Logout
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentRed.copy(alpha = 0.12f))
                    .border(1.dp, AccentRed.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { onLogout() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ExitToApp,
                        null,
                        tint     = AccentRed,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "Logout",
                        color      = AccentRed,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── TAB BAR (Super Admin only) ────────
        if (isSuper) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(BgTopBar)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Visits", "Users").forEachIndexed { i, label ->
                    val selected = currentTab == i
                    val accent   = if (i == 0) AccentBlue else AccentPurple
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) accent else BgCard)
                            .border(1.dp, if (selected) accent else BorderColor, RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { currentTab = i }
                            .padding(vertical = 8.dp),
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
        }

        // ── CONTENT ───────────────────────────
        if (currentTab == 0) {

            // ── VISITS TAB ───────────────────
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding      = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                item {
                    Text(
                        "Visit Records",
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 18.sp
                    )
                }

                // Summary card
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BgCard)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Session Overview",
                            color         = TextSub,
                            fontSize      = 10.sp,
                            fontWeight    = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SummaryItem("Total",  totalCount,  AccentBlue)
                            SummaryItem("Active", activeCount, AccentGreen)
                            SummaryItem("Done",   doneCount,   AccentAmber)
                        }
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                        Text(
                            "By Visitor Type",
                            color         = TextSub,
                            fontSize      = 10.sp,
                            fontWeight    = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SummaryItem("Student",  studentCount,  AccentBlue)
                            SummaryItem("Faculty",  facultyCount,  AccentPurple)
                            SummaryItem("External", externalCount, AccentAmber)
                        }
                    }
                }

                // Search
                item {
                    OutlinedTextField(
                        value         = search,
                        onValueChange = { search = it },
                        placeholder   = { Text("Search name, ID, purpose…", color = TextSub, fontSize = 13.sp) },
                        leadingIcon   = { Icon(Icons.Outlined.Search, null, tint = TextMuted) },
                        trailingIcon  = if (search.isNotEmpty()) {{
                            IconButton(onClick = { search = "" }) {
                                Icon(Icons.Outlined.Close, null, tint = TextMuted)
                            }
                        }} else null,
                        singleLine = true,
                        colors     = adminFieldColors(),
                        shape      = RoundedCornerShape(12.dp),
                        modifier   = Modifier.fillMaxWidth()
                    )
                }

                // Date filters
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminDateButton(
                            modifier = Modifier.weight(1f),
                            label    = if (dateFrom.isEmpty()) "From date" else dateFrom,
                            hasValue = dateFrom.isNotEmpty(),
                            onClick  = { showFromPicker = true },
                            onClear  = { dateFrom = "" }
                        )
                        AdminDateButton(
                            modifier = Modifier.weight(1f),
                            label    = if (dateTo.isEmpty()) "To date" else dateTo,
                            hasValue = dateTo.isNotEmpty(),
                            onClick  = { showToPicker = true },
                            onClear  = { dateTo = "" }
                        )
                    }
                }

                // Dropdown filters
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Status dropdown
                        Box(Modifier.weight(1f)) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BgCard)
                                    .border(
                                        1.dp,
                                        if (statusFilter != "All") AccentBlue.copy(0.5f) else BorderColor,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) { statusExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Status: $statusFilter",
                                    color    = if (statusFilter != "All") TextPrimary else TextSub,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Outlined.KeyboardArrowDown,
                                    null,
                                    tint     = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            DropdownMenu(
                                expanded         = statusExpanded,
                                onDismissRequest = { statusExpanded = false },
                                modifier         = Modifier.background(BgCard)
                            ) {
                                listOf("All", "Active", "Done").forEach { option ->
                                    DropdownMenuItem(
                                        text     = { Text(option, color = TextPrimary, fontSize = 13.sp) },
                                        onClick  = { statusFilter = option; statusExpanded = false },
                                        modifier = Modifier.background(BgCard)
                                    )
                                }
                            }
                        }

                        // Type dropdown
                        Box(Modifier.weight(1f)) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BgCard)
                                    .border(
                                        1.dp,
                                        if (typeFilter != "All") AccentBlue.copy(0.5f) else BorderColor,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) { typeExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Type: $typeFilter",
                                    color    = if (typeFilter != "All") TextPrimary else TextSub,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Outlined.KeyboardArrowDown,
                                    null,
                                    tint     = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            DropdownMenu(
                                expanded         = typeExpanded,
                                onDismissRequest = { typeExpanded = false },
                                modifier         = Modifier.background(BgCard)
                            ) {
                                listOf("All", "Student", "Faculty", "External").forEach { option ->
                                    DropdownMenuItem(
                                        text     = { Text(option, color = TextPrimary, fontSize = 13.sp) },
                                        onClick  = { typeFilter = option; typeExpanded = false },
                                        modifier = Modifier.background(BgCard)
                                    )
                                }
                            }
                        }
                    }
                }

                // Results + clear
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "Showing ${filtered.size} record${if (filtered.size != 1) "s" else ""}",
                            color    = TextMuted,
                            fontSize = 12.sp
                        )
                        if (search.isNotEmpty() || dateFrom.isNotEmpty() ||
                            dateTo.isNotEmpty() || statusFilter != "All" || typeFilter != "All"
                        ) {
                            TextButton(
                                onClick        = {
                                    search = ""; dateFrom = ""; dateTo = ""
                                    statusFilter = "All"; typeFilter = "All"
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("Clear filters", color = AccentBlue, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Empty state
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(BgCard)
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.Search,
                                    null,
                                    tint     = TextSub,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "No records match your filters",
                                    color    = TextSub,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filtered.reversed()) { visit ->
                        AdminVisitRow(
                            visit     = visit,
                            name      = getName(visit),
                            canDelete = isSuper,
                            onDelete  = {
                                visitToDelete    = visit
                                showDeleteDialog = true
                            }
                        )
                    }
                }

                item { Spacer(Modifier.height(4.dp)) }
            }

            // Export button
            Surface(color = BgTopBar, tonalElevation = 0.dp) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
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
                        Text(
                            "Export Report",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp
                        )
                    }
                    Text(
                        "Exporting ${filtered.size} record${if (filtered.size != 1) "s" else ""}  ·  Saved to phone Downloads",
                        color    = TextSub,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

        } else {
            // ── USERS TAB ────────────────────
            UserManagementSection()
        }
    }
}

// ─────────────────────────────────────────────
//  VISIT ROW
// ─────────────────────────────────────────────

@Composable
fun AdminVisitRow(visit: Visit, name: String, canDelete: Boolean, onDelete: () -> Unit) {
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
        Column(Modifier.padding(12.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    name,
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(Modifier.width(6.dp))
                AdminStatusBadge(isActive)
                if (canDelete) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentRed.copy(alpha = 0.12f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint     = AccentRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(3.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(typeAccent.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        visit.visitorType,
                        color      = typeAccent,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text("ID: ${visit.studentId}", color = TextSub, fontSize = 11.sp)
            }

            Text(
                visit.purpose,
                color    = TextMuted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdminTimeCell(Icons.Outlined.CheckCircle, "Time In",  fmtTime(visit.timeIn))
                AdminTimeCell(Icons.Outlined.Close,       "Time Out", visit.timeOut?.let { fmtTime(it) } ?: "—")
            }
        }
    }
}

@Composable
private fun AdminTimeCell(icon: ImageVector, label: String, value: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = TextMuted, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, color = TextSub, fontSize = 10.sp)
        }
        Text(value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AdminStatusBadge(isActive: Boolean) {
    val bg  = if (isActive) AccentGreen.copy(alpha = 0.15f) else AccentAmber.copy(alpha = 0.15f)
    val fg  = if (isActive) AccentGreen else AccentAmber
    val txt = if (isActive) "Active" else "Done"
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(0.5.dp, fg.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(txt, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────
//  DATE FILTER BUTTON
// ─────────────────────────────────────────────

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
            .border(
                1.dp,
                if (hasValue) AccentBlue.copy(0.5f) else BorderColor,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.DateRange,
            null,
            tint     = if (hasValue) AccentBlue else TextMuted,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            color    = if (hasValue) TextPrimary else TextSub,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        if (hasValue) {
            IconButton(onClick = onClear, modifier = Modifier.size(18.dp)) {
                Icon(Icons.Outlined.Close, null, tint = TextMuted, modifier = Modifier.size(12.dp))
            }
        } else {
            TextButton(
                onClick        = onClick,
                contentPadding = PaddingValues(0.dp),
                modifier       = Modifier.height(18.dp)
            ) {
                Text("Pick", color = AccentBlue, fontSize = 11.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  USER MANAGEMENT (Super Admin only)
// ─────────────────────────────────────────────

@Composable
fun UserManagementSection() {
    val users = AppStorage.users
    var userToEdit       by remember { mutableStateOf<User?>(null) }
    var showEditDialog   by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            "User Management",
            color      = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Manage system access and roles",
            color    = TextMuted,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(users) { u ->
                UserRow(u, onEditRole = {
                    userToEdit     = u
                    showEditDialog = true
                })
            }
        }
    }

    if (showEditDialog && userToEdit != null) {
        var selectedRole by remember { mutableStateOf(userToEdit!!.role) }
        val roles = listOf("User", "Staff", "Admin", "Super Admin")

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor   = BgCard,
            title = {
                Text(
                    "Edit Role: ${userToEdit!!.name.ifBlank { userToEdit!!.email }}",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    roles.forEach { role ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) { selectedRole = role }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRole == role,
                                onClick  = { selectedRole = role },
                                colors   = RadioButtonDefaults.colors(selectedColor = AccentBlue)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(role, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AppStorage.saveUser(userToEdit!!.copy(role = selectedRole))
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape  = RoundedCornerShape(10.dp)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun UserRow(user: User, onEditRole: () -> Unit) {
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
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.name.take(1).uppercase().ifBlank { "?" },
                    color      = AccentBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    user.name.ifBlank { "No Name" },
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
                Text(user.email, color = TextMuted, fontSize = 11.sp)
                Spacer(Modifier.height(4.dp))
                RoleBadge(user.role)
            }
            IconButton(onClick = onEditRole) {
                Icon(Icons.Outlined.Edit, null, tint = AccentBlue)
            }
        }
    }
}

@Composable
fun RoleBadge(role: String) {
    val color = when (role) {
        "Super Admin" -> AccentRed
        "Admin"       -> AccentAmber
        "Staff"       -> AccentGreen
        else          -> AccentBlue
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(role, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ─────────────────────────────────────────────
//  SAVE FILE
// ─────────────────────────────────────────────

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
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
            )
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use(writeData)
                Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Export failed: could not create file", Toast.LENGTH_LONG).show()
            }
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            FileOutputStream(File(dir, fileName)).use(writeData)
            Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

// ─────────────────────────────────────────────
//  PDF EXPORT
// ─────────────────────────────────────────────

fun exportPDF(context: Context, visits: List<Visit>, getName: (Visit) -> String) {
    val pdf    = PdfDocument()
    val W      = 595
    val H      = 842
    val margin = 36f

    var pageNum = 1
    var page    = pdf.startPage(PdfDocument.PageInfo.Builder(W, H, pageNum).create())
    var canvas  = page.canvas

    val pTitle = Paint().apply {
        textSize = 16f; typeface = Typeface.DEFAULT_BOLD
        color    = android.graphics.Color.parseColor("#1a1a2e")
    }
    val pHead = Paint().apply {
        textSize = 9f; typeface = Typeface.DEFAULT_BOLD
        color    = android.graphics.Color.parseColor("#5C7CFA")
    }
    val pBody = Paint().apply {
        textSize = 9f
        color    = android.graphics.Color.parseColor("#374151")
    }
    val pSub = Paint().apply {
        textSize = 8f
        color    = android.graphics.Color.parseColor("#9CA3AF")
    }
    val pLine = Paint().apply {
        color = android.graphics.Color.parseColor("#E5E7EB"); strokeWidth = 0.5f
    }
    val pRowBg = Paint().apply {
        color = android.graphics.Color.parseColor("#F9FAFB")
    }

    val now = System.currentTimeMillis()
    var y = margin + 20f
    canvas.drawText("VISITRA ITMSO — Visit Report", margin, y, pTitle)
    y += 16f
    canvas.drawText(
        "Generated: ${fmtExport(now)}   |   Total: ${visits.size}",
        margin, y, pSub
    )
    y += 8f
    pLine.strokeWidth = 1f
    canvas.drawLine(margin, y, W - margin, y, pLine)
    pLine.strokeWidth = 0.5f
    y += 14f

    val cols = listOf(
        "NAME"     to margin,
        "TYPE"     to margin + 100f,
        "PURPOSE"  to margin + 160f,
        "TIME IN"  to margin + 300f,
        "TIME OUT" to margin + 400f,
        "STATUS"   to margin + 500f
    )
    cols.forEach { (lbl, x) -> canvas.drawText(lbl, x, y, pHead) }
    y += 5f
    pLine.strokeWidth = 1f
    canvas.drawLine(margin, y, W - margin, y, pLine)
    pLine.strokeWidth = 0.5f
    y += 13f

    visits.forEachIndexed { idx, v ->
        if (y > H - margin - 24f) {
            canvas.drawText(
                "St. Bridget College Batangas · ITMSO   Page $pageNum",
                margin, H - 18f, pSub
            )
            pdf.finishPage(page); pageNum++
            page   = pdf.startPage(PdfDocument.PageInfo.Builder(W, H, pageNum).create())
            canvas = page.canvas; y = margin + 20f
        }
        if (idx % 2 == 0) canvas.drawRect(margin - 4f, y - 10f, W - margin + 4f, y + 5f, pRowBg)
        canvas.drawText(getName(v).take(18),                         cols[0].second, y, pBody)
        canvas.drawText(v.visitorType,                               cols[1].second, y, pBody)
        canvas.drawText(v.purpose.take(18),                          cols[2].second, y, pBody)
        canvas.drawText(fmtTime(v.timeIn),                           cols[3].second, y, pBody)
        canvas.drawText(v.timeOut?.let { fmtTime(it) } ?: "—",      cols[4].second, y, pBody)
        canvas.drawText(if (v.timeOut != null) "Done" else "Active", cols[5].second, y, pBody)
        y += 16f
        canvas.drawLine(margin, y - 4f, W - margin, y - 4f, pLine)
    }
    canvas.drawText(
        "St. Bridget College Batangas · ITMSO   Page $pageNum",
        margin, H - 18f, pSub
    )
    pdf.finishPage(page)

    saveFile(context, "visitra_${fmtFileName(now)}.pdf", "application/pdf") { stream ->
        pdf.writeTo(stream)
    }
    pdf.close()
}

// ─────────────────────────────────────────────
//  CSV EXPORT
// ─────────────────────────────────────────────

fun exportCSV(context: Context, visits: List<Visit>, getName: (Visit) -> String) {
    val header = "Name,Visitor Type,ID,Purpose,Time In,Time Out,Status\n"
    val rows   = visits.joinToString("\n") { v ->
        listOf(
            getName(v),
            v.visitorType,
            v.studentId,
            v.purpose,
            fmtTime(v.timeIn),
            v.timeOut?.let { fmtTime(it) } ?: "Still In",
            if (v.timeOut != null) "Completed" else "Active"
        ).joinToString(",") { "\"$it\"" }
    }
    val now = System.currentTimeMillis()
    saveFile(context, "visitra_${fmtFileName(now)}.csv", "text/csv") { stream ->
        stream.write((header + rows).toByteArray())
    }
}