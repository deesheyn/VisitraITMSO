package com.visitraitmso.app

import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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

private val PH_TZ = TimeZone.getTimeZone("Asia/Manila")

private fun fmtTimeStaff(ts: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

private fun fmtDateStaff(ts: Long): String {
    val sdf = SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault())
    sdf.timeZone = PH_TZ
    return sdf.format(Date(ts))
}

// ─────────────────────────────────────────────
//  STAFF LOGIN SCREEN
// ─────────────────────────────────────────────

@Composable
fun StaffLoginScreen(
    navController: NavHostController,
    viewModel:     StaffViewModel
) {
    LaunchedEffect(viewModel.loggedInStaff) {
        if (viewModel.loggedInStaff != null) {
            navController.navigate("staff_dashboard") {
                popUpTo("staff_login") { inclusive = true }
            }
        }
    }

    if (viewModel.checkingAuth) {
        Box(
            Modifier.fillMaxSize().background(BgDeep),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentPurple)
        }
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AccentPurple.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(Modifier.fillMaxSize()) {

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
                    fontSize = 11.sp
                )
            }

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
                    Box(
                        Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AccentPurple.copy(alpha = 0.3f),
                                        AccentPurple.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .border(2.dp, AccentPurple.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 34.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Staff Portal",
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 22.sp
                        )
                        Text(
                            "Sign in with your staff credentials",
                            color    = TextSub,
                            fontSize = 12.sp
                        )
                    }

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
                            leadingIcon   = {
                                Icon(Icons.Outlined.Email, null, tint = AccentPurple)
                            },
                            colors   = staffFieldColors(),
                            shape    = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value                = viewModel.password,
                            onValueChange        = { viewModel.password = it },
                            label                = { Text("Password", color = TextMuted) },
                            singleLine           = true,
                            leadingIcon          = {
                                Icon(Icons.Outlined.Lock, null, tint = AccentPurple)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            colors               = staffFieldColors(),
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
                                Text("⚠️", fontSize = 14.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    viewModel.loginError,
                                    color    = AccentRed,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.login {
                                    navController.navigate("staff_dashboard") {
                                        popUpTo("staff_login") { inclusive = true }
                                    }
                                }
                            },
                            enabled  = !viewModel.isLoggingIn,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor         = AccentPurple,
                                disabledContainerColor = AccentPurple.copy(alpha = 0.4f)
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
private fun staffFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    cursorColor             = AccentPurple,
    focusedBorderColor      = AccentPurple,
    unfocusedBorderColor    = BorderColor,
    focusedContainerColor   = BgInput,
    unfocusedContainerColor = BgInput
)

// ─────────────────────────────────────────────
//  STAFF DASHBOARD SCREEN
// ─────────────────────────────────────────────

