package com.example.adere.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.adere.core.utilities.HapticFeedbackHelper
import com.example.adere.domain.model.VaultCategory
import com.example.adere.presentation.components.AdereTopBar
import com.example.adere.presentation.components.BrandIconHelper
import com.example.adere.presentation.components.BrandInfo
import com.example.ui.theme.CategoryAppsYellow
import com.example.ui.theme.CategoryCardTeal
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.Shapes
import com.example.ui.theme.TotalSecurityPrimary
import com.example.ui.theme.TotalSecurityPrimaryContainer

data class CategoryOption(
    val category: VaultCategory,
    val title: String,
    val subtitle: String,
    val examples: String,
    val emoji: String,
    val iconColor: Color,
    val iconBgColor: Color,
    val featureTags: List<String>
)

data class QuickTemplateOption(
    val category: VaultCategory,
    val title: String,
    val description: String,
    val badge: String,
    val emoji: String,
    val accentColor: Color,
    val gradientColors: List<Color>
)

private val CleanBg: Color
    @Composable get() = MaterialTheme.colorScheme.background
private val CleanSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surface
private val CleanSurfaceVariant: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant
private val TextDarkMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val TextDarkPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
private val TextDarkSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryAddScreen(
    onSelectCategory: (VaultCategory) -> Unit,
    onSelectBrand: (BrandInfo) -> Unit,
    onLockClick: () -> Unit
) {
    val context = LocalContext.current
    var searchFilter by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }
    val tabs = remember { listOf("All", "Visa Cards", "Logins", "Crypto", "Banking", "Identity", "Wi-Fi", "Notes & 2FA") }

    val quickTemplates = remember {
        listOf(
            QuickTemplateOption(
                category = VaultCategory.BANKING,
                title = "Visa & Payment Card",
                description = "3D EMV Metallic Card & CVV",
                badge = "VISA • 3D",
                emoji = "💳",
                accentColor = Color(0xFF0284C7),
                gradientColors = listOf(Color(0xFF0F172A), Color(0xFF0284C7))
            ),
            QuickTemplateOption(
                category = VaultCategory.EMAIL,
                title = "Gmail / Google Account",
                description = "App passwords & recovery mail",
                badge = "OAUTH 2.0",
                emoji = "✉️",
                accentColor = Color(0xFFEA4335),
                gradientColors = listOf(Color(0xFFEA4335), Color(0xFFC5221F))
            ),
            QuickTemplateOption(
                category = VaultCategory.CRYPTO,
                title = "Web3 & Seed Phrase",
                description = "12/24-word recovery matrix",
                badge = "BIP-39",
                emoji = "₿",
                accentColor = Color(0xFFD97706),
                gradientColors = listOf(Color(0xFFF7931A), Color(0xFFD97706))
            ),
            QuickTemplateOption(
                category = VaultCategory.WIFI,
                title = "WPA3 Wi-Fi Network",
                description = "WPA3 key & offline QR code",
                badge = "OFFLINE QR",
                emoji = "📶",
                accentColor = Color(0xFF06B6D4),
                gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0284C7))
            ),
            QuickTemplateOption(
                category = VaultCategory.IDENTITY,
                title = "Passport & National ID",
                description = "Passports, SSN & Driver's License",
                badge = "SECURE ID",
                emoji = "🪪",
                accentColor = Color(0xFF10B981),
                gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669))
            ),
            QuickTemplateOption(
                category = VaultCategory.NOTES,
                title = "Hardware Encrypted Note",
                description = "Multi-line AES-256 vault memos",
                badge = "AES-256",
                emoji = "📝",
                accentColor = Color(0xFFF43F5E),
                gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
            ),
            QuickTemplateOption(
                category = VaultCategory.TOTP_2FA,
                title = "2FA Authenticator",
                description = "Time-based rotating OTP seed",
                badge = "RFC 6238",
                emoji = "🔢",
                accentColor = Color(0xFFA855F7),
                gradientColors = listOf(Color(0xFFA855F7), Color(0xFF7C3AED))
            )
        )
    }

    val categories = remember {
        listOf(
            CategoryOption(
                category = VaultCategory.BANKING,
                title = "Banking & Visa Cards",
                subtitle = "Credit cards, debit cards, bank portals, PINs",
                examples = "Visa Platinum, Mastercard, Chase, Revolut, IBAN",
                emoji = "🏦",
                iconColor = Color(0xFF0284C7),
                iconBgColor = Color(0xFFE0F2FE),
                featureTags = listOf("3D Card Preview", "Tap-to-Flip", "EMV Chip")
            ),
            CategoryOption(
                category = VaultCategory.EMAIL,
                title = "Email Accounts",
                subtitle = "Gmail, Google Workspace, Outlook, ProtonMail",
                examples = "Google Mail, App passwords, backup recovery codes",
                emoji = "✉️",
                iconColor = Color(0xFFEA4335),
                iconBgColor = Color(0xFFFEE2E2),
                featureTags = listOf("OAuth Ready", "App Passwords", "Gmail")
            ),
            CategoryOption(
                category = VaultCategory.SOCIAL,
                title = "Social Media",
                subtitle = "Facebook, Instagram, X, TikTok, Telegram",
                examples = "Social logins, handles, recovery codes",
                emoji = "🔐",
                iconColor = TotalSecurityPrimary,
                iconBgColor = TotalSecurityPrimaryContainer,
                featureTags = listOf("Auto-Mask", "Entropy Meter", "2FA Backup")
            ),
            CategoryOption(
                category = VaultCategory.CRYPTO,
                title = "Crypto Vault",
                subtitle = "MetaMask, Bitcoin, Ethereum, Seed Phrases",
                examples = "12/24-word recovery phrases, private keys, wallet addresses",
                emoji = "₿",
                iconColor = Color(0xFFD97706),
                iconBgColor = Color(0xFFFEF3C7),
                featureTags = listOf("12/24-Word Seed", "BIP-39", "Air-Gapped")
            ),
            CategoryOption(
                category = VaultCategory.WEBSITE,
                title = "Web Services & Apps",
                subtitle = "Figma, Spotify, Netflix, GitHub, Apple ID",
                examples = "SaaS accounts, e-commerce, cloud subscriptions",
                emoji = "🌐",
                iconColor = Color(0xFFA855F7),
                iconBgColor = Color(0xFFF3E8FF),
                featureTags = listOf("Favicon Match", "URL Autofill")
            ),
            CategoryOption(
                category = VaultCategory.WIFI,
                title = "Wi-Fi Networks",
                subtitle = "Router keys, home & office Wi-Fi",
                examples = "Network passwords, WPA3 credentials, guest access",
                emoji = "📶",
                iconColor = Color(0xFF06B6D4),
                iconBgColor = Color(0xFFCFFAFE),
                featureTags = listOf("Offline QR Code", "WPA3-Personal")
            ),
            CategoryOption(
                category = VaultCategory.IDENTITY,
                title = "Personal Identity",
                subtitle = "Passports, IDs & official documents",
                examples = "Driver's license, SSN, National ID numbers",
                emoji = "🪪",
                iconColor = Color(0xFF10B981),
                iconBgColor = Color(0xFFECFDF5),
                featureTags = listOf("Live ID Card", "Masked ID")
            ),
            CategoryOption(
                category = VaultCategory.NOTES,
                title = "Secure Notes",
                subtitle = "Encrypted private memos & secrets",
                examples = "Safe combinations, confidential diaries, codes",
                emoji = "📝",
                iconColor = Color(0xFFF43F5E),
                iconBgColor = Color(0xFFFFE4E6),
                featureTags = listOf("Multi-Line", "Hardware Encrypted")
            ),
            CategoryOption(
                category = VaultCategory.TOTP_2FA,
                title = "2FA Authenticator",
                subtitle = "Time-based OTP security keys",
                examples = "Google Authenticator seeds, backup codes",
                emoji = "🔢",
                iconColor = CategoryAppsYellow,
                iconBgColor = Color(0xFFFEF3C7),
                featureTags = listOf("RFC 6238", "Rotating 6-Digit", "Live Timer")
            ),
            CategoryOption(
                category = VaultCategory.RECOVERY,
                title = "Recovery Codes",
                subtitle = "Emergency backup codes for accounts",
                examples = "One-time account recovery backup kits",
                emoji = "🛡️",
                iconColor = HealthRiskRed,
                iconBgColor = Color(0xFFFEE2E2),
                featureTags = listOf("One-Time Codes", "Tag Matrix")
            ),
            CategoryOption(
                category = VaultCategory.WALLETS,
                title = "Wallets",
                subtitle = "Phantom, MetaMask, Keplr, Trust Wallet",
                examples = "Digital wallets, recovery phrases, private keys",
                emoji = "👛",
                iconColor = Color(0xFF7C3AED),
                iconBgColor = Color(0xFFF3E8FF),
                featureTags = listOf("Seed Phrase", "Hardware Ready")
            ),
            CategoryOption(
                category = VaultCategory.WORK,
                title = "Work & Enterprise",
                subtitle = "Slack, Jira, VPN, SSO, Internal Portals",
                examples = "Corporate email, single sign-on, workplace tools",
                emoji = "💼",
                iconColor = Color(0xFF0284C7),
                iconBgColor = Color(0xFFE0F2FE),
                featureTags = listOf("SSO Ready", "Company Accounts")
            ),
            CategoryOption(
                category = VaultCategory.SHOPPING,
                title = "Shopping & Retail",
                subtitle = "Amazon, eBay, Shopify, AliExpress",
                examples = "E-commerce stores, checkout accounts, order tracking",
                emoji = "🛍️",
                iconColor = Color(0xFFE11D48),
                iconBgColor = Color(0xFFFFE4E6),
                featureTags = listOf("Retail Profiles", "Saved Cards")
            ),
            CategoryOption(
                category = VaultCategory.OTHER,
                title = "Other Credentials",
                subtitle = "Custom encrypted fields & secrets",
                examples = "Server SSH keys, database logins, API keys",
                emoji = "🔑",
                iconColor = Color(0xFF64748B),
                iconBgColor = Color(0xFFF1F5F9),
                featureTags = listOf("Key-Value Pairs", "Custom Secret")
            )
        )
    }

    val filteredCategories = remember(searchFilter, selectedTab, categories) {
        val byTab = when (selectedTab) {
            "Visa Cards", "Banking" -> categories.filter { it.category == VaultCategory.BANKING }
            "Logins" -> categories.filter { it.category in listOf(VaultCategory.SOCIAL, VaultCategory.EMAIL, VaultCategory.WEBSITE, VaultCategory.WORK, VaultCategory.SHOPPING) }
            "Crypto" -> categories.filter { it.category in listOf(VaultCategory.CRYPTO, VaultCategory.WALLETS) }
            "Identity" -> categories.filter { it.category == VaultCategory.IDENTITY }
            "Wi-Fi" -> categories.filter { it.category == VaultCategory.WIFI }
            "Notes & 2FA" -> categories.filter { it.category in listOf(VaultCategory.NOTES, VaultCategory.TOTP_2FA, VaultCategory.RECOVERY, VaultCategory.OTHER) }
            else -> categories
        }

        if (searchFilter.isBlank()) {
            byTab
        } else {
            byTab.filter {
                it.title.contains(searchFilter, ignoreCase = true) ||
                it.subtitle.contains(searchFilter, ignoreCase = true) ||
                it.examples.contains(searchFilter, ignoreCase = true) ||
                it.category.name.contains(searchFilter, ignoreCase = true)
            }
        }
    }

    // Filter brand chips if user types a search query
    val matchingCryptoBrands = remember(searchFilter) {
        if (searchFilter.isBlank()) BrandIconHelper.POPULAR_CRYPTO_BRANDS
        else BrandIconHelper.POPULAR_CRYPTO_BRANDS.filter { it.name.contains(searchFilter, ignoreCase = true) }
    }

    val matchingEmailBrands = remember(searchFilter) {
        if (searchFilter.isBlank()) BrandIconHelper.POPULAR_EMAIL_BRANDS
        else BrandIconHelper.POPULAR_EMAIL_BRANDS.filter { it.name.contains(searchFilter, ignoreCase = true) }
    }

    val matchingBankBrands = remember(searchFilter) {
        if (searchFilter.isBlank()) BrandIconHelper.POPULAR_BANK_BRANDS
        else BrandIconHelper.POPULAR_BANK_BRANDS.filter { it.name.contains(searchFilter, ignoreCase = true) }
    }

    val matchingSocialBrands = remember(searchFilter) {
        if (searchFilter.isBlank()) BrandIconHelper.POPULAR_SOCIAL_BRANDS
        else BrandIconHelper.POPULAR_SOCIAL_BRANDS.filter { it.name.contains(searchFilter, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            AdereTopBar(
                title = "Add to Vault",
                subtitle = "Hardware-encrypted credential wizard",
                onLockClick = onLockClick
            )
        },
        containerColor = CleanBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security Status Hero Badge
            item {
                Surface(
                    shape = Shapes.medium,
                    color = CleanSurface,
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TotalSecurityPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TotalSecurityPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Zero-Knowledge Protection",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            )
                            Text(
                                text = "Hardware AES-256-GCM encryption on device",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDarkSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Surface(
                            shape = Shapes.medium,
                            color = Color(0xFFECFDF5)
                        ) {
                            Text(
                                text = "SECURE",
                                color = Color(0xFF059669),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar with Clear Button
            item {
                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = {
                        Text(
                            "Search Visa cards, Gmail, crypto, Wi-Fi...",
                            color = TextDarkMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (searchFilter.isNotBlank()) TotalSecurityPrimary else TextDarkMuted
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = searchFilter.isNotBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(onClick = {
                                searchFilter = ""
                                HapticFeedbackHelper.tick(context)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextDarkSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = Shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CleanSurface,
                        unfocusedContainerColor = CleanSurface,
                        focusedBorderColor = TotalSecurityPrimary,
                        unfocusedBorderColor = CleanBorder,
                        focusedTextColor = TextDarkPrimary,
                        unfocusedTextColor = TextDarkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_category_search_input")
                )
            }

            // Quick Category Filter Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs) { tab ->
                        val isSelected = selectedTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                HapticFeedbackHelper.tick(context)
                                selectedTab = tab
                            },
                            label = {
                                Text(
                                    text = tab,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = Shapes.medium,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TotalSecurityPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = CleanSurface,
                                labelColor = TextDarkSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) TotalSecurityPrimary else CleanBorder
                            )
                        )
                    }
                }
            }



            // If user is not searching and has "All" selected, show Quick Templates Hero Carousel
            if (searchFilter.isBlank() && selectedTab == "All") {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "QUICK TEMPLATES",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            )
                            Text(
                                text = "Popular",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(quickTemplates) { template ->
                                QuickTemplateCard(
                                    template = template,
                                    onClick = {
                                        HapticFeedbackHelper.click(context)
                                        onSelectCategory(template.category)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Curated Web3 & Crypto Section
            if (selectedTab in listOf("All", "Crypto") && matchingCryptoBrands.isNotEmpty()) {
                item {
                    Text(
                        text = "WEB3 WALLETS & SEED PHRASES (${matchingCryptoBrands.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Cold Storage & DeFi",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkPrimary
                                        )
                                    )
                                    Text(
                                        text = "Pre-configured for 12/24-word seeds and private keys",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            // Featured Professional Seed Phrase Hero Card
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(Shapes.medium)
                                    .clickable {
                                        HapticFeedbackHelper.click(context)
                                        val seedBrand = BrandInfo(
                                            id = "seed_phrase_vault",
                                            name = "12 / 24-Word Seed Phrase",
                                            defaultUrl = "https://bip39.org",
                                            category = VaultCategory.CRYPTO,
                                            iconRes = R.drawable.ic_brand_seedphrase,
                                            brandColor = Color(0xFF10B981)
                                        )
                                        onSelectBrand(seedBrand)
                                    },
                                shape = Shapes.medium,
                                color = Color(0xFF10B981).copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_brand_seedphrase),
                                            contentDescription = "Seed Phrase Vault",
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "12 / 24-Word Seed Phrase Vault",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextDarkPrimary,
                                                fontSize = 13.5.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Hardware-grade BIP-39 mnemonic & private key backup",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextDarkSecondary,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Surface(
                                                shape = Shapes.small,
                                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "BIP-39",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color(0xFF047857),
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 9.5.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Surface(
                                                shape = Shapes.small,
                                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "AIR-GAPPED",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color(0xFF047857),
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 9.5.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                matchingCryptoBrands.chunked(2).forEach { rowBrands ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        for (brand in rowBrands) {
                                            BrandQuickChip(
                                                brand = brand,
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    HapticFeedbackHelper.click(context)
                                                    onSelectBrand(brand)
                                                }
                                            )
                                        }
                                        if (rowBrands.size < 2) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedTab in listOf("All", "Visa Cards", "Banking") && matchingBankBrands.isNotEmpty()) {
                item {
                    Text(
                        text = "ETHIOPIAN BANKS & FINTECH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        matchingBankBrands.chunked(2).forEach { rowBrands ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (brand in rowBrands) {
                                    BrandQuickChip(
                                        brand = brand,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            HapticFeedbackHelper.click(context)
                                            onSelectBrand(brand)
                                        }
                                    )
                                }
                                if (rowBrands.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            if (selectedTab in listOf("All", "Logins") && matchingEmailBrands.isNotEmpty()) {
                item {
                    Text(
                        text = "GMAIL & CLOUD MAILBOX ACCOUNTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFEA4335),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Google Mail & Cloud Logins",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                matchingEmailBrands.chunked(2).forEach { rowBrands ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        for (brand in rowBrands) {
                                            BrandQuickChip(
                                                brand = brand,
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    HapticFeedbackHelper.click(context)
                                                    onSelectBrand(brand)
                                                }
                                            )
                                        }
                                        if (rowBrands.size < 2) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedTab in listOf("All", "Logins") && matchingSocialBrands.isNotEmpty()) {
                item {
                    Text(
                        text = "POPULAR SOCIAL & MEDIA ACCOUNTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TotalSecurityPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "One-Tap Add Social Accounts",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            )
                            Text(
                                text = "Instagram, X, TikTok, Telegram, WhatsApp & more",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                matchingSocialBrands.chunked(2).forEach { rowBrands ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        for (brand in rowBrands) {
                                            BrandQuickChip(
                                                brand = brand,
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    HapticFeedbackHelper.click(context)
                                                    onSelectBrand(brand)
                                                }
                                            )
                                        }
                                        if (rowBrands.size < 2) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section Label for Full Categories List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALL VAULT CATEGORIES (${filteredCategories.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Choose template",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // List of Category Cards
            items(filteredCategories, key = { it.category.name }) { option ->
                CategoryCardRow(
                    option = option,
                    onClick = {
                        HapticFeedbackHelper.click(context)
                        onSelectCategory(option.category)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Photorealistic 3D Visa & Payment Card Hero Banner


@Composable
private fun QuickTemplateCard(
    template: QuickTemplateOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(172.dp)
            .height(138.dp)
            .shadow(3.dp, Shapes.medium)
            .clip(Shapes.medium)
            .clickable(onClick = onClick)
            .testTag("quick_template_${template.category.name.lowercase()}"),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CleanSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(template.gradientColors)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = template.emoji,
                        fontSize = 20.sp
                    )
                }

                Surface(
                    shape = Shapes.medium,
                    color = template.accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = template.badge,
                        color = template.accentColor,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Column {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDarkSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BrandQuickChip(
    brand: BrandInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(86.dp)
            .shadow(1.dp, Shapes.medium)
            .clip(Shapes.medium)
            .clickable(onClick = onClick)
            .testTag("quick_brand_${brand.id}"),
        colors = CardDefaults.cardColors(containerColor = CleanSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = brand.iconRes),
                contentDescription = brand.name,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = brand.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDarkPrimary,
                    fontSize = 11.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryCardRow(
    option: CategoryOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, Shapes.medium)
            .clip(Shapes.medium)
            .clickable(onClick = onClick)
            .testTag("category_card_${option.category.name.lowercase()}"),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CleanSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(Shapes.medium)
                    .background(option.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDarkPrimary,
                            fontSize = 16.5.sp
                        )
                    )
                }

                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDarkSecondary,
                        fontSize = 12.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Feature Tags Pills Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (tag in option.featureTags.take(3)) {
                        Surface(
                            shape = Shapes.medium,
                            color = CleanSurfaceVariant
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextDarkSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Select",
                tint = TextDarkMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
