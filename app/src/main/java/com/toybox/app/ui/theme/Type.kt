package com.toybox.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val ToyTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Black,     fontSize = 40.sp, letterSpacing = (-1.5).sp, lineHeight = 46.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Black,     fontSize = 32.sp, letterSpacing = (-1).sp,   lineHeight = 38.sp),
    displaySmall  = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, letterSpacing = (-0.5).sp, lineHeight = 32.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Black,     fontSize = 28.sp, letterSpacing = (-0.5).sp, lineHeight = 34.sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.Black,     fontSize = 22.sp, letterSpacing = 0.sp,      lineHeight = 28.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, letterSpacing = 0.sp,      lineHeight = 24.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.Black,     fontSize = 20.sp, letterSpacing = (-0.3).sp, lineHeight = 26.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 0.sp,      lineHeight = 22.sp),
    titleSmall    = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 14.sp, letterSpacing = 0.sp,      lineHeight = 20.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 16.sp, letterSpacing = 0.sp,      lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 13.sp, letterSpacing = 0.sp,      lineHeight = 20.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 11.sp, letterSpacing = 0.sp,      lineHeight = 17.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 13.sp, letterSpacing = 0.02.sp),
    labelMedium   = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 11.sp, letterSpacing = 0.05.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 9.sp,  letterSpacing = 0.07.sp),
)

// Backward-compat alias used by MaterialTheme call sites
val Typography = ToyTypography
