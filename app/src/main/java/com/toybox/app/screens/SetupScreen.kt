package com.toybox.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toybox.app.CrashReporter
import com.toybox.app.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SetupScreen(
    userVm: UserViewModel,
    onNameSaved: () -> Unit
) {
    var nameInput     by remember { mutableStateOf("") }
    var showError     by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    // Parental consent checkbox — COPPA / GDPR gate
    var consentChecked by remember { mutableStateOf(false) }
    var showConsentError by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester     = remember { FocusRequester() }
    val isButtonEnabled    = nameInput.trim().isNotEmpty() && consentChecked

    // Bounce animation on the illustration
    val infiniteTransition = rememberInfiniteTransition(label = "emoji_bounce")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        contentVisible = true
        delay(300)
        focusRequester.requestFocus()
    }

    fun submit() {
        val trimmed = nameInput.trim()
        if (trimmed.isEmpty()) { showError = true; return }
        if (!consentChecked)   { showConsentError = true; return }

        keyboardController?.hide()
        // Persist name + consent, then enable crash reporting now that consent is granted
        userVm.saveKidName(trimmed)
        userVm.saveParentalConsent(true)
        CrashReporter.enableCollection()
        onNameSaved()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter   = fadeIn(tween(400)) + slideInVertically(
                animationSpec  = tween(400, easing = FastOutSlowInEasing),
                initialOffsetY = { it / 5 }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Bouncing illustration
                Text(
                    text      = "🧸",
                    fontSize  = 72.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.scale(emojiScale)
                )

                Spacer(Modifier.height(28.dp))

                Text(
                    text          = "Hi there! 👋",
                    fontSize      = 30.sp,
                    fontWeight    = FontWeight.Black,
                    color         = MaterialTheme.colorScheme.onBackground,
                    textAlign     = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text      = "What's your name?",
                    fontSize  = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Name input
                OutlinedTextField(
                    value       = nameInput,
                    onValueChange = {
                        nameInput = it
                        if (showError) showError = false
                    },
                    placeholder = {
                        Text(
                            "e.g. Alex 🦁",
                            color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                            fontSize = 15.sp
                        )
                    },
                    trailingIcon = {
                        if (nameInput.isNotEmpty()) {
                            IconButton(onClick = {
                                nameInput  = ""
                                showError  = false
                            }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    isError       = showError,
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType   = KeyboardType.Text,
                        imeAction      = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    shape  = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        errorBorderColor     = MaterialTheme.colorScheme.error
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                // Name error
                AnimatedVisibility(
                    visible = showError,
                    enter   = fadeIn(tween(200)) + expandVertically(tween(200)),
                    exit    = fadeOut(tween(150)) + shrinkVertically(tween(150))
                ) {
                    Row(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, start = 4.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, null,
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                        Text("Name can't be blank!", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Parental Consent Block ────────────────────────────────────
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showConsentError)
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (showConsentError)
                        androidx.compose.foundation.BorderStroke(
                            1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    else null
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked         = consentChecked,
                            onCheckedChange = {
                                consentChecked    = it
                                showConsentError  = false
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor   = MaterialTheme.colorScheme.primary,
                                uncheckedColor = if (showConsentError)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Text(
                                text       = "Parent / Guardian setup 👨‍👩‍👧",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text     = "I am setting this up for my child. I have read and agree to the ",
                                fontSize = 11.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                            // Privacy Policy link (annotated string)
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(
                                        color          = MaterialTheme.colorScheme.primary,
                                        fontWeight     = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline,
                                        fontSize       = 11.sp
                                    )) {
                                        append("Privacy Policy")
                                    }
                                    append(" and understand that this app stores data locally on this device only.")
                                },
                                fontSize   = 11.sp,
                                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Consent error
                AnimatedVisibility(
                    visible = showConsentError,
                    enter   = fadeIn(tween(200)) + expandVertically(tween(200)),
                    exit    = fadeOut(tween(150)) + shrinkVertically(tween(150))
                ) {
                    Row(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, null,
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                        Text("A parent or guardian must agree to continue.",
                            fontSize = 12.sp, color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // CTA — enabled only when name + consent are both provided
                Button(
                    onClick  = { submit() },
                    enabled  = isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape  = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        text       = when {
                            nameInput.trim().isEmpty() -> "Enter your name first"
                            !consentChecked            -> "Parent must agree above ☝️"
                            else                       -> "Let's Go! 🚀"
                        },
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = if (isButtonEnabled)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text      = "No ads · No tracking · Data stays on your device 🔒",
                    fontSize  = 10.sp,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
