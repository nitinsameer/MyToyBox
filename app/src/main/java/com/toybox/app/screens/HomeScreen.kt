package com.toybox.app.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.toybox.app.ChallengeProgress
import com.toybox.app.GamificationState
import com.toybox.app.InsightEngine
import com.toybox.app.ThemeViewModel
import com.toybox.app.Toy
import com.toybox.app.ui.theme.*
import java.io.File
import java.util.Calendar

// ── Smart greeting ────────────────────────────────────────────────────────────

fun getSmartGreeting(name: String): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val time = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }
    return if (name.isNotBlank()) "$time, $name! 👋" else "$time! 👋"
}

fun getInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].first().uppercaseChar().toString()
        else -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
    }
}

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    toys: List<Toy>,
    nav: NavController,
    themeVm: ThemeViewModel,
    isDark: Boolean,
    kidName: String,
    gamState: GamificationState,
    challengeProgresses: List<ChallengeProgress> = emptyList()
) {
    val catColors     = remember(isDark) { if (isDark) CATEGORY_COLORS_DARK else CATEGORY_COLORS_LIGHT }
    val catBgs        = remember(isDark) { if (isDark) CATEGORY_BG_DARK     else CATEGORY_BG_LIGHT }
    val totalSpent    = remember(toys) { toys.sumOf { it.price } }
    val onAddToy      = remember(nav) { { nav.navigate("addtoy") } }
    val onMyToys      = remember(nav) { { nav.navigate("mytoys") } }
    val onToggleTheme = remember(themeVm) { { themeVm.toggleTheme() } }
    val onSettings    = remember(nav) { { nav.navigate("settings") } }
    val onRewards     = remember(nav) { { nav.navigate("rewards") } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item(key = "header") {
            HomeHeader(
                kidName       = kidName,
                isDark        = isDark,
                gamState      = gamState,
                onToggleTheme = onToggleTheme,
                onSettings    = onSettings
            )
        }

        item(key = "quick_actions") {
            QuickActions(onAddToy = onAddToy, onMyToys = onMyToys, onRewards = onRewards)
        }

        if (challengeProgresses.isNotEmpty()) {
            item(key = "spacer_challenges") { Spacer(Modifier.height(4.dp)) }
            item(key = "challenges") { ChallengesSection(challenges = challengeProgresses) }
        }

        item(key = "dashboard") {
            FunDashboard(toys = toys, totalSpent = totalSpent, gamState = gamState)
        }

        item(key = "spacer_recent") { Spacer(Modifier.height(4.dp)) }

        item(key = "recent_header") {
            SectionHeader(title = "Recently Added", onSeeAll = onMyToys)
        }

        if (toys.isEmpty()) {
            item(key = "empty") { EmptyState(onAddToy = onAddToy) }
        } else {
            item(key = "recent_row") {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(toys.take(5), key = { it.id }) { toy ->
                        ToyChip(
                            toy      = toy,
                            isDark   = isDark,
                            catColor = catColors[toy.category] ?: MaterialTheme.colorScheme.primary,
                            catBg    = catBgs[toy.category]    ?: MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }
            }

            item(key = "spacer_all") { Spacer(Modifier.height(12.dp)) }
            item(key = "all_header") { SectionHeader(title = "All Toys", onSeeAll = null) }

            items(toys, key = { it.id }) { toy ->
                ToyListRow(
                    toy      = toy,
                    catColor = catColors[toy.category] ?: MaterialTheme.colorScheme.primary,
                    catBg    = catBgs[toy.category]    ?: MaterialTheme.colorScheme.primaryContainer
                )
            }
        }

        item(key = "bottom_spacer") { Spacer(Modifier.height(24.dp)) }
    }
}

// ── Gradient header with XP bar ───────────────────────────────────────────────

@Composable
private fun HomeHeader(
    kidName: String,
    isDark: Boolean,
    gamState: GamificationState,
    onToggleTheme: () -> Unit,
    onSettings: () -> Unit
) {
    val greeting   = remember(kidName) { getSmartGreeting(kidName) }
    val initials   = remember(kidName) { getInitials(kidName) }
    val level      = remember(gamState.points) { (gamState.points / 100) + 1 }
    val xpFraction = remember(gamState.points) { (gamState.points % 100) / 100f }
    val animXp by animateFloatAsState(
        targetValue   = xpFraction,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label         = "xp_bar"
    )
    val gradientBrush = remember(isDark) {
        if (isDark)
            Brush.verticalGradient(listOf(Color(0xFF1C1340), Color(0xFF2D2070)))
        else
            Brush.verticalGradient(listOf(ElectricIndigo, Color(0xFF9590FF)))
    }

    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 20.dp)
        ) {
            Column {
                // ── Top row: app tag + controls ───────────────────────────────
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            "🧸 ToyJoy Box",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Black,
                            color      = Color.White.copy(alpha = 0.92f),
                            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Settings
                        IconButton(
                            onClick  = onSettings,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint               = Color.White,
                                modifier           = Modifier.size(17.dp)
                            )
                        }
                        // Theme toggle
                        IconButton(
                            onClick  = onToggleTheme,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector        = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle theme",
                                tint               = Color.White,
                                modifier           = Modifier.size(17.dp)
                            )
                        }
                        // Avatar
                        Box(
                            Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                initials,
                                color      = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize   = 14.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Greeting ──────────────────────────────────────────────────
                Text(
                    text       = greeting,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Black,
                    color      = Color.White,
                    lineHeight = 28.sp
                )
                Text(
                    text    = "Let's play with your toys! 🎮",
                    fontSize = 12.sp,
                    color   = Color.White.copy(alpha = 0.75f)
                )

                Spacer(Modifier.height(16.dp))

                // ── XP / Level bar ────────────────────────────────────────────
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Level badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Sunshine.copy(alpha = 0.92f)
                    ) {
                        Text(
                            "Lv. $level",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Black,
                            color      = Color(0xFF1A1040),
                            modifier   = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                    // Progress bar
                    Box(
                        Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(animXp)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(listOf(Sunshine, Mint)),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    // Points label
                    Text(
                        "⭐ ${gamState.points % 100}/100",
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // ── Badges strip (below gradient, on surface) ─────────────────────────
        if (gamState.earnedBadges.isNotEmpty()) {
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(gamState.earnedBadges.toList()) { badge ->
                    Surface(
                        shape  = RoundedCornerShape(20.dp),
                        color  = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                    ) {
                        Text(
                            "${badge.emoji} ${badge.title}",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── 3D "Duolingo-style" action button ────────────────────────────────────────

@Composable
fun ToyActionButton(
    label: String,
    faceColor: Color,
    shadowColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    shadowDepth: Dp = 5.dp
) {
    var pressed by remember { mutableStateOf(false) }
    val offsetY by animateDpAsState(
        targetValue   = if (pressed) shadowDepth else 0.dp,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label         = "3d_btn_offset"
    )

    Box(modifier = modifier.height(height + shadowDepth)) {
        // Shadow layer — always sits at the bottom
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(22.dp))
                .background(shadowColor)
        )
        // Face layer — slides down on press to reveal no shadow (presses "in")
        Button(
            onClick = {
                pressed = true
                onClick()
            },
            modifier  = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.TopCenter)
                .offset(y = offsetY),
            shape     = RoundedCornerShape(22.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = faceColor),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp)
        ) {
            Text(
                text       = label,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Black,
                color      = textColor
            )
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(120)
            pressed = false
        }
    }
}

// ── Quick action buttons ──────────────────────────────────────────────────────

@Composable
private fun QuickActions(onAddToy: () -> Unit, onMyToys: () -> Unit, onRewards: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Primary CTA: green (Duolingo-style)
        ToyActionButton(
            label       = "Add Toy 🎉",
            faceColor   = Mint,
            shadowColor = MintDark,
            textColor   = Color.White,
            onClick     = onAddToy
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ToyActionButton(
                label       = "My Collection 🧸",
                faceColor   = ElectricIndigo,
                shadowColor = IndigoDark,
                textColor   = Color.White,
                onClick     = onMyToys,
                modifier    = Modifier.weight(1f),
                height      = 54.dp
            )
            ToyActionButton(
                label       = "Rewards ⭐",
                faceColor   = Sunshine,
                shadowColor = SunshineDark,
                textColor   = Color(0xFF1A1040),
                onClick     = onRewards,
                modifier    = Modifier.weight(1f),
                height      = 54.dp
            )
        }
    }
}

// ── Fun Dashboard ─────────────────────────────────────────────────────────────

@Composable
private fun FunDashboard(toys: List<Toy>, totalSpent: Double, gamState: GamificationState) {
    val headline   = remember(toys.size)       { InsightEngine.collectionHeadline(toys.size) }
    val spentLabel = remember(totalSpent)       { InsightEngine.spentInsight(totalSpent) }
    val favCat     = remember(toys)             { InsightEngine.favoriteCategory(toys) }
    val monthLabel = remember(toys)             { InsightEngine.monthlyInsight(toys) }
    val level      = remember(gamState.points)  { (gamState.points / 100) + 1 }

    Column(Modifier.padding(vertical = 4.dp)) {

        // Section label
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("🌍", fontSize = 16.sp)
            Text(
                "Your Toy World",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Black,
                color      = MaterialTheme.colorScheme.onBackground
            )
        }

        // Horizontal scrolling insight cards
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "toys_card") {
                DashboardCard(
                    emoji = "🧸",
                    value = "${toys.size}",
                    label = headline,
                    tint  = MaterialTheme.colorScheme.primary
                )
            }
            item(key = "spent_card") {
                DashboardCard(
                    emoji = "💰",
                    value = "₹${totalSpent.toInt()}",
                    label = spentLabel,
                    tint  = Coral
                )
            }
            item(key = "fav_card") {
                DashboardCard(
                    emoji = "❤️",
                    value = if (toys.isEmpty()) "—" else (EMOJIS.entries.firstOrNull { toys.any { t -> t.category == it.key } }?.value ?: "🎁"),
                    label = favCat,
                    tint  = Mint
                )
            }
            item(key = "level_card") {
                DashboardCard(
                    emoji = "🏆",
                    value = "Lv.$level",
                    label = "${gamState.points} pts total",
                    tint  = Sunshine
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Monthly progress (full-width)
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape     = RoundedCornerShape(18.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border    = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text("🗓️", fontSize = 22.sp, modifier = Modifier.padding(10.dp))
                }
                Column {
                    Text(
                        "This Month",
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = monthLabel,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Black,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ── Dashboard insight card ────────────────────────────────────────────────────

@Composable
fun DashboardCard(
    emoji: String,
    value: String,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.width(140.dp).height(120.dp),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border    = BorderStroke(1.5.dp, tint.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(14.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji, fontSize = 24.sp)
            Text(
                value,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Black,
                color      = tint,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                label,
                fontSize   = 9.sp,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, onSeeAll: (() -> Unit)?) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Black,
            color      = MaterialTheme.colorScheme.onBackground
        )
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) {
                Text("See all →", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(onAddToy: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape     = RoundedCornerShape(24.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border    = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("📦", fontSize = 64.sp)
                Text(
                    "Your toy box is empty 😢",
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Black,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Let's add your first toy today!",
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                ToyActionButton(
                    label       = "Add your first toy 🎉",
                    faceColor   = Mint,
                    shadowColor = MintDark,
                    textColor   = Color.White,
                    onClick     = onAddToy,
                    modifier    = Modifier.fillMaxWidth(),
                    height      = 56.dp,
                    shadowDepth = 4.dp
                )
            }
        }
    }
}

// ── ToyChip (recently added horizontal row) ───────────────────────────────────

@Composable
fun ToyChip(toy: Toy, isDark: Boolean, catColor: Color, catBg: Color) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(toy.id) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.85f)
    ) {
        Card(
            shape     = RoundedCornerShape(18.dp),
            colors    = CardDefaults.cardColors(containerColor = catBg),
            border    = BorderStroke(1.5.dp, catColor.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier  = Modifier.width(96.dp)
        ) {
            Column {
                Box(
                    Modifier.fillMaxWidth().height(74.dp).background(catBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (toy.photoUri.isNotEmpty()) {
                        AsyncImage(
                            model              = buildImageRequest(context, toy.photoUri),
                            contentDescription = toy.name,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        )
                    } else {
                        Text(EMOJIS[toy.category] ?: "🎁", fontSize = 30.sp)
                    }
                }
                // Thin category-color accent bar
                Box(Modifier.fillMaxWidth().height(2.dp).background(catColor))
                Column(Modifier.padding(8.dp)) {
                    Text(toy.name, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("₹${toy.price.toInt()}", fontSize = 10.sp,
                        fontWeight = FontWeight.Black, color = catColor)
                }
            }
        }
    }
}

// ── ToyListRow — Card with colored left accent bar ────────────────────────────

@Composable
fun ToyListRow(toy: Toy, catColor: Color, catBg: Color) {
    val context = LocalContext.current
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border    = BorderStroke(1.dp, catColor.copy(alpha = 0.18f)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left accent bar
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(catColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            Row(
                Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Toy image / emoji
                Box(
                    Modifier
                        .size(52.dp)
                        .background(catBg, RoundedCornerShape(12.dp))
                        .border(1.dp, catColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (toy.photoUri.isNotEmpty()) {
                        AsyncImage(
                            model              = buildImageRequest(context, toy.photoUri),
                            contentDescription = toy.name,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Text(EMOJIS[toy.category] ?: "🎁", fontSize = 22.sp)
                    }
                }

                Column(Modifier.weight(1f)) {
                    Text(
                        toy.name,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        if (toy.purchaseDate.isEmpty()) "No date" else toy.purchaseDate,
                        fontSize = 10.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(3.dp))
                    Surface(
                        shape  = RoundedCornerShape(20.dp),
                        color  = catBg,
                        border = BorderStroke(1.dp, catColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            "${EMOJIS[toy.category] ?: "🎁"} ${toy.category}",
                            fontSize   = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color      = catColor,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    "₹${toy.price.toInt()}",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Black,
                    color      = catColor
                )
            }
        }
    }
}

// ── Image request helper ──────────────────────────────────────────────────────

fun buildImageRequest(context: android.content.Context, uriString: String): Any = try {
    if (uriString.startsWith("content://")) android.net.Uri.parse(uriString)
    else File(uriString.removePrefix("file://"))
} catch (_: Exception) { uriString }
