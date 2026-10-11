# 🚀 Google Play Store Deployment Guide for ADERE Password Manager

This document provides a step-by-step walkthrough to build, sign, configure, and publish **ADERE (አደራ)** on the Google Play Console.

---

## 📁 1. Generated Deployment File
Your release Android App Bundle (`.aab`) has been compiled and is ready for upload:
- **File Location**: `app/build/outputs/bundle/release/app-release.aab`
- **Package ID**: `com.aistudio.adere.kxmpzq`
- **Target API**: Level 36 (Android 16 / Android 15+)
- **Min API**: Level 24 (Android 7.0+)

To re-compile this file at any time, run:
```bash
./gradlew app:bundleRelease
```

---

## 🔑 2. Step 1: Create Production Release Keystore (Optional for Custom Signing)
If you want to sign your build with a custom keystore before uploading:

1. Open Terminal or PowerShell and run `keytool`:
   ```powershell
   keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. Place `my-upload-key.jks` in the project root directory (`C:/Users/APLUS/ADERA--password-manger/my-upload-key.jks`).
3. Set environment variables (or let Google Play App Signing manage signing keys automatically upon `.aab` upload):
   ```powershell
   $env:KEYSTORE_PATH="my-upload-key.jks"
   $env:STORE_PASSWORD="YourStorePassword"
   $env:KEY_ALIAS="upload"
   $env:KEY_PASSWORD="YourKeyPassword"
   ```

---

## 🌐 3. Step 2: Google Play Console App Setup

1. Go to [Google Play Console](https://play.google.com/console) and click **Create App**.
2. **App Details**:
   - **App Name**: `ADERE · Zero-Knowledge Password Vault`
   - **Default Language**: `English (US)` (Add `Amharic` translation)
   - **App or Game**: `App`
   - **Free or Paid**: `Free`
3. Accept Developer Program Policies and click **Create App**.

---

## 📝 4. Step 3: Store Listing Copy & Metadata

### **Short Description (80 chars max)**
> Secure zero-knowledge password manager with hardware AES-256 local encryption.

### **Full Description**
> ADERE (አደራ) is an executive-grade, zero-knowledge password manager designed for total local privacy and security.
> 
> 🔒 **MILITARY-GRADE ENCRYPTION**: All passwords, payment cards, identity records, and seed phrases are encrypted locally with hardware-backed AES-256-GCM encryption and PBKDF2-HMAC-SHA256 key derivation (100,000 iterations).
> 
> ⚡ **ZERO-KNOWLEDGE ARCHITECTURE**: Plaintext secrets and master keys never leave your phone or touch cloud servers.
> 
> 👆 **HARDWARE BIOMETRIC UNLOCK**: Quick access using Android Keystore fingerprint or face recognition.
> 
> ⏱️ **BUILT-IN 2FA TOTP GENERATOR**: Offline RFC 6238 two-factor authentication rotating codes.
> 
> 📊 **SECURITY HEALTH AUDIT**: Real-time password strength, reuse detection, and vulnerability reporting.
> 
> 🔑 **EMERGENCY RECOVERY KEY**: Instant 24-character master recovery key generation for emergency vault restoration.

---

## 🛡️ 5. Step 4: Play Console Data Safety & Privacy Declarations

### **A. Data Safety Form Responses**
When completing the Data Safety Questionnaire:
1. **Does your app collect or share any user data?**
   - Answer: **Yes** (Due to Google AdMob SDK integration for ads).
2. **Data Types Disclosed**:
   - **Device or Other IDs**: *Device Identifiers* (Collected by Google AdMob for ad serving, analytics, and anti-fraud).
   - **User Accounts & Vault Data**: *Not Collected / Not Shared* (All vault credentials stay 100% on-device).
3. **Data Security Practices**:
   - Data is encrypted in transit (HTTPS enforced via `usesCleartextTraffic="false"`).
   - Users can request data deletion (Full vault wipe option available in Security Settings).

### **B. Privacy Policy URL**
Provide an HTTPS URL hosting your Privacy Policy (e.g., GitHub Pages or website).
- Privacy disclosures required: Zero-knowledge local encryption, no cloud telemetry, and Google AdMob ad network integration.

---

## 📤 6. Step 5: Upload App Bundle & Release

1. On Google Play Console, go to **Testing** > **Internal testing** (or **Closed testing** / **Production**).
2. Click **Create new release**.
3. Under **App bundles**, upload `app/build.gradle.kts` output:
   `app/build/outputs/bundle/release/app-release.aab`
4. Enter Release Notes (e.g., `Initial release of ADERE Password Manager`).
5. Click **Save** > **Review release** > **Start rollout**.

---

*Document generated: March 2026 for ADERE Release Deployment*
