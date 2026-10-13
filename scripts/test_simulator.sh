#!/bin/bash
# ==============================================================================
# SantaList iOS - Run Unit Tests on iOS Simulator
# ==============================================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

DEVICE_NAME="${1:-iPhone 15}"
SCHEME="SantaList"
PROJECT="SantaList.xcodeproj"

echo "=================================================="
echo "  🧪 Running SantaList Tests on iOS Simulator ($DEVICE_NAME)"
echo "=================================================="

xcodebuild test \
    -project "$PROJECT" \
    -scheme "$SCHEME" \
    -sdk iphonesimulator \
    -destination "platform=iOS Simulator,name=$DEVICE_NAME" \
    CODE_SIGNING_ALLOWED=NO

echo "=================================================="
echo "  ✅ All SantaList Tests Passed!"
echo "=================================================="
