package com.toybox.app.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.toybox.app.GamificationViewModel
import com.toybox.app.Toy
import com.toybox.app.ToyBoxApplication
import com.toybox.app.ToyViewModel
import com.toybox.app.ui.theme.*
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToyScreen(
    vm: ToyViewModel,
    nav: NavController,
    isDark: Boolean,
    gamVm: GamificationViewModel,
    currentToyCount: Int
) {
    val context = LocalContext.current
    val soundManager = (context.applicationContext as ToyBoxApplication).soundManager

    // ── Shared state across steps ─────────────────────────────────────────────
    var step      by remember { mutableIntStateOf(0) } // 0 = photo picker, 1 = form
    var photoUri  by remember { mutableStateOf<Uri?>(null) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Form state
    var name      by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var category  by remember { mutableStateOf("LEGO") }
    var price     by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf(false) }
    var date      by remember { mutableStateOf("") }
    var key       by remember { mutableStateOf("") }
    var expanded  by remember { mutableStateOf(false) }

    val categories = listOf(
        "LEGO", "Action figure", "Stuffed animal", "Vehicle",
        "Board game", "Puzzle", "Remote control", "Outdoor toy",
        "Arts & crafts", "Building blocks", "Doll", "Musical toy",
        "Educational", "Sports", "Card game", "Science kit", "Other"
    )

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d -> date = "%02d/%02d/%04d".format(d, m + 1, y) },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // ── Activity result launchers (declared at top level, shared across steps) ─
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }
            photoUri = it
            if (step == 0) step = 1   // auto-advance on step 0
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = cameraUri
            if (step == 0) step = 1   // auto-advance on step 0
        }
    }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File(
                context.getExternalFilesDir("Pictures"),
                "toy_${System.currentTimeMillis()}.jpg"
            )
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            cameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Step transition animation ─────────────────────────────────────────────
    AnimatedContent(
        targetState = step,
        transitionSpec = {
            if (targetState > initialState) {
                // Forward: slide in from right
                (slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))) togetherWith
                (slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(200)))
            } else {
                // Back: slide in from left
                (slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300))) togetherWith
                (slideOutHorizontally(tween(300)) { it } + fadeOut(tween(200)))
            }
        },
        label = "add_step"
    ) { currentStep ->
        when (currentStep) {
            // ── Step 0: Photo-first picker ────────────────────────────────────
            0 -> PhotoPickerStep(
                onCamera  = { cameraPermLauncher.launch(Manifest.permission.CAMERA) },
                onGallery = { galleryLauncher.launch("image/*") },
                onSkip    = { step = 1 }
            )

            // ── Step 1: Form ──────────────────────────────────────────────────
            else -> FormStep(
                photoUri      = photoUri,
                name          = name,
                nameError     = nameError,
                category      = category,
                categories    = categories,
                price         = price,
                priceError    = priceError,
                date          = date,
                receiptKey    = key,
                expanded      = expanded,
                onNameChange  = { name = it; nameError = false },
                onCategoryChange = { category = it },
                onPriceChange = { price = it; priceError = false },
                onExpandedChange = { expanded = it },
                onDatePick    = { datePickerDialog.show() },
                onKeyChange   = { key = it },
                onPhotoChange = { galleryLauncher.launch("image/*") },
                onCamera      = { cameraPermLauncher.launch(Manifest.permission.CAMERA) },
                onBack        = { step = 0 },
                onSave = {
                    nameError  = name.isEmpty()
                    priceError = price.isNotEmpty() && price.toIntOrNull() == null
                    if (!nameError && !priceError) {
                        val toy = Toy(
                            name         = name,
                            category     = category,
                            price        = price.toIntOrNull()?.toDouble() ?: 0.0,
                            purchaseDate = date,
                            purchaseKey  = key,
                            photoUri     = photoUri?.toString() ?: ""
                        )
                        vm.addToy(toy)
                        gamVm.onToyAdded(currentToyCount + 1, toy)  // passes toy for challenge eval
                        soundManager.playAddSound()
                        nav.navigate("home") { popUpTo("home") { inclusive = true } }
                    }
                }
            )
        }
    }
}

