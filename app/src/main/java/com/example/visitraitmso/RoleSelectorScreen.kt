package com.visitraitmso.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

private val BgDeep       = Color(0xFF0A0F1E)
private val BgCard       = Color(0xFF111827)
private val BgTopBar     = Color(0xFF0D1426)
private val AccentAmber  = Color(0xFFF59E0B)
private val AccentPurple = Color(0xFF8B5CF6)
private val TextPrimary  = Color(0xFFE0E8FF)
private val TextMuted    = Color(0xFF6B7FC4)
private val TextSub      = Color(0xFF4A5680)
private val BorderColor  = Color(0xFF1E2D5A)

@Composable
fun RoleSelectorScreen(
    navController: NavHostController,
    repository:    AppRepository
) {
    Column(Modifier.fillMaxSize().background(BgDeep)) {

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

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentAmber.copy(alpha = 0.12f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔐", fontSize = 24.sp)
                }

                Text(
                    "Personnel Portal",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    textAlign  = TextAlign.Center
                )

                Text(
                    "Select your role to continue",
                    color     = TextSub,
                    fontSize  = 12.sp,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

                RoleOption(
                    emoji    = "🛡️",
                    title    = "Administrator",
                    subtitle = "Full system access · Manage all records",
                    accent   = AccentAmber,
                    onClick  = { navController.navigate("dashboard") }
                )

                RoleOption(
                    emoji    = "👤",
                    title    = "Staff",
                    subtitle = "Manage your queue · Serve assigned visitors",
                    accent   = AccentPurple,
                    onClick  = { navController.navigate("staff_login") }
                )
            }
        }
    }
}

@Composable
private fun RoleOption(
    emoji:    String,
    title:    String,
    subtitle: String,
    accent:   Color,
    onClick:  () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.07f))
            .border(1.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color      = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                color      = TextMuted,
                fontSize   = 11.sp,
                lineHeight = 15.sp
            )
        }
        Text(">", color = BorderColor, fontSize = 16.sp)
    }
}