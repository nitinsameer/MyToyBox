package com.toybox.app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.toybox.app.SurpriseReward
import kotlin.math.sin
import kotlin.random.Random

// ── Confetti particles (created once, stable) ─────────────────────────────────

private data class Particle(
    val x: Float,      // initial x (0..1)
    val speed: Float,  // fall speed multiplier
    val drift: Float,  // horizontal sway amplitude
    val color: Color,
    val radius: Float,
    val phaseOffset: Float  // unique sway phase per particle
)

private val CONFETTI_COLORS = listOf(
    Color(0xFFFF6B6B), Color(0xFFFFE66D), Color(0xFF4ECDC4),
    Color(0xFF45B7D1), Color(0xFF96CEB4), Color(0xFFFF9FF3),
    Color(0xFFFC427B), Color(0xFFA29BFE), Color(0xFFFD79A8)
)

@Composable
private fun Confetti(modifier: Modifier = Modifier) {
    val particles = remember {
        List(45) {
            Particle(
                x           = Random.nextFloat(),
                speed       = 0.25f + Random.nextFloat() * 0.55f,
                drift       = (Random.nextFloat() - 0.5f) * 0.25f,
                color       = CONFETTI_COLORS[it % CONFETTI_COLORS.size],
                radius      = 4f + Random.nextFloat() * 7f,
                phaseOffset = Random.nextFloat() * 6.28f   // 0..2π
            )
        }
    }

    val inf = rememberInfiniteTransition(label = "confetti_time")
    val t by inf.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing)),
        label        = "t"
    )

    Canvas(modifier = modifier) {
        particles.forEach { p ->
            val yFrac = ((t * p.speed) % 1.1f)        // 0 → 1.1 loops
            val xFrac = p.x + p.drift * sin((t * 5f + p.phaseOffset).toDouble()).toFloat()
            drawCircle(
                color  = p.color,
                radius = p.radius,
                center = Offset(
                    x = xFrac.coerceIn(0f, 1f) * size.width,
                    y = yFrac * size.height
                )
            )
        }
    }
}

// ── Popup ─────────────────────────────────────────────────────────────────────

@Composable
fun SurprisePopup(reward: SurpriseReward, onDismiss: () -> Unit) {

    // Entrance bounce
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val cardScale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    // Emoji pulse
    val inf = rememberInfiniteTransition(label = "emoji_pulse")
    val emojiScale by inf.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.2f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            // Confetti behind the card
            Confetti(modifier = Modifier.fillMaxSize())

            // Card
            Card(
                modifier = Modifier
                    .width(290.dp)
                    .scale(cardScale),
                shape     = RoundedCornerShape(28.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text  = reward.emoji,
                        fontSize = 64.sp,
                        modifier = Modifier.scale(emojiScale)
                    )

                    Text(
                        text       = "Surprise! 🎊",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text       = reward.title,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Black,
                        color      = MaterialTheme.colorScheme.onSurface,
                        textAlign  = TextAlign.Center
                    )

                    Text(
                        text      = reward.message,
                        fontSize  = 13.sp,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    if (reward.bonusPoints > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text       = "+${reward.bonusPoints} bonus pts ⭐",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.primary,
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick   = onDismiss,
                        modifier  = Modifier.fillMaxWidth().height(50.dp),
                        shape     = RoundedCornerShape(16.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Woohoo! 🎉", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
