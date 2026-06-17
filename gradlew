#!/usr/bin/env sh
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
"$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
