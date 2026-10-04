# Quick Start Guide - Enhanced UI Components

This guide helps you quickly integrate the new enhanced UI components into existing screens.

---

## 🚀 Quick Integration Examples

### 1. Add Enhanced Vault List to Any Screen

```kotlin
import com.example.adere.presentation.components.EnhancedVaultList

@Composable
fun YourScreen(items: List<VaultItem>) {
    EnhancedVaultList(
        items = items,
        onItemClick = { item -> /* handle click */ },
        onToggleFavorite = { item -> /* toggle favorite */ },
        onQuickCopy = { item -> /* copy to clipboard */ }
    )
}
```

### 2. Add Empty State

```kotlin
import com.example.adere.presentation.components.EmptyVaultState

if (items.isEmpty()) {
    EmptyVaultState(
        onAddItem = { /* navigate to add item */ }
    )
}
```

### 3. Add Enhanced Search

```kotlin
import com.example.adere.presentation.components.EnhancedSearchBar

var searchQuery by remember { mutableStateOf("") }
var selectedCategories by remember { mutableStateOf(setOf<VaultCategory>()) }

EnhancedSearchBar(
    query = searchQuery,
    onQueryChange = { searchQuery = it },
    selectedCategories = selectedCategories,
    onCategoryToggle = { category ->
        selectedCategories = if (category in selectedCategories) {
            selectedCategories - category
        } else {
            selectedCategories + category
        }
    }
)
```

### 4. Add Security Health Dashboard

```kotlin
import com.example.adere.presentation.components.EnhancedSecurityHealthCard

EnhancedSecurityHealthCard(
    healthScore = 85,
    weakPasswords = 3,
    reusedPasswords = 5,
    oldPasswords = 2,
    onViewDetails = { /* navigate to security details */ }
)
```

### 5. Add Item Detail Fields

```kotlin
import com.example.adere.presentation.components.EnhancedSecretField
import com.example.adere.presentation.components.EnhancedItemHeader

Column {
    EnhancedItemHeader(
        title = item.title,
        category = item.category.title,
        isFavorite = item.isFavorite,
        onToggleFavorite = { /* toggle */ },
        avatar = { 
            VaultItemAvatar(
                title = item.title,
                category = item.category,
                url = item.payload.url
            )
        }
    )
    
    EnhancedSecretField(
        label = "Password",
        value = item.payload.password,
        isSecret = true,
        onCopy = { /* copy password */ },
        icon = Icons.Default.Key
    )
}
```

### 6. Add Master Password Screen

```kotlin
import com.example.adere.presentation.components.EnhancedMasterPasswordScreen

var password by remember { mutableStateOf("") }
var errorMessage by remember { mutableStateOf<String?>(null) }
var isLoading by remember { mutableStateOf(false) }

EnhancedMasterPasswordScreen(
    password = password,
    onPasswordChange = { password = it },
    onUnlock = { 
        isLoading = true
        // Verify password...
    },
    onBiometricUnlock = { /* trigger biometric */ },
    isLoading = isLoading,
    errorMessage = errorMessage,
    showBiometricOption = true
)
```

### 7. Add Onboarding

```kotlin
import com.example.adere.presentation.components.OnboardingScreen

OnboardingScreen(
    onComplete = { /* finish onboarding */ },
    onSkip = { /* skip onboarding */ }
)
```

### 8. Show Error State

```kotlin
import com.example.adere.presentation.components.ErrorState

ErrorState(
    title = "Something went wrong",
    description = "We couldn't load your vault. Please try again.",
    onRetry = { /* retry operation */ }
)
```

### 9. Show Success State

```kotlin
import com.example.adere.presentation.components.SuccessState

SuccessState(
    title = "Backup Complete",
    description = "Your vault has been successfully backed up.",
    onContinue = { /* continue */ },
    buttonText = "Done"
)
```

### 10. Use Enhanced Theme

```kotlin
import com.example.adere.presentation.theme.EnhancedAdereTheme
import com.example.adere.presentation.theme.AccessibilityPreferences
import com.example.adere.presentation.theme.ContrastLevel

EnhancedAdereTheme(
    palette = VaultThemePalette.TOTAL_SECURITY,
    accessibilityPrefs = AccessibilityPreferences(
        contrastLevel = ContrastLevel.STANDARD
    )
) {
    // Your app content
}
```

---

## 🎨 Using Theme Accents

Access theme colors in any composable:

```kotlin
import com.example.ui.theme.LocalThemeAccents

@Composable
fun YourComponent() {
    val themeAccents = LocalThemeAccents.current
    
    Box(
        modifier = Modifier.background(themeAccents.primary)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = themeAccents.success
        )
    }
}
```

Available colors:
- `primary` / `primaryVariant`
- `secondary`
- `success` / `warning` / `error` / `info`
- `background` / `surface` / `surfaceVariant`
- `onPrimary` / `onSecondary` / `onSurface` etc.
- Gradients: `primaryGradient`, `backgroundGradient`, `cardGradient`

