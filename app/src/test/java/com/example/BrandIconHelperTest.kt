package com.example

import com.example.adere.domain.model.VaultCategory
import com.example.adere.presentation.components.BrandIconHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrandIconHelperTest {

    @Test
    fun testSocialBrandResolution() {
        assertEquals(R.drawable.ic_brand_instagram, BrandIconHelper.resolveBrandDrawable("Instagram Profile"))
        assertEquals(R.drawable.ic_brand_facebook, BrandIconHelper.resolveBrandDrawable("My Facebook"))
        assertEquals(R.drawable.ic_brand_x_twitter, BrandIconHelper.resolveBrandDrawable("X (Twitter)"))
        assertEquals(R.drawable.ic_brand_telegram, BrandIconHelper.resolveBrandDrawable("Telegram Channel"))
        assertEquals(R.drawable.ic_brand_whatsapp, BrandIconHelper.resolveBrandDrawable("WhatsApp Web"))
        assertEquals(R.drawable.ic_brand_youtube, BrandIconHelper.resolveBrandDrawable("YouTube Studio"))
        assertEquals(R.drawable.ic_brand_tiktok, BrandIconHelper.resolveBrandDrawable("TikTok Creator"))
        assertEquals(R.drawable.ic_brand_discord, BrandIconHelper.resolveBrandDrawable("Discord Server"))
        assertEquals(R.drawable.ic_brand_linkedin, BrandIconHelper.resolveBrandDrawable("LinkedIn Job"))
        assertEquals(R.drawable.ic_brand_snapchat, BrandIconHelper.resolveBrandDrawable("Snapchat Account"))
        assertEquals(R.drawable.ic_brand_reddit, BrandIconHelper.resolveBrandDrawable("Reddit Login"))
    }

    @Test
    fun testBankingBrandResolution() {
        assertEquals(R.drawable.ic_brand_cbe, BrandIconHelper.resolveBrandDrawable("Commercial Bank of Ethiopia"))
        assertEquals(R.drawable.ic_brand_cbebirr, BrandIconHelper.resolveBrandDrawable("CBE Birr Account"))
        assertEquals(R.drawable.ic_brand_telebirr, BrandIconHelper.resolveBrandDrawable("Telebirr SuperApp"))
        assertEquals(R.drawable.ic_brand_chapa, BrandIconHelper.resolveBrandDrawable("Chapa Payment API"))
        assertEquals(R.drawable.ic_brand_dashen, BrandIconHelper.resolveBrandDrawable("Dashen Bank Mobile"))
        assertEquals(R.drawable.ic_brand_awash, BrandIconHelper.resolveBrandDrawable("Awash Bank Portal"))
        assertEquals(R.drawable.ic_brand_abyssinia, BrandIconHelper.resolveBrandDrawable("Bank of Abyssinia"))
        assertEquals(R.drawable.ic_brand_coop, BrandIconHelper.resolveBrandDrawable("Cooperative Bank of Oromia"))
        assertEquals(R.drawable.ic_brand_santimpay, BrandIconHelper.resolveBrandDrawable("SantimPay Gateway"))
        assertEquals(R.drawable.ic_brand_arifpay, BrandIconHelper.resolveBrandDrawable("ArifPay POS"))
    }

    @Test
    fun testCryptoBrandResolution() {
        assertEquals(R.drawable.ic_brand_bitcoin, BrandIconHelper.resolveBrandDrawable("Bitcoin Hardware Wallet"))
        assertEquals(R.drawable.ic_brand_ethereum, BrandIconHelper.resolveBrandDrawable("Ethereum Staking"))
        assertEquals(R.drawable.ic_brand_metamask, BrandIconHelper.resolveBrandDrawable("MetaMask Extension"))
        assertEquals(R.drawable.ic_brand_phantom, BrandIconHelper.resolveBrandDrawable("Phantom Solana Wallet"))
        assertEquals(R.drawable.ic_brand_binance, BrandIconHelper.resolveBrandDrawable("Binance Exchange"))
        assertEquals(R.drawable.ic_brand_seedphrase, BrandIconHelper.resolveBrandDrawable("12-Word Mnemonic Seed"))
        assertEquals(R.drawable.ic_brand_uniswap, BrandIconHelper.resolveBrandDrawable("Uniswap DEX"))
        assertEquals(R.drawable.ic_brand_pancakeswap, BrandIconHelper.resolveBrandDrawable("PancakeSwap Pool"))
        assertEquals(R.drawable.ic_brand_trustwallet, BrandIconHelper.resolveBrandDrawable("Trust Wallet"))
    }

    @Test
    fun testEmailAndWebBrandResolution() {
        assertEquals(R.drawable.ic_brand_google, BrandIconHelper.resolveBrandDrawable("Gmail Work Account"))
        assertEquals(R.drawable.ic_brand_google, BrandIconHelper.resolveBrandDrawable("Google Cloud Console"))
        assertEquals(R.drawable.ic_brand_apple, BrandIconHelper.resolveBrandDrawable("Apple ID iCloud"))
        assertEquals(R.drawable.ic_brand_github, BrandIconHelper.resolveBrandDrawable("GitHub Organization"))
        assertEquals(R.drawable.ic_brand_figma, BrandIconHelper.resolveBrandDrawable("Figma Design Vault"))
        assertEquals(R.drawable.ic_brand_spotify, BrandIconHelper.resolveBrandDrawable("Spotify Premium"))
        assertEquals(R.drawable.ic_brand_netflix, BrandIconHelper.resolveBrandDrawable("Netflix Family"))
    }

    @Test
    fun testUnknownBrandReturnsNull() {
        assertNull(BrandIconHelper.resolveBrandDrawable("Unregistered Unknown Portal 12345"))
    }

    @Test
    fun testCategoryColorsAndFallbackIcons() {
        for (category in VaultCategory.entries) {
            val color = BrandIconHelper.getCategoryColor(category)
            assertNotNull(color)
            val fallbackIcon = BrandIconHelper.getCategoryFallbackIcon(category)
            assertNotNull(fallbackIcon)
            val emoji = BrandIconHelper.getCategoryEmoji(category)
            assertNotNull(emoji)
        }
    }

    @Test
    fun testPopularBrandListsNotEmpty() {
        assertTrue(BrandIconHelper.POPULAR_BANK_BRANDS.isNotEmpty())
        assertTrue(BrandIconHelper.POPULAR_SOCIAL_BRANDS.isNotEmpty())
        assertTrue(BrandIconHelper.POPULAR_CRYPTO_BRANDS.isNotEmpty())
        assertTrue(BrandIconHelper.POPULAR_EMAIL_BRANDS.isNotEmpty())
        assertTrue(BrandIconHelper.ALL_POPULAR_BRANDS.isNotEmpty())
    }
}
