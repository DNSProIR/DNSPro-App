@ECHO OFF
SETLOCAL
SET DIR=%~dp0
WHERE gradle >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (
  gradle %*
  EXIT /B %ERRORLEVEL%
)
ECHO Gradle is not installed. Install Gradle 8.9+ or open this project in Android Studio.
EXIT /B 1