// ── Feature 2: Photo picker step ─────────────────────────────────────────────

@Composable
private fun PhotoPickerStep(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("📸", fontSize = 80.sp, textAlign = TextAlign.Center)

            Text(
                "Snap your toy!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                "Start with a photo — it makes your toy box way more fun!",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(8.dp))

            // Camera button — green 3D CTA
            ToyActionButton(
                label       = "Take a Photo 📷",
                faceColor   = Mint,
                shadowColor = MintDark,
                textColor   = androidx.compose.ui.graphics.Color.White,
                onClick     = onCamera,
                modifier    = Modifier.fillMaxWidth(),
                height      = 56.dp
            )

            // Gallery button
            OutlinedButton(
                onClick = onGallery,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Default.PhotoLibrary, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Pick from Gallery 🖼️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            TextButton(onClick = onSkip) {
                Text(
                    "Skip for now →",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Form step ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormStep(
    photoUri: Uri?,
    name: String,
    nameError: Boolean,
    category: String,
    categories: List<String>,
    price: String,
    priceError: Boolean,
    date: String,
    receiptKey: String,
    expanded: Boolean,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onDatePick: () -> Unit,
    onKeyChange: (String) -> Unit,
    onPhotoChange: () -> Unit,
    onCamera: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.ArrowBack, null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Column {
                    Text(
                        "Add new toy",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Fill in the details below",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Photo preview (large, tap to change)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(180.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
                .clickable { onPhotoChange() },
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                )
                // Overlay hint
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        "Tap to change photo",
                        fontSize = 11.sp,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        "Tap to pick from gallery",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Camera shortcut
        Button(
            onClick = onCamera,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(
                Icons.Default.CameraAlt, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Take photo with camera",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(4.dp))

        // Form card
        Card(
            Modifier.padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DarkField(
                    label = "Toy name *", value = name,
                    placeholder = "e.g. LEGO City Set",
                    isError = nameError, errorMsg = "Name is required",
                    onChange = onNameChange
                )

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
                    Column {
                        Text(
                            "CATEGORY",
                            fontSize = 9.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.07.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = category, onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = darkFieldColors()
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = { onCategoryChange(cat); onExpandedChange(false) }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        DarkField(
                            label = "Price (₹)", value = price, placeholder = "0",
                            isNumber = true, isError = priceError,
                            errorMsg = "Enter a valid price",
                            onChange = onPriceChange
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            "DATE (OPTIONAL)",
                            fontSize = 9.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.07.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = date, onValueChange = {}, readOnly = true,
                            placeholder = {
                                Text(
                                    "Tap to pick", fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = onDatePick,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CalendarMonth, null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDatePick() },
                            shape = RoundedCornerShape(10.dp),
                            colors = darkFieldColors()
                        )
                    }
                }

                DarkField(
                    label = "Receipt # (optional)", value = receiptKey,
                    placeholder = "e.g. AMZ-2026-001",
                    onChange = onKeyChange
                )

                // Save CTA — green 3D button
                ToyActionButton(
                    label       = "Add My Toy 🎉",
                    faceColor   = Mint,
                    shadowColor = MintDark,
                    textColor   = androidx.compose.ui.graphics.Color.White,
                    onClick     = onSave,
                    modifier    = Modifier.fillMaxWidth(),
                    height      = 56.dp,
                    shadowDepth = 5.dp
                )
            }
        }
    }
}

// ── Shared field components (used by FormStep) ────────────────────────────────

@Composable
fun DarkField(
    label: String,
    value: String,
    placeholder: String,
    isNumber: Boolean = false,
    isError: Boolean = false,
    errorMsg: String = "",
    onChange: (String) -> Unit
) {
    Column {
        Text(
            label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.07.sp
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = {
                Text(
                    placeholder,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            isError = isError,
            supportingText = if (isError) {
                { Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
            } else null,
            colors = darkFieldColors(),
            keyboardOptions = if (isNumber)
                KeyboardOptions(keyboardType = KeyboardType.Number)
            else
                KeyboardOptions.Default,
            singleLine = true
        )
    }
}

@Composable
fun darkFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedTextColor        = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
    cursorColor             = MaterialTheme.colorScheme.primary
)
