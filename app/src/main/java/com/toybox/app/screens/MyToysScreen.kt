package com.toybox.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.toybox.app.Toy
import com.toybox.app.ToyBoxApplication
import com.toybox.app.ToyViewModel
import com.toybox.app.ui.theme.*

private enum class ViewMode { GRID, SHELF }

@Composable
fun MyToysScreen(
    toys: List<Toy>,
    vm: ToyViewModel,
    nav: NavController,
    isDark: Boolean
) {
    var toyToDelete by remember { mutableStateOf<Toy?>(null) }
    var viewMode    by remember { mutableStateOf(ViewMode.GRID) }
    val totalValue  = remember(toys) { toys.sumOf { it.price } }

    val catColors    = remember(isDark) { if (isDark) CATEGORY_COLORS_DARK else CATEGORY_COLORS_LIGHT }
    val catBgs       = remember(isDark) { if (isDark) CATEGORY_BG_DARK     else CATEGORY_BG_LIGHT }
    val context      = LocalContext.current
    val soundManager = (context.applicationContext as ToyBoxApplication).soundManager

    // ── Delete confirmation dialog ────────────────────────────────────────────
    toyToDelete?.let { toy ->
        AlertDialog(
            onDismissRequest = { toyToDelete = null },
            containerColor   = MaterialTheme.colorScheme.surface,
            title = {
                Text("Remove toy? 🧸", fontWeight = FontWeight.Black)
            },
            text = {
                Text("Remove \"${toy.name}\" from your box? This can't be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteToy(toy)
                    soundManager.playDeleteSound()
                    toyToDelete = null
                }) {
                    Text("Remove 🗑️", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { toyToDelete = null }) {
                    Text("Keep it! 😊", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        if (isDark)
                            listOf(Color(0xFF1C1340), Color(0xFF13102A))
                        else
                            listOf(ElectricIndigo.copy(alpha = 0.92f), ElectricIndigo.copy(alpha = 0.75f))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "My Collection 🧸",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Black,
                        color      = Color.White
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.18f)
                        ) {
                            Text(
                                "${toys.size} toys",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White.copy(alpha = 0.9f),
                                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Sunshine.copy(alpha = 0.85f)
                        ) {
                            Text(
                                "₹${totalValue.toInt()} total",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Black,
                                color      = Color(0xFF1A1040),
                                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // View-mode toggle
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    ViewToggleBtn(
                        icon     = Icons.Default.GridView,
                        selected = viewMode == ViewMode.GRID,
                        onClick  = { viewMode = ViewMode.GRID }
                    )
                    ViewToggleBtn(
                        icon     = Icons.Default.ViewList,
                        selected = viewMode == ViewMode.SHELF,
                        onClick  = { viewMode = ViewMode.SHELF }
                    )
                }
            }
        }

        if (toys.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📦", fontSize = 64.sp)
                    Text(
                        "Your toy box is empty 😢",
                        color      = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black,
                        fontSize   = 16.sp
                    )
                    Text(
                        "Add your first toy to get started!",
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
                label = "view_mode"
            ) { mode ->
                when (mode) {
                    ViewMode.GRID  -> GridView(toys, catColors, catBgs, nav) { toyToDelete = it }
                    ViewMode.SHELF -> ShelfView(toys, catColors, catBgs, isDark, nav) { toyToDelete = it }
                }
            }
        }
    }
}

// ── View toggle button ────────────────────────────────────────────────────────

@Composable
private fun ViewToggleBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f)
    IconButton(
        onClick  = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(bg, RoundedCornerShape(10.dp))
    ) {
        Icon(
            icon, null,
            tint     = if (selected) Color.White else Color.White.copy(alpha = 0.55f),
            modifier = Modifier.size(17.dp)
        )
    }
}

// ── Grid view (2-col) ─────────────────────────────────────────────────────────

@Composable
private fun GridView(
    toys: List<Toy>,
    catColors: Map<String, Color>,
    catBgs: Map<String, Color>,
    nav: NavController,
    onDelete: (Toy) -> Unit
) {
    LazyVerticalGrid(
        columns               = GridCells.Fixed(2),
        contentPadding        = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement   = Arrangement.spacedBy(10.dp),
        modifier              = Modifier.fillMaxSize()
    ) {
        items(toys, key = { it.id }) { toy ->
            ToyGridCard(
                toy      = toy,
                catColor = catColors[toy.category] ?: MaterialTheme.colorScheme.primary,
                catBg    = catBgs[toy.category]    ?: MaterialTheme.colorScheme.primaryContainer,
                onEdit   = { nav.navigate("edittoy/${toy.id}") },
                onDelete = { onDelete(toy) }
            )
        }
    }
}

// ── Shelf view (category rows) ────────────────────────────────────────────────

