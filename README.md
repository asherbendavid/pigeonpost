# PigeonPost 🕊️

Watches the Android Studio release blog and notifies you when a new
stable, RC, or patch release goes live — skipping the noisier Canary
builds by default.

Built generically enough to potentially watch other IDE release feeds
(Arduino IDE, VS) in the future.

## Features
- Background polling via WorkManager (interval configurable in Settings)
- Notifies on new releases, bundling multiple into one notification if
  several arrived since the last check
- Configurable exclude-keyword filter (default: "Canary")
- Tap any entry (or the notification) to open the release announcement

## Stack
Kotlin, XML views, Retrofit + Gson, WorkManager, Preferences DataStore.

## License
BSD-3-Clause — see [ATTRIBUTIONS.md](ATTRIBUTIONS.md).

## Part of cvc.dashingdog
Single-purpose, ad-free personal tools, built as an alternative to
bloated commercial apps.