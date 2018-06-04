#!/usr/bin/env bash
if [ -d build/reports ]; then
    rm -rfv build/reports
fi
gradle test
open build/reports/tests/test/index.html
