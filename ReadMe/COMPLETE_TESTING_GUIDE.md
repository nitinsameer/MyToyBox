# MyToyBox - Complete Testing Guide

## 🧪 Test Scenarios for QA

---

## TEST SUITE 1: PRICE FIELD

### Test 1.1 - Valid Whole Number Price
```
Steps:
1. Open app → AddToyScreen
2. Fill name: "LEGO Set"
3. Enter price: "250"
4. Enter category: "LEGO"
5. Leave key and date empty
6. Click "Save my toy"

Expected Result:
✅ Toy saved successfully
✅ Price displays as "₹250" (not "₹250.00")
✅ Navigate to MyToysScreen
✅ Verify price shows "₹250"
```

### Test 1.2 - Invalid Decimal Price
```
Steps:
1. Open AddToyScreen
2. Fill name: "Puzzle Board"
3. Enter price: "100.50"
4. Try to save

Expected Result:
❌ Error message: "Enter a valid price"
❌ Toy NOT saved
❌ Stay on AddToyScreen
✅ Can correct and retry
```

### Test 1.3 - Invalid Text Price
```
Steps:
1. Open AddToyScreen
2. Fill name: "Doll"
3. Enter price: "abc"
4. Try to save

Expected Result:
❌ Error message: "Enter a valid price"
❌ Toy NOT saved
❌ Stay on AddToyScreen
```

### Test 1.4 - Negative Price
```
Steps:
1. Open AddToyScreen
2. Fill name: "Toy Car"
3. Enter price: "-100"
4. Try to save

Expected Result:
❌ Error message: "Enter a valid price"
❌ Toy NOT saved
```

### Test 1.5 - Empty Price (Should Default to 0)
```
Steps:
1. Open AddToyScreen
2. Fill name: "Action Figure"
3. Leave price empty
4. Fill category
5. Leave key and date empty
6. Save

Expected Result:
✅ Toy saved successfully
✅ Price defaults to "₹0"
✅ Can verify in MyToysScreen
```

### Test 1.6 - Edit Toy Price
```
Steps:
1. Open MyToysScreen
2. Find existing toy "LEGO Set" (₹250)
3. Click Edit button
4. Change price to "350"
5. Save

Expected Result:
✅ Price updated to "₹350"
✅ Display updated everywhere
✅ Database updated correctly
```

### Test 1.7 - Price Display Consistency
```
Steps:
1. Add toy with price "500"
2. Navigate to HomeScreen
3. Check Recent toys section
4. Check All toys section
5. Navigate to MyToysScreen

Expected Result:
✅ All displays show "₹500" (no decimals)
✅ Stats show "₹500" (not "₹500.00")
✅ Consistent formatting everywhere
```

---

## TEST SUITE 2: OPTIONAL RECEIPT KEY

### Test 2.1 - Save Without Receipt Key
```
Steps:
1. Open AddToyScreen
2. Fill name: "Board Game"
3. Fill price: "299"
4. Fill category: "Board game"
5. Leave Receipt Key EMPTY
6. Leave Date empty
7. Save

Expected Result:
✅ Toy saved successfully
✅ NO error message about receipt key
✅ Toy appears in MyToysScreen
```

### Test 2.2 - Display "No Receipt Key" When Empty
```
Steps:
1. Add toy without receipt key (from Test 2.1)
2. Navigate to MyToysScreen
3. Find the toy

Expected Result:
✅ Displays "No receipt key" instead of blank/error
✅ UI looks clean and intentional
✅ Doesn't look like a bug
```

### Test 2.3 - Save With Receipt Key
```
Steps:
1. Open AddToyScreen
2. Fill all required fields
3. Enter Receipt Key: "AMZ-12345-67890"
4. Save

Expected Result:
✅ Toy saved successfully
✅ Receipt key stored correctly
```

### Test 2.4 - Display Receipt Key When Present
```
Steps:
1. View toy from Test 2.3 in MyToysScreen

Expected Result:
✅ Displays "Key: AMZ-12345-67890"
✅ Proper formatting
```

### Test 2.5 - Edit: Remove Receipt Key
```
Steps:
1. Find toy with receipt key
2. Click Edit
3. Clear receipt key field
4. Save

Expected Result:
✅ Receipt key removed
✅ Displays "No receipt key" in list
```

