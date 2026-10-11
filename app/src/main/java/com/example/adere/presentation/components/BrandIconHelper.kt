package com.example.adere.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.adere.domain.model.VaultCategory

data class BrandInfo(
    val id: String,
    val name: String,
    val defaultUrl: String,
    val category: VaultCategory,
    @param:DrawableRes val iconRes: Int,
    val brandColor: Color,
)

object BrandIconHelper {

    val POPULAR_BANK_BRANDS = listOf(
        BrandInfo("cbe", "Commercial Bank of Ethiopia", "https://combanketh.et", VaultCategory.BANKING, R.drawable.ic_brand_cbe, Color(0xFF581C87)),
        BrandInfo("cbebirr", "CBE Birr", "https://combanketh.et", VaultCategory.BANKING, R.drawable.ic_brand_cbebirr, Color(0xFF581C87)),
        BrandInfo("telebirr", "Telebirr", "https://telebirr.et", VaultCategory.BANKING, R.drawable.ic_brand_telebirr, Color(0xFF0284C7)),
        BrandInfo("chapa", "Chapa Payment Gateway", "https://chapa.co", VaultCategory.BANKING, R.drawable.ic_brand_chapa, Color(0xFF059669)),
        BrandInfo("santimpay", "SantimPay", "https://santimpay.com", VaultCategory.BANKING, R.drawable.ic_brand_santimpay, Color(0xFF0F172A)),
        BrandInfo("arifpay", "ArifPay", "https://arifpay.net", VaultCategory.BANKING, R.drawable.ic_brand_arifpay, Color(0xFF1D4ED8)),
        BrandInfo("kacha", "Kacha Digital Financial", "https://kacha.et", VaultCategory.BANKING, R.drawable.ic_brand_kacha, Color(0xFF7C3AED)),
        BrandInfo("amole", "Amole Digital Wallet", "https://dashenbanksc.com", VaultCategory.BANKING, R.drawable.ic_brand_amole, Color(0xFF047857)),
        BrandInfo("ebirr", "E-Birr Mobile Money", "https://e-birr.com", VaultCategory.BANKING, R.drawable.ic_brand_ebirr, Color(0xFF0284C7)),
        BrandInfo("yenepay", "YenePay", "https://yenepay.com", VaultCategory.BANKING, R.drawable.ic_brand_yenepay, Color(0xFFDC2626)),
        BrandInfo("dashen", "Dashen Bank", "https://dashenbanksc.com", VaultCategory.BANKING, R.drawable.ic_brand_dashen, Color(0xFF047857)),
        BrandInfo("awash", "Awash Bank", "https://awashbank.com", VaultCategory.BANKING, R.drawable.ic_brand_awash, Color(0xFF0284C7)),
        BrandInfo("abyssinia", "Bank of Abyssinia", "https://bankofabyssinia.com", VaultCategory.BANKING, R.drawable.ic_brand_abyssinia, Color(0xFFD97706)),
        BrandInfo("coop", "Cooperative Bank of Oromia", "https://coopbankoromia.com.et", VaultCategory.BANKING, R.drawable.ic_brand_coop, Color(0xFF0284C7)),
        BrandInfo("hibret", "Hibret Bank", "https://hibretbank.com.et", VaultCategory.BANKING, R.drawable.ic_brand_hibret, Color(0xFFE11D48)),
        BrandInfo("zemen", "Zemen Bank", "https://zemenbank.com", VaultCategory.BANKING, R.drawable.ic_brand_zemen, Color(0xFF0F172A)),
        BrandInfo("lion", "Lion International Bank (Anbesa)", "https://anbesabank.com", VaultCategory.BANKING, R.drawable.ic_brand_lion, Color(0xFFF59E0B)),
        BrandInfo("addis", "Addis International Bank", "https://addisbanksc.com", VaultCategory.BANKING, R.drawable.ic_brand_addis, Color(0xFF1D4ED8)),
        BrandInfo("ahadu", "Ahadu Bank", "https://ahadubank.com", VaultCategory.BANKING, R.drawable.ic_brand_ahadu, Color(0xFFF97316)),
        BrandInfo("gadaa", "Gadaa Bank", "https://gadaabank.com.et", VaultCategory.BANKING, R.drawable.ic_brand_gadaa, Color(0xFF000000)),
        BrandInfo("rammis", "Rammis Bank", "https://rammisbank.com", VaultCategory.BANKING, R.drawable.ic_brand_rammis, Color(0xFF047857)),
        BrandInfo("wegagen", "Wegagen Bank", "https://wegagen.com", VaultCategory.BANKING, R.drawable.ic_brand_wegagen, Color(0xFFEA580C)),
        BrandInfo("nib", "Nib International Bank", "https://nibbank.com.et", VaultCategory.BANKING, R.drawable.ic_brand_nib, Color(0xFFEAB308)),
        BrandInfo("bunna", "Bunna International Bank", "https://bunnabanksc.com", VaultCategory.BANKING, R.drawable.ic_brand_bunna, Color(0xFF78350F)),
        BrandInfo("berhan", "Berhan Bank", "https://berhanbanksc.com", VaultCategory.BANKING, R.drawable.ic_brand_berhan, Color(0xFFFACC15)),
        BrandInfo("abay", "Abay Bank", "https://abaybank.com.et", VaultCategory.BANKING, R.drawable.ic_brand_abay, Color(0xFF0284C7)),
        BrandInfo("amhara", "Amhara Bank", "https://amharabank.com.et", VaultCategory.BANKING, R.drawable.ic_brand_amhara, Color(0xFF991B1B)),
        BrandInfo("enat", "Enat Bank", "https://enatbanksc.com", VaultCategory.BANKING, R.drawable.ic_brand_enat, Color(0xFFEC4899)),
        BrandInfo("siinqee", "Siinqee Bank", "https://siinqeebank.com.et", VaultCategory.BANKING, R.drawable.ic_brand_siinqee, Color(0xFF15803D)),
        BrandInfo("hijra", "Hijra Bank", "https://hijra-bank.com", VaultCategory.BANKING, R.drawable.ic_brand_hijra, Color(0xFF047857)),
        BrandInfo("zamzam", "ZamZam Bank", "https://zamzambank.com", VaultCategory.BANKING, R.drawable.ic_brand_zamzam, Color(0xFF0D9488)),
    )

