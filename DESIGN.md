---
version: alpha
name: Float-Volume-Linear-Design-System
description: |
  A near-black stealth volume utility built on Linear.app's quiet luxury canvas. Styled using a deep charcoal canvas (#1C1B1F), elevated slate cards (#2B2930), hairline borders (#49454F), and elegant glowing lavender accents (#D0BCFF / #4F378B). It balances a strict prestige-safe stealth bubble with highly-polished developer-tool ergonomics.

colors:
  primary: "#D0BCFF"               # Glowing lavender-purple accent (primary toggle, highlights)
  on-primary: "#381E72"            # Deep dark purple for high contrast text on active elements
  primary-container: "#4F378B"     # Dark indigo-purple container (dashboard status card background)
  on-primary-container: "#EADDFF"   # Soft lavender-white for text on dark containers
  ink: "#E6E1E5"                   # High-readability light gray for main body and headlines
  ink-muted: "#CAC4D0"             # Muted slate-gray for captions and metadata
  canvas: "#1C1B1F"                # Deep near-black background (dashboard base canvas)
  surface: "#2B2930"               # Charcoal elevated panels (mixer level, permissions, tip cards)
  hairline: "#49454F"              # 1px hairline border separating dashboard panels
  bottom-nav: "#211F26"            # Deepest charcoal for secondary navigation chrome
  stealth-bubble: "#FFFFFF"        # Solid white for the stealth volume overlay bubble
  stealth-border: "#EAEAEA"        # Soft gray border surrounding the stealth bubble

typography:
  display-xl:
    fontFamily: Default (System Sans)
    fontSize: 24px
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: 0.5px
  heading-lg:
    fontFamily: Default (System Sans)
    fontSize: 20px
    fontWeight: 600
    lineHeight: 1.25
    letterSpacing: 0.5px
  body-lg:
    fontFamily: Default (System Sans)
    fontSize: 16px
    fontWeight: 500
    lineHeight: 1.5
    letterSpacing: 0.5px
  body-md:
    fontFamily: Default (System Sans)
    fontSize: 14px
    fontWeight: 400
    lineHeight: 1.5
    letterSpacing: 0.25px
  body-sm:
    fontFamily: Default (System Sans)
    fontSize: 12px
    fontWeight: 400
    lineHeight: 1.4
    letterSpacing: 0.25px
  caption:
    fontFamily: Default (System Sans)
    fontSize: 11px
    fontWeight: 500
    lineHeight: 1.3
    letterSpacing: 0.4px

rounded:
  xs: 4px                          # Small inline labels, permission badges
  sm: 10px                         # M3 icon box container
  md: 12px                         # Access buttons, switches
  lg: 24px                         # Mixer card, permission panel, help card
  xl: 28px                         # Main Service Status Hero Card
  full: 9999px                     # Round custom switches, stealth bubbles

spacing:
  xxs: 2px                         # Tight micro gaps
  xs: 4px                          # Card inner vertical spacing
  sm: 8px                          # Touch element padding increments
  md: 12px                         # Switch padding, layout gaps
  lg: 16px                         # Mixer slider gutters, switch spacing
  xl: 20px                         # Base margin for all cards (20dp padding)
  xxl: 24px                        # Section divider space

components:
  service-status-hero:
    backgroundColor: "{colors.primary-container}"
    textColor: "{colors.on-primary-container}"
    rounded: "{rounded.xl}"
    padding: 24px 20px
  dashboard-card:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    rounded: "{rounded.lg}"
    padding: 20px
  custom-elegant-switch:
    trackColorOn: "{colors.primary}"
    trackColorOff: "{colors.hairline}"
    thumbColorOn: "{colors.on-primary}"
    thumbColorOff: "{colors.ink}"
    rounded: "{rounded.full}"
  stealth-overlay-bubble:
    backgroundColor: "{colors.stealth-bubble}"
    borderColor: "{colors.stealth-border}"
    borderWidth: 1.5dp
    rounded: "{rounded.full}"
    size: 54dp
  permission-badge-granted:
    backgroundColor: "#1a331c"
    textColor: "#b5e6b7"
    rounded: "{rounded.xs}"
  permission-button-denied:
    backgroundColor: "#4e2525"
    textColor: "#f5c2c2"
    rounded: "{rounded.xs}"
