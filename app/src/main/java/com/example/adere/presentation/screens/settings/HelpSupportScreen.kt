package com.example.adere.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader

@Composable
fun HelpSupportScreen(
    onViewFAQ: () -> Unit,
    onViewSecurityGuide: () -> Unit,
    onContactSupport: () -> Unit,
    onReportBug: () -> Unit,
    onRateApp: () -> Unit,
    onJoinCommunity: () -> Unit,
    onBackClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Help & Support",
                subtitle = "FAQ · Security guide · Contact support",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Documentation & guides ────────────────────────────────────────
            SettingsSectionTitle("Documentation & Guides")

            SettingsItemCard(
                icon = Icons.Default.QuestionMark,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Frequently Asked Questions",
                subtitle = "Common questions about vault setup and security features",
                onClick = onViewFAQ,
                testTag = "help_faq_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Security,
                iconTint = Color(0xFF10B981),
                iconBg = Color(0xFF10B981).copy(alpha = 0.1f),
                title = "Zero-Knowledge Security Guide",
                subtitle = "Understanding AES-256 encryption and master key security",
                onClick = onViewSecurityGuide,
                testTag = "help_security_guide_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Help,
                iconTint = Color(0xFF6366F1),
                iconBg = Color(0xFF6366F1).copy(alpha = 0.1f),
                title = "Getting Started Tutorial",
                subtitle = "Step-by-step setup guide for new vault users",
                onClick = { /* Navigate to tutorial */ },
                testTag = "help_tutorial_item"
            )

            // ── Support & feedback ────────────────────────────────────────────
            SettingsSectionTitle("Support & Feedback")

            SettingsItemCard(
                icon = Icons.Default.ContactSupport,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Contact Support",
                subtitle = "Get help with technical issues or account problems",
                onClick = onContactSupport,
                testTag = "help_contact_item"
            )

            SettingsItemCard(
                icon = Icons.Default.BugReport,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFF59E0B).copy(alpha = 0.1f),
                title = "Report a Bug",
                subtitle = "Found an issue? Help us improve ADERE by reporting it",
                onClick = onReportBug,
                testTag = "help_bug_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Email,
                iconTint = Color(0xFF0EA5E9),
                iconBg = Color(0xFF0EA5E9).copy(alpha = 0.1f),
                title = "Feature Request",
                subtitle = "Suggest new features or improvements for future updates",
                onClick = { /* Feature request */ },
                testTag = "help_feature_item"
            )

            // ── Community & rating ────────────────────────────────────────────
            SettingsSectionTitle("Community & Rating")

            SettingsItemCard(
                icon = Icons.Default.Star,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFF59E0B).copy(alpha = 0.1f),
                title = "Rate ADERE on Play Store",
                subtitle = "Enjoying the app? Leave a review to support development",
                onClick = onRateApp,
                testTag = "help_rate_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Forum,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Join Community Forum",
                subtitle = "Connect with other users and discuss security best practices",
                onClick = onJoinCommunity,
                testTag = "help_community_item"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}