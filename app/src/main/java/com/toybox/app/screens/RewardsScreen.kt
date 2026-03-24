package com.toybox.app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.toybox.app.Badge
import com.toybox.app.GamificationViewModel
import com.toybox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    gamVm: GamificationViewModel,
    nav: NavController
) {
    val gamState  by gamVm.state.collectAsState()
    val level     = remember(gamState.points) { (gamState.points / 100) + 1 }
    val allBadges = remember { Badge.values().toList() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rewards ⭐", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── Points summary ─────────────────────────────────────────────────
            item(key = "points_card") {
                PointsSummaryCard(points = gamState.points, level = level)
            }

            // ── Badges header ──────────────────────────────────────────────────
            item(key = "badges_header") {
                Row(
                    Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🏅", fontSize = 16.sp)
                    Text(
                        "Your Badges",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Black,
                        color      = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // ── One card per badge (earned = colored, locked = dimmed) ─────────
            items(allBadges, key = { it.name }) { badge ->
                BadgeCard(badge = badge, earned = badge in gamState.earnedBadges)
                Spacer(Modifier.height(8.dp))
            }

            // ── Coming soon placeholder ────────────────────────────────────────
            item(key = "coming_soon") {
                Spacer(Modifier.height(8.dp))
                ComingSoonCard()
            }
        }
    }
}

// ── Points summary card ────────────────────────────────────────────────────────

@Composable
private fun PointsSummaryCard(points: Int, level: Int) {
    val animPoints by animateIntAsState(
        targetValue   = points,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label         = "points_count"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                Brush.verticalGradient(listOf(ElectricIndigo, Color(0xFF9590FF))),
                RoundedCornerShape(24.dp)
            )
            .padding(vertical = 28.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.fillMaxWidth()
        ) {
            Text("⭐", fontSize = 44.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text       = "$animPoints",
                fontSize   = 52.sp,
                fontWeight = FontWeight.Black,
                color      = Color.White,
                lineHeight = 56.sp
            )
            Text(
                text     = "Total Points",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.22f)
            ) {
                Text(
                    text       = "Level $level 🚀",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Black,
                    color      = Color.White,
                    modifier   = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ── Individual badge row ───────────────────────────────────────────────────────

@Composable
private fun BadgeCard(badge: Badge, earned: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .alpha(if (earned) 1f else 0.48f),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (earned)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (earned)
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        else null
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Emoji / lock circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (earned)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (earned) {
                    Text(badge.emoji, fontSize = 26.sp)
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(Modifier.weight(1f)) {
                Text(
                    text       = badge.title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (earned)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = badge.description,
                    fontSize   = 11.sp,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }

            if (earned) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Earned",
                    tint     = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ── Coming soon placeholder ────────────────────────────────────────────────────

@Composable
private fun ComingSoonCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineSoft)
    ) {
        Text(
            text       = "More rewards coming soon 🎉",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF5A4200),
            textAlign  = TextAlign.Center,
            modifier   = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 16.dp)
        )
    }
}
