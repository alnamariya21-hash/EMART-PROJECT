#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
driver_version="9.4.0"
driver="lib/mysql-connector-j-${driver_version}.jar"
mkdir -p build/classes/emartproject/images dist lib
if [ ! -s "$driver" ]; then
  curl -fL --retry 3 "https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/${driver_version}/mysql-connector-j-${driver_version}.jar" -o "$driver"
fi
javac -encoding UTF-8 -d build/classes src/emartproject/*.java
cp src/emartproject/images/*.png build/classes/emartproject/images/
printf 'Manifest-Version: 1.0\r\nMain-Class: emartproject.EMartProject\r\nClass-Path: ../lib/mysql-connector-j-%s.jar\r\n\r\n' "$driver_version" > build/emart-manifest.mf
jar cfm dist/EMartProject.jar build/emart-manifest.mf -C build/classes .
echo "Built dist/EMartProject.jar (with external Connector/J in lib/)"
