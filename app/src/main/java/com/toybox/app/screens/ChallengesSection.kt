package com.toybox.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toybox.app.ChallengeProgress
import com.toybox.app.ChallengeType
import kotlinx.coroutines.delay

// ── Section composable (drop into HomeScreen) ─────────────────────────────────

@Composable
fun ChallengesSection(challenges: List<ChallengeProgress>) {
    if (challenges.isEmpty()) return

    Column(Modifier.fillMaxWidth()) {
        // Header
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("🎯", fontSize = 16.sp)
            Text(
                "Today's Challenges",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Black,
                color      = MaterialTheme.colorScheme.onBackground
            )
        }

        challenges.forEach { progress ->
            ChallengeCard(progress = progress)
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Individual challenge card ─────────────────────────────────────────────────

@Composable
private fun ChallengeCard(progress: ChallengeProgress) {
    val ch = progress.challenge

    // Animated progress fraction
    val animFraction by animateFloatAsState(
        targetValue   = progress.progressFraction,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label         = "ch_progress_${ch.id}"
    )

    val completedColor = MaterialTheme.colorScheme.primary
    val cardBg = if (progress.isCompleted)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg),
        border    = BorderStroke(
            1.dp,
            if (progress.isCompleted)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Emoji badge
            Box(
                Modifier
                    .size(44.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(ch.emoji, fontSize = 22.sp)
            }

            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text       = ch.title,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    if (ch.type == ChallengeType.DAILY) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "DAILY",
                                fontSize   = 8.sp,
                                fontWeight = FontWeight.Black,
                                color      = MaterialTheme.colorScheme.secondary,
                                modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text  = ch.description,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(6.dp))

                // Progress bar
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(animFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (progress.isCompleted) completedColor
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                    )
                }

                Spacer(Modifier.height(3.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text  = "${progress.currentCount}/${ch.targetCount}",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text  = "+${ch.rewardPoints} pts",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Completion tick (animated in)
            AnimatedVisibility(
                visible = progress.isCompleted,
                enter   = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint     = completedColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ── Challenge completed banner (shown as overlay from MainActivity) ───────────

@Composable
fun ChallengeCompletedBanner(progress: ChallengeProgress, onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(3000)       // auto-dismiss after 3 s
        visible = false
        delay(400)
        onDismiss()
    }

    val scale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0.8f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "banner_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter   = slideInVertically { it } + fadeIn(),
            exit    = slideOutVertically { it } + fadeOut()
        ) {
            Card(
                modifier  = Modifier.fillMaxWidth().scale(scale),
                shape     = RoundedCornerShape(18.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(progress.challenge.emoji, fontSize = 28.sp)
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Challenge Complete! 🎉",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            progress.challenge.title,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Black,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            "+${progress.challenge.rewardPoints} pts",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Black,
                            color      = MaterialTheme.colorScheme.onPrimary,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
