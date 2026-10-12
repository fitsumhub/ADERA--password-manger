# Privacy Policy for ADERA Password Manager

**Effective Date**: March 30, 2026  
**Last Updated**: March 30, 2026

**ADERA Security Labs** ("we," "our," or "us") operates the **ADERA (አደራ) Password Manager** mobile application (the "App"). This Privacy Policy explains how information is handled when you use ADERA.

ADERA is engineered around a strict **Zero-Knowledge Architecture**. Your vault secrets, passwords, financial records, identity documents, and seed phrases are encrypted locally on your Android device and are **never transmitted to, stored on, or accessible by our servers or any third parties**.

---

## 1. Summary of Core Privacy Commitments

| Policy Area | ADERA Commitment & Implementation |
| :--- | :--- |
| **Vault Passwords & Secrets** | 🔒 **100% On-Device Local Encryption (AES-256-GCM)**. Never sent to cloud servers. |
| **User Account Creation** | 🛑 **No Account Required**. You do not register an email or account to use ADERA. |
| **Master Password & Keys** | 🔑 **Zero-Knowledge**. We do not know, store, or have access to your Master Password. |
| **Biometric Data** | 👆 **Handled locally by Android BiometricPrompt API**. Raw biometric data never leaves hardware. |
| **Advertising Integration** | 📢 **Google AdMob SDK**. Uses Advertising ID for ad delivery, anti-fraud, and analytics. |

---

## 2. Information Handled locally on Your Device

### A. Encrypted Vault Data (Zero-Knowledge)
All sensitive data you input into ADERA—including account usernames, passwords, credit card numbers, Wi-Fi keys, secure notes, TOTP 2FA keys, and cryptocurrency seed phrases—is encrypted locally using **256-bit AES-GCM** encryption and key derivation (**PBKDF2-HMAC-SHA256** with 100,000 iterations).

- **No Remote Vault Sync or Server Telemetry**: ADERA does not maintain remote cloud servers for vault storage.
- **No Access**: Because encryption keys are derived strictly on your device from your Master Password, ADERA developers cannot decrypt or recover your vault if you lose your Master Password and Recovery Key.

### B. Biometric Authentication
If you enable Biometric Unlock (Fingerprint or Face Recognition), ADERA uses Android's native `BiometricPrompt` and `AndroidKeyStore` APIs. Biometric data is processed strictly within your device's Secure Element / Trusted Execution Environment (TEE) and is never accessible to the App or transmitted remotely.

---

## 3. Information Collected by Third-Party Services (Google AdMob)

To support free access to ADERA, the App integrates **Google AdMob** (a service provided by Google LLC) to display advertisements.

### A. Advertising Identifiers & Diagnostics
Google AdMob may collect and process certain non-personally identifiable technical information from your device, including:
- **Device Identifiers**: Google Advertising ID (AAID), IP address, device model, operating system version, and mobile network information.
- **Ad Interaction Data**: Ad impressions, clicks, diagnostic performance logs, and anti-fraud telemetry.

### B. Purpose of Ad Data Processing
This data is processed directly by Google AdMob for:
- Serving personalized or non-personalized advertisements.
- Frequency capping and preventing click fraud.
- Measuring ad performance and app stability.

For more details on how Google processes ad data, please review:
- [Google's Privacy & Terms](https://policies.google.com/privacy)
- [How Google uses information from apps that use their services](https://policies.google.com/technologies/partner-sites)

---

## 4. Permissions Requested by ADERA

ADERA requests only essential Android permissions necessary to provide vault functionality and display ads:

- `INTERNET` & `ACCESS_NETWORK_STATE`: Required strictly for Google AdMob advertising SDK network requests. *(Note: Vault secrets are NEVER transmitted over these connections)*.
- `USE_BIOMETRIC` / `USE_FINGERPRINT`: Enables local biometric unlock via Android Keystore.
- `VIBRATE`: Provides tactile haptic feedback for user interactions.

---

## 5. Data Retention & Deletion Rights

Because all vault data is stored locally on your device:
- **Instant Local Wipe**: You can delete all your vault data, keys, and configurations at any time in ADERA by navigating to **Settings > Security > Wipe Local Vault**.
- **App Uninstall**: Uninstalling the ADERA App from your Android device permanently and irreversibly erases all local encrypted database files.

---

## 6. Children's Privacy

ADERA is not directed at children under the age of 13. We do not knowingly collect or solicit personal information from children.

---

## 7. Security Practices

ADERA implements state-of-the-art cryptographic safeguards:
- Hardware-backed Android Keystore integration.
- Automatic clipboard sanitization (clears copied passwords after 30 seconds).
- Prevention of screen capture/recording on sensitive screens via `FLAG_SECURE`.
- Disabling cleartext HTTP communications (`usesCleartextTraffic="false"`).

---

## 8. Changes to This Privacy Policy

We may update this Privacy Policy from time to time. Any changes will be posted on this page with an updated "Effective Date."

---

## 9. Contact Us

If you have any questions, concerns, or feedback regarding this Privacy Policy or ADERA's security practices, please contact us at:

- **Email**: `privacy@aderevault.com` (or `fitsumenunu21@gmail.com`)
- **Developer**: ADERA Security Labs
- **Website**: `https://github.com/fitsumhub/ADERA-Password-Manager`