    val POPULAR_SOCIAL_BRANDS = listOf(
        BrandInfo("instagram", "Instagram", "https://instagram.com", VaultCategory.SOCIAL, R.drawable.ic_brand_instagram, Color(0xFFC13584)),
        BrandInfo("facebook", "Facebook", "https://facebook.com", VaultCategory.SOCIAL, R.drawable.ic_brand_facebook, Color(0xFF1877F2)),
        BrandInfo("x_twitter", "X (Twitter)", "https://x.com", VaultCategory.SOCIAL, R.drawable.ic_brand_x_twitter, Color(0xFF000000)),
        BrandInfo("tiktok", "TikTok", "https://tiktok.com", VaultCategory.SOCIAL, R.drawable.ic_brand_tiktok, Color(0xFF010101)),
        BrandInfo("whatsapp", "WhatsApp", "https://web.whatsapp.com", VaultCategory.SOCIAL, R.drawable.ic_brand_whatsapp, Color(0xFF25D366)),
        BrandInfo("telegram", "Telegram", "https://web.telegram.org", VaultCategory.SOCIAL, R.drawable.ic_brand_telegram, Color(0xFF24A1DE)),
        BrandInfo("youtube", "YouTube", "https://youtube.com", VaultCategory.SOCIAL, R.drawable.ic_brand_youtube, Color(0xFFFF0000)),
        BrandInfo("snapchat", "Snapchat", "https://snapchat.com", VaultCategory.SOCIAL, R.drawable.ic_brand_snapchat, Color(0xFFFFFC00)),
        BrandInfo("figma", "Figma", "https://figma.com", VaultCategory.WEBSITE, R.drawable.ic_brand_figma, Color(0xFFF24E1E)),
        BrandInfo("discord", "Discord", "https://discord.com", VaultCategory.SOCIAL, R.drawable.ic_brand_discord, Color(0xFF5865F2)),
        BrandInfo("linkedin", "LinkedIn", "https://linkedin.com", VaultCategory.SOCIAL, R.drawable.ic_brand_linkedin, Color(0xFF0A66C2)),
        BrandInfo("reddit", "Reddit", "https://reddit.com", VaultCategory.SOCIAL, R.drawable.ic_brand_reddit, Color(0xFFFF4500)),
        BrandInfo("google", "Google / Gmail", "https://accounts.google.com", VaultCategory.EMAIL, R.drawable.ic_brand_google, Color(0xFF4285F4)),
        BrandInfo("spotify", "Spotify", "https://spotify.com", VaultCategory.WEBSITE, R.drawable.ic_brand_spotify, Color(0xFF1ED760)),
        BrandInfo("netflix", "Netflix", "https://netflix.com", VaultCategory.WEBSITE, R.drawable.ic_brand_netflix, Color(0xFFE50914)),
        BrandInfo("github", "GitHub", "https://github.com", VaultCategory.WEBSITE, R.drawable.ic_brand_github, Color(0xFF24292F)),
        BrandInfo("apple", "Apple ID", "https://appleid.apple.com", VaultCategory.WEBSITE, R.drawable.ic_brand_apple, Color(0xFF1E293B)),
    )

