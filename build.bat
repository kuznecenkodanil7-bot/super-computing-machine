@echo off
chcp 65001 >nul
echo Сборка Chat AI Moderator для Fabric 1.21.11...
where gradle >nul 2>nul
if errorlevel 1 (
  echo.
  echo Gradle не найден.
  echo Самый простой вариант: залей проект на GitHub, вкладка Actions сама соберет jar.
  echo Либо установи Gradle 9.6+ и Java 21, потом снова запусти build.bat.
  pause
  exit /b 1
)
gradle build
if errorlevel 1 (
  echo.
  echo Ошибка сборки. Скинь сюда полный лог из консоли.
  pause
  exit /b 1
)
echo.
echo Готово! JAR находится в папке build\libs\
pause
