package com.toybox.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Brand palette ─────────────────────────────────────────────────────────────

// Primary: electric indigo (vivid, kid-friendly)
val ElectricIndigo  = Color(0xFF6C63FF)
val IndigoDark      = Color(0xFF3D35B5)
val IndigoDeep      = Color(0xFF26205C)
val IndigoSoft      = Color(0xFFEEECFF)

// Secondary: warm coral-orange (replaces pink — more playful)
val Coral           = Color(0xFFFF6B6B)
val CoralDark       = Color(0xFFD94F4F)
val CoralSoft       = Color(0xFFFFECEC)

// Tertiary: fresh mint-green (Duolingo-style CTA / success)
val Mint            = Color(0xFF4CAF6F)
val MintDark        = Color(0xFF2E7D50)
val MintSoft        = Color(0xFFE8F5E9)

// Accent: sunshine yellow (level badges, rewards)
val Sunshine        = Color(0xFFFFD93D)
val SunshineDark    = Color(0xFFE6B800)
val SunshineSoft    = Color(0xFFFFF8D6)

// Neutral light — warm cream, not cold purple
val CreamBg         = Color(0xFFFFFBF5)
val WarmWhite       = Color(0xFFFFFFFF)
val SoftLavender    = Color(0xFFF5F3FF)

// Neutral dark — rich deep space
val NightBg         = Color(0xFF0D0D1A)
val NightSurface    = Color(0xFF130E2B)
val NightCard       = Color(0xFF1C1340)
val NightElevated   = Color(0xFF231960)

// ── Light color scheme ────────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary              = ElectricIndigo,
    onPrimary            = Color(0xFFFFFFFF),
    primaryContainer     = IndigoSoft,
    onPrimaryContainer   = IndigoDeep,
    secondary            = Coral,
    onSecondary          = Color(0xFFFFFFFF),
    secondaryContainer   = CoralSoft,
    onSecondaryContainer = CoralDark,
    tertiary             = Mint,
    onTertiary           = Color(0xFFFFFFFF),
    tertiaryContainer    = MintSoft,
    onTertiaryContainer  = MintDark,
    background           = CreamBg,
    onBackground         = Color(0xFF1A1040),
    surface              = WarmWhite,
    onSurface            = Color(0xFF1A1040),
    surfaceVariant       = SoftLavender,
    onSurfaceVariant     = Color(0xFF666580),
    outline              = Color(0xFFCBC8E8),
    outlineVariant       = Color(0xFFE8E6FF),
    error                = Color(0xFFE53E3E),
    onError              = Color(0xFFFFFFFF),
    errorContainer       = CoralSoft,
    onErrorContainer     = CoralDark,
)

// ── Dark color scheme ─────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary              = Color(0xFFA89FF8),
    onPrimary            = Color(0xFF0D0D1A),
    primaryContainer     = NightCard,
    onPrimaryContainer   = Color(0xFFA89FF8),
    secondary            = Color(0xFFFF8A80),          // lighter coral for dark
    onSecondary          = Color(0xFF0D0D1A),
    secondaryContainer   = Color(0xFF2A1212),
    onSecondaryContainer = Color(0xFFFF8A80),
    tertiary             = Color(0xFF6EE794),           // bright mint for dark
    onTertiary           = Color(0xFF0D0D1A),
    tertiaryContainer    = Color(0xFF0A2015),
    onTertiaryContainer  = Color(0xFF6EE794),
    background           = NightBg,
    onBackground         = Color(0xFFFFFFFF),
    surface              = NightSurface,
    onSurface            = Color(0xFFFFFFFF),
    surfaceVariant       = NightCard,
    onSurfaceVariant     = Color(0xFF8A8AAA),
    outline              = Color(0xFF2D1F6E),
    outlineVariant       = Color(0xFF1A1040),
    error                = Color(0xFFFF5252),
    onError              = Color(0xFF0D0D1A),
    errorContainer       = Color(0xFF2A1012),
    onErrorContainer     = Color(0xFFFF8A80),
)

// ── Category accent colors ────────────────────────────────────────────────────

val CATEGORY_COLORS_DARK = mapOf(
    "LEGO"            to Color(0xFFA89FF8),
    "Action figure"   to Color(0xFFFF8A80),
    "Stuffed animal"  to Color(0xFFFF9EAD),
    "Vehicle"         to Color(0xFF6EE794),
    "Board game"      to Color(0xFFA89FF8),
    "Puzzle"          to Color(0xFFFFD54F),
    "Remote control"  to Color(0xFF40E0D0),
    "Outdoor toy"     to Color(0xFF6EE794),
    "Arts & crafts"   to Color(0xFFFF7043),
    "Building blocks" to Color(0xFF82AAFF),
    "Doll"            to Color(0xFFFF9EAD),
    "Musical toy"     to Color(0xFF40E0D0),
    "Educational"     to Color(0xFF64B5F6),
    "Sports"          to Color(0xFF6EE794),
    "Card game"       to Color(0xFFFFD54F),
    "Science kit"     to Color(0xFF40E0D0),
    "Other"           to Color(0xFF8A8AAA)
)

