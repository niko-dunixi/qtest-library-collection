#!/usr/bin/env bash
#if [ -d build/reports ]; then
#    rm -rfv build/reports
#fi
rm -rfv ./build
../gradlew clean build test
find . -type f -iname "index.html" -exec open \{\} \;
