#!/bin/sh
set -e
base_dir=$(dirname "$0")
case "$base_dir" in
    */) base_dir="${base_dir%/}" ;;
esac

# Find mvnw jar and conf
WRAPPER_JAR="$base_dir/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPS="$base_dir/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Error: Maven wrapper jar not found at $WRAPPER_JAR" >&2
    exit 1
fi

exec "$JAVACMD" $MAVEN_OPTS -jar "$WRAPPER_JAR" "$@"