    val POPULAR_CRYPTO_BRANDS = listOf(
        BrandInfo("seed_phrase_vault", "12 / 24-Word Seed Vault", "https://bip39.org", VaultCategory.CRYPTO, R.drawable.ic_brand_seedphrase, Color(0xFF10B981)),
        BrandInfo("uniswap", "Uniswap Protocol", "https://app.uniswap.org", VaultCategory.CRYPTO, R.drawable.ic_brand_uniswap, Color(0xFFFF007A)),
        BrandInfo("pancakeswap", "PancakeSwap", "https://pancakeswap.finance", VaultCategory.CRYPTO, R.drawable.ic_brand_pancakeswap, Color(0xFF1FC7D4)),
        BrandInfo("aave", "Aave DeFi Account", "https://app.aave.com", VaultCategory.CRYPTO, R.drawable.ic_brand_aave, Color(0xFFB6509E)),
        BrandInfo("lido", "Lido Staking", "https://lido.fi", VaultCategory.CRYPTO, R.drawable.ic_brand_lido, Color(0xFF00A3FF)),
        BrandInfo("metamask", "MetaMask Wallet", "https://metamask.io", VaultCategory.CRYPTO, R.drawable.ic_brand_metamask, Color(0xFFE2761B)),
        BrandInfo("phantom", "Phantom Wallet", "https://phantom.app", VaultCategory.CRYPTO, R.drawable.ic_brand_phantom, Color(0xFFAB9FF2)),
        BrandInfo("keplr", "Keplr Cosmos Wallet", "https://keplr.app", VaultCategory.CRYPTO, R.drawable.ic_brand_keplr, Color(0xFF1B1E36)),
        BrandInfo("rabby", "Rabby Web3 Wallet", "https://rabby.io", VaultCategory.CRYPTO, R.drawable.ic_brand_rabby, Color(0xFF8697FF)),
        BrandInfo("rainbow", "Rainbow Web3 Wallet", "https://rainbow.me", VaultCategory.CRYPTO, R.drawable.ic_brand_rainbow, Color(0xFF111111)),
        BrandInfo("safe", "Safe (Gnosis) Multi-Sig", "https://safe.global", VaultCategory.CRYPTO, R.drawable.ic_brand_safe, Color(0xFF12FF80)),
        BrandInfo("zerion", "Zerion Web3 Portfolio", "https://zerion.io", VaultCategory.CRYPTO, R.drawable.ic_brand_zerion, Color(0xFF2962FF)),
        BrandInfo("magiceden", "Magic Eden NFT", "https://magiceden.io", VaultCategory.CRYPTO, R.drawable.ic_brand_magiceden, Color(0xFFE32381)),
        BrandInfo("solflare", "Solflare Solana Wallet", "https://solflare.com", VaultCategory.CRYPTO, R.drawable.ic_brand_solflare, Color(0xFFFC8802)),
        BrandInfo("coinbasewallet", "Coinbase Web3 Wallet", "https://coinbase.com/wallet", VaultCategory.CRYPTO, R.drawable.ic_brand_coinbasewallet, Color(0xFF0052FF)),
        BrandInfo("binance", "Binance Exchange", "https://binance.com", VaultCategory.CRYPTO, R.drawable.ic_brand_binance, Color(0xFFF3BA2F)),
        BrandInfo("coinbase", "Coinbase Exchange", "https://coinbase.com", VaultCategory.CRYPTO, R.drawable.ic_brand_coinbase, Color(0xFF0052FF)),
        BrandInfo("kraken", "Kraken Exchange", "https://kraken.com", VaultCategory.CRYPTO, R.drawable.ic_brand_kraken, Color(0xFF5741D9)),
        BrandInfo("kucoin", "KuCoin Exchange", "https://kucoin.com", VaultCategory.CRYPTO, R.drawable.ic_brand_kucoin, Color(0xFF24AF82)),
        BrandInfo("bybit", "Bybit Exchange", "https://bybit.com", VaultCategory.CRYPTO, R.drawable.ic_brand_bybit, Color(0xFFF7A600)),
        BrandInfo("okx", "OKX Exchange", "https://okx.com", VaultCategory.CRYPTO, R.drawable.ic_brand_okx, Color(0xFF000000)),
        BrandInfo("cryptocom", "Crypto.com App", "https://crypto.com", VaultCategory.CRYPTO, R.drawable.ic_brand_cryptocom, Color(0xFF002D72)),
        BrandInfo("trustwallet", "Trust Wallet", "https://trustwallet.com", VaultCategory.CRYPTO, R.drawable.ic_brand_trustwallet, Color(0xFF3375BB)),
        BrandInfo("exodus", "Exodus Web3 Wallet", "https://exodus.com", VaultCategory.CRYPTO, R.drawable.ic_brand_exodus, Color(0xFF3B28CC)),
        BrandInfo("backpack", "Backpack Wallet", "https://backpack.app", VaultCategory.CRYPTO, R.drawable.ic_brand_backpack, Color(0xFFE11D48)),
        BrandInfo("trezor", "Trezor Suite", "https://trezor.io", VaultCategory.CRYPTO, R.drawable.ic_brand_trezor, Color(0xFF008542)),
        BrandInfo("subwallet", "SubWallet Polkadot", "https://subwallet.app", VaultCategory.CRYPTO, R.drawable.ic_brand_subwallet, Color(0xFF00E5FF)),
        BrandInfo("tonkeeper", "Tonkeeper TON Wallet", "https://tonkeeper.com", VaultCategory.CRYPTO, R.drawable.ic_brand_tonkeeper, Color(0xFF0098EA)),
        BrandInfo("ronin", "Ronin Gaming Wallet", "https://roninchain.com", VaultCategory.CRYPTO, R.drawable.ic_brand_ronin, Color(0xFF1273EA)),
        BrandInfo("robinhood", "Robinhood Crypto", "https://robinhood.com", VaultCategory.CRYPTO, R.drawable.ic_brand_robinhood, Color(0xFF00C805)),
        BrandInfo("ledger", "Ledger Live", "https://ledger.com", VaultCategory.CRYPTO, R.drawable.ic_brand_ledger, Color(0xFF101010)),
        BrandInfo("bitcoin", "Bitcoin Wallet", "https://bitcoin.org", VaultCategory.CRYPTO, R.drawable.ic_brand_bitcoin, Color(0xFFF7931A)),
        BrandInfo("ethereum", "Ethereum Wallet", "https://ethereum.org", VaultCategory.CRYPTO, R.drawable.ic_brand_ethereum, Color(0xFF627EEA)),
    )

