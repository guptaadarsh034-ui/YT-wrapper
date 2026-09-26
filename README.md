# YT Wrapper for Android 4.4 KitKat

A lightweight, minimal WebView wrapper designed to run YouTube on legacy Android devices (Android 4.4 KitKat / API 19) with limited hardware resources (512MB RAM).

## Features

- **Legacy Compatibility:** Targets API 19 (Android 4.4 KitKat).
- **RAM Optimization:** Strips unnecessary cache and disables database storage to stay within low RAM constraints.
- **Hardware Acceleration:** Enabled for smooth video playback where supported.
- **Auto Back-Navigation:** Uses built-in WebView back-stack handling.

## Project Structure

```text
├── .github/
│   └── workflows/
│       └── build.yml
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── com/
│           │       └── yourname/
│           │           └── ytwrapper/
│           │               └── MainActivity.java
│           └── res/
│               └── layout/
│                   └── activity_main.xml
├── build.gradle
└── settings.gradle
