package com.example.adere.presentation.components

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.utilities.ClipboardSecurityHelper
import com.example.adere.core.utilities.HapticFeedbackHelper
import com.example.ui.theme.Shapes

/**
 * Interactive Seed Phrase Box-by-Box Editor and Viewer Component
 * Supports 12, 18, and 24-word BIP-39 mnemonic recovery phrases.
 * Offers one-tap auto-paste, box-by-box typing, and privacy masking.
 */
@Composable
fun InteractiveSeedPhraseInput(
    seedPhrase: String,
    onSeedPhraseChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var isMasked by remember { mutableStateOf(true) }
    var targetWordCount by remember { mutableIntStateOf(12) }

    // Parse current seed phrase into individual words list
    val wordsList = remember(seedPhrase) {
        if (seedPhrase.isBlank()) {
            emptyList()
        } else {
            seedPhrase.trim().split(Regex("[\\s,]+")).filter { it.isNotBlank() }
        }
    }

    // Determine default mode based on current words length
    LaunchedEffect(seedPhrase) {
        if (wordsList.size in listOf(12, 18, 24)) {
            targetWordCount = wordsList.size
        }
    }

    fun updateWordAtIndex(index: Int, newWord: String) {
        val currentWords = wordsList.toMutableList()
        // Ensure list is padded up to index
        while (currentWords.size <= index) {
            currentWords.add("")
        }

        // Check if user pasted a multi-word string into a single box
        val trimmed = newWord.trim()
        if (trimmed.contains(" ") || trimmed.contains("\n") || trimmed.contains(",")) {
            val pastedWords = trimmed.split(Regex("[\\s,]+")).filter { it.isNotBlank() }
            if (pastedWords.size >= 12 && pastedWords.all { w -> w.all { c -> c.isLetter() } }) {
                var writeIdx = index
                for (w in pastedWords) {
                    val cleanWord = w.filter { c -> c.isLetter() }.lowercase()
                    if (writeIdx < currentWords.size) {
                        currentWords[writeIdx] = cleanWord
                    } else {
                        currentWords.add(cleanWord)
                    }
                    writeIdx++
                }
            } else {
                currentWords[index] = trimmed.filter { c -> c.isLetter() }.lowercase()
            }
        } else {
            currentWords[index] = trimmed.filter { c -> c.isLetter() }.lowercase()
        }

        val resultPhrase = currentWords.filter { it.isNotBlank() }.joinToString(" ")
        onSeedPhraseChange(resultPhrase)
    }

    fun handleAutoPaste() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()?.trim()

        if (clipText.isNullOrBlank()) {
            Toast.makeText(context, "Clipboard is empty or contains no text", Toast.LENGTH_SHORT).show()
            return
        }

        val extracted = clipText.split(Regex("[\\s,]+")).filter { it.isNotBlank() }
        val isAllWordsAlphabetic = extracted.isNotEmpty() && extracted.all { w -> w.all { c -> c.isLetter() } }

        if (extracted.size in listOf(12, 18, 24) && isAllWordsAlphabetic) {
            val cleanPhrase = extracted.joinToString(" ") { w -> w.filter { c -> c.isLetter() }.lowercase() }
            onSeedPhraseChange(cleanPhrase)
            targetWordCount = extracted.size
            HapticFeedbackHelper.confirm(context)
            Toast.makeText(
                context,
                "Populated ${extracted.size}-word seed phrase from clipboard!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (extracted.size >= 12 && isAllWordsAlphabetic) {
            val cleanPhrase = extracted.joinToString(" ") { w -> w.filter { c -> c.isLetter() }.lowercase() }
            onSeedPhraseChange(cleanPhrase)
            HapticFeedbackHelper.confirm(context)
            Toast.makeText(
                context,
                "Populated ${extracted.size} mnemonic words from clipboard!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "⚠️ Clipboard text is not a valid seed phrase (requires 12, 18, or 24 words). Password or non-mnemonic text ignored.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val isValidLength = wordsList.size in listOf(12, 18, 24)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(
                if (isValidLength) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Header & Quick Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = if (isValidLength) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Recovery Seed Phrase",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hardware-grade BIP-39 mnemonic phrase backup",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    )
                }

                Surface(
                    modifier = Modifier.requiredWidthIn(min = 90.dp),
                    shape = Shapes.small,
                    color = if (isValidLength) Color(0xFF10B981).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isValidLength) Icons.Default.CheckCircle else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isValidLength) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${wordsList.size} / $targetWordCount Words",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isValidLength) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }

            // Word Count Target Selector Segmented Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), Shapes.medium)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(12, 18, 24).forEach { count ->
                    val isSelected = targetWordCount == count
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(Shapes.small)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable {
                                HapticFeedbackHelper.click(context)
                                targetWordCount = count
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$count Words",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }

            // Hero One-Tap Paste & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { handleAutoPaste() },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = Shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Paste Seed Phrase",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(
                    onClick = { isMasked = !isMasked },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(Shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isMasked) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isMasked) "Reveal words" else "Mask words",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (wordsList.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onSeedPhraseChange("")
                            HapticFeedbackHelper.click(context)
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(Shapes.medium)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear all words",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Numbered Box-by-Box Word Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val gridTotal = maxOf(targetWordCount, wordsList.size)
                val rows = (0 until gridTotal).chunked(2)

                for (rowIndices in rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in rowIndices) {
                            val wordValue = wordsList.getOrElse(i) { "" }
                            SeedWordBox(
                                index = i + 1,
                                word = wordValue,
                                isMasked = isMasked,
                                onWordChange = { updateWordAtIndex(i, it) },
                                onNextFocus = { focusManager.moveFocus(FocusDirection.Next) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowIndices.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Numbered Seed Word Box Card
 */
@Composable
private fun SeedWordBox(
    index: Int,
    word: String,
    isMasked: Boolean,
    onWordChange: (String) -> Unit,
    onNextFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(50.dp),
        shape = Shapes.medium,
        color = if (word.isNotBlank()) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(
            1.dp,
            if (word.isNotBlank()) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word Index Badge (#1, #2, ...)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (word.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (word.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Word Input Field
            BasicTextField(
                value = word,
                onValueChange = { input ->
                    if (input.endsWith(" ") || input.endsWith("\n")) {
                        onWordChange(input.trim())
                        onNextFocus()
                    } else {
                        onWordChange(input)
                    }
                },
                singleLine = true,
                visualTransformation = if (isMasked && word.isNotBlank()) PasswordVisualTransformation() else VisualTransformation.None,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onNextFocus() }
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (word.isBlank()) {
                            Text(
                                text = "word $index",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Display Component for Saved Seed Phrase (Used in ItemDetailScreen)
 */
@Composable
fun InteractiveSeedPhraseDisplay(
    seedPhrase: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMasked by remember { mutableStateOf(true) }

    val wordsList = remember(seedPhrase) {
        seedPhrase.trim().split(Regex("[\\s,]+")).filter { it.isNotBlank() }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFF10B981).copy(alpha = 0.4f)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Crypto Recovery Seed Phrase",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${wordsList.size}-Word Mnemonic Phrase",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { isMasked = !isMasked },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (isMasked) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Seed Phrase",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Box-by-Box Display Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val rows = wordsList.indices.chunked(2)
                for (rowIndices in rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in rowIndices) {
                            val word = wordsList[i]
                            Surface(
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = Shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${i + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isMasked) "••••••" else word,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (rowIndices.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