    val POPULAR_EMAIL_BRANDS = listOf(
        BrandInfo("gmail", "Gmail / Google Account", "https://accounts.google.com", VaultCategory.EMAIL, R.drawable.ic_brand_google, Color(0xFF4285F4)),
        BrandInfo("outlook", "Outlook / Microsoft", "https://outlook.live.com", VaultCategory.EMAIL, R.drawable.ic_brand_google, Color(0xFF0078D4)),
        BrandInfo("apple_id", "iCloud / Apple ID", "https://appleid.apple.com", VaultCategory.EMAIL, R.drawable.ic_brand_apple, Color(0xFF1E293B)),
    )

    val ALL_POPULAR_BRANDS by lazy {
        (POPULAR_SOCIAL_BRANDS + POPULAR_EMAIL_BRANDS + POPULAR_BANK_BRANDS + POPULAR_CRYPTO_BRANDS).distinctBy { it.id }
    }

    fun resolveBrandDrawable(title: String, url: String? = null): Int? {
        val query = (title + " " + (url ?: "")).lowercase().trim()
        return when {
            query.contains("cbe birr") || query.contains("cbebirr") -> R.drawable.ic_brand_cbebirr
            query.contains("telebirr") || query.contains("ethio telecom") -> R.drawable.ic_brand_telebirr
            query.contains("cbe") || query.contains("commercial bank of ethiopia") -> R.drawable.ic_brand_cbe
            query.contains("chapa") -> R.drawable.ic_brand_chapa
            query.contains("santim") || query.contains("santimpay") -> R.drawable.ic_brand_santimpay
            query.contains("arifpay") || query.contains("arif") -> R.drawable.ic_brand_arifpay
            query.contains("kacha") -> R.drawable.ic_brand_kacha
            query.contains("amole") -> R.drawable.ic_brand_amole
            query.contains("ebirr") || query.contains("e-birr") -> R.drawable.ic_brand_ebirr
            query.contains("yenepay") -> R.drawable.ic_brand_yenepay
            query.contains("dashen") -> R.drawable.ic_brand_dashen
            query.contains("awash") -> R.drawable.ic_brand_awash
            query.contains("abyssinia") -> R.drawable.ic_brand_abyssinia
            query.contains("coop") || query.contains("cooperative") -> R.drawable.ic_brand_coop
            query.contains("hibret") || query.contains("united bank") -> R.drawable.ic_brand_hibret
            query.contains("zemen") -> R.drawable.ic_brand_zemen
            query.contains("telebirr") || query.contains("ethio telecom") -> R.drawable.ic_brand_telebirr
            query.contains("lion") || query.contains("anbesa") -> R.drawable.ic_brand_lion
            query.contains("addis") -> R.drawable.ic_brand_addis
            query.contains("ahadu") -> R.drawable.ic_brand_ahadu
            query.contains("gadaa") -> R.drawable.ic_brand_gadaa
            query.contains("rammis") -> R.drawable.ic_brand_rammis
            query.contains("wegagen") -> R.drawable.ic_brand_wegagen
            query.contains("nib") -> R.drawable.ic_brand_nib
            query.contains("bunna") -> R.drawable.ic_brand_bunna
            query.contains("berhan") -> R.drawable.ic_brand_berhan
            query.contains("abay") -> R.drawable.ic_brand_abay
            query.contains("amhara") -> R.drawable.ic_brand_amhara
            query.contains("enat") -> R.drawable.ic_brand_enat
            query.contains("siinqee") -> R.drawable.ic_brand_siinqee
            query.contains("hijra") -> R.drawable.ic_brand_hijra
            query.contains("zamzam") -> R.drawable.ic_brand_zamzam
            query.contains("opensea") -> R.drawable.ic_brand_opensea
            query.contains("uniswap") -> R.drawable.ic_brand_uniswap
            query.contains("pancakeswap") || query.contains("pancake") -> R.drawable.ic_brand_pancakeswap
            query.contains("aave") -> R.drawable.ic_brand_aave
            query.contains("lido") -> R.drawable.ic_brand_lido
            query.contains("keplr") -> R.drawable.ic_brand_keplr
            query.contains("rabby") -> R.drawable.ic_brand_rabby
            query.contains("rainbow") -> R.drawable.ic_brand_rainbow
            query.contains("safe") || query.contains("gnosis") -> R.drawable.ic_brand_safe
            query.contains("zerion") -> R.drawable.ic_brand_zerion
            query.contains("magiceden") || query.contains("magic eden") -> R.drawable.ic_brand_magiceden
            query.contains("solflare") -> R.drawable.ic_brand_solflare
            query.contains("binance") || query.contains("bnb") -> R.drawable.ic_brand_binance
            query.contains("coinbase wallet") -> R.drawable.ic_brand_coinbasewallet
            query.contains("coinbase") -> R.drawable.ic_brand_coinbase
            query.contains("kraken") -> R.drawable.ic_brand_kraken
            query.contains("kucoin") -> R.drawable.ic_brand_kucoin
            query.contains("bybit") -> R.drawable.ic_brand_bybit
            query.contains("okx") -> R.drawable.ic_brand_okx
            query.contains("crypto.com") || query.contains("cryptocom") -> R.drawable.ic_brand_cryptocom
            query.contains("seed") || query.contains("mnemonic") || query.contains("24-word") -> R.drawable.ic_brand_seedphrase
            query.contains("trust") || query.contains("trustwallet") -> R.drawable.ic_brand_trustwallet
            query.contains("exodus") -> R.drawable.ic_brand_exodus
            query.contains("backpack") -> R.drawable.ic_brand_backpack
            query.contains("trezor") -> R.drawable.ic_brand_trezor
            query.contains("subwallet") -> R.drawable.ic_brand_subwallet
            query.contains("tonkeeper") || query.contains("ton") -> R.drawable.ic_brand_tonkeeper
            query.contains("ronin") -> R.drawable.ic_brand_ronin
            query.contains("phantom") -> R.drawable.ic_brand_phantom
            query.contains("robinhood") -> R.drawable.ic_brand_robinhood
            query.contains("ledger") -> R.drawable.ic_brand_ledger
            query.contains("bitcoin") || query.contains("btc") -> R.drawable.ic_brand_bitcoin
            query.contains("ethereum") || query.contains("eth") -> R.drawable.ic_brand_ethereum
            query.contains("metamask") -> R.drawable.ic_brand_metamask
            query.contains("instagram") || query.contains("insta") -> R.drawable.ic_brand_instagram
            query.contains("facebook") || query.contains("fb.com") -> R.drawable.ic_brand_facebook
            query.contains("twitter") || query.contains("x.com") || query.contains("x (twitter") -> R.drawable.ic_brand_x_twitter
            query.contains("tiktok") -> R.drawable.ic_brand_tiktok
            query.contains("whatsapp") -> R.drawable.ic_brand_whatsapp
            query.contains("telegram") || query.contains("t.me") -> R.drawable.ic_brand_telegram
            query.contains("youtube") || query.contains("youtu.be") -> R.drawable.ic_brand_youtube
            query.contains("linkedin") -> R.drawable.ic_brand_linkedin
            query.contains("discord") -> R.drawable.ic_brand_discord
            query.contains("figma") -> R.drawable.ic_brand_figma
            query.contains("snapchat") || query.contains("snap") -> R.drawable.ic_brand_snapchat
            query.contains("reddit") -> R.drawable.ic_brand_reddit
            query.contains("google") || query.contains("gmail") -> R.drawable.ic_brand_google
            query.contains("spotify") -> R.drawable.ic_brand_spotify
            query.contains("netflix") -> R.drawable.ic_brand_netflix
            query.contains("github") -> R.drawable.ic_brand_github
            query.contains("apple") || query.contains("icloud") -> R.drawable.ic_brand_apple
            else -> null
        }
    }