---

## 📐 Using Global Spacing

```kotlin
import com.example.adere.presentation.components.AppSpacing

Column(
    modifier = Modifier.padding(AppSpacing.medium),
    verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
) {
    // Content with consistent spacing
}
```

Available spacing:
- `extraSmall` (4dp)
- `small` (8dp)
- `medium` (16dp)
- `large` (24dp)
- `extraLarge` (32dp)
- `huge` (48dp)

---

## 🔘 Using Global Radius

```kotlin
import com.example.adere.presentation.components.AppRadius

Card(
    shape = RoundedCornerShape(AppRadius.large)
) {
    // Card content
}
```

Available radius:
- `small` (8dp)
- `medium` (12dp)
- `large` (16dp)
- `extraLarge` (20dp)
- `huge` (24dp)

---

## ✨ Adding Animations

### Entrance Animation
```kotlin
var isVisible by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
    delay(100) // stagger delay
    isVisible = true
}

AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn() + scaleIn() + slideInVertically()
) {
    // Your content
}
```

### Press Animation
```kotlin
var isPressed by remember { mutableStateOf(false) }

val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.96f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)

Box(
    modifier = Modifier
        .scale(scale)
        .clickable { 
            isPressed = true
            // handle click
        }
)
```

---

## 🔊 Adding Haptic Feedback

```kotlin
val hapticFeedback = LocalHapticFeedback.current

Button(
    onClick = {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        // handle click
    }
) {
    Text("Click Me")
}
```

Feedback types:
- `TextHandleMove` - Light tap
- `LongPress` - Medium haptic
- `KeyboardPress` - Very light

---

## 📱 Responsive Design

### Adaptive Layout
```kotlin
val configuration = LocalConfiguration.current
val isTablet = configuration.screenWidthDp >= 600

if (isTablet) {
    // Two-column layout
} else {
    // Single column layout
}
```

### Dynamic Spacing
```kotlin
val horizontalPadding = if (isTablet) 
    AppSpacing.extraLarge 
else 
    AppSpacing.medium
```

---

## ♿ Accessibility

### Content Descriptions
```kotlin
Icon(
    imageVector = Icons.Default.Delete,
    contentDescription = "Delete item", // Always provide!
    modifier = Modifier.clickable { /* action */ }
)
```

### Minimum Touch Target
```kotlin
IconButton(
    onClick = { /* action */ },
    modifier = Modifier.size(48.dp) // Minimum touch target
) {
    Icon(...)
}
```

### Semantic Labels
```kotlin
Text(
    text = "Important message",
    modifier = Modifier.semantics {
        contentDescription = "Important: Your message here"
        heading()
    }
)
```

---

## 🧪 Testing

### Test Tags
```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier.testTag("submit_button")
) {
    Text("Submit")
}
```

### Verify in Tests
```kotlin
composeTestRule.onNodeWithTag("submit_button").performClick()
```

---

## 🐛 Common Issues & Solutions

### Issue: Animations stuttering
**Solution:** Reduce animation count or use hardware acceleration
```kotlin
modifier = Modifier.graphicsLayer { /* enable hardware */ }
```

### Issue: Colors not updating
**Solution:** Ensure you're using `LocalThemeAccents.current` not static colors

### Issue: Haptic not working
**Solution:** Check device supports haptics and permissions

### Issue: Empty states not showing
**Solution:** Verify your conditional logic:
```kotlin
if (items.isEmpty()) {
    EmptyVaultState(...)
} else {
    EnhancedVaultList(items = items, ...)
}
```

---

## 📚 Additional Resources

### Component Documentation
See `UI_ENHANCEMENTS_SUMMARY.md` for complete component documentation

### Design System
- Colors: See `EnhancedThemeSystem.kt`
- Spacing: See `UIUtilities.kt` → `AppSpacing`
- Typography: See `UIUtilities.kt` → `TextStyles`

### Animation Examples
All enhanced components contain animation examples you can study and adapt.

---

## 🎯 Next Steps

1. **Review** existing screens that need enhancement
2. **Choose** components that fit your needs
3. **Import** and integrate components
4. **Test** on multiple devices
5. **Iterate** based on user feedback

---

## 💡 Pro Tips

1. **Stagger animations** for lists (50-100ms per item)
2. **Use spring physics** for natural motion
3. **Always add haptic feedback** to interactive elements
4. **Provide content descriptions** for accessibility
5. **Test with large text sizes** enabled
6. **Use consistent spacing** from `AppSpacing`
7. **Leverage theme colors** instead of hardcoded values
8. **Add loading states** for async operations
9. **Handle errors gracefully** with error states
10. **Test on low-end devices** for performance

---

## 📞 Support

For questions or issues:
1. Check `UI_ENHANCEMENTS_SUMMARY.md`
2. Review component source code (well-documented)
3. Test with the demo implementations
4. Check Material Design 3 guidelines

---

**Happy coding! 🚀**

*Last updated: 2026-10-04*
