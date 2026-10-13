#!/bin/bash
# ==============================================================================
# SantaList iOS - Build, Install & Launch on iOS Simulator
# ==============================================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

DEVICE_NAME="${1:-iPhone 15}"
BUNDLE_ID="com.santalist.app"
SCHEME="SantaList"
PROJECT="SantaList.xcodeproj"
DERIVED_DATA="$PROJECT_DIR/build"

echo "=================================================="
echo "  🎄 Building SantaList for iOS Simulator ($DEVICE_NAME)"
echo "=================================================="

# 1. Clean & Build for Simulator
xcodebuild clean build \
    -project "$PROJECT" \
    -scheme "$SCHEME" \
    -sdk iphonesimulator \
    -destination "platform=iOS Simulator,name=$DEVICE_NAME" \
    -configuration Debug \
    -derivedDataPath "$DERIVED_DATA" \
    CODE_SIGNING_ALLOWED=NO

APP_PATH=$(find "$DERIVED_DATA/Build/Products/Debug-iphonesimulator" -name "SantaList.app" | head -n 1)

if [ -z "$APP_PATH" ]; then
    echo "❌ Error: SantaList.app build artifact not found!"
    exit 1
fi

echo "✅ Build Succeeded: $APP_PATH"

# 2. Boot Simulator
echo "📱 Booting iOS Simulator: $DEVICE_NAME..."
xcrun simctl boot "$DEVICE_NAME" 2>/dev/null || true
open -a Simulator 2>/dev/null || true

# 3. Install App
echo "📦 Installing SantaList on Simulator..."
xcrun simctl install booted "$APP_PATH"

# 4. Launch App
echo "🎉 Launching SantaList..."
xcrun simctl launch booted "$BUNDLE_ID"

echo "=================================================="
echo "  ✨ SantaList is now running on the iOS Simulator!"
echo "=================================================="