    fun getCategoryFallbackIcon(category: VaultCategory): ImageVector {
        return when (category) {
            VaultCategory.APPS -> Icons.Default.Smartphone
            VaultCategory.CRYPTO -> Icons.Default.CurrencyBitcoin
            VaultCategory.WALLETS -> Icons.Default.AccountBalanceWallet
            VaultCategory.EMAIL -> Icons.Default.Email
            VaultCategory.BANKING -> Icons.Default.AccountBalance
            VaultCategory.WORK -> Icons.Default.Work
            VaultCategory.SHOPPING -> Icons.Default.ShoppingCart
            VaultCategory.NOTES -> Icons.AutoMirrored.Filled.Note
            VaultCategory.WIFI -> Icons.Default.Wifi
            VaultCategory.TOTP_2FA -> Icons.Default.QrCode
            VaultCategory.RECOVERY -> Icons.Default.LockReset
            VaultCategory.IDENTITY -> Icons.Default.Badge
            VaultCategory.SOCIAL -> Icons.Default.Share
            VaultCategory.WEBSITE -> Icons.Default.Public
            else -> Icons.Default.Key
        }
    }

    fun getCategoryEmoji(category: VaultCategory): String {
        return when (category) {
            VaultCategory.APPS -> "📱"
            VaultCategory.SOCIAL -> "🔐"
            VaultCategory.EMAIL -> "✉️"
            VaultCategory.BANKING -> "🏦"
            VaultCategory.CRYPTO -> "₿"
            VaultCategory.WALLETS -> "👛"
            VaultCategory.WORK -> "💼"
            VaultCategory.SHOPPING -> "🛍️"
            VaultCategory.WEBSITE -> "🌐"
            VaultCategory.WIFI -> "📶"
            VaultCategory.IDENTITY -> "🪪"
            VaultCategory.NOTES -> "📝"
            VaultCategory.TOTP_2FA -> "🔢"
            VaultCategory.RECOVERY -> "🛡️"
            VaultCategory.FAVORITES -> "⭐"
            VaultCategory.ALL -> "🗄️"
            VaultCategory.OTHER -> "🔑"
        }
    }