@Composable
private fun ShelfView(
    toys: List<Toy>,
    catColors: Map<String, Color>,
    catBgs: Map<String, Color>,
    isDark: Boolean,
    nav: NavController,
    onDelete: (Toy) -> Unit
) {
    val grouped = remember(toys) {
        toys.groupBy { it.category }.entries.sortedByDescending { it.value.size }
    }

    LazyColumn(
        contentPadding      = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier            = Modifier.fillMaxSize()
    ) {
        grouped.forEach { (category, categoryToys) ->
            item(key = "header_$category") {
                ShelfRowHeader(
                    category = category,
                    count    = categoryToys.size,
                    catColor = catColors[category] ?: MaterialTheme.colorScheme.primary
                )
            }
            item(key = "row_$category") {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    items(categoryToys, key = { it.id }) { toy ->
                        ShelfToyCard(
                            toy      = toy,
                            catColor = catColors[toy.category] ?: MaterialTheme.colorScheme.primary,
                            catBg    = catBgs[toy.category]    ?: MaterialTheme.colorScheme.primaryContainer,
                            onEdit   = { nav.navigate("edittoy/${toy.id}") },
                            onDelete = { onDelete(toy) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ShelfRowHeader(category: String, count: Int, catColor: Color) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = catColor.copy(alpha = 0.15f)
        ) {
            Text(
                EMOJIS[category] ?: "🎁",
                fontSize = 16.sp,
                modifier = Modifier.padding(7.dp)
            )
        }
        Text(
            category,
            fontSize   = 15.sp,
            fontWeight = FontWeight.Black,
            color      = catColor
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = catColor.copy(alpha = 0.12f)
        ) {
            Text(
                "$count",
                fontSize   = 10.sp,
                fontWeight = FontWeight.Bold,
                color      = catColor,
                modifier   = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
            )
        }
    }
}

// ── Shelf toy card ────────────────────────────────────────────────────────────

@Composable
private fun ShelfToyCard(
    toy: Toy,
    catColor: Color,
    catBg: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "shelf_scale"
    )

    Card(
        modifier  = Modifier.width(120.dp).scale(scale),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border    = BorderStroke(1.5.dp, catColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(catBg),
                contentAlignment = Alignment.Center
            ) {
                if (toy.photoUri.isNotEmpty()) {
                    AsyncImage(
                        model              = buildImageRequest(context, toy.photoUri),
                        contentDescription = toy.name,
                        contentScale       = ContentScale.Fit,
                        modifier           = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    )
                } else {
                    Text(EMOJIS[toy.category] ?: "🎁", fontSize = 36.sp)
                }
                // Category emoji badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = catColor.copy(alpha = 0.88f)
                ) {
                    Text(
                        EMOJIS[toy.category] ?: "🎁",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            // Accent bar
            Box(Modifier.fillMaxWidth().height(2.5.dp).background(catColor))

            Column(Modifier.padding(8.dp)) {
                Text(toy.name, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("₹${toy.price.toInt()}", fontSize = 13.sp,
                    fontWeight = FontWeight.Black, color = catColor)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick  = onEdit,
                        modifier = Modifier
                            .size(26.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(7.dp))
                    ) {
                        Icon(Icons.Default.Edit, null,
                            tint     = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick  = onDelete,
                        modifier = Modifier
                            .size(26.dp)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(7.dp))
                    ) {
                        Icon(Icons.Default.Delete, null,
                            tint     = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

// ── Grid card (2-col) ─────────────────────────────────────────────────────────

@Composable
fun ToyGridCard(
    toy: Toy,
    catColor: Color,
    catBg: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pressed by remember { mutableStateOf(false) }
    var showFullScreen by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "grid_scale"
    )

    if (showFullScreen && toy.photoUri.isNotEmpty()) {
        Dialog(
            onDismissRequest = { showFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showFullScreen = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model              = buildImageRequest(context, toy.photoUri),
                    contentDescription = toy.name,
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Card(
        modifier  = modifier.fillMaxWidth().scale(scale),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border    = BorderStroke(1.5.dp, catColor.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Image area
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(catBg)
                    .then(if (toy.photoUri.isNotEmpty()) Modifier.clickable { showFullScreen = true } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (toy.photoUri.isNotEmpty()) {
                    AsyncImage(
                        model              = buildImageRequest(context, toy.photoUri),
                        contentDescription = toy.name,
                        contentScale       = ContentScale.Fit,
                        modifier           = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    )
                } else {
                    Text(EMOJIS[toy.category] ?: "🎁", fontSize = 46.sp)
                }

                // Category emoji badge (top-left overlay)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = catColor.copy(alpha = 0.88f)
                ) {
                    Text(
                        EMOJIS[toy.category] ?: "🎁",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            // Full-width colored accent bar (the "shelf edge")
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(catColor, catColor.copy(alpha = 0.4f))
                        )
                    )
            )

            // Info
            Column(Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 4.dp)) {
                Text(
                    toy.name,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "₹${toy.price.toInt()}",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Black,
                    color      = catColor
                )
            }

            // Action buttons
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick  = onEdit,
                    modifier = Modifier
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Edit, null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick  = onDelete,
                    modifier = Modifier
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Delete, null,
                        tint     = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
