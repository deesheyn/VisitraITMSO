package com.visitraitmso.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

private val Navy = Color(0xFF0B1220)
private val Blue1 = Color(0xFF1E3A8A)
private val Blue2 = Color(0xFF2563EB)
private val CardDark = Color(0xFF111827)
private val Accent = Color(0xFF60A5FA)

@Composable
fun HomeScreen(navController: NavHostController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Navy, Blue1, Blue2)
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🟦 TITLE ONLY (CLEAN IDENTITY)
            Text(
                text = "VisitraITMSO",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 🔘 BUTTONS (PROPER SPACING — NOT TOO CLOSE)
            HomeButton(
                title = "Register Student",
                subtitle = "Create new record",
                onClick = { navController.navigate("register") }
            )

            Spacer(modifier = Modifier.height(18.dp))

            HomeButton(
                title = "Login Visit",
                subtitle = "Time in / out logging",
                onClick = { navController.navigate("login") }
            )

            Spacer(modifier = Modifier.height(18.dp))

            HomeButton(
                title = "Admin Dashboard",
                subtitle = "View system records",
                onClick = { navController.navigate("dashboard") }
            )
        }
    }
}

@Composable
fun HomeButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(92.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardDark.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Accent,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}