@Composable
fun StaffDashboardScreen(
    navController: NavHostController,
    viewModel:     StaffViewModel
) {
    val allVisits by viewModel.allVisits.collectAsState()
    val allStaffs by viewModel.allStaffs.collectAsState()

    val waiting = viewModel.waitingVisits(allVisits)
    val serving = viewModel.servingVisits(allVisits)
    val done    = viewModel.doneVisits(allVisits)

    val staffName = viewModel.loggedInStaff?.name ?: "Staff"
    val staffInit = staffName.take(1).uppercase()
    val dateStr   = remember { fmtDateStaff(System.currentTimeMillis()) }

    val isAvailable = viewModel.loggedInStaff?.isAvailable ?: true
    val availColor  = if (isAvailable) AccentGreen else AccentRed
    val availLabel  = if (isAvailable) "Available" else "Unavailable"

    var visitToTransfer    by remember { mutableStateOf<Visit?>(null) }
    var showTransferDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(BgDeep)) {

        // ── TOP BAR — split into two rows for breathing room ──
        Column(
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 16.dp)
                .padding(top = 14.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: Avatar + Name/Date + Logout
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AccentPurple.copy(alpha = 0.5f),
                                        AccentPurple.copy(alpha = 0.2f)
                                    )
                                )
                            )
                            .border(1.5.dp, AccentPurple.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            staffInit,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                    }
                    Column {
                        Text(
                            staffName,
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp
                        )
                        Text(
                            dateStr,
                            color    = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Logout button
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentRed.copy(alpha = 0.1f))
                        .border(1.dp, AccentRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) {
                            viewModel.logout {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ExitToApp,
                            null,
                            tint     = AccentRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Logout",
                            color      = AccentRed,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Row 2: Staff badge + Availability toggle — full width, side by side
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Staff role badge
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentPurple.copy(alpha = 0.15f))
                        .border(1.dp, AccentPurple.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        "Staff",
                        color      = AccentPurple,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Availability toggle — takes remaining space
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(availColor.copy(alpha = 0.12f))
                        .border(1.dp, availColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { viewModel.setAvailability(!isAvailable) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(availColor)
                        )
                        Text(
                            availLabel,
                            color      = availColor,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Stat strip
        Row(
            Modifier
                .fillMaxWidth()
                .background(BgTopBar)
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StaffStatChip("Waiting", waiting.size, AccentAmber, Modifier.weight(1f))
            StaffStatChip("Serving", serving.size, AccentGreen, Modifier.weight(1f))
            StaffStatChip("Done",    done.size,    AccentBlue,  Modifier.weight(1f))
        }

        // Unavailable banner
        if (!isAvailable) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(AccentRed.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("🔴", fontSize = 12.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "You are currently unavailable. New visitors will not be assigned to you.",
                    color      = AccentRed,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign  = TextAlign.Center
                )
            }
        }

        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (serving.isNotEmpty()) {
                item { StaffSectionHeader("Now Serving", AccentGreen, serving.size) }
                items(serving) { visit ->
                    StaffServingCard(
                        visit      = visit,
                        onDone     = { viewModel.markDone(visit) },
                        onTransfer = {
                            visitToTransfer    = visit
                            showTransferDialog = true
                        }
                    )
                }
            }

            if (waiting.isNotEmpty()) {
                item { StaffSectionHeader("Waiting", AccentAmber, waiting.size) }
                items(waiting) { visit ->
                    StaffWaitingCard(
                        visit      = visit,
                        onCall     = { viewModel.callNext(visit) },
                        onTransfer = {
                            visitToTransfer    = visit
                            showTransferDialog = true
                        }
                    )
                }
            }

            if (done.isNotEmpty()) {
                item { StaffSectionHeader("Completed Today", AccentBlue, done.size) }
                items(done) { visit ->
                    StaffDoneCard(visit)
                }
            }

            if (waiting.isEmpty() && serving.isEmpty() && done.isEmpty()) {
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(AccentPurple.copy(alpha = 0.1f))
                                .border(1.dp, AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎉", fontSize = 32.sp)
                        }
                        Text(
                            "No visitors assigned yet",
                            color      = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                        Text(
                            "Visitors will appear here when they time in\nand get assigned to you.",
                            color      = TextSub,
                            fontSize   = 12.sp,
                            textAlign  = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    // Transfer dialog
    if (showTransferDialog && visitToTransfer != null) {
        AlertDialog(
            onDismissRequest = { showTransferDialog = false; visitToTransfer = null },
            containerColor   = BgCard,
            icon = {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentBlue.copy(alpha = 0.12f))
                        .border(1.dp, AccentBlue.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔄", fontSize = 24.sp)
                }
            },
            title = {
                Text(
                    "Transfer Visitor",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentAmber.copy(alpha = 0.07f))
                            .border(1.dp, AccentAmber.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                "#${visitToTransfer!!.queueNumber}",
                                color      = AccentAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                visitToTransfer!!.visitorName.ifBlank { visitToTransfer!!.studentId },
                                color      = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 13.sp
                            )
                            Text(
                                visitToTransfer!!.purpose,
                                color    = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        "Transfer to:",
                        color      = TextMuted,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    val otherStaff = allStaffs.filter {
                        it.uid != viewModel.loggedInStaff?.uid && it.isAvailable
                    }

                    if (otherStaff.isEmpty()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(BgInput)
                                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No other available staff to transfer to.",
                                color    = TextSub,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        otherStaff.forEach { staff ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentPurple.copy(alpha = 0.07f))
                                    .border(1.dp, AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) {
                                        viewModel.transferVisit(visitToTransfer!!, staff)
                                        showTransferDialog = false
                                        visitToTransfer    = null
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AccentPurple.copy(alpha = 0.2f))
                                        .border(1.dp, AccentPurple.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        staff.name.take(1).uppercase(),
                                        color      = AccentPurple,
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 14.sp
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        staff.name,
                                        color      = TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 13.sp
                                    )
                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            Modifier
                                                .size(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(AccentGreen)
                                        )
                                        Text(
                                            "Available",
                                            color    = AccentGreen,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Text("→", color = AccentPurple, fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showTransferDialog = false
                    visitToTransfer    = null
                }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────
//  COMPONENTS
// ─────────────────────────────────────────────

@Composable
private fun StaffStatChip(label: String, value: Int, accent: Color, modifier: Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.1f))
            .border(1.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Text(value.toString(), color = accent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun StaffSectionHeader(title: String, accent: Color, count: Int) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(accent.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(count.toString(), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StaffServingCard(
    visit:      Visit,
    onDone:     () -> Unit,
    onTransfer: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.08f,
        targetValue   = 0.18f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AccentGreen.copy(alpha = pulseAlpha))
            .border(1.5.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGreen.copy(alpha = 0.2f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#${visit.queueNumber}",
                        color      = AccentGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
                    )
                }
                Column {
                    Text(
                        visit.visitorName.ifBlank { visit.studentId },
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(visit.purpose, color = TextMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("In: ${fmtTimeStaff(visit.timeIn)}", color = TextSub, fontSize = 10.sp)
                }
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        when (visit.visitorType) {
                            "Faculty"  -> AccentPurple.copy(alpha = 0.15f)
                            "External" -> AccentAmber.copy(alpha = 0.15f)
                            else       -> AccentBlue.copy(alpha = 0.15f)
                        }
                    )
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    visit.visitorType,
                    color = when (visit.visitorType) {
                        "Faculty"  -> AccentPurple
                        "External" -> AccentAmber
                        else       -> AccentBlue
                    },
                    fontSize = 10.sp, fontWeight = FontWeight.SemiBold
                )
            }
        }

        HorizontalDivider(color = AccentGreen.copy(alpha = 0.15f), thickness = 0.5.dp)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .weight(1f).clip(RoundedCornerShape(10.dp)).background(AccentGreen)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDone() }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) { Text("✓  Done", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }

            Box(
                Modifier
                    .weight(1f).clip(RoundedCornerShape(10.dp)).background(BgInput)
                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTransfer() }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) { Text("🔄  Transfer", color = TextMuted, fontWeight = FontWeight.Medium, fontSize = 13.sp) }
        }
    }
}

@Composable
private fun StaffWaitingCard(
    visit:      Visit,
    onCall:     () -> Unit,
    onTransfer: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier
                        .size(48.dp).clip(RoundedCornerShape(12.dp))
                        .background(AccentAmber.copy(alpha = 0.12f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("#${visit.queueNumber}", color = AccentAmber, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

                Column {
                    Text(visit.visitorName.ifBlank { visit.studentId }, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(visit.purpose, color = TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Waiting since ${fmtTimeStaff(visit.timeIn)}", color = TextSub, fontSize = 10.sp)
                }
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(when (visit.visitorType) { "Faculty" -> AccentPurple.copy(alpha = 0.12f); "External" -> AccentAmber.copy(alpha = 0.12f); else -> AccentBlue.copy(alpha = 0.12f) })
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) { Text(visit.visitorType, color = when (visit.visitorType) { "Faculty" -> AccentPurple; "External" -> AccentAmber; else -> AccentBlue }, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) }
        }

        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .weight(1f).clip(RoundedCornerShape(10.dp)).background(AccentAmber)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCall() }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) { Text("📢  Call", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }

            Box(
                Modifier
                    .weight(1f).clip(RoundedCornerShape(10.dp)).background(BgInput)
                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTransfer() }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) { Text("🔄  Transfer", color = TextMuted, fontWeight = FontWeight.Medium, fontSize = 13.sp) }
        }
    }
}

@Composable
private fun StaffDoneCard(visit: Visit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(38.dp).clip(RoundedCornerShape(10.dp))
                .background(AccentBlue.copy(alpha = 0.08f))
                .border(1.dp, AccentBlue.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Text("#${visit.queueNumber}", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp) }

        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(visit.visitorName.ifBlank { visit.studentId }, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(visit.purpose, color = TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Done", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(visit.timeOut?.let { fmtTimeStaff(it) } ?: "—", color = TextSub, fontSize = 10.sp)
        }
    }
}