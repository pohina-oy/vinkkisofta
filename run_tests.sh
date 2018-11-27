#!/bin/sh

echo "chromedriver path: $(which chromedriver)"
echo "chromium-browser path: $(which chromium-browser)"

gradle check "-DchromeDriverBinary=$(which chromedriver)" "-DchromeBinary=$(which chromium-browser)"
