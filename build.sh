#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/classes/emartproject/images dist
javac -encoding UTF-8 -d build/classes src/emartproject/*.java
cp src/emartproject/images/*.png build/classes/emartproject/images/
jar cfe dist/EMartProject.jar emartproject.EMartProject -C build/classes .
echo "Built dist/EMartProject.jar"
