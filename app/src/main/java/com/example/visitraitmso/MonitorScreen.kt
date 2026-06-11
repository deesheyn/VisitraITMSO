package com.visitraitmso.app

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private val MonBgDeep    = Color(0xFF04080F)
private val MonBgCard    = Color(0xFF0A1628)
private val MonBgTopBar  = Color(0xFF060D1A)
private val MonBgSection = Color(0xFF080E1C)
private val MonGreen     = Color(0xFF10B981)
private val MonBlue      = Color(0xFF5C7CFA)
private val MonAmber     = Color(0xFFF59E0B)
private val MonPurple    = Color(0xFF8B5CF6)
private val MonTextPri   = Color(0xFFE0E8FF)
private val MonTextMuted = Color(0xFF6B7FC4)
private val MonTextSub   = Color(0xFF2A3A5A)
private val MonBorder    = Color(0xFF111E35)

private val PH_TZ_MON = TimeZone.getTimeZone("Asia/Manila")

@Composable
fun MonitorScreen(
    navController: NavHostController,
    repository:    AppRepository
) {
    val visitViewModel = remember { VisitViewModel(repository) }
    val allVisits      = visitViewModel.visits

    var clockStr by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            sdf.timeZone = PH_TZ_MON
            clockStr = sdf.format(Date())
            delay(1000)
        }
    }

    var dateStr by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault())
        sdf.timeZone = PH_TZ_MON
        dateStr = sdf.format(Date())
    }

    val todayStart = remember {
        Calendar.getInstance(PH_TZ_MON).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val todayVisits = allVisits.filter { it.timeIn >= todayStart && it.queueNumber > 0 }

    // ── FIX: changed isServing → serving ──
    val nowServing  = todayVisits.filter { it.timeOut == null && it.serving }
        .sortedBy { it.queueNumber }
    val waiting     = todayVisits.filter { it.timeOut == null && !it.serving }
        .sortedBy { it.queueNumber }
    val completed   = todayVisits.filter { it.timeOut != null }
        .sortedByDescending { it.queueNumber }

    Column(Modifier.fillMaxSize().background(MonBgDeep)) {

        // ── TOP BAR ──────────────────────────
        Column(
            Modifier
                .fillMaxWidth()
                .background(MonBgTopBar)
                .border(
                    width = 0.5.dp,
                    color = MonBorder,
                    shape = RoundedCornerShape(0.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                            .size(32.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(MonBlue.copy(alpha = 0.15f))
                            .border(1.dp, MonBlue.copy(alpha = 0.3f), RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🖥️", fontSize = 14.sp)
                    }
                    Column {
                        Text(
                            "VISITRA · ITMSO",
                            color         = MonTextPri,
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 13.sp,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            "St. Bridget College · Queue Monitor",
                            color    = MonTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            clockStr,
                            color      = MonTextPri,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            dateStr,
                            color    = MonTextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(MonBgCard)
                            .border(1.dp, MonBorder, RoundedCornerShape(7.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                null,
                                tint     = MonTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MonTopPill("Waiting", waiting.size,    MonAmber, Modifier.weight(1f))
                MonTopPill("Serving", nowServing.size, MonGreen, Modifier.weight(1f))
                MonTopPill("Done",    completed.size,  MonBlue,  Modifier.weight(1f))
            }
        }

        // ── BODY ─────────────────────────────
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── NOW SERVING ───────────────────
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MonBgSection)
                    .border(1.dp, MonBorder, RoundedCornerShape(14.dp))
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(7.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MonGreen)
                        )
                        Text(
                            "NOW SERVING",
                            color         = MonGreen,
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MonGreen.copy(alpha = 0.12f))
                            .border(1.dp, MonGreen.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            nowServing.size.toString(),
                            color      = MonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 11.sp
                        )
                    }
                }

                HorizontalDivider(color = MonBorder, thickness = 0.5.dp)

                if (nowServing.isEmpty() && waiting.isNotEmpty()) {
                    MonWaitingHint(waiting.first())
                } else if (nowServing.isEmpty()) {
                    MonEmptyState("No visitors being served", "⏸️")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        nowServing.forEach { visit ->
                            MonServingCard(visit)
                        }
                        if (waiting.isNotEmpty()) {
                            MonUpNextRow(waiting.take(3))
                        }
                    }
                }
            }

            // ── COMPLETED ─────────────────────
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MonBgSection)
                    .border(1.dp, MonBorder, RoundedCornerShape(14.dp))
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(7.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MonBlue)
                        )
                        Text(
                            "COMPLETED",
                            color         = MonBlue,
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MonBlue.copy(alpha = 0.12f))
                            .border(1.dp, MonBlue.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            completed.size.toString(),
                            color      = MonBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 11.sp
                        )
                    }
                }

                HorizontalDivider(color = MonBorder, thickness = 0.5.dp)

                if (completed.isEmpty()) {
                    MonEmptyState("No completed visits yet", "📋")
                } else {
                    LazyVerticalGrid(
                        columns               = GridCells.Fixed(4),
                        verticalArrangement   = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier              = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(completed) { visit ->
                            MonCompletedChip(visit)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonTopPill(label: String, value: Int, accent: Color, modifier: Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(1.dp, accent.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MonTextMuted, fontSize = 10.sp)
        Text(
            value.toString(),
            color      = accent,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp
        )
    }
}

@Composable
private fun MonServingCard(visit: Visit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.06f,
        targetValue   = 0.16f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MonGreen.copy(alpha = pulseAlpha + 0.04f),
                        MonGreen.copy(alpha = pulseAlpha)
                    )
                )
            )
            .border(1.5.dp, MonGreen.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MonGreen.copy(alpha = 0.15f))
                .border(1.dp, MonGreen.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "#${visit.queueNumber}",
                color      = MonGreen,
                fontWeight = FontWeight.Bold,
                fontSize   = if (visit.queueNumber < 10) 30.sp else 24.sp
            )
        }
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                visit.visitorName.ifBlank { visit.studentId },
                color      = MonTextPri,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                visit.purpose,
                color    = MonTextMuted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MonPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        visit.assignedStaffName.take(1).uppercase(),
                        color      = MonPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 8.sp
                    )
                }
                Text(
                    visit.assignedStaffName,
                    color    = MonPurple,
                    fontSize = 10.sp
                )
            }
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    when (visit.visitorType) {
                        "Faculty"  -> MonPurple.copy(alpha = 0.12f)
                        "External" -> MonAmber.copy(alpha = 0.12f)
                        else       -> MonBlue.copy(alpha = 0.12f)
                    }
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                visit.visitorType,
                color = when (visit.visitorType) {
                    "Faculty"  -> MonPurple
                    "External" -> MonAmber
                    else       -> MonBlue
                },
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MonWaitingHint(next: Visit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MonAmber.copy(alpha = 0.05f))
            .border(1.dp, MonAmber.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("⏳", fontSize = 24.sp)
        Column(Modifier.weight(1f)) {
            Text(
                "Next up",
                color      = MonAmber,
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "#${next.queueNumber} · ${next.visitorName.ifBlank { next.studentId }}",
                color      = MonTextPri,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MonUpNextRow(nextVisits: List<Visit>) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MonAmber.copy(alpha = 0.04f))
            .border(1.dp, MonAmber.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            "UP NEXT",
            color         = MonAmber,
            fontSize      = 9.sp,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            nextVisits.forEach { visit ->
                Row(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MonAmber.copy(alpha = 0.07f))
                        .border(1.dp, MonAmber.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "#${visit.queueNumber}",
                        color      = MonAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp
                    )
                    Text(
                        visit.visitorName.ifBlank { visit.studentId }.take(8),
                        color    = MonTextMuted,
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun MonCompletedChip(visit: Visit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MonBgCard)
            .border(1.dp, MonBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            "#${visit.queueNumber}",
            color      = MonBlue.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold,
            fontSize   = 14.sp
        )
        Text(
            "✓",
            color    = MonGreen.copy(alpha = 0.6f),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun MonEmptyState(message: String, emoji: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.width(8.dp))
        Text(message, color = MonTextSub, fontSize = 12.sp)
    }
}