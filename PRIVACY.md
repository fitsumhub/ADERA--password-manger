# Privacy Policy for ADERA Password Manager

**Effective Date**: March 30, 2026  
**Last Updated**: March 30, 2026  
**Publisher**: ADERA Security Labs ("we," "our," or "us")  
**Application**: ADERA (አደራ) Password Manager for Android  
**Official Repository**: [https://github.com/fitsumhub/ADERA--password-manger](https://github.com/fitsumhub/ADERA--password-manger)

---

## Executive Summary & Core Guarantees

ADERA (አደራ) Password Manager is engineered from the ground up around an uncompromised **Zero-Knowledge Privacy Architecture**. We believe your private credentials, financial accounts, identity records, and cryptographic keys belong solely to you. 

* **Zero Cloud Storage**: ADERA maintains no remote cloud servers, no central user databases, and no online telemetry services for vault records.
* **100% On-Device AES-256-GCM Encryption**: All vault items are encrypted locally on your Android device using 256-bit Galois/Counter Mode (GCM) encryption with PBKDF2-HMAC-SHA256 key derivation.
* **No Account Registration**: You are never required to provide an email address, phone number, name, or account credentials to use ADERA.
* **Hardware-Backed Biometrics**: Biometric authentication is handled exclusively by the native Android `BiometricPrompt` API within your device's Hardware-Backed Trusted Execution Environment (TEE).
* **Transparent Ad Disclosure**: ADERA integrates Google AdMob SDK to support free application distribution. AdMob processes non-sensitive device identifiers (such as Google Advertising ID) strictly for ad delivery and anti-fraud purposes. AdMob has **zero access** to your encrypted or decrypted vault memory.

---

## 1. Zero-Knowledge Cryptographic Architecture

### 1.1 Master Password & Encryption Keys
When you create a Master Password in ADERA:
1. Your Master Password is passed into a **PBKDF2-HMAC-SHA256** key derivation function executing **100,000 iterations** with a unique 128-bit cryptographic salt.
2. The derived key encrypts your local database using **AES-256-GCM**, providing both confidentiality and authenticated data integrity verification.
3. Additional secret keys are generated and sealed within the **Android Keystore System**, backed by hardware Secure Elements (StrongBox / TEE) where available.

### 1.2 Unrecoverable Design
Because we do not possess your Master Password, Master Encryption Key, or local database salt:
* **We cannot access, read, restore, or decrypt your vault contents.**
* **We cannot assist in resetting a lost Master Password if you do not possess your 24-character Emergency Recovery Key.**

---

## 2. Comprehensive Data Handling & Storage Categorization

| Data Category | Data Elements Included | Storage Location & Method | Third-Party Sharing |
| :--- | :--- | :--- | :--- |
| **Vault Credentials** | Account Usernames, Passwords, Credit Cards, Bank Accounts, Secure Notes, Wi-Fi Keys, TOTP 2FA Seeds, Crypto Seed Phrases | Encrypted locally on-device (`AES-256-GCM` SQLite) | 🛑 **NEVER SHARED** |
| **Master Password & Keys** | Master Password hash, Salt, Emergency Recovery Key | Hashed & encrypted inside Android Keystore TEE | 🛑 **NEVER SHARED** |
| **Biometric Credentials** | Fingerprint & Face Recognition templates | Stored securely inside Android Hardware Secure Element | 🛑 **NEVER SHARED** |
| **Advertising Identifiers** | Google Advertising ID (AAID), IP Address, Network Status, Device Model | Processed temporarily in volatile memory by Google AdMob SDK | 📢 **Google AdMob** (Ad serving, anti-fraud, analytics) |
| **App Configuration** | Theme preference, Auto-lock timeout, Biometric toggle state | Stored locally in Android `EncryptedSharedPreferences` | 🛑 **NEVER SHARED** |

---

## 3. Third-Party Services & AdMob SDK Integration

To maintain free availability while supporting ongoing security development, ADERA integrates **Google AdMob** (operated by Google LLC).

### 3.1 Data Processed by Google AdMob
Google AdMob may collect and process non-personally identifiable technical telemetry directly from your device:
* **Device Identifiers**: Google Advertising ID (AAID), IP address, mobile operator, hardware model, and OS version.
* **Ad Interaction Metrics**: Ad impressions, click-through rates, diagnostic performance logs, and anti-fraud signals.

### 3.2 Strict Vault Isolation Guarantee
The Google AdMob SDK operates in complete isolation from the application's cryptographic vault layer. The network requests made by AdMob cannot read, intercept, inspect, or transmit any part of your unencrypted or encrypted vault records.

For detailed information on Google's privacy practices:
* [Google Privacy Policy](https://policies.google.com/privacy)
* [How Google Uses Information From Apps That Use Their Services](https://policies.google.com/technologies/partner-sites)

---

## 4. Android Operating System Permissions Disclosures

ADERA requests only minimum essential Android permissions required to deliver local vault features and serve advertisements:

* **`android.permission.INTERNET`**: Required exclusively by the Google AdMob SDK to fetch ad units and transmit ad diagnostics. *(No vault data is ever sent across this connection)*.
* **`android.permission.ACCESS_NETWORK_STATE`**: Enables AdMob to detect offline/online connectivity states.
* **`android.permission.USE_BIOMETRIC` / `android.permission.USE_FINGERPRINT`**: Enables local hardware biometric authentication via `BiometricPrompt`.
* **`android.permission.VIBRATE`**: Provides tactile haptic feedback when copying passwords or interacting with UI controls.
* **`android.permission.POST_NOTIFICATIONS`**: (Android 13+) Used optionally for security alerts (e.g., auto-lock background warnings).

---

## 5. User Data Control, Rights & Absolute Erasure

Under international data protection regulations—including the **General Data Protection Regulation (GDPR)** and the **California Consumer Privacy Act (CCPA/CPRA)**—users maintain complete ownership and control over their data:

* **Instant Vault Wipe**: You can immediately and permanently purge all local vault entries, keys, and cached settings at any time in ADERA by navigating to **Settings ➔ Security ➔ Wipe Local Vault**.
* **Complete App Removal**: Uninstalling ADERA from your Android device permanently erases all encrypted database files, preferences, and cryptographic keys stored in Android Keystore.
* **Data Portability**: You can export your vault data anytime to an encrypted JSON backup or printable document format under **Settings ➔ Backup & Export**.
* **Advertising ID Reset**: You can reset or opt out of personalized advertising at any time on your Android device via **Settings ➔ Google ➔ Ads ➔ Reset Advertising ID / Delete Advertising ID**.

---

## 6. Children's Online Privacy Protection Act (COPPA) Compliance

ADERA is a general-audience security utility and is not directed at or structured to attract children under 13 years of age (or under 16 in the European Union). We do not knowingly solicit, collect, or process personal data from children. If you believe a child has provided personal information, please contact us immediately for prompt resolution.

---

## 7. Security Architecture Safeguards

ADERA incorporates multi-layered Android platform protection mechanisms:

1. **Screen Capture & Screen Recording Prevention**: Sensitive screens (Vault Details, Password Generator, Master Key Prompt) enforce `WindowManager.LayoutParams.FLAG_SECURE` to block screenshots, screen recording, and app switcher preview leaks.
2. **Clipboard Auto-Sanitization**: Passwords copied to the clipboard are automatically wiped after 30 seconds to prevent clipboard scraping attacks.
3. **Cleartext Communication Prohibition**: Cleartext HTTP traffic is explicitly disabled application-wide (`usesCleartextTraffic="false"`).
4. **Memory Scrubbing**: Plaintext passwords in memory are stored using temporary char arrays and cleared immediately after cryptographic processing.

---

## 8. Policy Updates

We may update this Privacy Policy periodically to reflect enhancements in our security practices or regulatory requirements. Any updates will be published directly to our GitHub repository and hosted on our official GitHub Pages site with a revised **Effective Date**.

---

## 9. Regulatory Compliance & Contact Information

If you have questions, security inquiries, or feedback regarding this Privacy Policy or ADERA's cryptographic safeguards, please contact our security team:

* **Security Email**: [fitsumenunu21@gmail.com](mailto:fitsumenunu21@gmail.com)
* **Publisher**: ADERA Security Labs
* **Official Repository**: [github.com/fitsumhub/ADERA--password-manger](https://github.com/fitsumhub/ADERA--password-manger)
* **Hosted Privacy Site**: [fitsumhub.github.io/ADERA--password-manger/](https://fitsumhub.github.io/ADERA--password-manger/)
