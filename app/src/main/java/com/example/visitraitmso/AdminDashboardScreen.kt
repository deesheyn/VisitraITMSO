package com.visitraitmso.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

    if (checkingAuth) {
        Box(Modifier.fillMaxSize().background(BgDeep), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
    } else if (loggedInUser == null) {
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
    } else {
        AdminDashboard(
            user = loggedInUser!!,
            onLogout = {
                auth.signOut()
                loggedInUser = null
                navController.navigate("home") { popUpTo("home") { inclusive = true } }
            }
        )
    }
}

@Composable
fun AdminLogin(onLogin: () -> Unit, navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var pass  by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(bottom = 20.dp)
                ) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BorderColor)
                            .clickable { navController.navigateUp() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("Back", color = TextSub, fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter            = painterResource(id = R.drawable.itmso_logo),
                        contentDescription = null,
                        modifier           = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("VISITRA · ITMSO", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Firebase Secure Login", color = TextMuted, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(Modifier.height(16.dp))

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
                    value         = pass,
                    onValueChange = { pass = it },
                    label         = { Text("Password", color = TextMuted) },
                    singleLine    = true,
                    leadingIcon   = { Icon(Icons.Outlined.Lock, null, tint = TextMuted) },
                    colors        = adminFieldColors(),
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
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
                        auth.signInWithEmailAndPassword(email.trim(), pass.trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = task.result?.user?.uid ?: ""
                                    AppStorage.getUser(uid) { user ->
                                        isLoading = false
                                        if (user != null && (user.role == "Admin" || user.role == "Super Admin" || user.role == "Staff")) {
                                            onLogin()
                                        } else if (user == null) {
                                            // Maybe check admins collection for backward compatibility
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
                                    error = task.exception?.message ?: "Login failed"
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape    = RoundedCornerShape(12.dp),
                    enabled  = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("LOGIN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        Text(value.toString(), color = accent, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(user: User, onLogout: () -> Unit) {
    val context   = LocalContext.current
    val allVisits = remember { AppStorage.visits }
    val isSuper   = user.role == "Super Admin"
    var currentTab by remember { mutableStateOf(0) } // 0: Visits, 1: Users (if Super Admin)

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

    val sdfKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply { timeZone = PH_TZ } }

    val filtered = remember(search, dateFrom, dateTo, statusFilter, typeFilter, allVisits.size) {
        allVisits.filter { v ->
            val name = getName(v)
            val matchSearch = search.isEmpty() || v.studentId.contains(search, true) || name.contains(search, true)
            val dateStr = fmtDate(v.timeIn)
            val matchFrom = dateFrom.isEmpty() || dateStr >= dateFrom
            val matchTo = dateTo.isEmpty() || dateStr <= dateTo
            val matchStatus = when (statusFilter) { "Active" -> v.timeOut == null; "Done" -> v.timeOut != null; else -> true }
            val matchType = typeFilter == "All" || v.visitorType == typeFilter
            matchSearch && matchFrom && matchTo && matchStatus && matchType
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Dashboard", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${user.role}: ${user.email}", color = TextMuted, fontSize = 11.sp)
                    }
                },
                actions = {
                    if (isSuper) {
                        IconButton(onClick = { showExportDialog = true }) { Icon(Icons.Outlined.Share, null, tint = AccentBlue) }
                    }
                    IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null, tint = AccentRed) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgTopBar)
            )
        },
        bottomBar = {
            if (isSuper) {
                NavigationBar(containerColor = BgTopBar) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Outlined.List, null) },
                        label = { Text("Visits") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Outlined.Person, null) },
                        label = { Text("Users") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentPurple, selectedTextColor = AccentPurple, unselectedIconColor = TextMuted, unselectedTextColor = TextMuted)
                    )
                }
            }
        },
        containerColor = BgDeep
    ) { p ->
        Column(Modifier.padding(p).fillMaxSize().padding(16.dp)) {
            if (currentTab == 0) {
                // VISITS TAB
                // Summary Cards
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryItem("TOTAL", allVisits.size, TextPrimary)
                            Spacer(Modifier.weight(1f))
                            SummaryItem("ACTIVE", allVisits.count { it.timeOut == null }, AccentGreen)
                            Spacer(Modifier.weight(1f))
                            SummaryItem("DONE", allVisits.count { it.timeOut != null }, AccentAmber)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Filters
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = search, onValueChange = { search = it },
                        placeholder = { Text("Search name/ID...", color = TextSub, fontSize = 13.sp) },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = adminFieldColors(),
                        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = TextSub, modifier = Modifier.size(18.dp)) },
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminDateButton(Modifier.weight(1f), if (dateFrom.isEmpty()) "From Date" else dateFrom, dateFrom.isNotEmpty(), { showFromPicker = true }, { dateFrom = "" })
                    AdminDateButton(Modifier.weight(1f), if (dateTo.isEmpty()) "To Date" else dateTo, dateTo.isNotEmpty(), { showToPicker = true }, { dateTo = "" })
                }

                Spacer(Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Status Filter
                    Box(Modifier.weight(1f)) {
                        AdminFilterChip("Status: $statusFilter", statusExpanded) { statusExpanded = true }
                        DropdownMenu(statusExpanded, { statusExpanded = false }, Modifier.background(BgCard)) {
                            listOf("All", "Active", "Done").forEach {
                                DropdownMenuItem(text = { Text(it, color = TextPrimary) }, onClick = { statusFilter = it; statusExpanded = false })
                            }
                        }
                    }
                    // Type Filter
                    Box(Modifier.weight(1f)) {
                        AdminFilterChip("Type: $typeFilter", typeExpanded) { typeExpanded = true }
                        DropdownMenu(typeExpanded, { typeExpanded = false }, Modifier.background(BgCard)) {
                            listOf("All", "Student", "Visitor").forEach {
                                DropdownMenuItem(text = { Text(it, color = TextPrimary) }, onClick = { typeFilter = it; typeExpanded = false })
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Records (${filtered.size})", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(filtered.reversed()) { v ->
                        AdminVisitRow(
                            visit = v,
                            name = getName(v),
                            canDelete = isSuper,
                            onDelete = { visitToDelete = v; showDeleteDialog = true }
                        )
                    }
                }
            } else {
                // USERS TAB (Role Based Access Management)
                UserManagementSection()
            }
        }
    }

    if (showFromPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { dateFrom = sdfKey.format(Date(it)) }
                    showFromPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state) }
    }
    if (showToPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { dateTo = sdfKey.format(Date(it)) }
                    showToPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state) }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = BgCard,
            title = { Text("Delete Record", color = TextPrimary) },
            text = { Text("Are you sure you want to delete this record for ${visitToDelete?.let { getName(it) }}?", color = TextMuted) },
            confirmButton = {
                Button(onClick = {
                    visitToDelete?.let { AppStorage.deleteVisit(it) }
                    showDeleteDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = AccentRed)) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = TextSub) } }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = BgCard,
            title = { Text("Export Records", color = TextPrimary) },
            text = { Text("Choose format to export the current filtered list (${filtered.size} items).", color = TextMuted) },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { exportPDF(context, filtered, ::getName); showExportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) { Text("PDF") }
                    Button(onClick = { exportCSV(context, filtered, ::getName); showExportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) { Text("CSV") }
                }
            },
            dismissButton = { TextButton(onClick = { showExportDialog = false }) { Text("Close", color = TextSub) } }
        )
    }
}