### Test 2.6 - Edit: Add Receipt Key
```
Steps:
1. Find toy without receipt key
2. Click Edit
3. Add receipt key: "FLIPKART-999"
4. Save

Expected Result:
✅ Receipt key added
✅ Displays "Key: FLIPKART-999" in list
```

---

## TEST SUITE 3: OPTIONAL DATE FIELD

### Test 3.1 - Save Without Date
```
Steps:
1. Open AddToyScreen
2. Fill name: "Remote Control Car"
3. Fill price: "450"
4. Fill category: "Remote control"
5. Leave Date EMPTY
6. Leave Receipt Key empty
7. Save

Expected Result:
✅ Toy saved successfully
✅ NO error message about date
✅ Date accepted as empty string
```

### Test 3.2 - Display "No Date" When Empty
```
Steps:
1. Add toy without date (from Test 3.1)
2. Navigate to MyToysScreen
3. Find the toy

Expected Result:
✅ Displays "No date" instead of blank
✅ Clear indication that date wasn't set
✅ Professional appearance
```

### Test 3.3 - Pick Date from DatePickerDialog
```
Steps:
1. Open AddToyScreen
2. Fill name: "Stuffed Animal"
3. Fill price: "199"
4. Fill category: "Stuffed animal"
5. Tap date field 📅 icon
6. DatePickerDialog appears
7. Select date: March 20, 2026
8. Save

Expected Result:
✅ DatePickerDialog opens correctly
✅ Date formats as "20/03/2026"
✅ Toy saves with date
✅ Date displays in list correctly
```

### Test 3.4 - Date Format Verification
```
Steps:
1. Add toy with date March 5, 2026
2. View in list

Expected Result:
✅ Shows "05/03/2026" (not "3/5/2026")
✅ Always two-digit month and day
✅ Consistent DD/MM/YYYY format
```

### Test 3.5 - Edit: Add Missing Date
```
Steps:
1. Find toy without date
2. Click Edit
3. Tap date field
4. Select date: March 21, 2026
5. Save

Expected Result:
✅ Date added successfully
✅ Displays "21/03/2026" in list
✅ Previous "No date" is replaced
```

### Test 3.6 - Edit: Change Existing Date
```
Steps:
1. Find toy with date "20/03/2026"
2. Click Edit
3. Tap date field
4. Change to March 15, 2026
5. Save

Expected Result:
✅ Date updated to "15/03/2026"
✅ Database updated
✅ List shows new date
```

### Test 3.7 - Edit: Clear Date
```
Steps:
1. Find toy with date
2. Click Edit
3. Clear date field (no interaction with calendar)
4. Save

Expected Result:
✅ Date removed
✅ Displays "No date" in list
```

---

## TEST SUITE 4: EXPANDED CATEGORIES

### Test 4.1 - All 17 Categories Present
```
Steps:
1. Open AddToyScreen
2. Click Category dropdown

Expected Categories (in order):
✅ 1. LEGO
✅ 2. Action figure
✅ 3. Stuffed animal
✅ 4. Vehicle
✅ 5. Board game
✅ 6. Puzzle
✅ 7. Remote control (NEW)
✅ 8. Outdoor toy (NEW)
✅ 9. Arts & crafts (NEW)
✅ 10. Building blocks (NEW)
✅ 11. Doll (NEW)
✅ 12. Musical toy (NEW)
✅ 13. Educational (NEW)
✅ 14. Sports (NEW)
✅ 15. Card game (NEW)
✅ 16. Science kit (NEW)
✅ 17. Other

Total: 17 categories ✅
```

### Test 4.2 - Select Each Category
```
For each category:
- Open AddToyScreen
- Select category from dropdown
- Add toy with complete data
- Verify saves correctly

Expected Result:
✅ All 17 categories work
✅ Each saves and displays correctly
```

### Test 4.3 - Category Display Consistency
```
Steps:
1. Add toy with category "Remote control"
2. View in HomeScreen
3. View in MyToysScreen
4. View in EditToyScreen

Expected Result:
✅ Category shows "Remote control" everywhere
✅ Consistent display across all screens
✅ Category badge appears correctly
```

### Test 4.4 - Edit: Change Category
```
Steps:
1. Add toy with category "LEGO"
2. Edit toy
3. Change category to "Building blocks"
4. Save

Expected Result:
✅ Category changed to "Building blocks"
✅ Updates everywhere (lists, screens)
✅ Database reflects change
```

