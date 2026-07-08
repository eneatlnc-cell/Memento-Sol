# fastlane setup (Memento-Sol Android)

Install:

```bash
brew install fastlane
```

Create a Google Play service account JSON key with Google Play Developer API access, then grant that service account access to the Memento-Sol app in Play Console.

Recommended local auth:

```bash
GOOGLE_PLAY_JSON_KEY=/absolute/path/to/google-play-service-account.json
```

Optional app targeting:

```bash
GOOGLE_PLAY_PACKAGE_NAME=com.memento.sol
```

## Release Commands

Validate auth:

```bash
cd apps/android
fastlane android auth_check
```

Archive locally without upload:

```bash
pnpm android:release:archive
```

Upload to Play Store:

```bash
pnpm android:release:upload
```