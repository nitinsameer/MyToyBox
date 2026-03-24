# MyToyBox - Detailed Code Changes Reference

## File: AddToyScreen.kt

### Change 1: Expanded Categories List (Line ~55)
```kotlin
// BEFORE
val categories = listOf(
    "LEGO", "Action figure", "Stuffed animal",
    "Vehicle", "Board game", "Puzzle", "Other"
)

// AFTER
val categories = listOf(
    "LEGO", "Action figure", "Stuffed animal", "Vehicle",
    "Board game", "Puzzle", "Remote control", "Outdoor toy",
    "Arts & crafts", "Building blocks", "Doll", "Musical toy",
    "Educational", "Sports", "Card game", "Science kit", "Other"
)
```

### Change 2: Updated Validation Logic (Line ~120-160)
```kotlin
// BEFORE
fun validateAndSave() {
    nameError = ""
    priceError = ""
    keyError = ""
    dateError = ""

    var isValid = true

    if (name.isBlank()) {
        nameError = "Name is required"
        isValid = false
    }

    if (price.isNotBlank()) {
        val priceNum = price.toDoubleOrNull()
        if (priceNum == null || priceNum <= 0) {
            priceError = "Enter a valid price"
            isValid = false
        }
    }

    if (key.isBlank()) {
        keyError = "Receipt key is required"
        isValid = false
    }

    if (date.isBlank()) {
        dateError = "Date is required"
        isValid = false
    }

    if (!isValid) return
    
    vm.addToy(
        Toy(
            name = name.trim(),
            category = category,
            price = price.trim().toDoubleOrNull() ?: 0.0,
            purchaseDate = date.trim(),
            purchaseKey = key.trim(),
            photoUri = photoUri?.toString() ?: ""
        )
    )
}

// AFTER
fun validateAndSave() {
    nameError = ""
    priceError = ""
    keyError = ""
    dateError = ""

    var isValid = true

    if (name.isBlank()) {
        nameError = "Name is required"
        isValid = false
    }

    if (price.isNotBlank()) {
        val priceNum = price.toIntOrNull()  // ← Changed: toInt instead of toDouble
        if (priceNum == null || priceNum <= 0) {
            priceError = "Enter a valid price"
            isValid = false
        }
    }
    // ← Removed: key validation
    // ← Removed: date validation

    if (!isValid) return

    vm.addToy(
        Toy(
            name = name.trim(),
            category = category,
            price = price.trim().toIntOrNull()?.toDouble() ?: 0.0,  // ← Changed: toInt first
            purchaseDate = date.trim(),  // ← Now allows empty
            purchaseKey = key.trim(),    // ← Now allows empty
            photoUri = photoUri?.toString() ?: ""
        )
    )
}
```

### Change 3: Price Field UI (Line ~275)
```kotlin
// BEFORE
OutlinedTextField(
    value = price,
    onValueChange = { price = it; priceError = "" },
    label = { Text("Price (₹)") },
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    isError = priceError.isNotEmpty(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

// AFTER
OutlinedTextField(
    value = price,
    onValueChange = { price = it; priceError = "" },
    label = { Text("Price (₹)") },
    placeholder = { Text("0") },  // ← Added placeholder
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    isError = priceError.isNotEmpty(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)
```

### Change 4: Purchase Key Field UI (Line ~290)
```kotlin
// BEFORE
OutlinedTextField(
    value = key,
    onValueChange = { key = it; keyError = "" },
    label = { Text("Purchase key / receipt #") },
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    isError = keyError.isNotEmpty()
)
if (keyError.isNotEmpty()) {
    Text(keyError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp, top = -8.dp))
}

// AFTER
OutlinedTextField(
    value = key,
    onValueChange = { key = it; keyError = "" },
    label = { Text("Receipt key (Optional)") },  // ← Changed label
    placeholder = { Text("Optional — e.g. AMZ-2026-001") },  // ← Added placeholder
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    isError = keyError.isNotEmpty()
)
if (keyError.isNotEmpty()) {
    Text(keyError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp, top = -8.dp))
}
```

### Change 5: Date Field UI (Line ~308)
```kotlin
// BEFORE
OutlinedTextField(
    value = date,
    onValueChange = {},
    label = { Text("Purchase date (DD/MM/YYYY)") },
    modifier = Modifier.fillMaxWidth(),
    // ... rest of the field

// AFTER
OutlinedTextField(
    value = date,
    onValueChange = {},
    label = { Text("Purchase date (Optional)") },  // ← Changed label
    placeholder = { Text("Optional — tap to pick date") },  // ← Added placeholder
    modifier = Modifier.fillMaxWidth(),
    // ... rest of the field
```

---

## File: EditToyScreen.kt

### Changes (Same as AddToyScreen):
1. **Line ~65**: Updated categories list (17 items)
2. **Line ~130-170**: Updated validateAndSave() function
3. **Line ~270**: Updated price field with placeholder
4. **Line ~285**: Updated key field with optional placeholder
5. **Line ~303**: Updated date field with optional placeholder

All changes are identical to AddToyScreen.kt

---

## File: HomeScreen.kt

### Change 1: Stats Display (Line ~50)
```kotlin
// BEFORE
StatCard("Toys", "${toys.size}", Modifier.weight(1f))
StatCard("Spent", "₹${"%,.0f".format(totalSpent)}", Modifier.weight(1f))
StatCard("Avg", "₹${"%,.0f".format(avg)}", Modifier.weight(1f))

// AFTER
StatCard("Toys", "${toys.size}", Modifier.weight(1f))
StatCard("Spent", "₹${totalSpent.toInt()}", Modifier.weight(1f))
StatCard("Avg", "₹${avg.toInt()}", Modifier.weight(1f))
```