---

## 🎨 Overview & Visual Philosophy

The **Float Volume** design system is heavily inspired by **Linear.app**'s dark mode execution. The core aesthetic is built on top of a deep charcoal near-black background (`{colors.canvas}` — `#1C1B1F`), creating an immersive experience. Elevated content panels (`{colors.surface}` — `#2B2930`) host all interactive elements, separated cleanly by hairline borders (`{colors.hairline}` — `#49454F`) rather than muddy drop shadows.

A single chromatic accent — **Lavender** (`{colors.primary}` — `#D0BCFF`) — guides the user's eye toward primary states, active switches, and important labels. 

The most critical visual element of this project is the **Stealth Bubble** (`{component.stealth-overlay-bubble}`). It is styled as a flawless solid-white circle with no volume glyph inside. By masquerading as a default system accessibility touch element, it preserves the user's prestige (hiding broken hardware keys) while offering maximum utility.

---

## 📐 Colors & Theming Scale

*   **Canvas Background** (`{colors.canvas}`): #1C1B1F. The anchor dark color that sits beneath all sections.
*   **Surface Card** (`{colors.surface}`): #2B2930. Provides one step of visual elevation for content cards (Mixer levels, Perms, Instructions).
*   **Hairline Dividers** (`{colors.hairline}`): #49454F. Elegant 1dp thin borders providing crisp, clean edge separations.
*   **Lavender Highlight** (`{colors.primary}`): #D0BCFF. The primary branding accent, used as the active state color for custom switches and highlighted numerical texts.

---

## 🔠 Typography Scale

The typographic hierarchy is designed using the native system sans font, scaled using clean proportional weights to reinforce the layout rhythm:

*   **Display XL** (24px / Bold): Used for top-level headers (e.g., status labels).
*   **Heading Large** (20px / SemiBold): Section headers in secondary cards.
*   **Body Large** (16px / Medium): Card titles and primary labels (e.g., "Mixer Level", "Tampil Di Atas Aplikasi Lain").
*   **Body Medium** (14px / Regular): Standard readable copy, user tips, and link labels.
*   **Body Small** (12px / Regular): Micro captions and descriptive subtext.
*   **Caption** (11px / Bold): Status identifiers, versioning headers, and badge indicators.

---

## 📱 Layout & Spacing Rules

Float Volume implements an **8dp spacing rhythm** derived from core Material Design 3 and Linear.app guidelines:

*   **Dashboard Padding**: All primary dashboard items maintain a strict `{spacing.xl}` (20dp) horizontal padding.
*   **Inner Card Gaps**: Gaps between headers, sliders, and subtexts inside cards sit at `{spacing.md}` (12dp) or `{spacing.lg}` (16dp).
*   **System Bar Clearance**: Layout interfaces are configured to strictly respect top/bottom system safe-areas so content never collides with notches, status indicators, or home gesture bars.

---

## 🛠️ Design Do's & Don'ts

### Do:
*   **Do keep the bubble stealth**: The overlay bubble must remain solid white with no icon (`View.GONE`) to preserve the stealth pen-touch concept.
*   **Do use theme tokens**: Always map card backgrounds, text properties, and border variables to theme tokens (`MaterialTheme.colorScheme`). Do not hardcode static hex codes.
*   **Do animate micro-interactions**: State changes (like the elegant custom switch) must fade smoothly using `animateColorAsState` over `200ms` rather than instant flashes.
*   **Do support TalkBack**: Ensure that custom layout components have explicit `Role.Switch` declarations and descriptions to be accessible.

### Don't:
*   **Don't add shadows**: Build depth from the multi-step surface color ladder rather than using heavy shadows.
*   **Don't introduce secondary colors**: Avoid adding bright reds, blues, or oranges to the chrome surfaces. Keep it purely monochrome dark with lavender highlights.
*   **Don't hardcode pixel measurements**: Never define finger-gesture drag/click boundaries in pixels. Always convert them dynamically based on device density metrics.
