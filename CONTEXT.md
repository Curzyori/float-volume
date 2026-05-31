# 🤖 AI Agent Context & Repository Map (Float Volume 10)

This file serves as a system-level architectural blueprint and constraint guide specifically optimized for **AI Coding Assistants**. Read this file before initiating any code changes or refactoring tasks in this repository.

---

## 🎯 1. Core Product Identity (Stealth Volume Controller)
*   **Purpose**: A free, ad-free, battery-efficient Android utility to adjust system volume without touching physical buttons.
*   **Stealth Philosophy (Prestige-Safe)**: The floating overlay is intentionally styled as a **plain, solid-white circle with no icon** (`floatingIcon.visibility = View.GONE`). It mimics standard system accessibility controls (e.g., assistive touch) to hide the fact that the phone has a broken hardware volume key.
*   **Stealth Activation**: Clicking the blank bubble triggers the system-default volume panel dialog (`AudioManager.ADJUST_SAME` + `FLAG_SHOW_UI`), seamlessly emulating a physical key press.

---

## 🗺️ 2. Repository & File Map (Where to Find What)

Use this map to locate the correct directory and files when executing specific tasks:

### 📂 A. Source Code (`app/src/main/java/com/example/`)
*   **`MainActivity.kt`**: Contains the main Jetpack Compose dashboard UI.
    *   *Usage*: Handle dashboard switches, permission checks (overlay/notifications), and visual cards.
    *   *Semantics*: Custom elegant switches must support TalkBack accessibility (`Role.Switch`, semantic states).
*   **`VolumeFloatingService.kt`**: The core background service handling overlay WindowManager draw loops.
    *   *Usage*: Handle touch dragging, click vs drag detection, and idle dimming (to 30% alpha).
    *   *State*: Saves persistent bubble coordinates (`bubble_x`, `bubble_y`) on `MotionEvent.ACTION_UP` to SharedPreferences.
*   **`BootReceiver.kt`**: Automatic startup listener (`BOOT_COMPLETED`).
    *   *Usage*: Automatically launches `VolumeFloatingService` on device boot if `service_enabled` is set to `true` in SharedPreferences.
*   **`ui/theme/`**: Layout styling guidelines.
    *   *Theme.kt*: Dynamic dark theme configuration using `DarkColorScheme`.
    *   *Color.kt*: Dark theme palette tokens (e.g., `ElegantDarkBackground`, `ElegantDarkSurface`, `ElegantDarkPrimary`).
    *   *Type.kt*: Default typography scales.

### 📂 B. App Resources (`app/src/main/res/`)
*   **`layout/floating_volume_layout.xml`**: XML layout file for the floating bubble overlay window.
    *   *Note*: Contains a hidden `slider_panel` which is disabled by default to maintain the stealth aesthetic.
*   **`drawable/ic_launcher_foreground.xml`**: Premium adaptive vector APK icon.
    *   *Standard*: Styled precisely to match [logo.svg](file:///home/curzy/Documents/Project%20+/Float-Volume-10/logo.svg) in the root directory (charcoal rounded card, dotted track, linear lavender gradient).
*   **`values/strings.xml`**: Localized string assets.
    *   *Standard*: All application visible strings, error messages, and system permissions descriptions **must be in Indonesian**.

---

## 📐 3. AI Coding Standards & Constraints

When writing or modifying code in this codebase, you must adhere strictly to these principles:

1.  **Density-Aware Math**: Never use raw pixels for touch, gesture, or drag detection. Always convert pixel boundaries to Density-Independent Pixels (DP) dynamically using:
    ```kotlin
    val density = resources.displayMetrics.density
    val dragThreshold = (8 * density).toInt() // DP-safe bounds
    ```
2.  **Theme Token Integrity**: Do not hardcode raw hex colors (e.g., `Color(0xFF4F378B)`) directly on dashboard cards in `MainActivity.kt`. Always map color properties dynamically to `MaterialTheme.colorScheme` tokens (e.g., `primaryContainer`, `surface`, etc.).
3.  **Stealth Muted Notifications**: Foreground service notifications must remain strictly silent and unobtrusive. The Notification Channel importance must be set to `NotificationManager.IMPORTANCE_MIN` and priority to `NotificationCompat.PRIORITY_MIN` to prevent polluting the device status bar.
4.  **Persistent Coordination**: The floating bubble coordinates must be saved to SharedPreferences under the keys `"bubble_x"` and `"bubble_y"`. Pparams must load these values dynamically on startup to prevent resetting to default coords (100, 400).
5.  **State Synchronicity**: Service active states must be synchronized across device reboots and user actions using the `"service_enabled"` SharedPreferences boolean key.

---

## 📈 4. Recent Refactoring & Solved Bug History
*   *Boot Auto-Start*: Integrated `BootReceiver` and `RECEIVE_BOOT_COMPLETED` permissions.
*   *Stealth Notification*: Muted and minimized FGS notification labels.
*   *Indonesian Localization*: Fully translated resources (`strings.xml`).
*   *TalkBack Support*: Added semantik accessibility to custom Compose buttons.
*   *Adaptive Icon*: Vector launcher foreground is synchronized with `logo.svg`.
*   *Jitter Fix*: Shifted pixel drag thresholds to dynamic DP metrics.