    fun getCategoryColor(category: VaultCategory): Color {
        return when (category) {
            VaultCategory.APPS -> Color(0xFF8B5CF6)
            VaultCategory.SOCIAL -> Color(0xFF2563EB)
            VaultCategory.EMAIL -> Color(0xFFEA580C)
            VaultCategory.BANKING -> Color(0xFF059669)
            VaultCategory.CRYPTO -> Color(0xFFD97706)
            VaultCategory.WALLETS -> Color(0xFF7C3AED)
            VaultCategory.WORK -> Color(0xFF0284C7)
            VaultCategory.SHOPPING -> Color(0xFFE11D48)
            VaultCategory.WEBSITE -> Color(0xFF4F46E5)
            VaultCategory.WIFI -> Color(0xFF0D9488)
            VaultCategory.IDENTITY -> Color(0xFF9333EA)
            VaultCategory.NOTES -> Color(0xFF64748B)
            VaultCategory.TOTP_2FA -> Color(0xFFDB2777)
            VaultCategory.RECOVERY -> Color(0xFFB45309)
            VaultCategory.FAVORITES -> Color(0xFFF59E0B)
            VaultCategory.ALL -> Color(0xFF10B981)
            VaultCategory.OTHER -> Color(0xFF6B7280)
        }
    }
}