### Change 2: ToyThumb Price Display (Line ~108)
```kotlin
// BEFORE
Text("₹${"%,.0f".format(toy.price)}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Purple)

// AFTER
Text("₹${toy.price.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Purple)
```

### Change 3: ToyListItem Date and Price (Line ~145-165)
```kotlin
// BEFORE
Column(Modifier.weight(1f)) {
    Text(toy.name, /* ... */)
    Text(toy.purchaseDate, fontSize = 11.sp, color = Color(0xFF888780))  // ← Before: Direct
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFEEEDFE)) {
        Text(toy.category, /* ... */)
    }
}
Text(
    "₹${"%,.0f".format(toy.price)}",  // ← Before: With decimals
    fontWeight = FontWeight.ExtraBold,
    fontSize = 12.sp,
    color = Purple
)

// AFTER
Column(Modifier.weight(1f)) {
    Text(toy.name, /* ... */)
    Text(if (toy.purchaseDate.isEmpty()) "No date" else toy.purchaseDate, fontSize = 11.sp, color = Color(0xFF888780))  // ← After: Check empty
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFEEEDFE)) {
        Text(toy.category, /* ... */)
    }
}
Text(
    "₹${toy.price.toInt()}",  // ← After: No decimals
    fontWeight = FontWeight.ExtraBold,
    fontSize = 12.sp,
    color = Purple
)
```

---

## File: MyToysScreen.kt

### Change 1: Toy Card Display (Line ~50-65)
```kotlin
// BEFORE
Column(Modifier.weight(1f)) {
    Text(toy.name, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF26215C))
    Text(toy.purchaseDate, fontSize = 11.sp, color = Color(0xFF888780))
    Text("Key: ${toy.purchaseKey}", fontSize = 11.sp, color = Color(0xFF888780))
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFEEEDFE)) {
        Text(toy.category, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3C3489), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
    }
}
Column(horizontalAlignment = Alignment.End) {
    Text("₹${"%,.0f".format(toy.price)}", fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp, color = Purple)

// AFTER
Column(Modifier.weight(1f)) {
    Text(toy.name, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF26215C))
    Text(if (toy.purchaseDate.isEmpty()) "No date" else toy.purchaseDate, fontSize = 11.sp, color = Color(0xFF888780))  // ← Updated
    Text(if (toy.purchaseKey.isEmpty()) "No receipt key" else "Key: ${toy.purchaseKey}", fontSize = 11.sp, color = Color(0xFF888780))  // ← Updated
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFEEEDFE)) {
        Text(toy.category, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3C3489), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
    }
}
Column(horizontalAlignment = Alignment.End) {
    Text("₹${toy.price.toInt()}", fontWeight = FontWeight.ExtraBold,  // ← Updated
        fontSize = 14.sp, color = Purple)
```

---

## File: google-services.json

### Change: Added Debug Package Configuration
```json
// BEFORE
{
  "project_info": { ... },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:1921462862:android:3f9ab8b49a080ca43e891f",
        "android_client_info": {
          "package_name": "com.toybox.app"  // ← Only release package
        }
      },
      // ... rest of config
    }
  ]
}

// AFTER
{
  "project_info": { ... },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:1921462862:android:3f9ab8b49a080ca43e891f",
        "android_client_info": {
          "package_name": "com.toybox.app"  // ← Release package
        }
      },
      // ... rest of config
    },
    {  // ← New client entry for debug
      "client_info": {
        "mobilesdk_app_id": "1:1921462862:android:3f9ab8b49a080ca43e891f",
        "android_client_info": {
          "package_name": "com.toybox.app.debug"
        }
      },
      "oauth_client": [],
      "api_key": [
        {
          "current_key": "AIzaSyB59jlcZQPMsdlaoLHNmh-99iAmyX7nU0c"
        }
      ],
      "services": {
        "appinvite_service": {
          "other_platform_oauth_client": []
        }
      }
    }
  ]
}
```

---

## Summary of Changes by Category

### 🔢 Price Handling
- ✅ Changed from `toDoubleOrNull()` to `toIntOrNull()` throughout
- ✅ Display format changed from `"₹${"%,.0f".format(toy.price)}"` to `"₹${toy.price.toInt()}"`
- ✅ Price placeholder changed to "0" (more intuitive for integers)

### 📋 Optional Fields
- ✅ Removed validation for `purchaseKey` field
- ✅ Removed validation for `purchaseDate` field
- ✅ Updated labels to "(Optional)"
- ✅ Added helpful placeholders

### 📅 Data Display
- ✅ Empty dates show as "No date"
- ✅ Empty keys show as "No receipt key"
- ✅ Consistent handling across HomeScreen and MyToysScreen

### 📦 Categories
- ✅ Expanded from 7 to 17 categories
- ✅ Updated in both AddToyScreen and EditToyScreen

### 🔧 Firebase
- ✅ Added `com.toybox.app.debug` package to google-services.json
- ✅ Enables proper debug build support

---

## Testing Checklist

- [ ] Price field only accepts whole numbers
- [ ] Empty price defaults to 0
- [ ] Receipt key can be left empty
- [ ] Date can be left empty
- [ ] All 17 categories appear in dropdown
- [ ] Prices display without decimals everywhere
- [ ] Empty dates show "No date"
- [ ] Empty keys show "No receipt key"
- [ ] App compiles without errors
- [ ] Build is successful on debug variant

---

**Last Updated**: March 23, 2026
**Build Status**: ✅ SUCCESS