---

## TEST SUITE 5: FIREBASE & BUILD

### Test 5.1 - Debug Build Compiles
```
Command: ./gradlew assembleDebug

Expected Result:
✅ BUILD SUCCESSFUL
❌ No errors
❌ No blocking warnings
✅ APK created successfully
```

### Test 5.2 - Release Build Compiles
```
Command: ./gradlew assembleRelease

Expected Result:
✅ BUILD SUCCESSFUL
❌ No errors
✅ Signed APK created (if configured)
```

### Test 5.3 - Firebase Integration
```
Steps:
1. Install APK on device
2. Open app
3. Trigger a crash (intentional for testing)
4. Check Firebase Console

Expected Result:
✅ Crash logged in Firebase
✅ Error details captured
✅ Stack trace available
```

---

## TEST SUITE 6: END-TO-END USER FLOWS

### Flow 1: Quick Add Toy (Minimal Data)
```
Steps:
1. Open AddToyScreen
2. Take photo
3. Enter name: "Mystery Toy"
4. Select category: "Other"
5. Leave everything else empty
6. Save

Expected Result:
✅ Toy saved with minimal data
✅ Appears in MyToysScreen
✅ Shows "No date" and "No receipt key"
✅ Price is "₹0"
```

### Flow 2: Add Toy (Complete Data)
```
Steps:
1. Open AddToyScreen
2. Take photo
3. Enter name: "Premium LEGO Set"
4. Enter price: "5999"
5. Select category: "LEGO"
6. Enter receipt key: "AMAZON-2026-001"
7. Select date: March 20, 2026
8. Save

Expected Result:
✅ Toy saved with all data
✅ Displays correctly everywhere
✅ Price shows "₹5999" (not decimals)
✅ All fields appear in MyToysScreen
```

### Flow 3: Edit & Update Price
```
Steps:
1. Add toy with price "1000"
2. Go to MyToysScreen
3. Click Edit
4. Change price to "1500"
5. Save

Expected Result:
✅ Price updated everywhere
✅ Displays "₹1500"
✅ HomeScreen totals updated
✅ Stats recalculated
```

### Flow 4: Delete Toy
```
Steps:
1. Find toy in MyToysScreen
2. Click Delete button
3. Confirm delete

Expected Result:
✅ Toy deleted
✅ Removed from lists
✅ HomeScreen stats updated
✅ No database issues
```

---

## TEST SUITE 7: EDGE CASES

### Edge Case 1: Maximum Price
```
Input: "999999999"

Expected Result:
✅ Saves successfully
✅ Displays correctly
❌ No overflow or crashes
```

### Edge Case 2: Very Long Name
```
Input: "This is an incredibly long toy name that might break the UI..."

Expected Result:
✅ Saves successfully
✅ Truncated in list (ellipsis)
✅ Full name visible when viewing details
```

### Edge Case 3: Special Characters in Receipt Key
```
Input: "AMZ-2026-001!@#$%^&*()"

Expected Result:
✅ Saves successfully
✅ Displays correctly
❌ No injection/security issues
```

### Edge Case 4: Rapid Clicking Add Button
```
Steps:
1. Fill form completely
2. Click "Save my toy" multiple times quickly

Expected Result:
✅ Only one toy created
❌ No duplicate entries
❌ No crashes
```

### Edge Case 5: Network Interruption During Save
```
Steps:
1. Fill form
2. Turn off WiFi/Mobile
3. Click Save
4. Turn connectivity back on

Expected Result:
✅ Graceful handling
❌ No crash
⚠️ Clear error message
```

---

## TEST SUITE 8: UI/UX VERIFICATION

### UI Check 1: Field Layout
```
AddToyScreen Layout:
┌─────────────────────────┐
│ [Photo Section]         │
│ [Camera Button]         │
│ [Name Field] ✅         │
│ [Price Field] ✅        │
│ [Receipt Key] ✅        │
│ [Date Field] 📅 ✅      │
│ [Category] ▼ ✅         │
│ [Save Button]           │
└─────────────────────────┘

Expected Result:
✅ All fields visible
✅ Proper spacing
✅ Placeholders show
✅ Icons visible
```

### UI Check 2: Error Display
```
When entering invalid data:

Expected Result:
✅ Error text appears below field
✅ Error text is RED
✅ Field has error border
✅ Multiple errors possible
✅ Errors clear when corrected
```

