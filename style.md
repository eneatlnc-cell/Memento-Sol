# Memento-Sol Android UI Style Guide

Scope: all native Android UI in Memento-Sol (Jetpack Compose).
Goal: one coherent visual system across all screens.

## 1. Design Direction

- Clean, dark surfaces matching Memento-X palette.
- Strong readability first.
- One clear primary action per screen state.
- Progressive disclosure for advanced controls.

## 2. Core Tokens

- Background: `#0A0A0F`
- Surface: `#12121F`
- Border: `#1E1E3A`
- Text primary: `#FFFFFF`
- Text secondary: `#888888`
- Text tertiary: `#555555`
- Accent primary: `#6C5CE7`
- Accent soft: `#1A1A3E`
- Success: `#00C48C`
- Warning: `#FFB347`

Rule: do not introduce random per-screen colors when an existing token fits.

## 3. Typography

Primary type family: system default sans-serif.

Recommended scale:

- Display: `34sp / 40sp`, bold
- Section title: `24sp / 30sp`, semibold
- Body: `15sp / 22sp`, medium
- Caption: `12sp / 16sp`, medium

## 4. Architecture Rules

- Durable UI state in `MainViewModel`.
- Composables: state in, callbacks out.
- No business/network logic in composables.
- Keep side effects explicit (`LaunchedEffect`, activity result APIs).

## 5. Source Of Truth

- `app/src/main/java/com/memento/sol/ui/MainScreen.kt`
- `app/src/main/java/com/memento/sol/ui/AccountScreen.kt`
- `app/src/main/java/com/memento/sol/ui/CaptureScreen.kt`
- `app/src/main/java/com/memento/sol/ui/AssetListScreen.kt`
- `app/src/main/java/com/memento/sol/NodeApp.kt`

If style and implementation diverge, update both in the same change.