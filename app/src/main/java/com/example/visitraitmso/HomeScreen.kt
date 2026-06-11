package com.visitraitmso.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.util.Calendar
import java.util.TimeZone

private val BgDeep      = Color(0xFF0A0F1E)
private val BgCard      = Color(0xFF111827)
private val AccentBlue  = Color(0xFF5C7CFA)
private val AccentGreen = Color(0xFF34D399)
private val AccentAmber = Color(0xFFFBBF24)
private val AccentCyan  = Color(0xFF06B6D4)
private val TextPrimary = Color(0xFFE0E8FF)
private val TextMuted   = Color(0xFF6B7FC4)
private val TextSub     = Color(0xFF4A5680)
private val BorderColor = Color(0xFF1E2D5A)

private val PH_TIMEZONE = TimeZone.getTimeZone("Asia/Manila")

@Composable
fun HomeScreen(navController: NavHostController) {

    val calendar    = remember { Calendar.getInstance(PH_TIMEZONE) }
    val currentYear = calendar.get(Calendar.YEAR)

    val greeting = remember {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11  -> "Good Morning!"
            in 12..17 -> "Good Afternoon!"
            else      -> "Good Evening!"
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Logo
            Box(
                Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        color = AccentBlue.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.itmso_logo),
                    contentDescription = "ITMSO Visitra Logo",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "VisitraITMSO",
                color         = TextPrimary,
                fontWeight    = FontWeight.Bold,
                fontSize      = 26.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "Office of IT Management & Systems",
                color     = TextMuted,
                fontSize  = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Box(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentBlue.copy(alpha = 0.1f))
                    .border(1.dp, AccentBlue.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    greeting,
                    color      = AccentBlue,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(36.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    color     = BorderColor,
                    thickness = 0.5.dp
                )
                Text(
                    "  QUICK ACCESS  ",
                    color         = TextSub,
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    color     = BorderColor,
                    thickness = 0.5.dp
                )
            }

            Spacer(Modifier.height(16.dp))

            HomeNavButton(
                icon     = Icons.Outlined.Add,
                iconBg   = AccentBlue.copy(alpha = 0.15f),
                iconTint = AccentBlue,
                title    = "Register",
                subtitle = "Add student, faculty, or external visitor",
                badge    = null,
                onClick  = { navController.navigate("register") }
            )

            Spacer(Modifier.height(10.dp))

            HomeNavButton(
                icon     = Icons.Outlined.Person,
                iconBg   = AccentGreen.copy(alpha = 0.15f),
                iconTint = AccentGreen,
                title    = "Log a Visit",
                subtitle = "Time in / time out · Queue number assigned",
                badge    = null,
                onClick  = { navController.navigate("login") }
            )

            Spacer(Modifier.height(10.dp))

            HomeNavButtonEmoji(
                emoji     = "🖥️",
                iconBg    = AccentCyan.copy(alpha = 0.15f),
                title     = "Queue Monitor",
                subtitle  = "Display queue numbers on a monitor or TV",
                badge     = "Live",
                badgeTint = AccentCyan,
                onClick   = { navController.navigate("monitor") }
            )

            Spacer(Modifier.height(10.dp))

            HomeNavButton(
                icon     = Icons.Outlined.Settings,
                iconBg   = AccentAmber.copy(alpha = 0.15f),
                iconTint = AccentAmber,
                title    = "Personnel Portal",
                subtitle = "Admin dashboard · Staff management",
                badge    = null,
                onClick  = { navController.navigate("role_select") }
            )

            Spacer(Modifier.height(24.dp))

            // ── Info strip ────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgCard)
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // SBC Logo — circle
                    Image(
                        painter            = painterResource(id = R.drawable.sbc_logo),
                        contentDescription = "SBC Logo",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, BorderColor, CircleShape)
                    )
                    Column {
                        Text(
                            "St. Bridget College Batangas",
                            color      = TextMuted,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "ITMSO  ·  $currentYear",
                            color    = TextSub,
                            fontSize = 10.sp
                        )
                    }
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        "v2.0",
                        color      = AccentBlue,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────
//  NAV BUTTON — with ImageVector icon
// ─────────────────────────────────────────────

@Composable
fun HomeNavButton(
    icon:     ImageVector,
    iconBg:   Color,
    iconTint: Color,
    title:    String,
    subtitle: String,
    badge:    String?,
    onClick:  () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint     = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
                if (badge != null) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(iconTint.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            badge,
                            color      = iconTint,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                color      = TextSub,
                fontSize   = 11.sp,
                lineHeight = 15.sp
            )
        }

        Spacer(Modifier.width(8.dp))
        Text(">", color = BorderColor, fontSize = 18.sp, fontWeight = FontWeight.Light)
    }
}

// ─────────────────────────────────────────────
//  NAV BUTTON — with emoji
// ─────────────────────────────────────────────

@Composable
fun HomeNavButtonEmoji(
    emoji:     String,
    iconBg:    Color,
    title:     String,
    subtitle:  String,
    badge:     String?,
    badgeTint: Color,
    onClick:   () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
                if (badge != null) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(badgeTint.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            badge,
                            color      = badgeTint,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                color      = TextSub,
                fontSize   = 11.sp,
                lineHeight = 15.sp
            )
        }

        Spacer(Modifier.width(8.dp))
        Text(">", color = BorderColor, fontSize = 18.sp, fontWeight = FontWeight.Light)
    }
}