/**
 * Renders the real brand logo if matching a known brand (OpenSea, Uniswap, MetaMask, Phantom, etc.),
 * or falls back to the clean category-specific vector icon with 3D glass background.
 */
@Composable
fun VaultItemAvatar(
    title: String,
    category: VaultCategory,
    modifier: Modifier = Modifier,
    url: String? = null,
    customEmoji: String? = null,
    size: Dp = 42.dp,
    iconSize: Dp = 22.dp,
    cornerRadius: Dp = 10.dp,
) {
    val brandDrawable = BrandIconHelper.resolveBrandDrawable(title, url)
    val categoryColor = BrandIconHelper.getCategoryColor(category)

    if (!customEmoji.isNullOrBlank()) {
        Box(
            modifier = modifier
                .size(size)
                .shadow(3.dp, RoundedCornerShape(cornerRadius), spotColor = categoryColor.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            categoryColor.copy(alpha = 0.22f),
                            categoryColor.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.7f), categoryColor.copy(alpha = 0.25f))
                    ),
                    RoundedCornerShape(cornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = customEmoji,
                fontSize = (size.value * 0.5f).sp
            )
        }
    } else if (brandDrawable != null) {
        Box(
            modifier = modifier
                .size(size)
                .shadow(4.dp, RoundedCornerShape(cornerRadius), spotColor = categoryColor.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8FAFC)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White, Color.LightGray.copy(alpha = 0.35f))
                    ),
                    RoundedCornerShape(cornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = brandDrawable),
                contentDescription = title,
                modifier = Modifier
                    .size(size * 0.72f)
                    .shadow(1.dp, RoundedCornerShape(cornerRadius * 0.4f))
                    .clip(RoundedCornerShape(cornerRadius * 0.4f))
            )
        }
    } else {
        val fallbackIcon = BrandIconHelper.getCategoryFallbackIcon(category)
        Box(
            modifier = modifier
                .size(size)
                .shadow(3.dp, RoundedCornerShape(cornerRadius), spotColor = categoryColor.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            categoryColor.copy(alpha = 0.22f),
                            categoryColor.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.7f), categoryColor.copy(alpha = 0.25f))
                    ),
                    RoundedCornerShape(cornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
