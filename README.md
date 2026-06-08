# Hermes QC Plan Android App

Professional **offline native Android application** source for Hermes Maftool.

## This version includes
- **Multi-page structure**
- **Full dashboard screen**
- **Navigation drawer menu**
- Separate pages for:
  - Dashboard
  - Raw Material
  - Pickling / Phosphating
  - Spheroidizing
  - Final Inspection
  - MTC
- **Bilingual switch inside the app**: English / فارسی
- Hermes Maftool real logo
- Content based on:
  - ASTM F2282-03
  - ISO 9717
  - BS EN 10263-1
  - Internal Hermes Maftool QC boards

## Tech
- Kotlin
- Jetpack Compose
- Material 3
- Offline app content

## Project info
- App name: `Hermes QC Plan`
- Package: `com.hermesmaftool.qcplan`
- Min SDK: 24
- Target SDK: 34
- Version: 2.0.0

## Open in Android Studio
1. Open Android Studio
2. Choose **Open**
3. Select folder: `HermesQCPlan`
4. Let Gradle sync complete
5. Run on phone or emulator

## APK note
In this environment I prepared the **full Android Studio project**, but I could not generate the final APK here because a full Android SDK / build environment was not available.

If you open the project in Android Studio, you can build:
- Debug APK
- Release APK
- AAB

## Suggested next upgrades
- Add company-specific numeric limits for pickling / phosphating baths
- Add user login or document control versioning
- Add export to PDF / screenshot reports
- Add dark mode / tablet layout