val CATEGORY_COLORS_LIGHT = mapOf(
    "LEGO"            to ElectricIndigo,
    "Action figure"   to Color(0xFFD32F2F),
    "Stuffed animal"  to Color(0xFFC2185B),
    "Vehicle"         to Color(0xFF2E7D32),
    "Board game"      to Color(0xFF512DA8),
    "Puzzle"          to Color(0xFFE65100),
    "Remote control"  to Color(0xFF00838F),
    "Outdoor toy"     to Color(0xFF33691E),
    "Arts & crafts"   to Color(0xFFBF360C),
    "Building blocks" to Color(0xFF283593),
    "Doll"            to Color(0xFFAD1457),
    "Musical toy"     to Color(0xFF00695C),
    "Educational"     to Color(0xFF1565C0),
    "Sports"          to Color(0xFF2E7D32),
    "Card game"       to Color(0xFF6D4C41),
    "Science kit"     to Color(0xFF00838F),
    "Other"           to Color(0xFF546E7A)
)

val CATEGORY_BG_DARK = mapOf(
    "LEGO"            to Color(0xFF1C1340),
    "Action figure"   to Color(0xFF2A1012),
    "Stuffed animal"  to Color(0xFF2A1020),
    "Vehicle"         to Color(0xFF0A1A0D),
    "Board game"      to Color(0xFF1C1340),
    "Puzzle"          to Color(0xFF1A1408),
    "Remote control"  to Color(0xFF081A18),
    "Outdoor toy"     to Color(0xFF0A1A0D),
    "Arts & crafts"   to Color(0xFF1A0F08),
    "Building blocks" to Color(0xFF0D1440),
    "Doll"            to Color(0xFF2A1020),
    "Musical toy"     to Color(0xFF081A18),
    "Educational"     to Color(0xFF08121A),
    "Sports"          to Color(0xFF0A1A0D),
    "Card game"       to Color(0xFF1A1408),
    "Science kit"     to Color(0xFF081A18),
    "Other"           to Color(0xFF1A1A2E)
)

val CATEGORY_BG_LIGHT = mapOf(
    "LEGO"            to Color(0xFFEEECFF),
    "Action figure"   to Color(0xFFFFEBEB),
    "Stuffed animal"  to Color(0xFFFCE4EC),
    "Vehicle"         to Color(0xFFE8F5E9),
    "Board game"      to Color(0xFFEDE7F6),
    "Puzzle"          to Color(0xFFFFF3E0),
    "Remote control"  to Color(0xFFE0F7FA),
    "Outdoor toy"     to Color(0xFFF1F8E9),
    "Arts & crafts"   to Color(0xFFFBE9E7),
    "Building blocks" to Color(0xFFE8EAF6),
    "Doll"            to Color(0xFFFCE4EC),
    "Musical toy"     to Color(0xFFE0F2F1),
    "Educational"     to Color(0xFFE3F2FD),
    "Sports"          to Color(0xFFE8F5E9),
    "Card game"       to Color(0xFFEFEBE9),
    "Science kit"     to Color(0xFFE0F7FA),
    "Other"           to Color(0xFFF5F5F5)
)

// ── Category emojis ───────────────────────────────────────────────────────────

val EMOJIS = mapOf(
    "LEGO"            to "🧱",
    "Action figure"   to "🦸",
    "Stuffed animal"  to "🧸",
    "Vehicle"         to "🚗",
    "Board game"      to "🎲",
    "Puzzle"          to "🧩",
    "Remote control"  to "🎮",
    "Outdoor toy"     to "⛺",
    "Arts & crafts"   to "🎨",
    "Building blocks" to "🏗",
    "Doll"            to "🪆",
    "Musical toy"     to "🎵",
    "Educational"     to "📚",
    "Sports"          to "⚽",
    "Card game"       to "🃏",
    "Science kit"     to "🔬",
    "Other"           to "🎁"
)

// ── Theme entry point ─────────────────────────────────────────────────────────

@Composable
fun ToyBoxTheme(isDark: Boolean = true, content: @Composable () -> Unit) {
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ToyTypography,
        content     = content
    )
}
