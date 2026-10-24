#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
driver_version="9.4.0"
driver="lib/mysql-connector-j-${driver_version}.jar"
mkdir -p dist lib
# Older NetBeans .class artifacts were committed in this repository (some use Java 26).
# Always start with an empty output directory to prevent stale bytecode entering the JAR.
rm -rf build/classes
mkdir -p build/classes/emartproject/images
if [ ! -s "$driver" ]; then
  curl -fL --retry 3 "https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/${driver_version}/mysql-connector-j-${driver_version}.jar" -o "$driver"
fi
# Target Java 8 bytecode regardless of whether the developer has JDK 8, 17 or 26.
# This prevents UnsupportedClassVersionError when the runtime is older than javac.
compiler_version="$(javac -version 2>&1)"
if [[ "$compiler_version" == javac\ 1.8* ]]; then
  javac -source 8 -target 8 -encoding UTF-8 -d build/classes src/emartproject/*.java
else
  javac --release 8 -encoding UTF-8 -d build/classes src/emartproject/*.java
fi
cp src/emartproject/images/*.png build/classes/emartproject/images/
printf 'Manifest-Version: 1.0\r\nMain-Class: emartproject.EMartProject\r\nClass-Path: ../lib/mysql-connector-j-%s.jar\r\n\r\n' "$driver_version" > build/emart-manifest.mf
jar cfm dist/EMartProject.jar build/emart-manifest.mf -C build/classes .
echo "Built dist/EMartProject.jar (with external Connector/J in lib/)"