@Composable
fun UserManagementSection() {
    val users = AppStorage.users
    var userToEdit by remember { mutableStateOf<User?>(null) }
    var showEditRoleDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Text("User Management", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Manage system access and roles", color = TextMuted, fontSize = 13.sp)

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(users) { u ->
                UserRow(u, onEditRole = {
                    userToEdit = u
                    showEditRoleDialog = true
                })
            }
        }
    }

    if (showEditRoleDialog && userToEdit != null) {
        var selectedRole by remember { mutableStateOf(userToEdit!!.role) }
        val roles = listOf("User", "Staff", "Admin", "Super Admin")

        AlertDialog(
            onDismissRequest = { showEditRoleDialog = false },
            containerColor = BgCard,
            title = { Text("Edit Role: ${userToEdit!!.name}", color = TextPrimary) },
            text = {
                Column {
                    roles.forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { selectedRole = role }.padding(vertical = 8.dp)
                        ) {
                            RadioButton(selected = (selectedRole == role), onClick = { selectedRole = role })
                            Text(role, color = TextPrimary, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    AppStorage.saveUser(userToEdit!!.copy(role = selectedRole))
                    showEditRoleDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditRoleDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun UserRow(user: User, onEditRole: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = BgCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(user.name.ifBlank { "No Name" }, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(user.email, color = TextMuted, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                RoleBadge(user.role)
            }
            IconButton(onClick = onEditRole) { Icon(Icons.Outlined.Edit, null, tint = AccentBlue) }
        }
    }
}

@Composable
fun RoleBadge(role: String) {
    val color = when(role) {
        "Super Admin" -> AccentRed
        "Admin"       -> AccentAmber
        "Staff"       -> AccentGreen
        else          -> AccentBlue
    }
    Box(Modifier.clip(RoundedCornerShape(4.dp)).background(color.copy(0.15f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text(role, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AdminFilterChip(label: String, expanded: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BgCard)
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Icon(if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown, null, tint = TextSub, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun AdminVisitRow(visit: Visit, name: String, canDelete: Boolean, onDelete: () -> Unit) {
    val isActive = visit.timeOut == null
    Card(colors = CardDefaults.cardColors(containerColor = BgCard), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AdminStatusBadge(isActive)
                    if (canDelete) {
                        IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, null, tint = AccentRed, modifier = Modifier.size(18.dp)) }
                    }
                }
            }
            Text("ID: ${visit.studentId} · ${visit.visitorType}", color = TextSub, fontSize = 11.sp)
            Text(visit.purpose, color = TextMuted, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("In: ${fmtTime(visit.timeIn)}", color = TextPrimary, fontSize = 11.sp)
                Text("Out: ${visit.timeOut?.let { fmtTime(it) } ?: "—"}", color = TextPrimary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun AdminStatusBadge(isActive: Boolean) {
    val bg = if (isActive) AccentGreen.copy(0.15f) else AccentAmber.copy(0.15f)
    val fg = if (isActive) AccentGreen else AccentAmber
    Box(Modifier.clip(RoundedCornerShape(20.dp)).background(bg).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(if (isActive) "Active" else "Done", color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AdminDateButton(modifier: Modifier, label: String, hasValue: Boolean, onClick: () -> Unit, onClear: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BgCard)
            .border(1.dp, if (hasValue) AccentBlue else BorderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (hasValue) TextPrimary else TextSub, fontSize = 12.sp, modifier = Modifier.weight(1f))
        if (hasValue) IconButton(onClick = onClear, modifier = Modifier.size(18.dp)) { Icon(Icons.Outlined.Close, null, tint = TextMuted, modifier = Modifier.size(12.dp)) }
    }
}

private fun saveFile(context: Context, fileName: String, mimeType: String, writeData: (java.io.OutputStream) -> Unit) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            uri?.let { context.contentResolver.openOutputStream(it)?.use(writeData); Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_SHORT).show() }
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            FileOutputStream(File(dir, fileName)).use(writeData); Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
}

fun exportPDF(context: Context, visits: List<Visit>, getName: (Visit) -> String) {
    val pdf = PdfDocument()
    val page = pdf.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
    val canvas = page.canvas
    val paint = Paint().apply { textSize = 12f }
    var y = 50f
    canvas.drawText("Visitra Report - ${fmtExport(System.currentTimeMillis())}", 50f, y, paint)
    y += 30f
    visits.forEach { v ->
        canvas.drawText("${getName(v)} | ${v.visitorType} | ${v.purpose} | In: ${fmtTime(v.timeIn)}", 50f, y, paint)
        y += 20f
    }
    pdf.finishPage(page)
    saveFile(context, "visitra_${fmtFileName(System.currentTimeMillis())}.pdf", "application/pdf") { pdf.writeTo(it) }
    pdf.close()
}

fun exportCSV(context: Context, visits: List<Visit>, getName: (Visit) -> String) {
    val csv = "Name,Type,Purpose,Time In,Time Out\n" + visits.joinToString("\n") { v ->
        "${getName(v)},${v.visitorType},${v.purpose},${fmtTime(v.timeIn)},${v.timeOut?.let { fmtTime(it) } ?: "Active"}"
    }
    saveFile(context, "visitra_${fmtFileName(System.currentTimeMillis())}.csv", "text/csv") { it.write(csv.toByteArray()) }
}
