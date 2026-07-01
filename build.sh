#!/usr/bin/env bash
set -e
echo "Сборка Chat AI Moderator для Fabric 1.21.11..."
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle не найден. Установи Gradle 9.6+ и Java 21 или собери через GitHub Actions."
  exit 1
fi
gradle build
echo "Готово! JAR находится в build/libs/"