### UI Check 3: Optional Field Indicators
```
Receipt Key field should:
✅ Have label "(Optional)"
✅ Have placeholder text
✅ NOT show error when empty
✅ Look different from required fields

Date field should:
✅ Have label "(Optional)"
✅ Have placeholder text
✅ Have calendar icon button
✅ NOT show error when empty
```

### UI Check 4: List Displays
```
MyToysScreen should show:
✅ Toy image/icon
✅ Toy name (truncated with ellipsis)
✅ Date (or "No date")
✅ Receipt key (or "No receipt key")
✅ Category badge
✅ Price as "₹XXX" (no decimals)
✅ Edit button
✅ Delete button
```

---

## TEST SUITE 9: PERFORMANCE

### Performance Test 1: Add 50 Toys
```
Steps:
1. Rapidly add 50 toys with various data
2. Navigate between screens
3. Check performance

Expected Result:
✅ App remains responsive
✅ No lag or stuttering
✅ HomeScreen loads quickly
✅ MyToysScreen scrolls smoothly
❌ No crashes or ANRs
```

### Performance Test 2: Search & Filter
```
Steps:
1. Have 50 toys in list
2. Search for toys
3. Filter by category

Expected Result:
✅ Searches complete quickly (< 1 second)
✅ Filters work correctly
❌ No lag during filtering
```

---

## TEST CHECKLIST

### Before Release
```
Category: Price Field
  ☐ Test 1.1 - Valid whole number price
  ☐ Test 1.2 - Invalid decimal price
  ☐ Test 1.3 - Invalid text price
  ☐ Test 1.4 - Negative price
  ☐ Test 1.5 - Empty price (defaults to 0)
  ☐ Test 1.6 - Edit toy price
  ☐ Test 1.7 - Price display consistency

Category: Optional Receipt Key
  ☐ Test 2.1 - Save without receipt key
  ☐ Test 2.2 - Display "No receipt key" when empty
  ☐ Test 2.3 - Save with receipt key
  ☐ Test 2.4 - Display receipt key when present
  ☐ Test 2.5 - Edit: Remove receipt key
  ☐ Test 2.6 - Edit: Add receipt key

Category: Optional Date
  ☐ Test 3.1 - Save without date
  ☐ Test 3.2 - Display "No date" when empty
  ☐ Test 3.3 - Pick date from DatePickerDialog
  ☐ Test 3.4 - Date format verification
  ☐ Test 3.5 - Edit: Add missing date
  ☐ Test 3.6 - Edit: Change existing date
  ☐ Test 3.7 - Edit: Clear date

Category: Categories
  ☐ Test 4.1 - All 17 categories present
  ☐ Test 4.2 - Select each category
  ☐ Test 4.3 - Category display consistency
  ☐ Test 4.4 - Edit: Change category

Category: Firebase & Build
  ☐ Test 5.1 - Debug build compiles
  ☐ Test 5.2 - Release build compiles
  ☐ Test 5.3 - Firebase integration

Category: End-to-End Flows
  ☐ Flow 1 - Quick add toy (minimal data)
  ☐ Flow 2 - Add toy (complete data)
  ☐ Flow 3 - Edit & update price
  ☐ Flow 4 - Delete toy

Category: Edge Cases
  ☐ Edge Case 1 - Maximum price
  ☐ Edge Case 2 - Very long name
  ☐ Edge Case 3 - Special characters
  ☐ Edge Case 4 - Rapid clicking
  ☐ Edge Case 5 - Network interruption

Category: UI/UX
  ☐ UI Check 1 - Field layout
  ☐ UI Check 2 - Error display
  ☐ UI Check 3 - Optional field indicators
  ☐ UI Check 4 - List displays

Category: Performance
  ☐ Performance Test 1 - Add 50 toys
  ☐ Performance Test 2 - Search & filter
```

---

## 🎯 Pass Criteria

✅ **PASS** if:
- All tests pass or are marked as expected
- No crashes or ANRs occur
- Build completes successfully
- UI looks clean and professional
- Data persists correctly
- Performance is acceptable

❌ **FAIL** if:
- Any test fails unexpectedly
- Crashes occur
- Build fails
- Data doesn't persist
- Performance is unacceptable

---

**Testing Document Version**: 1.0  
**Last Updated**: March 23, 2026  
**Status**: Ready for QA Testing

