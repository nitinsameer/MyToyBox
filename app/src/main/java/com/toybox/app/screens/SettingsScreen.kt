package com.toybox.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.toybox.app.CrashReporter
import com.toybox.app.GamificationViewModel
import com.toybox.app.ToyViewModel
import com.toybox.app.UserViewModel

// ── Contact / link constants (update before Play Store submission) ─────────────
private const val SUPPORT_EMAIL  = "nitinsameer@gmail.com"
private const val PRIVACY_URL    = "https://nitinsameer.github.io/my-toy-box-privacy/"
private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.toybox.app"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userVm: UserViewModel,
    toyVm: ToyViewModel,
    gamVm: GamificationViewModel,
    nav: NavController
) {
    val context          = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Read version name at composition time — crash-safe
    val versionName = remember {
        runCatching {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "1.0"
        }.getOrDefault("1.0")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // ── About ──────────────────────────────────────────────────────────
            SectionLabel("About")
            Spacer(Modifier.height(8.dp))
            Card(
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    InfoRow(label = "App", value = "ToyJoy Box 🧸")
                    RowDivider()
                    InfoRow(label = "Version", value = "v$versionName")
                    RowDivider()
                    InfoRow(label = "Developer", value = "Nishaan Tech")
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Support ───────────────────────────────────────────────────────
            SectionLabel("Support")
            Spacer(Modifier.height(8.dp))
            Card(
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    LinkRow(
                        icon     = Icons.Default.Email,
                        title    = "Contact Us",
                        subtitle = SUPPORT_EMAIL
                    ) {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:$SUPPORT_EMAIL")
                                    putExtra(Intent.EXTRA_SUBJECT, "ToyJoy Box Support")
                                }
                            )
                        }
                    }
                    RowDivider()
                    LinkRow(
                        icon     = Icons.Default.Language,
                        title    = "Privacy Policy",
                        subtitle = "View in browser"
                    ) {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL))
                            )
                        }
                    }
                    RowDivider()
                    LinkRow(
                        icon     = Icons.Default.Star,
                        title    = "Rate the App ⭐",
                        subtitle = "Enjoying ToyJoy Box? Let us know!"
                    ) {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL))
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Privacy & Data ────────────────────────────────────────────────
            SectionLabel("Privacy & Data")
            Spacer(Modifier.height(8.dp))
            Card(
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Data stored on-device only",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "No ads, no tracking, no cloud sync.",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "🔒",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Danger Zone ───────────────────────────────────────────────────
            SectionLabel(label = "Danger Zone", isError = true)
            Spacer(Modifier.height(8.dp))
            Card(
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Delete All My Data",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Permanently removes your child's name, all toys, " +
                        "badges, points, and challenge progress from this device. " +
                        "This cannot be undone.",
                        fontSize   = 12.sp,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        colors  = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border  = androidx.compose.foundation.BorderStroke(
                            1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                        ),
                        shape   = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Delete Everything", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Legal footer ──────────────────────────────────────────────────
            Text(
                text       = "GDPR Art. 17 · COPPA compliant\nAll data stays locally on this device.",
                fontSize   = 10.sp,
                color      = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                textAlign  = TextAlign.Center,
                lineHeight = 14.sp,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }

    // ── Confirm Delete dialog ─────────────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Delete all data? 🗑️",
                    fontWeight = FontWeight.Black,
                    textAlign  = TextAlign.Center
                )
            },
            text = {
                Text(
                    "This will permanently erase your child's name, every toy, " +
                    "all points, badges, and challenges.\n\nThere is no way to undo this.",
                    textAlign  = TextAlign.Center,
                    fontSize   = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        // GDPR Art. 17 — Right to Erasure
                        toyVm.deleteAllToys()
                        userVm.clearUserData()
                        gamVm.clearAllData()
                        CrashReporter.disableCollection()
                        nav.navigate("setup") { popUpTo(0) { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete Everything", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Keep my data 😊")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ── Reusable helpers ───────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(label: String, isError: Boolean = false) {
    Text(
        text          = label.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color         = if (isError) MaterialTheme.colorScheme.error
                        else         MaterialTheme.colorScheme.primary
    )
}

/** Non-clickable info row (label on left, value on right). */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Clickable row that opens an external link / intent. */
@Composable
private fun LinkRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(
                title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
        modifier  = Modifier.padding(horizontal = 16.dp)
    )
}
